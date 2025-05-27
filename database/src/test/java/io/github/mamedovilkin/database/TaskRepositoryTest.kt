package io.github.mamedovilkin.database

import io.github.mamedovilkin.database.mock.FakeTaskRepository
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
    private var task1 = Task("1", "Clean my room up")
    private var task2 = Task("2", "Do homework", true)

    @Test
    fun repositoryInsert_insertsTaskIntoRepository() = runBlocking {
        taskRepository.insert(task1)

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks[0], task1)
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
    fun repositoryUpdateTasks_updatesTasksInRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        taskRepository.update(Task("1", "Walk my dog", true))
        taskRepository.update(Task("2", "Call mom"))

        val allTasks = taskRepository.tasks.first()

        assertEquals(allTasks[0], Task("1", "Walk my dog", true))
        assertEquals(allTasks[1], Task("2", "Call mom"))
    }

    @Test
    fun repositoryDeleteTasks_deletesAllTasksFromRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        taskRepository.delete(task1)
        taskRepository.delete(task2)

        val allTasks = taskRepository.tasks.first()

        assertTrue(allTasks.isEmpty())
    }

    @Test
    fun repositorySearchForTasks_searchForTasksInRepository() = runBlocking {
        taskRepository.insert(task1)
        taskRepository.insert(task2)

        val tasks = taskRepository.searchForTasks("Do").first()

        assertTrue(tasks.size == 1)
        assertEquals(task2, tasks.first())
    }
}