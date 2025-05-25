package io.github.mamedovilkin.todoapp.mock

import io.github.mamedovilkin.database.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataStoreRepository : DataStoreRepository {

    private val showStatisticsFlow = MutableStateFlow(false)

    override suspend fun setShowStatistics(showStatistics: Boolean) {
        showStatisticsFlow.value = showStatistics
    }

    override val showStatistics: Flow<Boolean>
        get() = showStatisticsFlow
}