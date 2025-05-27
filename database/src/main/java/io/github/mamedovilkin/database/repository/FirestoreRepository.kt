package io.github.mamedovilkin.database.repository

import io.github.mamedovilkin.database.room.Task

interface FirestoreRepository {
    fun insert(task: Task, callback: (Exception?) -> Unit)
    fun delete(task: Task, callback: (Exception?) -> Unit)
}