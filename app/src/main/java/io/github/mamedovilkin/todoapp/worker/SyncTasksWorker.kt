package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class SyncTasksWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val taskRepository: TaskRepository by inject()
    private val firestoreRepository: FirestoreRepository by inject()
    private val taskReminderRepository: TaskReminderRepository by inject()

    override suspend fun doWork(): Result {
        val userID = dataStoreRepository.userID.first()

        if (userID.isNotEmpty() && isInternetAvailable()) {
            try {
                val uid = userID.toString()
                val remoteTasks = firestoreRepository.get(uid)
                val localTasks = taskRepository.tasks.first()
                val localTasksMap = localTasks.associateBy { it.id }
                val remoteTaskIds = remoteTasks.map { it.id }.toSet()

                for (remoteTask in remoteTasks) {
                    val localTask = localTasksMap[remoteTask.id]
                    val localUpdatedAt = localTask?.updatedAt ?: 0L
                    val remoteUpdatedAt = remoteTask.updatedAt

                    when {
                        localTask == null -> {
                            taskRepository.insert(remoteTask)

                            if (System.currentTimeMillis() < remoteTask.datetime) {
                                taskReminderRepository.scheduleReminder(remoteTask)
                            }
                        }

                        remoteUpdatedAt > localUpdatedAt -> {
                            taskRepository.update(remoteTask)

                            if (System.currentTimeMillis() < remoteTask.datetime) {
                                taskReminderRepository.scheduleReminder(remoteTask)
                            }
                        }

                        localUpdatedAt > remoteUpdatedAt -> {
                            val synced = localTask.copy(isSynced = true)
                            firestoreRepository.insert(uid, synced)
                            taskRepository.update(synced)
                        }
                    }
                }

                for (localTask in localTasks) {
                    if (localTask.id !in remoteTaskIds && !localTask.isSynced) {
                        val synced = localTask.copy(isSynced = true)
                        firestoreRepository.insert(uid, synced)
                        taskRepository.update(synced)
                    }
                }

                return Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                return Result.retry()
            }
        } else {
            return Result.retry()
        }
    }
}
