package com.example.smartattendancesystem.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartattendancesystem.data.local.database.AppDatabase
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    private val _allAttendance = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val allAttendance: StateFlow<List<AttendanceEntity>> = _allAttendance.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AttendanceRepository(
            database.studentDao(),
            database.attendanceDao()
        )
        loadAttendance()
    }

    fun loadAttendance() {
        viewModelScope.launch {
            _allAttendance.value = repository.getAllAttendance()
        }
    }

    fun getDailyAttendance() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startTime = calendar.timeInMillis
            
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endTime = calendar.timeInMillis

            _allAttendance.value = repository.getAttendanceInRange(startTime, endTime)
        }
    }

    fun getWeeklyAttendance() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            _allAttendance.value = repository.getAttendanceInRange(startTime, endTime)
        }
    }

    fun getMonthlyAttendance() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            _allAttendance.value = repository.getAttendanceInRange(startTime, endTime)
        }
    }

    fun syncData() {
        viewModelScope.launch {
            repository.syncWithBackend()
        }
    }
}