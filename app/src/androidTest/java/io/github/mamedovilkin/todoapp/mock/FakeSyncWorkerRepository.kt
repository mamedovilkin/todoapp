package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository

class FakeSyncWorkerRepository : SyncWorkerRepository {
    override fun scheduleSyncDeleteTaskWork(taskId: String) {}
    override fun scheduleSyncTasksWork() {}
    override fun rescheduleUncompletedTasksWork() {}
    override fun cancelRescheduleUncompletedTasksWork() {}
    override fun scheduleAutoDeleteTasksWork(autoDeleteIndex: Int) {}
    override fun cancelAutoDeleteTasksWork() {}
}