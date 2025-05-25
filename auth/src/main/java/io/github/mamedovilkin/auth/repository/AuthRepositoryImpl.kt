package io.github.mamedovilkin.auth.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class AuthResult {
    data class Success(val firebaseUser: FirebaseUser?): AuthResult()
    data class Failure(val e: Exception?): AuthResult()
    data object Init: AuthResult()
}

class AuthRepositoryImpl(
    private val context: Context,
    private val serverClientId: String,
    private val auth: FirebaseAuth
) : AuthRepository {

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override fun addAuthStateListener(callback: (FirebaseUser?) -> Unit) {
        auth.addAuthStateListener {
            callback(it.currentUser)
        }
    }

    override suspend fun signInWithGoogle(): AuthResult = withContext(Dispatchers.IO) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(serverClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result.credential)
        } catch (e: GetCredentialException) {
            AuthResult.Failure(e)
        }
    }

    private suspend fun handleSignIn(credential: Credential): AuthResult {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            return AuthResult.Failure(Exception("Credential is not of type Google ID!"))
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): AuthResult = suspendCoroutine { cont ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(AuthResult.Success(auth.currentUser))
                } else {
                    cont.resume(AuthResult.Failure(task.exception))
                }
            }
    }


    override suspend fun signOut(): AuthResult {
        auth.signOut()

        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)

            return AuthResult.Success(null)
        } catch (e: ClearCredentialException) {
            return AuthResult.Failure(e)
        } catch (e: NoCredentialException) {
            return AuthResult.Failure(e)
        }
    }
}