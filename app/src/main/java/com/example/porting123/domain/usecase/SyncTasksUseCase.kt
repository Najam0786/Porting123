package com.example.porting123.domain.usecase

import com.example.porting123.data.repository.TaskRepository
import javax.inject.Inject

class SyncTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() {
        repository.syncTasks()
    }
}
