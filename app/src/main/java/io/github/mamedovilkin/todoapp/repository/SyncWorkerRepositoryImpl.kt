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
import io.github.mamedovilkin.todoapp.worker.SyncAutoDeleteTasksWorker
import io.github.mamedovilkin.todoapp.worker.SyncDeleteWorker
import io.github.mamedovilkin.todoapp.worker.SyncTasksWorker
import io.github.mamedovilkin.todoapp.worker.SyncToggleTasksWorker
import io.github.mamedovilkin.todoapp.worker.SyncUncompletedTasksWorker
import java.time.Duration
import java.time.LocalDateTime
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

    override fun scheduleSyncUncompletedTasksWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val now = LocalDateTime.now()
        val targetTime = now.withHour(9).withMinute(0).withSecond(0)
        var delay = Duration.between(now, targetTime).toMinutes()

        if (delay < 0) {
            delay += TimeUnit.DAYS.toMinutes(1)
        }

        val syncUncompletedTasksWorker = PeriodicWorkRequestBuilder<SyncUncompletedTasksWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "syncUncompletedTasksWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            syncUncompletedTasksWorker
        )
    }

    override fun cancelScheduleSyncUncompletedTasksWork() {
        WorkManager.getInstance(context).cancelUniqueWork("syncUncompletedTasksWorker")
    }

    override fun scheduleSyncAutoDeleteTasksWork(autoDeleteIndex: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val now = LocalDateTime.now()
        val targetTime = now.withHour(9).withMinute(0).withSecond(0)
        var delay = Duration.between(now, targetTime).toMinutes()

        if (delay < 0) {
            delay += TimeUnit.DAYS.toMinutes(1)
        }

        val syncAutoDeleteTasksWorker: PeriodicWorkRequest.Builder? = when (autoDeleteIndex) {
            1 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWorker>(1, TimeUnit.DAYS)
            2 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWorker>(7, TimeUnit.DAYS)
            3 -> PeriodicWorkRequestBuilder<SyncAutoDeleteTasksWorker>(30, TimeUnit.DAYS)
            else -> { null }
        }

        syncAutoDeleteTasksWorker?.let {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "syncAutoDeleteTasksWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                it.setConstraints(constraints)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .build()
            )
        }
    }

    override fun cancelSyncAutoDeleteTasksWork() {
        WorkManager.getInstance(context).cancelUniqueWork("syncAutoDeleteTasksWorker")
    }

    override fun scheduleSyncToggleTasksWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val now = LocalDateTime.now()
        val targetTime = now.withHour(9).withMinute(0).withSecond(0)
        var delay = Duration.between(now, targetTime).toMinutes()

        if (delay < 0) {
            delay += TimeUnit.DAYS.toMinutes(1)
        }

        val syncToggleTasksWorker = PeriodicWorkRequestBuilder<SyncToggleTasksWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "syncToggleTasksWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            syncToggleTasksWorker
        )
    }

    override fun cancelScheduleSyncToggleTasksWork() {
        WorkManager.getInstance(context).cancelUniqueWork("syncToggleTasksWorker")
    }
}