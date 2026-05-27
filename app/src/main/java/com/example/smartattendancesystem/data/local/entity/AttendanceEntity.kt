package com.example.smartattendancesystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val studentId: Int,

    val studentName: String,

    val timestamp: Long,

    val imagePath: String? = null,

    val isSynced: Boolean = false
)