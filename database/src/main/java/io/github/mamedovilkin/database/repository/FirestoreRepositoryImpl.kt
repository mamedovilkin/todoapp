package io.github.mamedovilkin.database.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.toHashMap

class FirestoreRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : FirestoreRepository {

    override fun insert(task: Task, callback: (Exception?) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("tasks")
                .document(task.id)
                .set(task.toHashMap())
                .addOnCompleteListener { result ->
                    callback(result.exception)
                }
        } else {
            callback(Exception("There is no current user! Sign in to your Google Account."))
        }
    }

    override fun delete(task: Task, callback: (Exception?) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("tasks")
                .document(task.id)
                .delete()
                .addOnCompleteListener { result ->
                    callback(result.exception)
                }
        } else {
            callback(Exception("There is no current user! Sign in to your Google Account."))
        }
    }
}