package io.github.mamedovilkin.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeFailureAuthRepository
import io.github.mamedovilkin.todoapp.mock.FakeSuccessAuthRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskReminderRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskRepository
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.todoapp.ui.screen.home.Result
import junit.framework.TestCase.assertEquals
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = FakeSuccessAuthRepository()
    private val taskRepository: TaskRepository = FakeTaskRepository()
    private val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val homeViewModel = HomeViewModel(
        authRepository,
        taskRepository,
        taskReminderRepository,
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
        homeViewModel.isSignedIn()

        advanceUntilIdle()

        val currentUser = homeViewModel.uiState.value.currentUser

        assertTrue(currentUser != null)
    }

    @Test
    fun viewModelIsSignedIn_null() = runTest {
        val authRepository: AuthRepository = FakeFailureAuthRepository()
        val homeViewModel = HomeViewModel(
            authRepository,
            taskRepository,
            taskReminderRepository,
            dataStoreRepository
        )
        homeViewModel.isSignedIn()

        advanceUntilIdle()

        val currentUser = homeViewModel.uiState.value.currentUser

        assertTrue(currentUser == null)
    }

    @Test
    fun viewModelGetShowStatistics_getShowStatistics() = runTest {
        dataStoreRepository.setShowStatistics(true)
        homeViewModel.getShowStatistics()

        advanceUntilIdle()

        val showStatistics = homeViewModel.uiState.value.showStatistics

        assertTrue(showStatistics)
    }

    @Test
    fun viewModelNewTask_insertNewTask() = runTest {
        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals("Clean my room up", allTasks[0].title)
    }

    @Test
    fun viewModelDeleteTask_deleteTask() = runTest {
        val task = Task(id = "0", title = "Clean my room up")

        homeViewModel.newTask(task)
        homeViewModel.deleteTask(task)
        homeViewModel.observeTasks()

        advanceUntilIdle()

        assertTrue(homeViewModel.uiState.value.result is Result.NoTasks)
    }

    @Test
    fun viewModelToggleDone_toggleDone() = runTest {
        homeViewModel.newTask(Task(id = "0", title = "Clean my room up"))
        homeViewModel.toggleDone(Task(id = "0", title = "Clean my room up"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertTrue(allTasks.first().isDone)
    }

    @Test
    fun viewModelUpdateTask_updateTask() = runTest {
        homeViewModel.newTask(Task(id = "0", title = "Clean my room up"))
        homeViewModel.updateTask(Task(id = "0", title = "Walk my dog"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals("Walk my dog", allTasks[0].title)
    }

    @Test
    fun viewModelGetNotDoneTasksCount_getNotDoneTasksCount() = runTest {
        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val count = homeViewModel.uiState.value.notDoneTasksCount

        assertEquals(1, count)
    }

    @Test
    fun viewModelSearchForTasks_searchForTasks() = runTest {
        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.newTask(Task(title = "Do homework"))
        homeViewModel.searchForTasks("Do")
        homeViewModel.observeTasks()

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals(1, allTasks.size)
        assertEquals("Do homework", allTasks.first().title)
    }
}