package com.example.smartattendancesystem.data.local.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromFloatArray(array: FloatArray): String {

        return array.joinToString(",")
    }

    @TypeConverter
    fun toFloatArray(data: String): FloatArray {

        if (data.isEmpty()) return floatArrayOf()

        return data
            .split(",")
            .map { it.toFloat() }
            .toFloatArray()
    }
}