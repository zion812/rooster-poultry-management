package com.rooster.farmerhome.data.repository

import com.rooster.farmerhome.data.source.ProductionMetricsRemoteDataSource
// TODO: Import local data source
import com.rooster.farmerhome.domain.model.ProductionSummary
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductionMetricsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductionMetricsRemoteDataSource
    // TODO: private val localDataSource: ProductionMetricsLocalDataSource
) : ProductionMetricsRepository {

    override fun getProductionSummary(farmId: String): Flow<ProductionSummary> {
        // TODO: Implement offline-first caching strategy as per AGENTS.md
        // For now, directly fetching from remote
        return remoteDataSource.getProductionSummary(farmId)
    }
}
