package com.example.smartattendancesystem.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartattendancesystem.data.local.database.AppDatabase
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.local.entity.StudentEntity
import com.example.smartattendancesystem.data.repository.AttendanceRepository
import com.example.smartattendancesystem.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    private val _attendanceStatus = MutableStateFlow<String>("Scan Face to Start")
    val attendanceStatus: StateFlow<String> = _attendanceStatus.asStateFlow()

    private val _lastDetectedStudent = MutableStateFlow<StudentEntity?>(null)
    val lastDetectedStudent: StateFlow<StudentEntity?> = _lastDetectedStudent.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AttendanceRepository(
            database.studentDao(),
            database.attendanceDao()
        )
    }

    private var isProcessing = false

    fun processFace(embedding: FloatArray, bitmap: Bitmap, isLive: Boolean) {
        if (isProcessing) return

        viewModelScope.launch {
            isProcessing = true
            
            if (!isLive) {
                _attendanceStatus.value = "Liveness Check Failed! Please Blink."
                isProcessing = false
                return@launch
            }

            val student = repository.findStudentByEmbedding(embedding)
            if (student != null) {
                _lastDetectedStudent.value = student
                _attendanceStatus.value = "Hello ${student.name}! Marking attendance..."
                
                val imagePath = ImageUtils.saveBitmapToInternalStorage(
                    getApplication(), 
                    bitmap, 
                    "attendance_pics"
                )

                // Record attendance
                repository.markAttendance(
                    AttendanceEntity(
                        studentId = student.id,
                        studentName = student.name,
                        timestamp = System.currentTimeMillis(),
                        imagePath = imagePath
                    )
                )
                
                _attendanceStatus.value = "Attendance Marked for ${student.name}!"
                // Add a delay so the message can be read
                kotlinx.coroutines.delay(3000)
                _attendanceStatus.value = "Ready for next student"
            } else {
                _attendanceStatus.value = "Student Not Recognized"
            }
            
            isProcessing = false
        }
    }

    fun registerStudent(name: String, rollNumber: String, embedding: FloatArray, bitmap: Bitmap) {
        viewModelScope.launch {
            val imagePath = ImageUtils.saveBitmapToInternalStorage(
                getApplication(), 
                bitmap, 
                "student_pics"
            )

            repository.insertStudent(
                StudentEntity(
                    name = name,
                    rollNumber = rollNumber,
                    embedding = embedding,
                    imagePath = imagePath
                )
            )
            _attendanceStatus.value = "Student $name Registered Successfully!"
        }
    }
}