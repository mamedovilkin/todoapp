package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTaskRepository : TaskRepository {

    private var tasksList = mutableListOf<Task>()
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    override val tasks: Flow<List<Task>> get() = _tasksFlow
    private val _unSyncedTasksFlow = MutableStateFlow<List<Task>>(emptyList())
    override val unSyncedTasks: Flow<List<Task>> get() = _unSyncedTasksFlow

    override suspend fun insert(task: Task) {
        tasksList.add(task)
        _tasksFlow.value = tasksList.toList()
        _unSyncedTasksFlow.value = tasksList.filter { !it.isSynced }.toList()
    }

    override suspend fun insertAll(tasks: List<Task>) {
        tasksList.addAll(tasks)
        _tasksFlow.value = tasksList.toList()
        _unSyncedTasksFlow.value = tasksList.filter { !it.isSynced }.toList()
    }

    override suspend fun delete(task: Task) {
        tasksList.remove(task)
        _tasksFlow.value = tasksList.toList()
        _unSyncedTasksFlow.value = tasksList.filter { !it.isSynced }.toList()
    }

    override suspend fun deleteAll() {
        tasksList = mutableListOf()
        _tasksFlow.value = tasksList.toList()
        _unSyncedTasksFlow.value = tasksList.filter { !it.isSynced }.toList()
    }

    override suspend fun update(task: Task) {
        val index = tasksList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasksList[index] = task
            _tasksFlow.value = tasksList.toList()
            _unSyncedTasksFlow.value = tasksList.filter { !it.isSynced }.toList()
        }
    }

    override fun getTask(id: String): Task? {
        return tasksList.find { it.id == id }
    }
}