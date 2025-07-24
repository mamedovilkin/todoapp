package io.github.mamedovilkin.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val taskRepository: TaskRepository by inject()
    private val taskReminderRepository: TaskReminderRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val tasks = taskRepository.tasks.first().filter {
                    it.datetime != 0L && !(it.isDone && it.repeatType == RepeatType.ONE_TIME)
                }

                tasks.forEach { task ->
                    taskReminderRepository.scheduleReminder(task)
                }
            }
        }
    }
}