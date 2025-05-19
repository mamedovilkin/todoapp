package io.github.mamedovilkin.todoapp.repository

import android.app.PendingIntent
import io.github.mamedovilkin.database.room.Task

interface TaskReminderRepository {
    fun scheduleReminder(task: Task)
    fun cancelReminder(task: Task)
    fun getPendingIntents(task: Task): List<PendingIntent>
}