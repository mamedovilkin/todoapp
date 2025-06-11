package io.github.mamedovilkin.todoapp.ui.screen.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class SettingsUiState(
    val showSignOutDialog: Boolean = false,
    val showDeleteAllDataDialog: Boolean = false,
)

class SettingsViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val firestoreRepository: FirestoreRepository,
    private val taskRepository: TaskRepository,
    private val syncWorkerRepository: SyncWorkerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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

    val rescheduleUncompletedTasks = dataStoreRepository.rescheduleUncompletedTasks
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val autoDeleteIndex = dataStoreRepository.autoDeleteIndex
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    fun setShowStatistics(showStatistics: Boolean) = viewModelScope.launch {
        dataStoreRepository.setShowStatistics(showStatistics)
    }

    fun setRescheduleUncompletedTasks(rescheduleUncompletedTasks: Boolean) = viewModelScope.launch {
        dataStoreRepository.setRescheduleUncompletedTasks(rescheduleUncompletedTasks)

        if (rescheduleUncompletedTasks) {
            syncWorkerRepository.rescheduleUncompletedTasksWork()
        } else {
            syncWorkerRepository.cancelRescheduleUncompletedTasksWork()
        }
    }

    fun setAutoDeleteIndex(autoDeleteIndex: Int) = viewModelScope.launch {
        dataStoreRepository.setAutoDeleteIndex(autoDeleteIndex)

        if (autoDeleteIndex == 0) {
            syncWorkerRepository.cancelAutoDeleteTasksWork()
        } else {
            syncWorkerRepository.scheduleAutoDeleteTasksWork(autoDeleteIndex)
        }
    }

    fun setShowSignOutDialog(showDialog: Boolean) = _uiState.update { currentState ->
        currentState.copy(
            showSignOutDialog = showDialog
        )
    }

    fun setShowDeleteAllDataDialog(showDeleteAllDataDialog: Boolean) = _uiState.update { currentState ->
        currentState.copy(
            showDeleteAllDataDialog = showDeleteAllDataDialog
        )
    }

    fun deleteAllData() {
        val deleteAllDataLocalJob = viewModelScope.launch {
            taskRepository.deleteAll()
        }

        val deleteAllDataRemoteJob = viewModelScope.launch {
            if (userID.value.isNotEmpty()) {
                firestoreRepository.deleteAllData(userID.value)
            }
        }

        deleteAllDataLocalJob.invokeOnCompletion {
            deleteAllDataRemoteJob
        }
    }
}