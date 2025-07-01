package io.github.mamedovilkin.todoapp.mock

import android.app.PendingIntent
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.room.Task
import java.util.Calendar

class FakeTaskReminderRepository : TaskReminderRepository {

    override fun scheduleReminder(task: Task, isPremium: Boolean): Task = task

    override fun getTaskWithUpdatedDatetime(task: Task): Task = task

    override fun getNextWeeklyReminder(task: Task, base: Calendar): Long = task.datetime

    override fun cancelReminder(task: Task, isPremium: Boolean) {}

    override fun getPendingIntents(task: Task, isPremium: Boolean): List<PendingIntent> = emptyList()
}