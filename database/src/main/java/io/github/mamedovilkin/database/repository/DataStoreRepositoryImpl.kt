package io.github.mamedovilkin.database.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    private companion object {
        val SHOW_STATISTICS = booleanPreferencesKey("showStatistics")
        val USER_ID = stringPreferencesKey("userID")
        val PHOTO_URL = stringPreferencesKey("photoURL")
        val DISPLAY_NAME = stringPreferencesKey("displayName")
        val PREMIUM = booleanPreferencesKey("premium")
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

    override suspend fun setUserID(userID: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userID
        }
    }

    override val userID: Flow<String> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[USER_ID] ?: ""
        }

    override suspend fun setPhotoURL(photoURL: String) {
        dataStore.edit { preferences ->
            preferences[PHOTO_URL] = photoURL
        }
    }

    override val photoURL: Flow<String> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PHOTO_URL] ?: ""
        }

    override suspend fun setDisplayName(displayName: String) {
        dataStore.edit { preferences ->
            preferences[DISPLAY_NAME] = displayName
        }
    }

    override val displayName: Flow<String> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[DISPLAY_NAME] ?: ""
        }

    override suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[PREMIUM] = isPremium
        }
    }

    override val isPremium: Flow<Boolean> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PREMIUM] == true
        }
}

fun createTestDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = {
            File(context.filesDir, "test_${UUID.randomUUID()}.preferences_pb")
        },
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )
}