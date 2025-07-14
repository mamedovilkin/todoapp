package io.github.mamedovilkin.database.repository

import io.github.mamedovilkin.database.room.Task

interface FirestoreRepository {
    suspend fun setSubscriptionToken(uid: String, token: String)
    suspend fun getSubscriptionToken(uid: String): String
    suspend fun setLastSignIn(uid: String)
    suspend fun getLatestVersion(): String
    suspend fun deleteAllData(uid: String)
    suspend fun insert(uid: String, task: Task)
    suspend fun delete(uid: String, taskId: String)
    suspend fun get(uid: String): List<Task>
}