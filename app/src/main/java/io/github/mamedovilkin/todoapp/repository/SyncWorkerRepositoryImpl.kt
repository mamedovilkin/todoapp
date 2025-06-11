package io.github.mamedovilkin.todoapp.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.mamedovilkin.todoapp.worker.SyncAutoDeleteTasksWork
import io.github.mamedovilkin.todoapp.worker.SyncDeleteWorker
import io.github.mamedovilkin.todoapp.worker.SyncTasksWorker
import io.github.mamedovilkin.todoapp.worker.SyncUncompletedTasksWork
import java.util.concurrent.TimeUnit

class SyncWorkerRepositoryImpl(
    private val context: Context
) : SyncWorkerRepository {

    override fun scheduleSyncDeleteTaskWork(taskId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf("taskId" to taskId)

        val workRequest = OneTimeWorkRequestBuilder<SyncDeleteWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun scheduleSyncTasksWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun rescheduleUncompletedTasksWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncUncompletedTasksWork = PeriodicWorkRequestBuilder<SyncUncompletedTasksWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "syncUncompletedTasksWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncUncompletedTasksWork
        )
    }

    override fun cancelRescheduleUncompletedTasksWork() {
        WorkManager.getInstance(context).cancelUniqueWork("syncUncompletedTasksWork")
    }

    override fun scheduleAutoDeleteTasksWork(autoDeleteIndex: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncAutoDeleteTasksWork: PeriodicWorkRequest.Builder? = when (autoDeleteIndex) {
            1 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWork>(1, TimeUnit.DAYS)
            2 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWork>(7, TimeUnit.DAYS)
            3 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWork>(30, TimeUnit.DAYS)
            else -> { null }
        }

        syncAutoDeleteTasksWork?.let {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "syncAutoDeleteTasksWork",
                ExistingPeriodicWorkPolicy.KEEP,
                it.setConstraints(constraints).build()
            )
        }
    }

    override fun cancelAutoDeleteTasksWork() {
        WorkManager.getInstance(context).cancelUniqueWork("syncAutoDeleteTasksWork")
    }
}