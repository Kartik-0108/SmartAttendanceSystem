package com.example.smartattendancesystem.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val rollNumber: String,

    val embedding: FloatArray,

    val imagePath: String? = null,

    val isSynced: Boolean = false
)