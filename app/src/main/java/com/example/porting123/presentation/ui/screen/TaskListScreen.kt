package com.example.porting123.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.porting123.domain.model.Priority
import com.example.porting123.presentation.ui.component.SearchBar
import com.example.porting123.presentation.ui.component.TaskItem
import com.example.porting123.presentation.ui.component.BadgeChip
import com.example.porting123.presentation.viewmodel.TaskListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    onAddTask: () -> Unit
) {
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPriority by viewModel.selectedPriority.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Tasks") }, actions = {
                IconButton(onClick = { viewModel.syncTasks() }) {
                    Text("🔄", fontSize = 20.sp)
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Text("➕", fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChanged
                )

                // Priority filter row with FilterChips, counts and a clear filter icon
                val counts by viewModel.priorityCounts.collectAsStateWithLifecycle()
                val total by viewModel.totalCount.collectAsStateWithLifecycle()
                Row(modifier = Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Start) {
                    // All chip
                    BadgeChip(
                        label = "All",
                        count = total,
                        selected = selectedPriority == null,
                        color = Color.Transparent,
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { viewModel.onPrioritySelected(null) }
                    )

                    Priority.entries.forEach { p ->
                        val count = counts[p] ?: 0
                        val color = when (p) {
                            Priority.HIGH -> Color(0xFFFFCDD2) // light red
                            Priority.MEDIUM -> Color(0xFFFFF9C4) // light amber
                            Priority.LOW -> Color(0xFFC8E6C9) // light green
                        }
                        BadgeChip(
                            label = p.name,
                            count = count,
                            selected = selectedPriority == p,
                            color = color,
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { viewModel.onPrioritySelected(p) }
                        )
                    }

                    // Clear filter icon (visible when a filter is active)
                    if (selectedPriority != null) {
                        IconButton(onClick = { viewModel.onPrioritySelected(null) }, modifier = Modifier.padding(start = 4.dp)) {
                            Text("✕", fontSize = 18.sp)
                        }
                    }
                }
                if (tasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tasks yet. Tap + to add one.")
                    }
                } else {
                    LazyColumn {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onToggleComplete = viewModel::toggleTaskCompletion,
                                onDelete = viewModel::deleteTask
                            )
                        }
                    }
                }
            }
        }
    }
}
