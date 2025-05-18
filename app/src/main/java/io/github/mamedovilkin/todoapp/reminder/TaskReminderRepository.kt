package io.github.mamedovilkin.todoapp.reminder

import android.app.PendingIntent
import io.github.mamedovilkin.database.room.Task

interface TaskReminderRepository {
    fun scheduleReminder(task: Task)
    fun cancelReminder(task: Task)
    fun getPendingIntents(task: Task): List<PendingIntent>
}