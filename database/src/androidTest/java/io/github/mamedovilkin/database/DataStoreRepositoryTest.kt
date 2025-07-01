package io.github.mamedovilkin.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.database.repository.createTestDataStore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStoreRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreRepository: DataStoreRepository

    @Before
    fun setUp() {
        dataStore = createTestDataStore(ApplicationProvider.getApplicationContext())
        dataStoreRepository = DataStoreRepositoryImpl(dataStore)
    }

    @Test
    fun repositorySetWasFirstLaunch_getWasFirstLaunch() = runBlocking {
        dataStoreRepository.setWasFirstLaunch(true)

        val wasFirstLaunch = dataStoreRepository.wasFirstLaunch.first()

        assertTrue(wasFirstLaunch)
    }

    @Test
    fun repositorySetShowStatistics_getShowStatistics() = runBlocking {
        dataStoreRepository.setShowStatistics(true)

        val showStatistics = dataStoreRepository.showStatistics.first()

        assertTrue(showStatistics)
    }

    @Test
    fun repositorySetUserID_getUserID() = runBlocking {
        dataStoreRepository.setUserID("12345678")

        val userID = dataStoreRepository.userID.first()

        assertEquals("12345678", userID)
    }

    @Test
    fun repositorySetPhotoURL_getPhotoURL() = runBlocking {
        dataStoreRepository.setPhotoURL("https://www.example.com/photo.png")

        val photoURL = dataStoreRepository.photoURL.first()

        assertEquals("https://www.example.com/photo.png", photoURL)
    }

    @Test
    fun repositorySetDisplayName_getDisplayName() = runBlocking {
        dataStoreRepository.setDisplayName("John Doe")

        val displayName = dataStoreRepository.displayName.first()

        assertEquals("John Doe", displayName)
    }

    @Test
    fun repositorySetPremium_isPremium() = runBlocking {
        dataStoreRepository.setPremium(true)

        val isPremium = dataStoreRepository.isPremium.first()

        assertTrue(isPremium)
    }

    @Test
    fun repositorySetRescheduleUncompletedTasks_rescheduleUncompletedTasks() = runBlocking {
        dataStoreRepository.setRescheduleUncompletedTasks(true)

        val rescheduleUncompletedTasks = dataStoreRepository.rescheduleUncompletedTasks.first()

        assertTrue(rescheduleUncompletedTasks)
    }

    @Test
    fun repositorySetAutoDeleteIndex_autoDeleteIndex() = runBlocking {
        dataStoreRepository.setAutoDeleteIndex(1)

        val autoDeleteIndex = dataStoreRepository.autoDeleteIndex.first()

        assertEquals(1, autoDeleteIndex)
    }
}