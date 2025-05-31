package io.github.mamedovilkin.todoapp.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.ui.activity.ToDoAppActivity
import io.github.mamedovilkin.todoapp.util.CHANNEL_ID
import io.github.mamedovilkin.todoapp.util.MARK_TASK_COMPLETED_ACTION
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.REQUEST_CODE
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val taskReminderRepository: TaskReminderRepository by inject()
    private val taskRepository: TaskRepository by inject()
    private val auth: FirebaseAuth by inject()
    private val firestoreRepository: FirestoreRepository by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TASK_KEY, Task::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Task>(TASK_KEY)
        } ?: return

        val offset = intent.getIntExtra("OFFSET", 0)

        createNotification(task, context)

        if (offset == -1 && task.repeatType != RepeatType.ONE_TIME) {
            CoroutineScope(Dispatchers.IO).launch {
                handleRepeatableTask(task)
            }
        }
    }

    private suspend fun handleRepeatableTask(task: Task) {
        val updatedTask = taskReminderRepository.scheduleReminder(task)

        taskRepository.update(updatedTask)

        if (auth.currentUser != null && isInternetAvailable()) {
            firestoreRepository.insert(updatedTask.copy(isSynced = true)) { e ->
                if (e == null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        taskRepository.update(updatedTask.copy(isSynced = true))
                    }
                }
            }
        } else if (auth.currentUser != null) {
            syncWorkerRepository.scheduleUnSyncTasksWork()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun createNotification(task: Task, context: Context) {
        createNotificationChannelIfNeeded(context)

        val pendingIntent: PendingIntent = createPendingIntent(context)
        val markTaskCompletedPendingIntent: PendingIntent = createMarkCompletedPendingIntent(context, task)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task)
            .setContentTitle(task.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .addAction(0, context.getString(R.string.mark_as_completed), markTaskCompletedPendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.resources.getString(R.string.notification_channel),
                importance
            )

            channel.description = context.resources.getString(R.string.notification_channel)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ToDoAppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createMarkCompletedPendingIntent(context: Context, task: Task): PendingIntent {
        val intent = Intent(context, MarkTaskCompletedReceiver::class.java).apply {
            action = MARK_TASK_COMPLETED_ACTION
            putExtra(TASK_KEY, task)
        }

        return PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}