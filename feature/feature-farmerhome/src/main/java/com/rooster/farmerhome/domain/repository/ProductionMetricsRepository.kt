package com.rooster.farmerhome.domain.repository

import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.flow.Flow

interface ProductionMetricsRepository {
    fun getProductionSummary(farmId: String): Flow<DataState<ProductionSummary?>> // Updated return type
    // May add methods for more specific metrics or time ranges later
}
