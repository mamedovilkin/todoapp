package io.github.mamedovilkin.todoapp.repository

interface SyncWorkerRepository {
    fun scheduleSyncDeleteTaskWork(taskId: String)
    fun scheduleSyncTasksWork()
    fun rescheduleUncompletedTasksWork()
    fun cancelRescheduleUncompletedTasksWork()
    fun scheduleAutoDeleteTasksWork(autoDeleteIndex: Int)
    fun cancelAutoDeleteTasksWork()
}