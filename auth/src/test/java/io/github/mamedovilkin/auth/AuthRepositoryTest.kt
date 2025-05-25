package io.github.mamedovilkin.auth

import com.google.firebase.auth.FirebaseUser
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.auth.repository.AuthResult
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@RunWith(JUnit4::class)
class AuthRepositoryTest {

    @Test
    fun repositorySignInWithGoogle_returnsSuccessAuthResult() {
        runBlocking {
            val authRepository: AuthRepository = mock()

            `when`(authRepository.signInWithGoogle())
                .thenReturn(AuthResult.Success(mock<FirebaseUser>()))

            val authResult = authRepository.signInWithGoogle()

            assertTrue(authResult is AuthResult.Success)
        }
    }

    @Test
    fun repositorySignInWithGoogle_returnsFailureAuthResult() {
        runBlocking {
            val authRepository: AuthRepository = mock()

            `when`(authRepository.signInWithGoogle())
                .thenReturn(AuthResult.Failure(Exception("An error occurred.")))

            val authResult = authRepository.signInWithGoogle()

            assertTrue(authResult is AuthResult.Failure)
        }
    }

    @Test
    fun repositorySignOut_returnsNull() {
        runBlocking {
            val authRepository: AuthRepository = mock()
            `when`(authRepository.signOut())
                .thenReturn(AuthResult.Success(null))

            val authResult = authRepository.signOut()

            assertTrue(authResult is AuthResult.Success)
        }
    }

    @Test
    fun repositorySignOut_returnsException() {
        runBlocking {
            val authRepository: AuthRepository = mock()

            `when`(authRepository.signOut())
                .thenReturn(AuthResult.Failure(Exception("An error occurred.")))

            val authResult = authRepository.signOut()

            assertTrue(authResult is AuthResult.Failure)
        }
    }
}