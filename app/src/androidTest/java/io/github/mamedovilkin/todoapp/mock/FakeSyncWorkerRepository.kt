package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository

class FakeSyncWorkerRepository : SyncWorkerRepository {
    override fun scheduleSyncDeleteTaskWork(taskId: String) {}
    override fun scheduleSyncTasksWork() {}
    override fun scheduleSyncUncompletedTasksWork() {}
    override fun cancelScheduleSyncUncompletedTasksWork() {}
    override fun scheduleSyncAutoDeleteTasksWork(autoDeleteIndex: Int) {}
    override fun cancelSyncAutoDeleteTasksWork() {}
    override fun scheduleSyncToggleTasksWork() {}
    override fun cancelScheduleSyncToggleTasksWork() {}
}