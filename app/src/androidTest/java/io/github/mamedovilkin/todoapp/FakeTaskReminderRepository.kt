package io.github.mamedovilkin.todoapp

import android.app.PendingIntent
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.room.Task

class FakeTaskReminderRepository : TaskReminderRepository {
    override fun scheduleReminder(task: Task) {}
    override fun cancelReminder(task: Task) {}
    override fun getPendingIntents(task: Task): List<PendingIntent> { return emptyList() }
}