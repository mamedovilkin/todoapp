package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncDeleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val firestoreRepository: FirestoreRepository by inject()

    override suspend fun doWork(): Result {
        val userID = dataStoreRepository.userID.first()
        val isPremium = dataStoreRepository.isPremium.first()

        if (userID.isNotEmpty() && isPremium && isInternetAvailable()) {
            try {
                val taskId = inputData.getString("taskId") ?: return Result.failure()

                firestoreRepository.delete(userID, taskId)

                return Result.success()
            } catch (_: Exception) {
                return Result.retry()
            }
        } else {
            return Result.retry()
        }
    }
}
