package io.github.mamedovilkin.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeFailureAuthRepository
import io.github.mamedovilkin.todoapp.mock.FakeSuccessAuthRepository
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = FakeSuccessAuthRepository()
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val settingsViewModel: SettingsViewModel = SettingsViewModel(
        authRepository,
        dataStoreRepository
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModelIsSignedIn_currentUser() = runTest {
        settingsViewModel.isSignedIn()

        advanceUntilIdle()

        val currentUser = settingsViewModel.uiState.value.currentUser

        assertTrue(currentUser != null)
    }

    @Test
    fun viewModelIsSignedIn_null() = runTest {
        val authRepository: AuthRepository = FakeFailureAuthRepository()
        val settingsViewModel = SettingsViewModel(
            authRepository,
            dataStoreRepository
        )
        settingsViewModel.isSignedIn()

        advanceUntilIdle()

        val currentUser = settingsViewModel.uiState.value.currentUser

        assertTrue(currentUser == null)
    }

    @Test
    fun viewModelGetShowStatistics_getShowStatistics() = runTest {
        settingsViewModel.setShowStatistics(true)
        settingsViewModel.getShowStatistics()

        advanceUntilIdle()

        val showStatistics = settingsViewModel.uiState.value.showStatistics

        assertTrue(showStatistics)
    }

    @Test
    fun viewModelSetShowDialog_setShowDialog() {
        settingsViewModel.setShowDialog(true)

        val showDialog = settingsViewModel.uiState.value.showDialog

        assertTrue(showDialog)
    }

    @Test
    fun viewModelSignInWithGoogle_currentUser() = runTest {
        settingsViewModel.signInWithGoogle()

        advanceUntilIdle()

        val currentUser = settingsViewModel.uiState.value.currentUser

        assertTrue(currentUser != null)
    }

    @Test
    fun viewModelSignOut_null() = runTest {
        settingsViewModel.signOut()

        advanceUntilIdle()

        val currentUser = settingsViewModel.uiState.value.currentUser

        assertTrue(currentUser == null)
    }

    @Test
    fun viewModelSignInWithGoogle_exception() = runTest {
        val authRepository: AuthRepository = FakeFailureAuthRepository()
        val settingsViewModel = SettingsViewModel(
            authRepository,
            dataStoreRepository
        )
        settingsViewModel.signInWithGoogle()

        advanceUntilIdle()

        val exception = settingsViewModel.uiState.value.exception

        assertTrue(exception != null)
    }

    @Test
    fun viewModelSignOut_exception() = runTest {
        val authRepository: AuthRepository = FakeFailureAuthRepository()
        val settingsViewModel = SettingsViewModel(
            authRepository,
            dataStoreRepository
        )
        settingsViewModel.signOut()

        advanceUntilIdle()

        val exception = settingsViewModel.uiState.value.exception

        assertTrue(exception != null)
    }
}