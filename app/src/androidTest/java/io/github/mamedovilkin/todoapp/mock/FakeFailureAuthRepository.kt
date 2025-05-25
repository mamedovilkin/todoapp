package io.github.mamedovilkin.todoapp.mock

import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.auth.repository.AuthResult

class FakeFailureAuthRepository : AuthRepository {

    override fun addAuthStateListener(callback: (FirebaseUser?) -> Unit) {
        callback(null)
    }

    override suspend fun signInWithGoogle(): AuthResult {
        return AuthResult.Failure(Exception("An error occurred."))
    }

    override suspend fun signOut(): AuthResult {
        return AuthResult.Failure(Exception("An error occurred."))
    }
}