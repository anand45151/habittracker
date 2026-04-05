package com.example.habittracker.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import com.example.habittracker.routes.Screen
import com.example.habittracker.ui.theme.Background

@Composable
fun HomeScreen(onStartTimer: (String) -> Unit = {}

) {

    val navController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            composable(Screen.Calendar.route) {
                CalendarScreen(onStartTimer = onStartTimer)
            }

            composable(Screen.Add.route) {
                AddHabitScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}


