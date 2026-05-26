package com.example.smartattendancesystem.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.*

object ImageUtils {

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, folderName: String): String? {
        val fileName = "${UUID.randomUUID()}.jpg"
        val directory = File(context.filesDir, folderName)
        
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}