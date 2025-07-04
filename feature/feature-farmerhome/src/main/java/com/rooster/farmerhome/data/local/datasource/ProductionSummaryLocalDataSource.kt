package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.model.ProductionSummaryWithMetrics // Using the wrapper for queries
import com.rooster.farmerhome.domain.model.ProductionSummary // Domain model for saving
import kotlinx.coroutines.flow.Flow

interface ProductionSummaryLocalDataSource {
    fun getProductionSummaryWithMetrics(farmId: String): Flow<ProductionSummaryWithMetrics?>
    suspend fun insertProductionSummary(farmId: String, productionSummary: ProductionSummary) // Takes domain model
    suspend fun deleteProductionSummary(farmId: String)
}
