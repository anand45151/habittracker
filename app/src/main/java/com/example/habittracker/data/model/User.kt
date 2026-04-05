package com.example.habittracker.data.model

data class User(
    val id: String = "",
    val name: String = "Productive User",
    val email: String = "",
    val tokenBalance: Double = 0.0,
    val totalMined: Double = 0.0
)
