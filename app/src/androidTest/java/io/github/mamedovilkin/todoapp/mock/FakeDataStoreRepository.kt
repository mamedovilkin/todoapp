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
    private val rescheduleUncompletedTasksFlow = MutableStateFlow(false)
    private val autoDeleteIndexFlow = MutableStateFlow(0)

    override suspend fun setShowStatistics(showStatistics: Boolean) {
        showStatisticsFlow.value = showStatistics
    }

    override val showStatistics: Flow<Boolean> = showStatisticsFlow

    override suspend fun setUserID(userID: String) {
        userIDFlow.value = userID
    }

    override val userID: Flow<String> = userIDFlow

    override suspend fun setPhotoURL(photoURL: String) {
        photoURLFlow.value = photoURL
    }

    override val photoURL: Flow<String> = photoURLFlow

    override suspend fun setDisplayName(displayName: String) {
        displayNameFlow.value = displayName
    }

    override val displayName: Flow<String> = displayNameFlow

    override suspend fun setPremium(isPremium: Boolean) {
        isPremiumFlow.value = isPremium
    }

    override val isPremium: Flow<Boolean> = isPremiumFlow

    override suspend fun setRescheduleUncompletedTasks(rescheduleUncompletedTasks: Boolean) {
        rescheduleUncompletedTasksFlow.value = rescheduleUncompletedTasks
    }

    override val rescheduleUncompletedTasks: Flow<Boolean> = rescheduleUncompletedTasksFlow

    override suspend fun setAutoDeleteIndex(autoDeleteIndex: Int) {
        autoDeleteIndexFlow.value = autoDeleteIndex
    }

    override val autoDeleteIndex: Flow<Int> = autoDeleteIndexFlow
}