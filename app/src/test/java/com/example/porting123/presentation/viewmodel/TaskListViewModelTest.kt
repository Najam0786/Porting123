package com.example.porting123.presentation.viewmodel

import com.example.porting123.data.repository.TaskRepository
import com.example.porting123.domain.model.Priority
import com.example.porting123.domain.model.Task
import com.example.porting123.domain.usecase.GetTasksUseCase
import com.example.porting123.domain.usecase.ToggleTaskCompletionUseCase
import com.example.porting123.domain.usecase.SyncTasksUseCase
import com.example.porting123.domain.usecase.DeleteTaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskListViewModelTest {

    @Test
    fun priorityCounts_reflects_repository_tasks() = runBlocking {
        val tasks = listOf(
            Task(title = "A", description = "", priority = Priority.HIGH),
            Task(title = "B", description = "", priority = Priority.MEDIUM),
            Task(title = "C", description = "", priority = Priority.LOW),
            Task(title = "D", description = "", priority = Priority.HIGH),
        )

        val repo = object : TaskRepository {
            override fun getAllTasks(): Flow<List<Task>> = flowOf(tasks)
            override suspend fun insertTask(task: Task) {}
            override suspend fun deleteTask(task: Task) {}
            override suspend fun syncTasks() {}
            override suspend fun toggleTaskCompletion(task: Task) {}
        }

        val getTasksUseCase = GetTasksUseCase(repo)
        val vm = TaskListViewModel(
            getTasksUseCase = getTasksUseCase,
            syncTasksUseCase = SyncTasksUseCase(repo),
            deleteTaskUseCase = DeleteTaskUseCase(repo),
            toggleTaskCompletionUseCase = ToggleTaskCompletionUseCase(repo)
        )

        val counts = vm.priorityCounts
        assertEquals(2, counts.value[Priority.HIGH])
        assertEquals(1, counts.value[Priority.MEDIUM])
        assertEquals(1, counts.value[Priority.LOW])
    }

    @Test
    fun filter_by_priority_only() = runBlocking {
        val tasks = listOf(
            Task(title = "Alpha", description = "", priority = Priority.HIGH),
            Task(title = "Beta", description = "", priority = Priority.MEDIUM),
            Task(title = "Gamma", description = "", priority = Priority.MEDIUM),
            Task(title = "Delta", description = "", priority = Priority.LOW),
        )
        val repo = object : TaskRepository {
            override fun getAllTasks(): Flow<List<Task>> = flowOf(tasks)
            override suspend fun insertTask(task: Task) {}
            override suspend fun deleteTask(task: Task) {}
            override suspend fun syncTasks() {}
            override suspend fun toggleTaskCompletion(task: Task) {}
        }
        val getTasksUseCase = GetTasksUseCase(repo)
        val vm = TaskListViewModel(getTasksUseCase, SyncTasksUseCase(repo), DeleteTaskUseCase(repo), ToggleTaskCompletionUseCase(repo))

        // Initially all
        assertEquals(4, vm.totalCount.value)

        // Apply MEDIUM filter
        vm.onPrioritySelected(Priority.MEDIUM)
        val filtered = vm.filteredTasks
        assertEquals(2, filtered.value.size)
    }

    @Test
    fun search_and_filter_combination() = runBlocking {
        val tasks = listOf(
            Task(title = "Buy milk", description = "", priority = Priority.HIGH),
            Task(title = "Buy bread", description = "", priority = Priority.MEDIUM),
            Task(title = "Clean house", description = "", priority = Priority.MEDIUM),
            Task(title = "Pay bills", description = "", priority = Priority.LOW),
        )
        val repo = object : TaskRepository {
            override fun getAllTasks(): Flow<List<Task>> = flowOf(tasks)
            override suspend fun insertTask(task: Task) {}
            override suspend fun deleteTask(task: Task) {}
            override suspend fun syncTasks() {}
            override suspend fun toggleTaskCompletion(task: Task) {}
        }
        val getTasksUseCase = GetTasksUseCase(repo)
        val vm = TaskListViewModel(getTasksUseCase, SyncTasksUseCase(repo), DeleteTaskUseCase(repo), ToggleTaskCompletionUseCase(repo))

        // Search 'Buy' and filter MEDIUM
        vm.onSearchQueryChanged("Buy")
        vm.onPrioritySelected(Priority.MEDIUM)

        val filtered = vm.filteredTasks
        assertEquals(1, filtered.value.size)
        assertEquals("Buy bread", filtered.value[0].title)
    }
}
