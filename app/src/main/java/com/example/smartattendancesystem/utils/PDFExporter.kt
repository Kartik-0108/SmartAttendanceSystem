package com.example.smartattendancesystem.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PDFExporter {

    fun exportAttendanceToPDF(context: Context, attendanceList: List<AttendanceEntity>): String? {
        val fileName = "Attendance_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.textSize = 20f
        titlePaint.color = Color.BLACK
        titlePaint.isFakeBoldText = true
        
        canvas.drawText("Smart Attendance System - Report", 50f, 50f, titlePaint)
        
        paint.textSize = 12f
        paint.color = Color.DKGRAY
        canvas.drawText("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}", 50f, 80f, paint)

        var yPos = 120f
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Student Name", 50f, yPos, paint)
        canvas.drawText("Roll Number", 250f, yPos, paint)
        canvas.drawText("Date & Time", 400f, yPos, paint)
        
        yPos += 30f
        paint.isFakeBoldText = false
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        for (attendance in attendanceList) {
            if (yPos > 800) break // Simple single page support for now
            
            canvas.drawText(attendance.studentName, 50f, yPos, paint)
            canvas.drawText(attendance.studentId.toString(), 250f, yPos, paint)
            canvas.drawText(dateFormat.format(Date(attendance.timestamp)), 400f, yPos, paint)
            yPos += 25f
        }

        pdfDocument.finishPage(page)

        try {
            val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(folder, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }
}