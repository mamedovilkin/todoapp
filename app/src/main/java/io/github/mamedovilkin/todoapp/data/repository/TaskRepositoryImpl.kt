package io.github.mamedovilkin.todoapp.data.repository

import io.github.mamedovilkin.todoapp.data.room.Task
import io.github.mamedovilkin.todoapp.data.room.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun insert(task: Task) = taskDao.insert(task)

    override suspend fun delete(task: Task) = taskDao.delete(task)

    override suspend fun update(task: Task) = taskDao.update(task)

    override val tasks: Flow<List<Task>> = taskDao.getTasks()

    override fun searchForTasks(query: String): Flow<List<Task>> {
        return taskDao.searchForTasks(query)
    }
}