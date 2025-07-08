package io.github.mamedovilkin.todoapp.repository

import android.app.PendingIntent
import io.github.mamedovilkin.database.room.Task
import java.util.Calendar

interface TaskReminderRepository {
    suspend fun scheduleReminder(task: Task): Task
    suspend fun cancelReminder(task: Task)
    fun getTaskWithUpdatedDatetime(task: Task): Task
    fun getNextWeeklyReminder(task: Task, base: Calendar): Long
    fun getPendingIntents(task: Task, reminderCount: Int, isPremium: Boolean): List<PendingIntent>
}