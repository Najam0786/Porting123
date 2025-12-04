package com.example.porting123.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.porting123.domain.model.Priority
import com.example.porting123.domain.model.Task
import com.example.porting123.domain.usecase.DeleteTaskUseCase
import com.example.porting123.domain.usecase.GetTasksUseCase
import com.example.porting123.domain.usecase.SyncTasksUseCase
import com.example.porting123.domain.usecase.ToggleTaskCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val syncTasksUseCase: SyncTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority: StateFlow<Priority?> = _selectedPriority.asStateFlow()

    // Source tasks flow from repository
    private val tasksFlow = getTasksUseCase()

    // Expose counts per priority and total
    val priorityCounts = tasksFlow.map { list ->
        val low = list.count { it.priority == Priority.LOW }
        val med = list.count { it.priority == Priority.MEDIUM }
        val high = list.count { it.priority == Priority.HIGH }
        mapOf(Priority.LOW to low, Priority.MEDIUM to med, Priority.HIGH to high)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mapOf(Priority.LOW to 0, Priority.MEDIUM to 0, Priority.HIGH to 0))

    val totalCount = tasksFlow.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredTasks = combine(
        tasksFlow,
        _searchQuery,
        _selectedPriority
    ) { tasks, query, priority ->
        val filtered = tasks.filter { task ->
            val queryMatch = query.isBlank() || task.title.contains(query, ignoreCase = true)
            val priorityMatch = priority == null || task.priority == priority
            queryMatch && priorityMatch
        }
        Log.d("TaskListViewModel", "Filtered tasks count=${filtered.size}")
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onPrioritySelected(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun syncTasks() {
        viewModelScope.launch {
            syncTasksUseCase()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task)
        }
    }
}
