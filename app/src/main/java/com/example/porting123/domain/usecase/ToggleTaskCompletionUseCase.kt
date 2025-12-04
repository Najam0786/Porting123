package com.example.porting123.domain.usecase

import com.example.porting123.data.repository.TaskRepository
import com.example.porting123.domain.model.Task
import javax.inject.Inject

class ToggleTaskCompletionUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.toggleTaskCompletion(task)
    }
}
