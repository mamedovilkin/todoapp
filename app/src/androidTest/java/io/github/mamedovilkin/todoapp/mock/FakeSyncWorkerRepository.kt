package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository

class FakeSyncWorkerRepository : SyncWorkerRepository {
    override fun scheduleSyncDeleteTaskWork(taskId: String) {}
    override fun scheduleSyncTasksWork() {}
}