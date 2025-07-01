package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.isExpired
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import kotlin.getValue

class SyncUncompletedTasksWorker(
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
                val updatedTasks = tasks
                    .filter {
                        it.isExpired() && !it.isDone && it.repeatType == RepeatType.ONE_TIME
                    }
                    .map {
                        val oldDatetime = Calendar.getInstance().apply {
                            timeInMillis = it.datetime
                        }
                        val newDatetime = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, oldDatetime.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, oldDatetime.get(Calendar.MINUTE))
                            set(Calendar.SECOND, oldDatetime.get(Calendar.SECOND))
                            set(Calendar.MILLISECOND, oldDatetime.get(Calendar.MILLISECOND))
                        }
                        it.copy(
                            datetime = newDatetime.timeInMillis,
                            isSynced = false,
                            updatedAt = System.currentTimeMillis()
                        )
                    }

                updatedTasks.forEach {
                    taskRepository.update(it)
                }

                syncWorkerRepository.scheduleSyncTasksWork()
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