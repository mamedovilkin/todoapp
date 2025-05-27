package io.github.mamedovilkin.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.TaskDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var taskDao: TaskDao
    private lateinit var taskDatabase: TaskDatabase
    private var task1 = Task("1", "Clean my room up", isSynced = true)
    private var task2 = Task("2", "Do homework", true)

    @Before
    fun createDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()

        taskDatabase = Room.inMemoryDatabaseBuilder(
            context,
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = taskDatabase.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        taskDatabase.close()
    }

    private suspend fun addOneTaskToDatabase() {
        taskDao.insert(task1)
    }

    private suspend fun addTwoTaskToDatabase() {
        taskDao.insert(task1)
        taskDao.insert(task2)
    }

    @Test
    @Throws(IOException::class)
    fun daoInsert_insertsTaskIntoDatabase() = runBlocking {
        addOneTaskToDatabase()

        val allTasks = taskDao.getTasks().first()

        assertEquals(allTasks[0], task1)
    }

    @Test
    @Throws(IOException::class)
    fun daoGetTasks_returnsAllTasksFromDatabase() = runBlocking {
        addTwoTaskToDatabase()

        val allTasks = taskDao.getTasks().first()

        assertEquals(allTasks[0], task1)
        assertEquals(allTasks[1], task2)
    }

    @Test
    @Throws(IOException::class)
    fun daoUpdateTasks_updatesTasksInDatabase() = runBlocking {
        addTwoTaskToDatabase()

        taskDao.update(Task("1", "Walk my dog", true))
        taskDao.update(Task("2", "Call mom"))

        val allTasks = taskDao.getTasks().first()

        assertEquals(allTasks[0].title, "Call mom")
        assertEquals(allTasks[1].title, "Walk my dog")
    }

    @Test
    @Throws(IOException::class)
    fun daoDeleteTasks_deletesAllTasksFromDatabase() = runBlocking {
        addTwoTaskToDatabase()

        taskDao.delete(task1)
        taskDao.delete(task2)

        val allTasks = taskDao.getTasks().first()

        assertTrue(allTasks.isEmpty())
    }

    @Test
    @Throws(IOException::class)
    fun daoInsertAll_insertsAllTasksIntoDatabase() = runBlocking {
        taskDao.insertAll(listOf(task1, task2))

        val allTasks = taskDao.getTasks().first()

        assertTrue(allTasks.size == 2)
    }

    @Test
    @Throws(IOException::class)
    fun daoGetUnSyncedTasks_returnsUnSyncedTasks() = runBlocking {
        addTwoTaskToDatabase()

        val unSyncedTasks = taskDao.getUnSyncedTasks().first()

        assertTrue(unSyncedTasks.size == 1)
    }
}