package io.github.mamedovilkin.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeFirestoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeSyncWorkerRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskReminderRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val firestoreRepository: FirestoreRepository = FakeFirestoreRepository()
    private val taskRepository: TaskRepository = FakeTaskRepository()
    private val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
    private val syncWorkerRepository: SyncWorkerRepository = FakeSyncWorkerRepository()
    private var settingsViewModel: SettingsViewModel = SettingsViewModel(
        dataStoreRepository,
        firestoreRepository,
        taskRepository,
        taskReminderRepository,
        syncWorkerRepository
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
    fun viewModelSetUserID_getUserID() = runTest {
        val collected = CompletableDeferred<String>()

        val job = launch {
            settingsViewModel.userID.collect { value ->
                if (value == "test") {
                    collected.complete(value)
                    cancel()
                }
            }
        }

        dataStoreRepository.setUserID("test")

        val result = collected.await()
        assertEquals("test", result)

        job.cancel()
    }

    @Test
    fun viewModelSetPhotoURL_getPhotoURL() = runTest {
        val collected = CompletableDeferred<String>()

        val job = launch {
            settingsViewModel.photoURL.collect { value ->
                if (value == "test") {
                    collected.complete(value)
                    cancel()
                }
            }
        }

        dataStoreRepository.setPhotoURL("test")

        val result = collected.await()
        assertEquals("test", result)

        job.cancel()
    }

    @Test
    fun viewModelSetDisplayName_getDisplayName() = runTest {
        val collected = CompletableDeferred<String>()

        val job = launch {
            settingsViewModel.displayName.collect { value ->
                if (value == "test") {
                    collected.complete(value)
                    cancel()
                }
            }
        }

        dataStoreRepository.setDisplayName("test")

        val result = collected.await()
        assertEquals("test", result)

        job.cancel()
    }

    @Test
    fun viewModelSetPremium_isPremium() = runTest {
        val collected = CompletableDeferred<Boolean>()

        val job = launch {
            settingsViewModel.isPremium.collect { value ->
                if (value) {
                    collected.complete(true)
                    cancel()
                }
            }
        }

        dataStoreRepository.setPremium(true)

        val result = collected.await()
        assertEquals(true, result)

        job.cancel()
    }

    @Test
    fun viewModelSetRescheduleUncompletedTasks_getRescheduleUncompletedTasks() = runTest {
        val collected = CompletableDeferred<Boolean>()

        val job = launch {
            settingsViewModel.rescheduleUncompletedTasks.collect { value ->
                if (value) {
                    collected.complete(true)
                    cancel()
                }
            }
        }

        dataStoreRepository.setRescheduleUncompletedTasks(true)

        val result = collected.await()
        assertEquals(true, result)

        job.cancel()
    }

    @Test
    fun viewModelSetReminderCount_getReminderCount() = runTest {
        val collected = CompletableDeferred<Int>()

        val job = launch {
            settingsViewModel.reminderCount.collect { value ->
                if (value == 5) {
                    collected.complete(value)
                    cancel()
                }
            }
        }

        dataStoreRepository.setReminderCount(5)

        val result = collected.await()
        assertEquals(5, result)

        job.cancel()
    }

    @Test
    fun viewModelSetAutoDeleteIndex_getAutoDeleteIndex() = runTest {
        val collected = CompletableDeferred<Int>()

        val job = launch {
            settingsViewModel.autoDeleteIndex.collect { value ->
                if (value == 1) {
                    collected.complete(value)
                    cancel()
                }
            }
        }

        dataStoreRepository.setAutoDeleteIndex(1)

        val result = collected.await()
        assertEquals(1, result)

        job.cancel()
    }

    @Test
    fun viewModelSetShowStatistics_getShowStatistics() = runTest {
        settingsViewModel.setShowStatistics(true)

        advanceUntilIdle()

        val showStatistics = settingsViewModel.showStatistics.first { it }

        assertTrue(showStatistics)
    }

    @Test
    fun viewModelSetShowSignOutDialog_getShowSignOutDialog() {
        settingsViewModel.setShowSignOutDialog(true)

        val showSignOutDialog = settingsViewModel.uiState.value.showSignOutDialog

        assertTrue(showSignOutDialog)
    }

    @Test
    fun viewModelSetShowDeleteAllDataDialog_getShowDeleteAllDataDialog() {
        settingsViewModel.setShowDeleteAllDataDialog(true)

        val showDeleteAllDataDialog = settingsViewModel.uiState.value.showDeleteAllDataDialog

        assertTrue(showDeleteAllDataDialog)
    }

    @Test
    fun viewModelDeleteAllData_deletesAllData() = runTest {
        val taskRepository = FakeTaskRepository().apply {
            insertAll(listOf(
                Task(title = "Clean my room up")
            ))
        }

        var allTasks = taskRepository.tasks.first()

        assertTrue(allTasks.isNotEmpty())

        settingsViewModel = SettingsViewModel(
            dataStoreRepository,
            firestoreRepository,
            taskRepository,
            taskReminderRepository,
            syncWorkerRepository
        )

        settingsViewModel.deleteAllData()

        allTasks = taskRepository.tasks.first { it.isEmpty() }

        assertTrue(allTasks.isEmpty())
    }
}