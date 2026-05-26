package com.example.smartattendancesystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(
        attendance: AttendanceEntity
    )

    @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
    suspend fun getAllAttendance(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getAttendanceInRange(startTime: Long, endTime: Long): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY timestamp DESC")
    suspend fun getAttendanceByStudent(studentId: Int): List<AttendanceEntity>

    @Query("DELETE FROM attendance")
    suspend fun deleteAllAttendance()
}