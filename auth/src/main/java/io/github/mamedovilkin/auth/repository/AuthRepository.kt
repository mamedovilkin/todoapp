package io.github.mamedovilkin.auth.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    fun addAuthStateListener(callback: (FirebaseUser?) -> Unit)
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signOut(): AuthResult
}