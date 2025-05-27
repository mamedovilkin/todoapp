package io.github.mamedovilkin.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.util.MARK_TASK_COMPLETED_ACTION
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkTaskCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val auth: FirebaseAuth by inject()
    private val taskReminderRepository: TaskReminderRepository by inject()
    private val taskRepository: TaskRepository by inject()
    private val firestoreRepository: FirestoreRepository by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

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
                    val currentUser = auth.currentUser

                    val updatedTask = it.copy(
                        isDone = true,
                        isSynced = false
                    )

                    taskReminderRepository.cancelReminder(updatedTask)
                    taskRepository.update(updatedTask)

                    if (currentUser != null && isInternetAvailable()) {
                        firestoreRepository.insert(updatedTask.copy(isSynced = true)) {}
                        taskRepository.update(updatedTask.copy(isSynced = true))
                    } else {
                        syncWorkerRepository.scheduleUnSyncTasksWork()
                    }

                    withContext(Dispatchers.Main) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.cancel(NOTIFICATION_ID)
                    }
                }
            }
        }
    }
}