package io.github.mamedovilkin.todoapp.repository

interface SyncWorkerRepository {
    fun scheduleSyncDeleteTaskWork(taskId: String)
    fun scheduleSyncTasksWork()
}