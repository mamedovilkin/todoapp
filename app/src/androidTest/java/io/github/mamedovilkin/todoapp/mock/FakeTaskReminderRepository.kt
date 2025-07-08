package io.github.mamedovilkin.todoapp.mock

import android.app.PendingIntent
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.room.Task
import java.util.Calendar

class FakeTaskReminderRepository : TaskReminderRepository {

    override suspend fun scheduleReminder(task: Task): Task = task

    override suspend fun cancelReminder(task: Task) {}

    override fun getTaskWithUpdatedDatetime(task: Task): Task = task

    override fun getNextWeeklyReminder(task: Task, base: Calendar): Long = task.datetime

    override fun getPendingIntents(task: Task, reminderCount: Int, isPremium: Boolean): List<PendingIntent> = emptyList()
}