package io.github.mamedovilkin.todoapp.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Result {
    data class Failure(val error: Throwable): Result()
    data class Success(
        val tasks: List<Task>,
        val categories: Set<String>
    ): Result()
    data object Loading : Result()
    data object NoTasks : Result()
}

@Immutable
data class HomeUiState(
    val currentUser: FirebaseUser? = null,
    val query: String = "",
    val selectedCategory: String = "",
    val notDoneTasksCount: Int = 0,
    val showStatistics: Boolean = false,
    val result: Result = Result.Loading,
    val exception: Exception? = null,
    val task: Task? = null,
    val showNewTaskBottomSheet: Boolean = false,
    val showEditTaskBottomSheet: Boolean = false
)

class HomeViewModel(
    private val auth: FirebaseAuth,
    private val taskRepository: TaskRepository,
    private val taskReminderRepository: TaskReminderRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val firestoreRepository: FirestoreRepository,
    private val syncWorkerRepository: SyncWorkerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun isSignedIn() = viewModelScope.launch {
        auth.addAuthStateListener {
            _uiState.update { currentState ->
                currentState.copy(
                    currentUser = it.currentUser
                )
            }
        }
    }

    fun getShowStatistics() = viewModelScope.launch {
        dataStoreRepository.showStatistics
            .catch {
                _uiState.update { currentState ->
                    currentState.copy(
                        showStatistics = false
                    )
                }
            }
            .collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        showStatistics = it
                    )
                }
            }
    }

    fun observeTasks() = viewModelScope.launch {
        taskRepository.tasks
            .catch { error ->
                _uiState.update { currentState ->
                    currentState.copy(result = Result.Failure(error))
                }
            }
            .collect { tasks ->
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
    }

    fun newTask(task: Task) = viewModelScope.launch {
        var newTask = task.copy(id = UUID.randomUUID().toString())
        val currentUser = _uiState.value.currentUser

        newTask = taskReminderRepository.scheduleReminder(newTask)
        taskRepository.insert(newTask)

        if (currentUser != null && isInternetAvailable()) {
            firestoreRepository.insert(newTask.copy(isSynced = true)) { e ->
                if (e == null) {
                    viewModelScope.launch {
                        taskRepository.insert(newTask.copy(isSynced = true))
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            exception = e
                        )
                    }
                }
            }
        } else if (currentUser != null) {
            syncWorkerRepository.scheduleUnSyncTasksWork()
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskReminderRepository.cancelReminder(task)
        taskRepository.delete(task)
        syncWorkerRepository.scheduleSyncDeleteTaskWork(task.id)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        val currentUser = _uiState.value.currentUser

        var updatedTask = if (task.isDone) {
            taskReminderRepository.cancelReminder(task)
            task
        } else {
            taskReminderRepository.scheduleReminder(task)
        }

        taskRepository.update(updatedTask)

        if (currentUser != null && isInternetAvailable()) {
            firestoreRepository.insert(updatedTask.copy(isSynced = true)) { e ->
                if (e == null) {
                    viewModelScope.launch {
                        taskRepository.update(updatedTask.copy(isSynced = true))
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            exception = e
                        )
                    }
                }
            }
        } else if (currentUser != null) {
            syncWorkerRepository.scheduleUnSyncTasksWork()
        }
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