package io.github.mamedovilkin.todoapp.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mamedovilkin.todoapp.reminder.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class Result {
    data class Failure(val error: Throwable): Result()
    data class Success(val tasks: List<Task>): Result()
    data object Loading : Result()
    data object NoTasks : Result()
}

@Immutable
data class HomeUiState(
    val query: String = "",
    val notDoneTasksCount: Int = 0,
    val result: Result = Result.Loading
)

@HiltViewModel
class HomeViewModel @Inject constructor(
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
            delay(1000)

            taskRepository.tasks
                .catch { error ->
                    _uiState.update { currentState ->
                        currentState.copy(result = Result.Failure(error))
                    }
                }
                .collect { tasks ->
                    if (tasks.isEmpty()) {
                        _uiState.update { currentState ->
                            currentState.copy(result = Result.NoTasks)
                        }
                    } else {
                        _uiState.update { currentState ->
                            val notDoneTasksCount = tasks.filter { !it.isDone }.size

                            currentState.copy(
                                result = Result.Success(tasks = tasks),
                                notDoneTasksCount = notDoneTasksCount
                            )
                        }
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
                        currentState.copy(
                            result = Result.Failure(error = error)
                        )
                    }
                }
                .collect { tasks ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            result = Result.Success(tasks = tasks)
                        )
                    }
                }
        }
    }
}