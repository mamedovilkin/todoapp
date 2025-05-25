package io.github.mamedovilkin.todoapp.ui.screen.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.auth.repository.AuthResult
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
    val exception: Exception? = null,
    val showDialog: Boolean = false,
    val showStatistics: Boolean = false,
)

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun isSignedIn() = viewModelScope.launch {
        authRepository.addAuthStateListener { currentUser ->
            _uiState.update { currentState ->
                currentState.copy(
                    currentUser = currentUser
                )
            }
        }
    }

    fun signInWithGoogle() = viewModelScope.launch {
        fetchAuthResult(authRepository.signInWithGoogle())
    }

    fun signOut() = viewModelScope.launch {
        fetchAuthResult(authRepository.signOut())
    }

    private fun fetchAuthResult(authResult: AuthResult) {
        when (authResult) {
            AuthResult.Init -> {}
            is AuthResult.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        exception = authResult.e
                    )
                }
            }
            is AuthResult.Success -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentUser = authResult.firebaseUser
                    )
                }
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