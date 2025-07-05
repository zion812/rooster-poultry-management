package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.flow.Flow

interface ProductionMetricsRemoteDataSource {
    fun getProductionSummary(farmId: String): Flow<ProductionSummary>
    // This would interact with an API wrapping the Python backend (MarketService, FarmRepository)
}
