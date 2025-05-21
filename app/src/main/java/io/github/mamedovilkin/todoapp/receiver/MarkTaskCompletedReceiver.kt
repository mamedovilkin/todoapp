package io.github.mamedovilkin.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.util.MARK_TASK_COMPLETED_ACTION
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkTaskCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val taskRepository: TaskRepository by inject()

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == MARK_TASK_COMPLETED_ACTION) {
            val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TASK_KEY, Task::class.java)
            } else {
                intent.getParcelableExtra<Task>(TASK_KEY)
            }

            task?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    taskRepository.update(task.copy(isDone = true))

                    withContext(Dispatchers.Main) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.cancel(NOTIFICATION_ID)
                    }
                }
            }
        }
    }
}