package io.github.mamedovilkin.todoapp

import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.repository.TaskRepository
import io.github.mamedovilkin.todoapp.data.room.Task
import io.github.mamedovilkin.todoapp.ui.screens.HomeViewModel
import io.github.mamedovilkin.todoapp.ui.screens.Result
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
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModelNewTask_insertNewTask() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        homeViewModel.newTask(Task(title = "Clean my room up"))

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals("Clean my room up", allTasks[0].title)
    }

    @Test
    fun viewModelDeleteTask_deleteTask() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        val task = Task(id = "0", title = "Clean my room up")

        homeViewModel.newTask(task)
        homeViewModel.deleteTask(task)

        advanceUntilIdle()

        assertTrue(homeViewModel.uiState.value.result is Result.NoTasks)
    }

    @Test
    fun viewModelToggleDone_toggleDone() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        homeViewModel.newTask(Task(id = "0", title = "Clean my room up"))
        homeViewModel.toggleDone(Task(id = "0", title = "Clean my room up"))

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertTrue(allTasks.first().isDone)
    }

    @Test
    fun viewModelUpdateTask_updateTask() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        homeViewModel.newTask(Task(id = "0", title = "Clean my room up"))
        homeViewModel.updateTask(Task(id = "0", title = "Walk my dog"))

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals("Walk my dog", allTasks[0].title)
    }

    @Test
    fun viewModelGetNotDoneTasksCount_getNotDoneTasksCount() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        homeViewModel.newTask(Task(title = "Clean my room up"))

        advanceUntilIdle()

        val count = homeViewModel.uiState.value.notDoneTasksCount

        assertEquals(1, count)
    }

    @Test
    fun viewModelSearchForTasks_searchForTasks() = runTest {
        val taskRepository: TaskRepository = FakeTaskRepository()
        val taskReminderRepository: TaskReminderRepository = FakeTaskReminderRepository()
        val homeViewModel = HomeViewModel(taskRepository, taskReminderRepository)

        homeViewModel.newTask(Task(title = "Clean my room up"))
        homeViewModel.newTask(Task(title = "Do homework"))
        homeViewModel.searchForTasks("Do")

        advanceUntilIdle()

        val allTasks = (homeViewModel.uiState.value.result as Result.Success).tasks

        assertEquals(1, allTasks.size)
        assertEquals("Do homework", allTasks.first().title)
    }
}