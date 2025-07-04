package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import kotlinx.coroutines.flow.Flow

interface FarmDataRepository {
    // Assuming a farmer might have multiple farms, but for home screen,
    // we might show one primary or selected farm.
    // Or, if a farmer only has one farm, this simplifies.
    // For now, let's assume fetching a specific farm's basic info.
    fun getFarmBasicInfo(farmId: String): Flow<DataState<FarmBasicInfo?>> // Updated return type
}
