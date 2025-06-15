package io.github.mamedovilkin.todoapp.ui.screen.home

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.util.vibrate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.text.isNotEmpty

sealed class Result {
    data class Failure(val error: Throwable): Result()
    data class Success(
        val tasks: List<Task>,
        val categories: Set<String>
    ): Result()
    data object Loading: Result()
    data object NoTasks: Result()
}

@Immutable
data class HomeUiState(
    val query: String = "",
    val selectedCategory: String = "",
    val notDoneTasksCount: Int = 0,
    val result: Result = Result.Loading,
    val exception: Exception? = null,
    val task: Task? = null,
    val showNewTaskBottomSheet: Boolean = false,
    val showEditTaskBottomSheet: Boolean = false
)

class HomeViewModel(
    private val application: Application,
    private val taskRepository: TaskRepository,
    private val taskReminderRepository: TaskReminderRepository,
    private val syncWorkerRepository: SyncWorkerRepository,
    dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val userID = dataStoreRepository.userID
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    val photoURL = dataStoreRepository.photoURL
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    val displayName = dataStoreRepository.displayName
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    val showStatistics = dataStoreRepository.showStatistics
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val isPremium = dataStoreRepository.isPremium
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    fun observeTasks() = viewModelScope.launch {
        taskRepository.tasks
            .catch { error ->
                setFailureResult(error)
            }
            .collect { tasks ->
                setSuccessResult(tasks)
            }
    }

    private fun setFailureResult(error: Throwable) {
        _uiState.update { currentState ->
            currentState.copy(result = Result.Failure(error))
        }
    }

    private fun setSuccessResult(tasks: List<Task>) {
        _uiState.update { currentState ->
            if (tasks.isEmpty()) {
                currentState.copy(result = Result.NoTasks)
            } else {
                val notDoneTasksCount = tasks.filter { !it.isDone }.size
                val categories = tasks
                    .filter { it.category.isNotEmpty() }
                    .map { it.category }
                    .toSet()

                currentState.copy(
                    result = Result.Success(
                        tasks = tasks,
                        categories = categories
                    ),
                    notDoneTasksCount = notDoneTasksCount
                )
            }
        }
    }

    fun newTask(task: Task) = viewModelScope.launch {
        var newTask = task.copy(id = UUID.randomUUID().toString())

        newTask = taskReminderRepository.scheduleReminder(newTask, isPremium.value)

        taskRepository.insert(newTask)
        syncWorkerRepository.scheduleSyncTasksWork()
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }.invokeOnCompletion {
            taskReminderRepository.cancelReminder(task, isPremium.value)
            syncWorkerRepository.scheduleSyncDeleteTaskWork(task.id)
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        var updatedTask = task.copy(
            isSynced = false,
            updatedAt = System.currentTimeMillis()
        )

        taskReminderRepository.cancelReminder(updatedTask, isPremium.value)

        if (updatedTask.repeatType != RepeatType.ONE_TIME) {
            updatedTask = taskReminderRepository.scheduleReminder(updatedTask, isPremium.value)
        }

        taskRepository.update(updatedTask)
        syncWorkerRepository.scheduleSyncTasksWork()
    }


    fun toggleTask(task: Task) = viewModelScope.launch {
        var updatedTask = task.copy(
            isSynced = false,
            updatedAt = System.currentTimeMillis()
        )

        taskReminderRepository.cancelReminder(updatedTask, isPremium.value)

        if (updatedTask.repeatType != RepeatType.ONE_TIME) {
            updatedTask = taskReminderRepository.scheduleReminder(updatedTask, isPremium.value)
        }

        taskRepository.update(updatedTask)
        syncWorkerRepository.scheduleSyncTasksWork()

        vibrate(application)
    }

    fun searchForTasks(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = query
            )
        }
    }

    fun setTaskToEdit(task: Task) {
        _uiState.update { currentState ->
            currentState.copy(
                task = task
            )
        }
    }

    fun setShowNewTaskBottomSheet(showNewTaskBottomSheet: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showNewTaskBottomSheet = showNewTaskBottomSheet
            )
        }
    }

    fun setShowEditTaskBottomSheet(showEditTaskBottomSheet: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showEditTaskBottomSheet = showEditTaskBottomSheet
            )
        }
    }

    fun setSelectedCategory(selectedCategory: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = if (currentState.selectedCategory == selectedCategory) "" else selectedCategory
            )
        }
    }
}