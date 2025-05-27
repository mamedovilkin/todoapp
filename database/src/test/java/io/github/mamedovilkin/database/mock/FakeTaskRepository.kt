package io.github.mamedovilkin.database.mock

import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTaskRepository : TaskRepository {

    private var tasksList = mutableListOf<Task>()
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    override val tasks: Flow<List<Task>> get() = _tasksFlow

    override suspend fun insert(task: Task) {
        tasksList.add(task)
        _tasksFlow.value = tasksList.toList()
    }

    override suspend fun delete(task: Task) {
        tasksList.remove(task)
        _tasksFlow.value = tasksList.toList()
    }

    override suspend fun update(task: Task) {
        val index = tasksList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasksList[index] = task
            _tasksFlow.value = tasksList.toList()
        }
    }

    override fun searchForTasks(query: String): Flow<List<Task>> {
        tasksList = tasksList.filter { it.title.contains(query) }.toMutableList()
        _tasksFlow.value = tasksList.toList()
        return _tasksFlow
    }
}