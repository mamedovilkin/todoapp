package io.github.mamedovilkin.todoapp.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.mamedovilkin.todoapp.worker.SyncDeleteWorker
import io.github.mamedovilkin.todoapp.worker.SyncTasksWorker

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
}