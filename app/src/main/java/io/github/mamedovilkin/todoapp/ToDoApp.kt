package io.github.mamedovilkin.todoapp

import android.app.Application
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepositoryImpl
import io.github.mamedovilkin.todoapp.data.repository.TaskRepository
import io.github.mamedovilkin.todoapp.data.room.TaskDatabase
import io.github.mamedovilkin.todoapp.data.repository.TaskRepositoryImpl

class ToDoApp : Application() {

    lateinit var taskRepository: TaskRepository
    lateinit var taskReminderRepository: TaskReminderRepository

    override fun onCreate() {
        super.onCreate()

        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)
        taskReminderRepository = TaskReminderRepositoryImpl(this)
    }
}