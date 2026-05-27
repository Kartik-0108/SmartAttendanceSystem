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
    private var lastMarkedStudentId: Int? = null
    private var lastMarkedTime: Long = 0

    fun processFace(embedding: FloatArray, bitmap: Bitmap, isLive: Boolean) {
        if (isProcessing) return

        viewModelScope.launch {
            isProcessing = true
            try {
                if (!isLive) {
                    _attendanceStatus.value = "Liveness Check Failed! Please Blink."
                    return@launch
                }

                val student = repository.findStudentByEmbedding(embedding)
                if (student != null) {
                    // Prevent marking the same student again within 1 minute
                    val currentTime = System.currentTimeMillis()
                    if (student.id == lastMarkedStudentId && (currentTime - lastMarkedTime) < 60000) {
                        _attendanceStatus.value = "Attendance already marked for ${student.name}"
                        return@launch
                    }

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
                            timestamp = currentTime,
                            imagePath = imagePath
                        )
                    )
                    
                    lastMarkedStudentId = student.id
                    lastMarkedTime = currentTime
                    
                    _attendanceStatus.value = "Attendance Marked for ${student.name}!"
                    // Keep the success message visible
                    kotlinx.coroutines.delay(4000)
                    _attendanceStatus.value = "Ready for next student"
                } else {
                    _attendanceStatus.value = "Student Not Recognized"
                    kotlinx.coroutines.delay(2000)
                    _attendanceStatus.value = "Ready for next student"
                }
            } catch (e: Exception) {
                _attendanceStatus.value = "Error: ${e.message}"
            } finally {
                isProcessing = false
            }
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