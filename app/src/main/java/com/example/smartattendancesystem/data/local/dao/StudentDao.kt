package com.example.smartattendancesystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartattendancesystem.data.local.entity.StudentEntity

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(
        student: StudentEntity
    )

    @Query("SELECT * FROM students")
    suspend fun getAllStudents():
            List<StudentEntity>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR rollNumber LIKE '%' || :query || '%'")
    suspend fun searchStudents(query: String): List<StudentEntity>

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudent(studentId: Int)

    @Query("SELECT * FROM students WHERE isSynced = 0")
    suspend fun getUnsyncedStudents(): List<StudentEntity>

    @Query("UPDATE students SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)
}