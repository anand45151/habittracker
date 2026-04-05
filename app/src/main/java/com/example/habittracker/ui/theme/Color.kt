package com.example.habittracker.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Premium Palette
val Primary = Color(0xFF6366F1) // Indigo
val Secondary = Color(0xFFEC4899) // Pink
val Tertiary = Color(0xFF14B8A6) // Teal
val Background = Color(0xFF0F172A) // Dark slate
val CardBg = Color(0xFF1E293B) // Lighter slate
val TextMain = Color(0xFFF8FAFC)
val TextGray = Color(0xFF94A3B8)

val DarkBlue = Color(0xFF1E293B)

val mainGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F172A),
        Color(0xFF1E293B),
        Color(0xFF334155)
    )
)

val accentGradient = Brush.horizontalGradient(
    colors = listOf(Primary, Secondary)
)
