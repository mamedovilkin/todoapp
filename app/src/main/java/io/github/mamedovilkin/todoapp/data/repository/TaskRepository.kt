package io.github.mamedovilkin.todoapp.data.repository

import io.github.mamedovilkin.todoapp.data.room.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insert(task: Task)
    suspend fun delete(task: Task)
    suspend fun update(task: Task)
    val tasks: Flow<List<Task>>
    fun searchForTasks(query: String): Flow<List<Task>>
}