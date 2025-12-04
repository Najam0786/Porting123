package com.example.porting123.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.porting123.domain.model.Priority
import com.example.porting123.domain.model.Task
import com.example.porting123.domain.usecase.AddTaskUseCase
import com.example.porting123.domain.service.ValidationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val validationService: ValidationService
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority = _priority.asStateFlow()

    private val _isTitleValid = MutableStateFlow(true)
    val isTitleValid = _isTitleValid.asStateFlow()

    private val _taskAdded = MutableSharedFlow<Unit>(replay = 0)
    val taskAdded = _taskAdded.asSharedFlow()

    fun onTitleChanged(title: String) {
        _title.value = title
        _isTitleValid.value = validationService.validateTaskTitle(title)
    }

    fun onDescriptionChanged(description: String) {
        _description.value = description
    }

    fun onPriorityChanged(priority: Priority) {
        _priority.value = priority
    }

    fun addTask() {
        Log.d("AddTaskViewModel", "addTask called, title=${_title.value}, priority=${_priority.value}")
        if (!validationService.validateTaskTitle(_title.value)) {
            _isTitleValid.value = false
            return
        }

        viewModelScope.launch {
            addTaskUseCase(
                Task(
                    title = _title.value,
                    description = _description.value,
                    priority = _priority.value
                )
            )
            // Clear input fields after successful add
            _title.value = ""
            _description.value = ""
            _priority.value = Priority.MEDIUM
            _isTitleValid.value = true

            Log.d("AddTaskViewModel", "Task added successfully")
            _taskAdded.emit(Unit)
        }
    }
}
