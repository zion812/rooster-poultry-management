package com.rooster.farmerhome.domain.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState

 main
import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.flow.Flow

interface ProductionMetricsRepository {
 feature/dashboard-scaffolding-and-weather-api
    fun getProductionSummary(farmId: String): Flow<DataState<ProductionSummary?>> // Updated return type

    fun getProductionSummary(farmId: String): Flow<ProductionSummary>
 main
    // May add methods for more specific metrics or time ranges later
}
