package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.toHashMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class UnSyncTasksWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val taskDao: TaskDao by inject()
    private val auth: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()

    override suspend fun doWork(): Result {
        val unSyncedTasks = taskDao.getUnSyncedTasks().first()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            try {
                for (task in unSyncedTasks) {
                    val updatedTask = task.copy(isSynced = true)
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .collection("tasks")
                        .document(updatedTask.id)
                        .set(updatedTask.toHashMap())
                        .await()

                    taskDao.update(updatedTask)
                }

                return Result.success()
            } catch (_: Exception) {
                return Result.retry()
            }
        } else {
            return Result.retry()
        }
    }
}
