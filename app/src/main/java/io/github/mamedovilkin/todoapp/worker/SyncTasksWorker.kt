package io.github.mamedovilkin.todoapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.toHashMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncTasksWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val taskDao: TaskDao by inject()
    private val auth: FirebaseAuth by inject()
    private val firestore: FirebaseFirestore by inject()

    override suspend fun doWork(): Result {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            try {
                val localTasks = taskDao.getTasks().first()

                val remoteTasks = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("tasks")
                    .get()
                    .await()
                    .toObjects(Task::class.java)

                taskDao.insertAll(remoteTasks)

                val batch = firestore.batch()

                localTasks.forEach { task ->
                    val docRef = firestore
                        .collection("users")
                        .document(currentUser.uid)
                        .collection("tasks")
                        .document(task.id)

                    batch.set(docRef, task.copy(isSynced = true).toHashMap())
                }

                batch.commit().await()

                return Result.success()
            } catch (_: Exception) {
                return Result.retry()
            }
        } else {
            return Result.retry()
        }
    }
}
