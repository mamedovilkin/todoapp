package io.github.mamedovilkin.todoapp.repository

interface SyncWorkerRepository {
    fun scheduleUnSyncTasksWork()
    fun scheduleSyncDeleteTaskWork(taskId: String)
    fun scheduleSyncTasksWork()
}