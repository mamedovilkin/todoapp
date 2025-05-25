package io.github.mamedovilkin.database.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setShowStatistics(showStatistics: Boolean)
    val showStatistics: Flow<Boolean>
}