package io.github.mamedovilkin.database.mock

import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.room.Task

class FakeFailureFirestoreRepository : FirestoreRepository {
    override fun insert(
        task: Task,
        callback: (Exception?) -> Unit
    ) {
        callback(Exception("An error occurred."))
    }

    override fun delete(
        task: Task,
        callback: (Exception?) -> Unit
    ) {
        callback(Exception("An error occurred."))
    }
}