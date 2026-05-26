package com.example.smartattendancesystem.utils

import android.content.Context
import android.os.Environment
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CSVExporter {

    fun exportAttendanceToCSV(context: Context, attendanceList: List<AttendanceEntity>): String? {
        val fileName = "Attendance_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        
        try {
            val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(folder, fileName)
            val writer = FileWriter(file)

            // Header
            writer.append("ID,Student ID,Student Name,Timestamp,Date\n")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            for (attendance in attendanceList) {
                writer.append("${attendance.id},")
                writer.append("${attendance.studentId},")
                writer.append("${attendance.studentName},")
                writer.append("${attendance.timestamp},")
                writer.append("${dateFormat.format(Date(attendance.timestamp))}\n")
            }

            writer.flush()
            writer.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}