package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncDeleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val auth: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()

    override suspend fun doWork(): Result {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            try {
                val taskId = inputData.getString("taskId") ?: return Result.failure()

                firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("tasks")
                    .document(taskId)
                    .delete()
                    .await()

                return Result.success()
            } catch (_: Exception) {
                return Result.retry()
            }
        } else {
            return Result.retry()
        }
    }
}
