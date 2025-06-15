package io.github.mamedovilkin.todoapp.repository

interface SyncWorkerRepository {
    fun scheduleSyncDeleteTaskWork(taskId: String)
    fun scheduleSyncTasksWork()
    fun scheduleSyncUncompletedTasksWork()
    fun cancelScheduleSyncUncompletedTasksWork()
    fun scheduleSyncAutoDeleteTasksWork(autoDeleteIndex: Int)
    fun cancelSyncAutoDeleteTasksWork()
    fun scheduleSyncToggleTasksWork()
    fun cancelScheduleSyncToggleTasksWork()
}