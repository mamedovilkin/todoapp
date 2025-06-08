package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataStoreRepository : DataStoreRepository {

    private val showStatisticsFlow = MutableStateFlow(false)
    private val userIDFlow = MutableStateFlow("")
    private val photoURLFlow = MutableStateFlow("")
    private val displayNameFlow = MutableStateFlow("")
    private val isPremiumFlow = MutableStateFlow(false)

    override suspend fun setShowStatistics(showStatistics: Boolean) {
        showStatisticsFlow.value = showStatistics
    }

    override val showStatistics: Flow<Boolean>
        get() = showStatisticsFlow

    override suspend fun setUserID(userID: String) {
        userIDFlow.value = userID
    }

    override val userID: Flow<String>
        get() = userIDFlow

    override suspend fun setPhotoURL(photoURL: String) {
        photoURLFlow.value = photoURL
    }

    override val photoURL: Flow<String>
        get() = photoURLFlow

    override suspend fun setDisplayName(displayName: String) {
        displayNameFlow.value = displayName
    }

    override val displayName: Flow<String>
        get() = displayNameFlow

    override suspend fun setPremium(isPremium: Boolean) {
        isPremiumFlow.value = isPremium
    }

    override val isPremium: Flow<Boolean>
        get() = isPremiumFlow
}