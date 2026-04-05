package com.example.habittracker.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.habittracker.routes.Screen
import com.example.habittracker.ui.theme.CardBg
import com.example.habittracker.ui.theme.Primary
import com.example.habittracker.ui.theme.Secondary
import com.example.habittracker.ui.theme.TextGray

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Screen.Calendar,
        Screen.Add,
        Screen.Profile
    )

    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        containerColor = CardBg,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    val iconColor by animateColorAsState(
                        targetValue = if (selected) Primary else TextGray,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                    
                    when (screen) {
                        Screen.Calendar -> Icon(Icons.Default.DateRange, contentDescription = "Home", tint = iconColor)
                        Screen.Add -> Icon(Icons.Default.Add, contentDescription = "Add Task", tint = iconColor)
                        Screen.Profile -> Icon(Icons.Default.Person, contentDescription = "Profile", tint = iconColor)
                        else -> {}
                    }
                },
                label = {
                    Text(
                        text = when (screen) {
                            Screen.Calendar -> "Home"
                            Screen.Add -> "Add"
                            Screen.Profile -> "Profile"
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Primary else TextGray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}