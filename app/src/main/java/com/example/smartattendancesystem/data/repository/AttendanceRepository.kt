package com.example.smartattendancesystem.data.repository

import com.example.smartattendancesystem.data.local.dao.AttendanceDao
import com.example.smartattendancesystem.data.local.dao.StudentDao
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.local.entity.StudentEntity
import com.example.smartattendancesystem.data.remote.api.RetrofitClient
import com.example.smartattendancesystem.utils.FaceMath

class AttendanceRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao
) {

    suspend fun syncWithBackend() {
        try {
            val api = RetrofitClient.instance
            val localStudents = studentDao.getAllStudents()
            val localAttendance = attendanceDao.getAllAttendance()
            
            api.syncStudents(localStudents)
            api.syncAttendance(localAttendance)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insertStudent(student: StudentEntity) {
        studentDao.insertStudent(student)
    }

    suspend fun getAllStudents(): List<StudentEntity> {
        return studentDao.getAllStudents()
    }

    suspend fun searchStudents(query: String): List<StudentEntity> {
        return studentDao.searchStudents(query)
    }

    suspend fun deleteStudent(studentId: Int) {
        studentDao.deleteStudent(studentId)
    }

    suspend fun markAttendance(attendance: AttendanceEntity) {
        attendanceDao.insertAttendance(attendance)
    }

    suspend fun getAllAttendance(): List<AttendanceEntity> {
        return attendanceDao.getAllAttendance()
    }

    suspend fun getAttendanceInRange(startTime: Long, endTime: Long): List<AttendanceEntity> {
        return attendanceDao.getAttendanceInRange(startTime, endTime)
    }

    suspend fun getAttendanceByStudent(studentId: Int): List<AttendanceEntity> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    suspend fun deleteAllAttendance() {
        attendanceDao.deleteAllAttendance()
    }

    suspend fun findStudentByEmbedding(
        liveEmbedding: FloatArray,
        threshold: Float = 0.8f
    ): StudentEntity? {
        val students = studentDao.getAllStudents()
        var bestMatch: StudentEntity? = null
        var maxSimilarity = -1f

        for (student in students) {
            val similarity = FaceMath.calculateCosineSimilarity(
                liveEmbedding,
                student.embedding
            )
            if (similarity > threshold && similarity > maxSimilarity) {
                maxSimilarity = similarity
                bestMatch = student
            }
        }
        return bestMatch
    }
}