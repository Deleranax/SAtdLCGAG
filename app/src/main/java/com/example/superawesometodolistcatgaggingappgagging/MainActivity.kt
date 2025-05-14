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
import com.example.superawesometodolistcatgaggingappgagging.screens.CalendarScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.LoginScreen
import com.example.superawesometodolistcatgaggingappgagging.screens.NoteScreen
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme

enum class Screens() {
    Login, Calendar, Note
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
            CalendarScreen()
        }
        dialog(route = Screens.Note.name) {
            NoteScreen()
        }
    }
}