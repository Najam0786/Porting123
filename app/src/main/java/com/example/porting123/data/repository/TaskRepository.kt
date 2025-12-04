package com.example.porting123.data.repository

import com.example.porting123.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun syncTasks()
    suspend fun toggleTaskCompletion(task: Task)
}
