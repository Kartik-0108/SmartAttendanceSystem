package com.example.smartattendancesystem.data.remote.api

import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.local.entity.StudentEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AttendanceApi {

    @POST("/students")
    suspend fun syncStudents(@Body students: List<StudentEntity>)

    @POST("/attendance")
    suspend fun syncAttendance(@Body attendance: List<AttendanceEntity>)

    @GET("/students")
    suspend fun getStudents(): List<StudentEntity>
}