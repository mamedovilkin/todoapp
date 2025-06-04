package io.github.mamedovilkin.database.repository

import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun insert(task: Task) = taskDao.insert(task)

    override suspend fun insertAll(tasks: List<Task>) = taskDao.insertAll(tasks)

    override suspend fun delete(task: Task) = taskDao.delete(task)

    override suspend fun deleteAll() = taskDao.deleteAll()

    override suspend fun update(task: Task) = taskDao.update(task)

    override val tasks: Flow<List<Task>> = taskDao.getTasks()

    override val unSyncedTasks: Flow<List<Task>> = taskDao.getUnSyncedTasks()
}