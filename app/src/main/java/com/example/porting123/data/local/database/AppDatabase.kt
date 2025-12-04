package com.example.porting123.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.porting123.data.local.dao.TaskDao
import com.example.porting123.data.local.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
