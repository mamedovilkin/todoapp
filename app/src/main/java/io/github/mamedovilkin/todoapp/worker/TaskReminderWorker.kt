package io.github.mamedovilkin.todoapp.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.mamedovilkin.todoapp.util.WORK_MANAGER_INPUT_DATA_KEY
import io.github.mamedovilkin.todoapp.util.makeReminderNotification

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val title = inputData.getString(WORK_MANAGER_INPUT_DATA_KEY)

        makeReminderNotification(title.toString(), applicationContext)

        return Result.success()
    }
}