package com.example.smartattendancesystem.data.remote.firebase

import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.local.entity.StudentEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser

    // AUTHENTICATION
    suspend fun signIn(email: String, pass: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() = auth.signOut()

    // FIRESTORE ONLY (No Storage)
    suspend fun uploadStudent(student: StudentEntity): Boolean {
        val user = currentUser ?: return false
        
        // Save to Firestore (Photos stay on local device only)
        val studentMap = hashMapOf(
            "name" to student.name,
            "rollNumber" to student.rollNumber,
            "embedding" to student.embedding.toList(),
            "localImagePath" to student.imagePath, // Keep path for local reference
            "createdBy" to user.uid,
            "updatedAt" to System.currentTimeMillis()
        )

        return try {
            db.collection("students").document(student.rollNumber).set(studentMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun uploadAttendance(attendance: AttendanceEntity): Boolean {
        val user = currentUser ?: return false

        val attendanceMap = hashMapOf(
            "studentId" to attendance.studentId,
            "studentName" to attendance.studentName,
            "timestamp" to attendance.timestamp,
            "localImagePath" to attendance.imagePath,
            "markedBy" to user.uid
        )

        return try {
            db.collection("attendance").add(attendanceMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}