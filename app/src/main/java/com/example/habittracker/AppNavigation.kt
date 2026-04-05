package com.example.habittracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import com.example.habittracker.routes.Screen
import com.example.habittracker.screen.HomeScreen
import com.example.habittracker.screen.LoginScreen
import com.example.habittracker.screen.RegisterScreen
import com.example.habittracker.screen.TimerScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(onStartTimer = { taskId ->
                navController.navigate(Screen.Timer.createRoute(taskId))
            })
        }

        composable(Screen.Timer.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TimerScreen(taskId = taskId, onBack = {
                navController.popBackStack()
            })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    AppNavigation()
}