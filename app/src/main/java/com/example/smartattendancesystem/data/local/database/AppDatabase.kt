package com.example.smartattendancesystem.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartattendancesystem.data.local.dao.AttendanceDao
import com.example.smartattendancesystem.data.local.dao.StudentDao
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.data.local.entity.StudentEntity

@Database(
    entities = [
        StudentEntity::class,
        AttendanceEntity::class
    ],
    version = 3
)

@TypeConverters(Converters::class)

abstract class AppDatabase :
    RoomDatabase() {

    abstract fun studentDao():
            StudentDao

    abstract fun attendanceDao():
            AttendanceDao

    companion object {

        @Volatile
        private var INSTANCE:
                AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "attendance_database"
                    ).fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}