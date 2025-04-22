package io.github.mamedovilkin.todoapp.ui.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.mamedovilkin.todoapp.ToDoApp
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.repository.TaskRepository
import io.github.mamedovilkin.todoapp.data.room.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@Immutable
data class HomeUiState(
    val query: String = "",
    val errorMessage: String? = null,
    val tasks: List<Task> = emptyList(),
    val notDoneTasksCount: Int = 0,
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val taskReminderRepository: TaskReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.tasks
                .catch { error ->
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = error.message)
                    }
                }
                .collect { tasks ->
                    _uiState.update { currentState ->
                        val notDoneTasksCount = tasks.filter { !it.isDone }.size

                        currentState.copy(
                            tasks = tasks,
                            notDoneTasksCount = notDoneTasksCount
                        )
                    }
                }
        }
    }

    fun newTask(task: Task) {
        viewModelScope.launch {
            var newTask = task

            if (newTask.id.isEmpty()) {
                val id = UUID.randomUUID().toString()
                newTask = task.copy(id = id)
            }

            taskReminderRepository.scheduleReminder(newTask)
            taskRepository.insert(newTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskReminderRepository.cancelReminder(task)
            taskRepository.delete(task)
        }
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = !task.isDone)
            if (updatedTask.isDone) {
                taskReminderRepository.cancelReminder(task)
            } else {
                taskReminderRepository.scheduleReminder(task)
            }

            taskRepository.update(updatedTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskReminderRepository.scheduleReminder(task)
            taskRepository.update(task)
        }
    }

    fun searchForTasks(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = query
            )
        }

        if (query.isEmpty()) {
            observeTasks()
        }

        viewModelScope.launch {
            taskRepository.searchForTasks("%${query.trim()}%")
                .catch { error ->
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = error.message)
                    }
                }
                .collect { tasks ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            tasks = tasks
                        )
                    }
                }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ToDoApp)
                HomeViewModel(application.taskRepository, application.taskReminderRepository)
            }
        }
    }
}