package com.example.smartattendancesystem.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartattendancesystem.data.local.database.AppDatabase
import com.example.smartattendancesystem.data.local.entity.StudentEntity
import com.example.smartattendancesystem.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    private val _students = MutableStateFlow<List<StudentEntity>>(emptyList())
    val students: StateFlow<List<StudentEntity>> = _students.asStateFlow()

    private val _totalStudents = MutableStateFlow(0)
    val totalStudents: StateFlow<Int> = _totalStudents.asStateFlow()

    private val _totalAttendance = MutableStateFlow(0)
    val totalAttendance: StateFlow<Int> = _totalAttendance.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AttendanceRepository(
            database.studentDao(),
            database.attendanceDao()
        )
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            val allStudents = repository.getAllStudents()
            _students.value = allStudents
            _totalStudents.value = allStudents.size
            _totalAttendance.value = repository.getAllAttendance().size
        }
    }

    fun searchStudents(query: String) {
        viewModelScope.launch {
            _students.value = repository.searchStudents(query)
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
            loadStudents()
        }
    }

    fun deleteAllAttendance() {
        viewModelScope.launch {
            repository.deleteAllAttendance()
            loadStudents()
        }
    }
}