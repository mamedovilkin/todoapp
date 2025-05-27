package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.room.Task

class FakeFirestoreRepository : FirestoreRepository {
    override fun insert(
        task: Task,
        callback: (Exception?) -> Unit
    ) {}

    override fun delete(
        task: Task,
        callback: (Exception?) -> Unit
    ) {}
}