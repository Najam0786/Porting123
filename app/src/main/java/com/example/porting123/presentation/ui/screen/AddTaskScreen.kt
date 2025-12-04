package com.example.porting123.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.porting123.domain.model.Priority
import com.example.porting123.presentation.viewmodel.AddTaskViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel(),
    onTaskAdded: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val isTitleValid by viewModel.isTitleValid.collectAsState()
    val currentPriority by viewModel.priority.collectAsState()
    val taskAddedFlow = viewModel.taskAdded

    LaunchedEffect(taskAddedFlow) {
        taskAddedFlow.collect {
            onTaskAdded()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Title") },
            isError = !isTitleValid,
            modifier = Modifier.fillMaxWidth()
        )
        if (!isTitleValid) {
            Text("Title cannot be empty", color = androidx.compose.ui.graphics.Color.Red)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Priority selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Priority.values().forEach { p ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (p == currentPriority),
                            onClick = { viewModel.onPriorityChanged(p) },
                            role = Role.RadioButton
                        )
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = p == currentPriority,
                        onClick = { viewModel.onPriorityChanged(p) },
                        colors = RadioButtonDefaults.colors()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = p.name)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.addTask()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}
