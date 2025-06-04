package io.github.mamedovilkin.database.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.toHashMap
import io.github.mamedovilkin.database.room.Converters
import kotlinx.coroutines.tasks.await

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirestoreRepository {

    override suspend fun setLastSignIn(uid: String) {
        firestore
            .collection("users")
            .document(uid)
            .set(mapOf("lastSignIn" to System.currentTimeMillis()))
            .await()
    }

    override suspend fun setPremium(uid: String, isPremium: Boolean) {
        firestore
            .collection("users")
            .document(uid)
            .set(mapOf("isPremium" to isPremium))
            .await()
    }

    override suspend fun deleteAllData(uid: String) {
        val tasks = firestore
            .collection("users")
            .document(uid)
            .collection("tasks")
            .get().await()

        val batch = firestore.batch()

        tasks.documents.forEach { document ->
            batch.delete(document.reference)
        }

        batch.commit().await()
    }

    override suspend fun insert(uid: String, task: Task) {
        firestore
            .collection("users")
            .document(uid)
            .collection("tasks")
            .document(task.id)
            .set(task.toHashMap())
            .await()
    }

    override suspend fun delete(uid: String, taskId: String) {
        firestore
            .collection("users")
            .document(uid)
            .collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun get(uid: String): List<Task> {
        val snapshots = firestore
            .collection("users")
            .document(uid)
            .collection("tasks")
            .get()
            .await()

        return snapshots.documents.mapNotNull { it.toTask() }
    }
}

fun DocumentSnapshot.toTask(): Task? {
    return try {
        val id = getString("id") ?: return null
        val repeatTypeRaw = getString("repeatType")
        val repeatType = RepeatType.entries.firstOrNull { it.name == repeatTypeRaw } ?: RepeatType.ONE_TIME
        val repeatDaysOfWeekRaw = get("repeatDaysOfWeek")
        val repeatDaysOfWeek: List<Int> = when (repeatDaysOfWeekRaw) {
            is List<*> -> repeatDaysOfWeekRaw.filterIsInstance<Number>().map { it.toInt() }
            is String -> Converters().fromStringToList(repeatDaysOfWeekRaw)
            else -> emptyList()
        }

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
