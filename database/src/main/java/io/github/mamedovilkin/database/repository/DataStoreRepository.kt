package io.github.mamedovilkin.database.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setWasFirstLaunch(wasFirstLaunch: Boolean)
    val wasFirstLaunch: Flow<Boolean>

    suspend fun setShowStatistics(showStatistics: Boolean)
    val showStatistics: Flow<Boolean>

    suspend fun setUserID(userID: String)
    val userID: Flow<String>

    suspend fun setPhotoURL(photoURL: String)
    val photoURL: Flow<String>

    suspend fun setDisplayName(displayName: String)
    val displayName: Flow<String>

    suspend fun setPremium(isPremium: Boolean)
    val isPremium: Flow<Boolean>

    suspend fun setRescheduleUncompletedTasks(rescheduleUncompletedTasks: Boolean)
    val rescheduleUncompletedTasks: Flow<Boolean>

    suspend fun setReminderCount(reminderCount: Int)
    val reminderCount: Flow<Int>

    suspend fun setAutoDeleteIndex(autoDeleteIndex: Int)
    val autoDeleteIndex: Flow<Int>

    suspend fun setHidePremiumAd(hidePremiumAd: Boolean)
    val hidePremiumAd: Flow<Boolean>
}