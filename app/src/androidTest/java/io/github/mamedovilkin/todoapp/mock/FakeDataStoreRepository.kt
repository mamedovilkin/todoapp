package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataStoreRepository : DataStoreRepository {

    private val wasFirstLaunchFlow = MutableStateFlow(false)
    private val showStatisticsFlow = MutableStateFlow(false)
    private val userIDFlow = MutableStateFlow("")
    private val photoURLFlow = MutableStateFlow("")
    private val displayNameFlow = MutableStateFlow("")
    private val isPremiumFlow = MutableStateFlow(false)
    private val rescheduleUncompletedTasksFlow = MutableStateFlow(false)
    private val reminderCountFlow = MutableStateFlow(3)
    private val autoDeleteIndexFlow = MutableStateFlow(0)

    override val wasFirstLaunch: Flow<Boolean> = wasFirstLaunchFlow
    private val showHidePremiumAd = MutableStateFlow(false)

    override suspend fun setWasFirstLaunch(wasFirstLaunch: Boolean) {
        wasFirstLaunchFlow.value = wasFirstLaunch
    }

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

    override suspend fun setReminderCount(reminderCount: Int) {
        reminderCountFlow.value = reminderCount
    }

    override val reminderCount: Flow<Int> = reminderCountFlow

    override suspend fun setAutoDeleteIndex(autoDeleteIndex: Int) {
        autoDeleteIndexFlow.value = autoDeleteIndex
    }

    override val autoDeleteIndex: Flow<Int> = autoDeleteIndexFlow

    override suspend fun setHidePremiumAd(hidePremiumAd: Boolean) {
        showHidePremiumAd.value = hidePremiumAd
    }

    override val hidePremiumAd: Flow<Boolean> = showHidePremiumAd
}