package io.github.mamedovilkin.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.mock.FakeDataStoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeFirestoreRepository
import io.github.mamedovilkin.todoapp.mock.FakeSyncWorkerRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskReminderRepository
import io.github.mamedovilkin.todoapp.mock.FakeTaskRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
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
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val taskRepository: TaskRepository = FakeTaskRepository()
    private val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
    private val dataStoreRepository: DataStoreRepository = FakeDataStoreRepository()
    private val firestoreRepository: FirestoreRepository = FakeFirestoreRepository()
    private val syncWorkerRepository: SyncWorkerRepository = FakeSyncWorkerRepository()
    private val homeViewModel = HomeViewModel(
        mock<FirebaseAuth>(),
        taskRepository,
        taskReminderRepository,
        dataStoreRepository,
        firestoreRepository,
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
}