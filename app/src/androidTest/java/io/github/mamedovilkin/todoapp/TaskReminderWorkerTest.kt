package io.github.mamedovilkin.todoapp

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import io.github.mamedovilkin.todoapp.util.WORK_MANAGER_INPUT_DATA_KEY
import io.github.mamedovilkin.todoapp.worker.TaskReminderWorker
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class TaskReminderWorkerTest {

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var testDriver: TestDriver
    private var duration = 1000L

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        workManager = WorkManager.getInstance(context)
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    }

    @Test
    fun taskReminderWorker_executesSuccessfully() {
        val inputData = workDataOf(WORK_MANAGER_INPUT_DATA_KEY to "Test")

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .addTag("TASK_REMINDER_0")
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "TASK_REMINDER_0",
            ExistingWorkPolicy.REPLACE,
            request
        ).result.get()

        testDriver.setInitialDelayMet(request.id)

        Thread.sleep(duration)

        val workInfo = workManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo?.state)
    }

    @Test
    fun taskReminderWorker_cancelSuccessfully() {
        val inputData = workDataOf(WORK_MANAGER_INPUT_DATA_KEY to "Test")

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .addTag("TASK_REMINDER_1")
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "TASK_REMINDER_1",
            ExistingWorkPolicy.REPLACE,
            request
        ).result.get()

        testDriver.setInitialDelayMet(request.id)

        Thread.sleep(500)

        workManager.cancelUniqueWork("TASK_REMINDER_1")
            .result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo?.state)
    }
}