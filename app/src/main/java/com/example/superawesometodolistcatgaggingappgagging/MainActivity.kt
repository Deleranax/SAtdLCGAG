package com.example.superawesometodolistcatgaggingappgagging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.CalendarScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.Day
import com.example.superawesometodolistcatgaggingappgagging.screens.login.LoginScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.task.TaskScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.task.ToDo_Item
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme

enum class Screens() {
    Login, Calendar, Task
}

val tempDay = Day(15, 5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTempDay(tempDay)
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
            CalendarScreen()
        }
        dialog(route = Screens.Task.name) {
            //Pass current day into task
            TaskScreen()
        }
    }
}

fun initTempDay(day: Day){
    val tempItem1 = ToDo_Item(
        day = 15,
        month = 5,
        "Birthday Party",
        "This is the description for a birthday"
    )

    val tempItem2 = ToDo_Item(
        day = 20,
        month = 1,
        "Start school",
        "This is the description for school starting"
    )
    day.addItem(tempItem1)
    day.addItem(tempItem2)
}