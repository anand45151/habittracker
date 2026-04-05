package com.example.habittracker.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.habittracker.data.model.HabitTask
import com.example.habittracker.data.model.User
import com.example.habittracker.firebase.FirebaseService
import com.example.habittracker.ui.theme.*
import com.google.firebase.Timestamp

@Composable
fun CalendarScreen(onStartTimer: (String) -> Unit = {}) {
    val firebaseService = FirebaseService()
    val scope = rememberCoroutineScope()
    var tasks by remember { mutableStateOf<List<HabitTask>>(emptyList()) }
    var userProfile by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTaskId by remember { mutableStateOf<String?>(null) }

    val refreshTasks = {
        scope.launch {
            tasks = firebaseService.getTasks()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        tasks = firebaseService.getTasks()
        userProfile = firebaseService.getUserProfile()
        isLoading = false
    }

    CalendarContent(
        tasks = tasks,
        user = userProfile,
        isLoading = isLoading,
        onToggleTask = { task ->
            if (task.id == "REFRESH") {
                scope.launch {
                    isLoading = true
                    tasks = firebaseService.getTasks()
                    userProfile = firebaseService.getUserProfile()
                    isLoading = false
                }
                return@CalendarContent
            }
            scope.launch {
                firebaseService.updateTaskStatus(task.id, !task.completed)
                tasks = firebaseService.getTasks()
                userProfile = firebaseService.getUserProfile() // Refresh balance
            }
        },
        onStartTimer = { taskId ->
            selectedTaskId = taskId
            showDialog = true
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("COMMIT TO FOCUS?", fontWeight = FontWeight.Bold) },
            text = { Text("Entering the Zone will lock your screen until the timer ends. Are you ready to deep focus?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        selectedTaskId?.let { onStartTimer(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("YES, START")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("NOT YET", color = TextGray)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun CalendarContent(
    tasks: List<HabitTask>,
    user: User?,
    isLoading: Boolean,
    onToggleTask: (HabitTask) -> Unit = {},
    onStartTimer: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mining Balance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Primary,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onToggleTask(HabitTask(id = "REFRESH")) } // Hacky way to trigger a refresh for now
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🪙 ${"%.2f".format(user?.tokenBalance ?: 0.0)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD700) // Gold Color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tokens",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Token Price",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
                Text(
                    text = "$0.05",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CalendarHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Streak Section
        StreakSection(tasks)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Today Tasks",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextMain
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No tasks for today!", color = TextGray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleTask = { onToggleTask(task) },
                        onStartTimer = onStartTimer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val mockTasks = listOf(
        HabitTask(id = "1", title = "Morning Yoga", category = "Health", completed = true, createdAt = Timestamp.now()),
        HabitTask(id = "2", title = "Project Meeting", category = "Work", completed = false, createdAt = Timestamp.now()),
        HabitTask(id = "3", title = "Read 10 Pages", category = "Learning", completed = false, createdAt = Timestamp.now())
    )
    HabittrackerTheme {
        CalendarContent(
            tasks = mockTasks,
            user = User(tokenBalance = 12.45),
            isLoading = false
        )
    }
}

@Composable
fun StreakSection(tasks: List<HabitTask>) {
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.completed }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    
    // Streak logic: Count consecutive days where all tasks were completed (dummy for now, real calculation based on timestamps possible)
    // For now, let's show today's progress and a fire indicator
    
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Your Streak",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔥",
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = if (completedTasks == totalTasks && totalTasks > 0) "Streak Maintained!" else "Finish your tasks!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                val currentStreak = calculateStreak(tasks)
                Text(
                    text = "Current Streak: $currentStreak Days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = Primary.copy(alpha = 0.1f),
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = Primary,
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completedTasks/$totalTasks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextMain
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: HabitTask, onToggleTask: () -> Unit, onStartTimer: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (task.completed) Primary.copy(alpha = 0.05f) else CardBg
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleTask() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (task.completed) Primary.copy(alpha = 0.2f) else Primary.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (task.completed) "🔥" else getIconForCategory(task.category),
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    ),
                    color = if (task.completed) TextGray else TextMain
                )
                Text(
                    text = "${task.category} • ${task.targetTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            if (task.completed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                IconButton(
                    onClick = { onStartTimer(task.id) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentGradient)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun CalendarHeader() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "October 2025",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dates = listOf("12", "13", "14", "15", "16", "17", "18")
                dates.forEachIndexed { index, date ->
                    val isSelected = index == 3
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Primary else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (index == 3) "WED" else "S M T W T F S"[index*2].toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else TextGray
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else TextMain
                        )
                    }
                }
            }
        }
    }
}

fun getIconForCategory(category: String): String {
    return when (category) {
        "Health" -> "🧘"
        "Work" -> "💼"
        "Learning" -> "📖"
        "Art" -> "🎨"
        else -> "📝"
    }
}

fun calculateStreak(allTasks: List<HabitTask>): Int {
    if (allTasks.isEmpty()) return 0
    
    val groupedByDay = allTasks.groupBy { 
        val date = it.createdAt.toDate()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        sdf.format(date)
    }

    val sortedDates = groupedByDay.keys.sortedDescending()
    var streak = 0
    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    
    // Check from today or yesterday
    var currentDateStr = today
    
    // Special case: if today has no tasks yet, streak might be from yesterday
    if (!groupedByDay.containsKey(today)) {
        val yesterday = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DATE, -1) }.time
        currentDateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(yesterday)
    }

    while (groupedByDay.containsKey(currentDateStr)) {
        val dayTasks = groupedByDay[currentDateStr]!!
        if (dayTasks.all { it.completed }) {
            streak++
            // Move to previous day
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(currentDateStr)
            val cal = java.util.Calendar.getInstance()
            cal.time = date!!
            cal.add(java.util.Calendar.DATE, -1)
            currentDateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
        } else {
            break
        }
    }
    
    return streak
}
