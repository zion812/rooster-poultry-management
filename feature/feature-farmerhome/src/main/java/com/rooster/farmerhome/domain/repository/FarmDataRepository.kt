package com.rooster.farmerhome.domain.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState

 main
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import kotlinx.coroutines.flow.Flow

interface FarmDataRepository {
    // Assuming a farmer might have multiple farms, but for home screen,
    // we might show one primary or selected farm.
    // Or, if a farmer only has one farm, this simplifies.
    // For now, let's assume fetching a specific farm's basic info.
 feature/dashboard-scaffolding-and-weather-api
    fun getFarmBasicInfo(farmId: String): Flow<DataState<FarmBasicInfo?>> // Updated return type

    fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfo?> // Nullable if farm not found or error
 main
}
