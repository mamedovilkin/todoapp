package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class SyncAutoDeleteTasksWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val taskRepository: TaskRepository by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

    override suspend fun doWork(): Result {
        val tasks = taskRepository.tasks.first()
        val userID = dataStoreRepository.userID.first()
        val isPremium = dataStoreRepository.isPremium.first()

        if (userID.isNotEmpty() && isPremium && isInternetAvailable()) {
            try {
                val completedTasks = tasks.filter {
                    it.isDone && it.repeatType == RepeatType.ONE_TIME
                }

                completedTasks.forEach {
                    taskRepository.delete(it)
                    syncWorkerRepository.scheduleSyncDeleteTaskWork(it.id)
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