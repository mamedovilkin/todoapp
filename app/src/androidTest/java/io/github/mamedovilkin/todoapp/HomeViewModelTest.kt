package io.github.mamedovilkin.todoapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeSyncWorkerRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskReminderRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.todoapp.ui.screen.home.Result
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val taskRepository: TaskRepository = FakeTaskRepository()
    private val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val syncWorkerRepository: SyncWorkerRepository = FakeSyncWorkerRepository()
    private var homeViewModel = HomeViewModel(
        ApplicationProvider.getApplicationContext(),
        taskRepository,
        taskReminderRepository,
        syncWorkerRepository,
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
    fun viewModelSetUserID_getUserID() = runTest {
        val collected = CompletableDeferred<String>()

        val job = launch {
            homeViewModel.userID.collect { value ->
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
            homeViewModel.photoURL.collect { value ->
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
    fun viewModelSetShowStatistics_getShowStatistics() = runTest {
        val dataStoreRepository = FakeDataStoreRepository().apply {
            setShowStatistics(true)
        }

        homeViewModel = HomeViewModel(
            ApplicationProvider.getApplicationContext(),
            taskRepository,
            taskReminderRepository,
            syncWorkerRepository,
            dataStoreRepository
        )

        advanceUntilIdle()

        val showStatistics = homeViewModel.showStatistics.first { it }

        assertTrue(showStatistics)
    }

    @Test
    fun viewModelNewTask_insertNewTask() = runTest {
        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val task = homeViewModel.uiState
            .filter { it.result is Result.Success }
            .map { it.result as Result.Success }
            .map { it.tasks }
            .first { it.isNotEmpty() }
            .first()

        assertEquals("Clean my room up", task.title)
    }

    @Test
    fun viewModelDeleteTask_deleteTask() = runTest {
        val task = Task(title = "Clean my room up")

        taskRepository.insert(task)
        homeViewModel.deleteTask(task)
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val result = homeViewModel.uiState
            .filter { it.result is Result.NoTasks }
            .first()

        assertTrue(result.result is Result.NoTasks)
    }

    @Test
    fun viewModelToggleTask_toggleTask() = runTest {
        val task = Task(title = "Clean my room up")

        taskRepository.insert(task)
        homeViewModel.toggleTask(task.copy(isDone = !task.isDone))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val result = homeViewModel.uiState
            .filter { it.result is Result.Success }
            .map { it.result as Result.Success }
            .map { it.tasks }
            .first { it.isNotEmpty() }
            .first()

        assertTrue(result.isDone)
    }

    @Test
    fun viewModelUpdateTask_updateTask() = runTest {
        val task = Task(title = "Clean my room up")

        taskRepository.insert(task)
        homeViewModel.updateTask(task.copy(title = "Walk my dog"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val result = homeViewModel.uiState
            .filter { it.result is Result.Success }
            .map { it.result as Result.Success }
            .map { it.tasks }
            .first { it.isNotEmpty() }
            .first()

        assertEquals("Walk my dog", result.title)
    }

    @Test
    fun viewModelGetNotDoneTasksCount_getNotDoneTasksCount() = runTest {
        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val count = homeViewModel.uiState
            .filter { it.notDoneTasksCount == 1 }
            .first()
            .notDoneTasksCount

        assertEquals(1, count)
    }

    @Test
    fun viewModelSearchForTasks_searchForTasks() = runTest {
        homeViewModel.searchForTasks("Do")

        advanceUntilIdle()

        val query = homeViewModel.uiState.value.query

        assertEquals("Do", query)
    }

    @Test
    fun viewModelSetTaskToEdit_taskToEdit() = runTest {
        homeViewModel.setTaskToEdit(Task(title = "Do homework"))

        advanceUntilIdle()

        val task = homeViewModel.uiState.value.task

        assertTrue(task != null)
    }

    @Test
    fun viewModelSetShowNewTaskBottomSheet_showNewTaskBottomSheet() = runTest {
        homeViewModel.setShowNewTaskBottomSheet(true)

        advanceUntilIdle()

        val showNewTaskBottomSheet = homeViewModel.uiState.value.showNewTaskBottomSheet

        assertTrue(showNewTaskBottomSheet)
    }

    @Test
    fun viewModelSetShowEditTaskBottomSheet_showEditTaskBottomSheet() = runTest {
        homeViewModel.setShowEditTaskBottomSheet(true)

        advanceUntilIdle()

        val showEditTaskBottomSheet = homeViewModel.uiState.value.showEditTaskBottomSheet

        assertTrue(showEditTaskBottomSheet)
    }

    @Test
    fun viewModelSetSelectedCategory_selectedCategory() = runTest {
        homeViewModel.setSelectedCategory("test")

        advanceUntilIdle()

        val selectedCategory = homeViewModel.uiState.value.selectedCategory

        assertEquals("test", selectedCategory)
    }
}