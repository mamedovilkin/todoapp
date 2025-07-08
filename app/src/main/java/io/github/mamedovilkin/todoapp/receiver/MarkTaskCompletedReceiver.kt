package io.github.mamedovilkin.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.util.MARK_TASK_COMPLETED_ACTION
import io.github.mamedovilkin.todoapp.util.TASK_ID_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkTaskCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val taskReminderRepository: TaskReminderRepository by inject()
    private val taskRepository: TaskRepository by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == MARK_TASK_COMPLETED_ACTION) {
            val id = intent.getStringExtra(TASK_ID_KEY)

            CoroutineScope(Dispatchers.IO).launch {
                if (id != null) {
                    val task = taskRepository.getTask(id)

                    task?.let {
                        var updatedTask = it.copy(
                            isDone = true,
                            isSynced = false,
                            updatedAt = System.currentTimeMillis()
                        )

                        taskReminderRepository.cancelReminder(updatedTask)

                        if (updatedTask.repeatType != RepeatType.ONE_TIME) {
                            updatedTask = taskReminderRepository.scheduleReminder(updatedTask)
                        }

                        taskRepository.update(updatedTask)
                        syncWorkerRepository.scheduleSyncTasksWork()

                        withContext(Dispatchers.Main) {
                            val notificationManager = NotificationManagerCompat.from(context)
                            notificationManager.cancel(task.id.hashCode())
                        }
                    }
                }
            }
        }
    }
}