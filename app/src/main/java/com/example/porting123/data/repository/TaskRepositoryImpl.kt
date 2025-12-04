package com.example.porting123.data.repository

import com.example.porting123.data.local.dao.TaskDao
import com.example.porting123.data.local.entity.TaskEntity
import com.example.porting123.data.remote.api.TaskApiService
import com.example.porting123.data.remote.dto.toTaskEntity
import com.example.porting123.domain.model.Priority
import com.example.porting123.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApiService: TaskApiService
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    override suspend fun insertTask(task: Task) {
        android.util.Log.d("TaskRepositoryImpl", "Inserting task: ${task.title} id=${task.id}")
        taskDao.insertTask(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        android.util.Log.d("TaskRepositoryImpl", "Deleting task: ${task.title} id=${task.id}")
        taskDao.deleteTask(task.toTaskEntity())
    }

    override suspend fun syncTasks() {
        try {
            val remoteTasks = taskApiService.getTasks()
            taskDao.insertAll(remoteTasks.map { it.toTaskEntity() })
        } catch (e: Exception) {
            android.util.Log.e("TaskRepositoryImpl", "Error syncing tasks", e)
        }
    }

    override suspend fun toggleTaskCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        android.util.Log.d("TaskRepositoryImpl", "Toggling task completion: ${task.title} -> ${updatedTask.isCompleted}")
        taskDao.insertTask(updatedTask.toTaskEntity())
    }
}

private fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        priority = Priority.valueOf(this.priority),
        createdAt = this.createdAt,
        dueDate = this.dueDate
    )
}

private fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        priority = this.priority.name,
        createdAt = this.createdAt,
        dueDate = this.dueDate
    )
}
