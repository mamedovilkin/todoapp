package io.github.mamedovilkin.todoapp.ui.screen.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.database.repository.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class SettingsUiState(
    val currentUser: FirebaseUser? = null,
    val showDialog: Boolean = false,
    val showStatistics: Boolean = false,
)

class SettingsViewModel(
    private val auth: FirebaseAuth,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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

    fun setShowStatistics(showStatistics: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setShowStatistics(showStatistics)
        }
    }

    fun setShowDialog(showDialog: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showDialog = showDialog
            )
        }
    }
}