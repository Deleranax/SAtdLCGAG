package com.example.superawesometodolistcatgaggingappgagging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.superawesometodolistcatgaggingappgagging.api.TodoApi
import com.example.superawesometodolistcatgaggingappgagging.api.TodoFetchWorker
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.CalendarScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.login.LoginScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.task.TaskScreen
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import java.time.LocalDate

enum class Screens() {
    Login, Calendar, Task
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoApi.init(LocalContext.current)

            AppTheme {
                Routes()
            }
        }
    }
}

@Composable
fun Routes(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    var newTaskDate = remember { mutableStateOf(LocalDate.now()) }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Login.name
    ) {
        composable(route = Screens.Login.name) {
            LoginScreen(
                onSignIn = {
                    // Fetching data from API
                    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<TodoFetchWorker>().build()
                    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

                    navController.navigate(
                        route = Screens.Calendar.name,
                        navOptions {
                            popUpTo(Screens.Login.name) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        composable(route = Screens.Calendar.name) {
            //Include button in calendar screen then passing the selected day into NoteScreen
            CalendarScreen(
                onNewTask = {
                    newTaskDate.value = it
                    navController.navigate(
                        route = Screens.Task.name
                    )
                },
                onLogout = {
                    navController.navigate(
                        route = Screens.Login.name,
                        navOptions {
                            popUpTo(Screens.Calendar.name) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        dialog(route = Screens.Task.name) {
            //Pass current day (selected from calendar) into NoteScreen
            TaskScreen(
                date = newTaskDate.value,
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}