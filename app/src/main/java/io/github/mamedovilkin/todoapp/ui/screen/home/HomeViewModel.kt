package io.github.mamedovilkin.todoapp.ui.screen.home

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.PriorityType
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
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
    val selectedPriority: PriorityType = PriorityType.NONE,
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
    private val firestoreRepository: FirestoreRepository,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isLatestVersion: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLatestVersion: StateFlow<Boolean> = _isLatestVersion.asStateFlow()

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

    val hidePremiumAd = dataStoreRepository.hidePremiumAd
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    init {
        syncWorkerRepository.scheduleSyncTasksWork()

        viewModelScope.launch {
            if (isInternetAvailable()) {
                val currentVersion = application.packageManager.getPackageInfo(application.packageName, 0).versionName.toString()
                _isLatestVersion.value = currentVersion == firestoreRepository.getLatestVersion()
            }
        }
    }

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
                val notDoneTasksCount = tasks.count { !it.isDone }
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

        if (newTask.datetime != 0L) {
            newTask = taskReminderRepository.scheduleReminder(newTask)
        }

        taskRepository.insert(newTask)
        syncWorkerRepository.scheduleSyncTasksWork()
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
        taskReminderRepository.cancelReminder(task)
        syncWorkerRepository.scheduleSyncDeleteTaskWork(task.id)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        val updatedTask = task.copy(
            isSynced = false,
            updatedAt = System.currentTimeMillis()
        )

        if (updatedTask.datetime != 0L) {
            taskReminderRepository.cancelReminder(updatedTask)
            taskReminderRepository.scheduleReminder(updatedTask)
        } else {
            taskReminderRepository.cancelReminder(updatedTask)
        }
        taskRepository.update(updatedTask)
        syncWorkerRepository.scheduleSyncTasksWork()
    }

    fun toggleTask(task: Task) = viewModelScope.launch {
        var updatedTask = task.copy(
            isSynced = false,
            updatedAt = System.currentTimeMillis()
        )

        taskReminderRepository.cancelReminder(updatedTask)

        if (updatedTask.repeatType != RepeatType.ONE_TIME) {
            updatedTask = taskReminderRepository.scheduleReminder(updatedTask)
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
                query = "",
                selectedPriority = PriorityType.NONE,
                selectedCategory = if (currentState.selectedCategory == selectedCategory) "" else selectedCategory
            )
        }
    }

    fun setSelectedPriority(selectedPriority: PriorityType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = "",
                selectedPriority = selectedPriority
            )
        }
    }

    fun setHidePremiumAd(hidePremiumAd: Boolean) = viewModelScope.launch {
        dataStoreRepository.setHidePremiumAd(hidePremiumAd)
    }
}