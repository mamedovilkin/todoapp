package io.github.mamedovilkin.database.mock

import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.room.Task

class FakeSuccessFirestoreRepository : FirestoreRepository {

    var remoteTask: Task? = null

    override fun insert(
        task: Task,
        callback: (Exception?) -> Unit
    ) {
        remoteTask = task
        callback(null)
    }

    override fun delete(
        task: Task,
        callback: (Exception?) -> Unit
    ) {
        remoteTask = null
        callback(null)
    }
}