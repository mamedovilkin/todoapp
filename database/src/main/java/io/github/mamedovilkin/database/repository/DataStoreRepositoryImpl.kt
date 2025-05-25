package io.github.mamedovilkin.database.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    private companion object {
        val SHOW_STATISTICS = booleanPreferencesKey("showStatistics")
    }

    override suspend fun setShowStatistics(showStatistics: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_STATISTICS] = showStatistics
        }
    }

    override val showStatistics: Flow<Boolean> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[SHOW_STATISTICS] == true
        }
}

fun createTestDataStore(context: Context): DataStore<Preferences> {
    val dataStoreFile = File(context.filesDir, "test.preferences_pb")

    return if (dataStoreFile.exists()) {
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(dataStoreFile.name)
        }
    } else {
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                File(context.filesDir, "test.preferences_pb").also {
                    it.deleteOnExit()
                }
            }
        )
    }
}