package com.example.porting123.domain.usecase

import com.example.porting123.data.repository.TaskRepository
import com.example.porting123.domain.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getAllTasks()
    }
}
