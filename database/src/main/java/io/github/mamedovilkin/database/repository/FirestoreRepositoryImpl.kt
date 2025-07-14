package io.github.mamedovilkin.database.repository

import android.app.Application
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.toHashMap
import kotlinx.coroutines.tasks.await

class FirestoreRepositoryImpl(
    private val application: Application,
    private val firestore: FirebaseFirestore,
) : FirestoreRepository {

    override suspend fun setSubscriptionToken(uid: String, token: String) {
        try {
            firestore
                .collection("users")
                .document(uid)
                .set(mapOf("subscriptionToken" to token), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getSubscriptionToken(uid: String): String {
        return try {
            val document = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            document.getString("subscriptionToken") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun setLastSignIn(uid: String) {
        try {
            firestore
                .collection("users")
                .document(uid)
                .set(mapOf("lastSignIn" to System.currentTimeMillis()), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getLatestVersion(): String {
        val currentVersion = application.packageManager.getPackageInfo(application.packageName, 0).versionName.toString()

        return try {
            val document = firestore
                .collection("about")
                .document("version")
                .get()
                .await()

            document.getString("latest") ?: currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            currentVersion
        }
    }

    override suspend fun deleteAllData(uid: String) {
        try {
            val tasks = firestore
                .collection("users")
                .document(uid)
                .collection("tasks")
                .get()
                .await()

            val batch = firestore.batch()

            tasks.documents.forEach { document ->
                batch.delete(document.reference)
            }

            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insert(uid: String, task: Task) {
        try {
            firestore
                .collection("users")
                .document(uid)
                .collection("tasks")
                .document(task.id)
                .set(task.toHashMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun delete(uid: String, taskId: String) {
        try {
            firestore
                .collection("users")
                .document(uid)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun get(uid: String): List<Task> {
        return try {
            val snapshots = firestore
                .collection("users")
                .document(uid)
                .collection("tasks")
                .get()
                .await()

            snapshots.documents.mapNotNull { it.toTask() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

fun DocumentSnapshot.toTask(): Task? {
    return try {
        val id = getString("id") ?: return null
        val repeatTypeRaw = getString("repeatType")
        val repeatType = RepeatType.entries.firstOrNull { it.name == repeatTypeRaw } ?: RepeatType.ONE_TIME
        val repeatDaysOfWeekRaw = get("repeatDaysOfWeek") as List<*>
        val repeatDaysOfWeek: List<Int> = repeatDaysOfWeekRaw.filterIsInstance<Number>().map { it.toInt() }

        Task(
            id = id,
            title = getString("title") ?: "",
            description = getString("description") ?: "",
            category = getString("category") ?: "",
            isDone = getBoolean("isDone") == true,
            datetime = getLong("datetime") ?: 0L,
            isSynced = getBoolean("isSynced") == true,
            repeatType = repeatType,
            repeatDaysOfWeek = repeatDaysOfWeek,
            updatedAt = getLong("updatedAt") ?: 0L,
        )
    } catch (_: Exception) {
        null
    }
}
