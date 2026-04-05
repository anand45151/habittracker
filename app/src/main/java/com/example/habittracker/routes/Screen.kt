package com.example.habittracker.routes

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")

    object Add : Screen("add")
    object Calendar : Screen("calendar")
    object Timer : Screen("timer/{taskId}") {
        fun createRoute(taskId: String) = "timer/$taskId"
    }
    object Profile : Screen("profile")
}