package io.github.mamedovilkin.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
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
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val settingsViewModel: SettingsViewModel = SettingsViewModel(
        mock<FirebaseAuth>(), dataStoreRepository,
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
}