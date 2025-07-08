package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.room.Task

class FakeFirestoreRepository : FirestoreRepository {
    override suspend fun setLastSignIn(uid: String) {}

    override suspend fun getLatestVersion(): String = ""

    override suspend fun deleteAllData(uid: String) {}

    override suspend fun insert(uid: String, task: Task) {}

    override suspend fun delete(uid: String, taskId: String) {}

    override suspend fun get(uid: String): List<Task> = emptyList()
}