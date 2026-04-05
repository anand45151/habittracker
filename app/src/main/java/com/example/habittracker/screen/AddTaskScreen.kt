package com.example.habittracker.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.model.HabitTask
import com.example.habittracker.firebase.FirebaseService
import com.example.habittracker.ui.theme.*
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen() {
    val scrollState = rememberScrollState()
    var taskName by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    
    // Categories State
    var categories by remember { mutableStateOf(listOf("Health", "Work", "Focus", "Fitness", "Personal")) }
    var taskCategory by remember { mutableStateOf(categories[0]) }
    var showNewCategoryInput by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    var taskTime by remember { mutableStateOf("00:00") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val firebaseService = FirebaseService()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Create New Task",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextMain
        )
        Text(
            text = "Enter details below",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Task Name Input
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextGray,
                focusedBorderColor = Primary,
                focusedTextColor = TextMain,
                unfocusedTextColor = TextGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            color = TextMain,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = taskCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { taskCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardBg,
                        selectedContainerColor = Primary.copy(alpha = 0.2f),
                        labelColor = TextGray,
                        selectedLabelColor = Primary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Add New Category Button
            FilterChip(
                selected = showNewCategoryInput,
                onClick = { showNewCategoryInput = !showNewCategoryInput },
                label = { Text(if (showNewCategoryInput) "Cancel" else "+ Add New") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = CardBg,
                    labelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (showNewCategoryInput) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("New Category Name", color = TextGray.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TextGray.copy(alpha = 0.3f),
                        focusedBorderColor = Primary,
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCategoryName.isNotBlank() && !categories.contains(newCategoryName)) {
                            categories = categories + newCategoryName
                            taskCategory = newCategoryName
                            newCategoryName = ""
                            showNewCategoryInput = false
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Add", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description Input
        OutlinedTextField(
            value = taskDesc,
            onValueChange = { taskDesc = it },
            label = { Text("Short Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextGray,
                focusedBorderColor = Primary,
                focusedTextColor = TextMain,
                unfocusedTextColor = TextGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Time Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Target Time", color = TextGray)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = taskTime, color = TextMain, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Create Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isLoading) SolidColor(CardBg) else accentGradient)
                .clickable(enabled = !isLoading) {
                    if (taskName.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            val task = HabitTask(
                                title = taskName,
                                description = taskDesc,
                                category = taskCategory,
                                targetTime = taskTime
                            )
                            val success = firebaseService.addTask(task)
                            isLoading = false
                            if (success) {
                                taskName = ""
                                taskDesc = ""
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Create Task",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
