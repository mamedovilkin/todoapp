package io.github.mamedovilkin.todoapp.mock

import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.auth.repository.AuthResult
import org.mockito.Mockito.mock

class FakeSuccessAuthRepository : AuthRepository {

    private val firebaseUser = mock<FirebaseUser>()

    override fun addAuthStateListener(callback: (FirebaseUser?) -> Unit) {
        callback(firebaseUser)
    }

    override suspend fun signInWithGoogle(): AuthResult {
        return AuthResult.Success(firebaseUser)
    }

    override suspend fun signOut(): AuthResult {
        return AuthResult.Success(null)
    }
}