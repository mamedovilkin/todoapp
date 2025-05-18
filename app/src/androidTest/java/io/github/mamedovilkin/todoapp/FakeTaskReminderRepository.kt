package io.github.mamedovilkin.todoapp

import android.app.PendingIntent
import io.github.mamedovilkin.todoapp.reminder.TaskReminderRepository
import io.github.mamedovilkin.database.room.Task

class FakeTaskReminderRepository : TaskReminderRepository {
    override fun scheduleReminder(task: Task) {}
    override fun cancelReminder(task: Task) {}
    override fun getPendingIntents(task: Task): List<PendingIntent> { return emptyList() }
}