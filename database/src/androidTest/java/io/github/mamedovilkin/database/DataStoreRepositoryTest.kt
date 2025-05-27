package io.github.mamedovilkin.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.database.repository.createTestDataStore
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
    fun repositorySetShowStatistics_getShowStatistics() = runBlocking {
        dataStoreRepository.setShowStatistics(true)

        val showStatistics = dataStoreRepository.showStatistics.first()

        assertTrue(showStatistics)
    }
}