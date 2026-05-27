package com.example.smartattendancesystem.data.repository

import com.example.smartattendancesystem.data.local.dao.AttendanceDao
import com.example.smartattendancesystem.data.local.dao.StudentDao
import com.example.smartattendancesystem.data.remote.firebase.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val firebaseManager: FirebaseManager
) {

    suspend fun syncEverything() = withContext(Dispatchers.IO) {
        if (firebaseManager.currentUser == null) return@withContext

        // 1. Sync Students to Cloud
        val unsyncedStudents = studentDao.getUnsyncedStudents()
        unsyncedStudents.forEach { student ->
            val success = firebaseManager.uploadStudent(student)
            if (success) {
                studentDao.markSynced(student.id)
            }
        }

        // 2. Sync Attendance to Cloud
        val unsyncedAttendance = attendanceDao.getUnsyncedAttendance()
        unsyncedAttendance.forEach { record ->
            val success = firebaseManager.uploadAttendance(record)
            if (success) {
                attendanceDao.markSynced(record.id)
            }
        }
    }
}