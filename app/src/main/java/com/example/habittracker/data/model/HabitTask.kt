package com.example.habittracker.data.model

import com.google.firebase.Timestamp

data class HabitTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "Health",
    val targetTime: String = "00:00",
    val createdAt: Timestamp = Timestamp.now(),
    val completed: Boolean = false,
    val userId: String = "",
    val tokensAwarded: Boolean = false
)
