package com.example.porting123

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.porting123.presentation.ui.screen.AddTaskScreen
import com.example.porting123.presentation.ui.screen.TaskListScreen
import com.example.porting123.ui.theme.Porting123Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Porting123Theme {
                PortingApp()
            }
        }
    }
}

@Composable
fun PortingApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TaskListScreen(
                onAddTask = { navController.navigate("addTask") }
            )
        }
        composable("addTask") {
            AddTaskScreen(
                onTaskAdded = { navController.popBackStack() }
            )
        }
    }
}
