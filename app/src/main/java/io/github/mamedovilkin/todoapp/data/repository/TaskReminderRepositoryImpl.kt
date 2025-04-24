package io.github.mamedovilkin.todoapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.mamedovilkin.todoapp.data.room.Task
import io.github.mamedovilkin.todoapp.util.WORK_MANAGER_INPUT_DATA_KEY
import io.github.mamedovilkin.todoapp.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskReminderRepositoryImpl @Inject constructor(
    private val context: Context
) : TaskReminderRepository {

    @SuppressLint("RestrictedApi")
    override fun scheduleReminder(task: Task) {
        val duration = task.datetime - System.currentTimeMillis()

        if (duration <= 0) {
            return
        }

        val inputData = Data.Builder()
            .putString(WORK_MANAGER_INPUT_DATA_KEY, task.title)
            .build()

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .addTag("TASK_REMINDER_${task.id}")
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "TASK_REMINDER_${task.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancelReminder(task: Task) {
        WorkManager.getInstance(context).cancelUniqueWork("TASK_REMINDER_${task.id}")
    }
}