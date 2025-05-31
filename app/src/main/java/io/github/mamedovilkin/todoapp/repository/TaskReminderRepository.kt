package io.github.mamedovilkin.todoapp.repository

import android.app.PendingIntent
import io.github.mamedovilkin.database.room.Task
import java.util.Calendar

interface TaskReminderRepository {
    fun scheduleReminder(task: Task): Task
    fun getTaskWithUpdatedDatetime(task: Task): Task
    fun getNextWeeklyReminder(task: Task, base: Calendar): Long
    fun cancelReminder(task: Task)
    fun getPendingIntents(task: Task): List<PendingIntent>
}