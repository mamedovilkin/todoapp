package io.github.mamedovilkin.database

import io.github.mamedovilkin.database.mock.FakeFailureFirestoreRepository
import io.github.mamedovilkin.database.mock.FakeSuccessFirestoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.room.Task
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FirestoreRepositoryTest {

    private val successFirestoreRepository: FakeSuccessFirestoreRepository = FakeSuccessFirestoreRepository()
    private val failureFirestoreRepository: FirestoreRepository = FakeFailureFirestoreRepository()
    private var task = Task("1", "Clean my room up")

    @Test
    fun repositoryInsert_insertsTaskIntoFirestoreSuccessfully() = runBlocking {
        successFirestoreRepository.insert(task) {
            assertEquals(null, it)
            assertTrue(successFirestoreRepository.remoteTask != null)
        }
    }

    @Test
    fun repositoryInsert_insertsTaskIntoFirestoreFailure() = runBlocking {
        failureFirestoreRepository.insert(task) {
            assertTrue(it != null)
            assertEquals(null, successFirestoreRepository.remoteTask)
        }
    }

    @Test
    fun repositoryDelete_deletesTaskFromFirestoreSuccessfully() = runBlocking {
        successFirestoreRepository.insert(task) {}

        successFirestoreRepository.delete(task) {
            assertEquals(null, it)
            assertEquals(null, successFirestoreRepository.remoteTask)
        }
    }

    @Test
    fun repositoryDelete_deletesTaskFromFirestoreFailure() = runBlocking {
        failureFirestoreRepository.insert(task) {}

        failureFirestoreRepository.delete(task) {
            assertTrue(it != null)
        }
    }

}