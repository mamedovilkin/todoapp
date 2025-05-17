package io.github.mamedovilkin.todoapp.data.repository

import android.app.PendingIntent
import io.github.mamedovilkin.todoapp.data.room.Task

interface TaskReminderRepository {
    fun scheduleReminder(task: Task)
    fun cancelReminder(task: Task)
    fun getPendingIntents(task: Task): List<PendingIntent>
}