package com.example.habittracker.firebase

import android.util.Log
import com.example.habittracker.data.model.HabitTask
import com.example.habittracker.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addTask(task: HabitTask): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val newTask = task.copy(userId = user.uid)
                firestore.collection("tasks").add(newTask).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error adding task", e)
            false
        }
    }

    suspend fun getTasks(): List<HabitTask> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val snapshot = firestore.collection("tasks")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()
                snapshot.toObjects(HabitTask::class.java)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting tasks", e)
            emptyList()
        }
    }

    suspend fun updateTaskStatus(taskId: String, completed: Boolean): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null && completed) {
                // Check if tokens were already awarded
                val task = getTaskById(taskId)
                if (task != null && !task.tokensAwarded) {
                    val batch = firestore.batch()
                    val taskRef = firestore.collection("tasks").document(taskId)
                    val userRef = firestore.collection("users").document(user.uid)
                    
                    batch.update(taskRef, "completed", true)
                    batch.update(taskRef, "tokensAwarded", true)
                    batch.update(userRef, "tokenBalance", FieldValue.increment(0.1))
                    batch.update(userRef, "totalMined", FieldValue.increment(0.1))
                    
                    batch.commit().await()
                    return true
                }
            }
            
            // Standard update if not mining or already awarded
            firestore.collection("tasks").document(taskId)
                .update("completed", completed)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating task/mining", e)
            false
        }
    }

    suspend fun getUserProfile(): User? {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val doc = firestore.collection("users").document(user.uid).get().await()
                if (doc.exists()) {
                    doc.toObject(User::class.java)
                } else {
                    // Create first time entry
                    val newUser = User(id = user.uid, email = user.email ?: "", name = user.displayName ?: "Productive User")
                    firestore.collection("users").document(user.uid).set(newUser).await()
                    newUser
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting user profile", e)
            null
        }
    }

    suspend fun getTaskById(taskId: String): HabitTask? {
        return try {
            val snapshot = firestore.collection("tasks").document(taskId).get().await()
            snapshot.toObject(HabitTask::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting task by Id", e)
            null
        }
    }
}
