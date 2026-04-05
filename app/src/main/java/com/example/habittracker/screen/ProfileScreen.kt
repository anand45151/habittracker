package com.example.habittracker.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.*

@Composable
fun ProfileScreen() {
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
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextMain
            )
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .background(CardBg, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextMain)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(accentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("JD", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "John Doe",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = "Productive Warrior",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("43", "Day Streak")
                    StatItem("180", "Total Tasks")
                    StatItem("12h", "Focus Time")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Achievements List
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextMain
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { AchievementItem("7 Day Streak", "Completed tasks for a full week", Icons.Default.Star, Primary) }
            item { AchievementItem("Focus Master", "100 hours of total focus time", Icons.Default.Notifications, Secondary) }
            item { AchievementItem("Early Bird", "Completed 10 tasks before 9 AM", Icons.Default.Star, Tertiary) }
        }
    }
}

@Composable
fun StatItem(valStr: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valStr, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextGray)
    }
}

@Composable
fun AchievementItem(title: String, desc: String, icon: ImageVector, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = TextMain)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextGray)
            }
        }
    }
}


@Preview(showSystemUi = true , showBackground = true)
@Composable
fun ProfilePreView(){
    ProfileScreen()
}
