package io.github.mamedovilkin.todoapp

import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.room.Task

class FakeTaskReminderRepository : TaskReminderRepository {
    override fun scheduleReminder(task: Task) {}
    override fun cancelReminder(task: Task) {}
}