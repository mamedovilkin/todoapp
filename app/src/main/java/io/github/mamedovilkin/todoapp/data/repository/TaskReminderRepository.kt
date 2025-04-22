package io.github.mamedovilkin.todoapp.data.repository

import io.github.mamedovilkin.todoapp.data.room.Task

interface TaskReminderRepository {
    fun scheduleReminder(task: Task)
    fun cancelReminder(task: Task)
}