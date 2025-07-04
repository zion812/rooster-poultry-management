package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.dao.ProductionSummaryDao
import com.rooster.farmerhome.data.local.model.ProductionSummaryWithMetrics
import com.rooster.farmerhome.data.local.model.toEntity // For ProductionMetricItem.toEntity
import com.rooster.farmerhome.data.local.model.toSummaryEntity // For ProductionSummary.toSummaryEntity
import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductionSummaryLocalDataSourceImpl @Inject constructor(
    private val productionSummaryDao: ProductionSummaryDao
) : ProductionSummaryLocalDataSource {

    override fun getProductionSummaryWithMetrics(farmId: String): Flow<ProductionSummaryWithMetrics?> {
        return productionSummaryDao.getProductionSummaryWithMetrics(farmId)
    }

    override suspend fun insertProductionSummary(farmId: String, productionSummary: ProductionSummary) {
        val summaryEntity = productionSummary.toSummaryEntity(farmId)
        val metricEntities = productionSummary.metrics.map { it.toEntity(farmId) }
        productionSummaryDao.insertSummaryAndMetrics(summaryEntity, metricEntities)
    }

    override suspend fun deleteProductionSummary(farmId: String) {
        productionSummaryDao.deleteSummaryAndMetricsForFarm(farmId)
    }
}
