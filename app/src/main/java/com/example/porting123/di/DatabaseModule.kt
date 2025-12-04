package com.example.porting123.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.porting123.data.local.dao.TaskDao
import com.example.porting123.data.local.database.AppDatabase
import com.example.porting123.data.local.entity.TaskEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate database on creation
                CoroutineScope(Dispatchers.IO).launch {
                    val sample = TaskEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        title = "Welcome Task",
                        description = "This is a sample task",
                        isCompleted = false,
                        priority = "MEDIUM",
                        createdAt = System.currentTimeMillis(),
                        dueDate = null
                    )
                    // Use a direct writable database insert via SQL
                    val insert = "INSERT INTO tasks (id, title, description, isCompleted, priority, createdAt, dueDate) VALUES ('${sample.id}', '${sample.title.replace("'","''")}', '${sample.description.replace("'","''")}', ${if (sample.isCompleted) 1 else 0}, '${sample.priority}', ${sample.createdAt}, NULL)"
                    db.execSQL(insert)
                }
            }
        }).build()
    }

    @Provides
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }
}
