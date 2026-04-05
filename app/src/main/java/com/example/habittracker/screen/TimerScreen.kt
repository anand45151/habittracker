package com.example.habittracker.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.example.habittracker.data.model.HabitTask
import com.example.habittracker.firebase.FirebaseService
import com.example.habittracker.ui.theme.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TimerScreen(taskId: String? = null, onBack: () -> Unit = {}) {
    val firebaseService = FirebaseService()
    var task by remember { mutableStateOf<HabitTask?>(null) }
    var timeLeft by remember { mutableStateOf(0) }
    var totalTime by remember { mutableStateOf(1) } // Avoid divide by zero
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        taskId?.let {
            val fetchedTask = firebaseService.getTaskById(it)
            task = fetchedTask
            fetchedTask?.let { t ->
                // Parse "00:00" format
                val parts = t.targetTime.split(":")
                if (parts.size == 2) {
                    val seconds = parts[0].toInt() * 60 + parts[1].toInt()
                    timeLeft = seconds
                    totalTime = if (seconds > 0) seconds else 1
                }
            }
        }
    }

    // Lock back button if timer is running and not finished
    BackHandler(enabled = isRunning && timeLeft > 0) {
        // Block back press
    }

    val progress = (timeLeft.toFloat() / totalTime).coerceIn(0f, 1f)

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isRunning = false
            if (timeLeft == 0) {
                isCompleted = true
                taskId?.let {
                    firebaseService.updateTaskStatus(it, true)
                }
            }
        }
    }

    TimerContent(
        task = task,
        timeLeft = timeLeft,
        totalTime = totalTime,
        isRunning = isRunning,
        isCompleted = isCompleted,
        onToggleTimer = { isRunning = !isRunning },
        onBack = onBack
    )
}

@Composable
fun TimerContent(
    task: HabitTask?,
    timeLeft: Int,
    totalTime: Int,
    isRunning: Boolean,
    isCompleted: Boolean,
    onToggleTimer: () -> Unit,
    onBack: () -> Unit
) {
    val progress = (timeLeft.toFloat() / (if (totalTime > 0) totalTime else 1)).coerceIn(0f, 1f)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isRunning) Color(0xFF0A0E21) else Background) // Darker calm background during focus
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isRunning || timeLeft == 0) {
            // Show header only if not focusing or done
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMain)
                }
                Text(
                    text = if (isCompleted) "MISSION COMPLETE" else "Focus Mode",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isCompleted) Primary else TextMain
                )
                Box(modifier = Modifier.size(48.dp)) // Spacer
            }
        }

        // Timer Progress Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(320.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = if (isRunning) Color.White.copy(alpha = 0.05f) else CardBg,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = if (isCompleted) Brush.sweepGradient(listOf(Primary, Secondary)) else accentGradient,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeLeft),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    color = if (isRunning) Color.White else TextMain
                )
                Text(
                    text = when {
                        isCompleted -> "MINING SUCCESSFUL! +0.1 🪙"
                        isRunning -> "BREATHE & FOCUS"
                        else -> "READY TO START?"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isCompleted) Color(0xFFFFD700) else if (isRunning) Color.White.copy(alpha = 0.6f) else TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        if (!isCompleted) {
            LargeFloatingActionButton(
                onClick = onToggleTimer,
                shape = CircleShape,
                containerColor = if (isRunning) Color.White.copy(alpha = 0.1f) else Primary,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = "Toggle Timer",
                    modifier = Modifier.size(36.dp),
                    tint = if (isRunning) Color.White else Color.White
                )
            }
        } else {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
            ) {
                Text("BACK TO HOME", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Task Name
        task?.let {
            Text(
                text = it.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isRunning) Color.White else TextMain
            )
            Text(
                text = it.category,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isRunning) Color.White.copy(alpha = 0.5f) else TextGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    val mockTask = HabitTask(
        title = "Deep Work Session",
        category = "Work",
        targetTime = "00:25"
    )
    HabittrackerTheme {
        TimerContent(
            task = mockTask,
            timeLeft = 1500,
            totalTime = 1500,
            isRunning = false,
            isCompleted = false,
            onToggleTimer = {},
            onBack = {}
        )
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
