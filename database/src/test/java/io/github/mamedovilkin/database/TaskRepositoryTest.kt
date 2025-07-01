package io.github.mamedovilkin.database

import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TaskRepositoryTest {

    private val taskRepository: TaskRepository = FakeTaskRepository()
    private var task1 = Task("1", "Clean my room up", isSynced = true)
    private var task2 = Task("2", "Do homework", isDone = true)

    @Test
    fun repositoryGetTaskById_returnsTaskByIdFromRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        val task = taskRepository.getTask("1")

        assertEquals(task, task1)
    }

    @Test
    fun repositoryGetTasks_returnsAllTasksFromRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks[0], task1)
        assertEquals(allTasks[1], task2)
    }

    @Test
    fun repositoryUnSyncedTasks_returnsAllUnSyncedTasksFromRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        val allTasks = taskRepository.unSyncedTasks.first()

        assertEquals(allTasks.size, 1)
    }

    @Test
    fun repositoryInsert_insertsTaskIntoRepository() = runBlocking {
        taskRepository.insert(task1)

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks[0], task1)
    }

    @Test
    fun repositoryInsert_insertsAllTasksIntoRepository() = runBlocking {
        taskRepository.insertAll(listOf(task1, task2))

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks.size, 2)
    }

    @Test
    fun repositoryDeleteTask_deleteTaskFromRepository() = runBlocking {
        taskRepository.insert(task1)

        taskRepository.delete(task1)

        val allTasks = taskRepository.tasks.first()

        assertTrue(allTasks.isEmpty())
    }

    @Test
    fun repositoryDeleteAllTasks_deleteAllTasksFromRepository() = runBlocking {
        taskRepository.insert(task1)

        taskRepository.deleteAll()

        val allTasks = taskRepository.tasks.first()

        assertTrue(allTasks.isEmpty())
    }

    @Test
    fun repositoryUpdateTasks_updatesTasksInRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        taskRepository.update(task1.copy(title = "Walk my dog"))
        taskRepository.update(task2.copy(title = "Call mom"))

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks[0].title, "Walk my dog")
        assertEquals(allTasks[1].title, "Call mom")
    }
}