package com.example.superawesometodolistcatgaggingappgagging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.CalendarScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.login.LoginScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.task.TaskScreen
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import java.time.LocalDate

enum class Screens() {
    Login, Calendar, Task()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
    var newTaskDate = remember { mutableStateOf(LocalDate.now()) }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Login.name
    ) {
        composable(route = Screens.Login.name) {
            LoginScreen(
                onSignIn = {
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