package com.rooster.farmerhome.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rooster.farmerhome.data.local.model.ProductionMetricItemEntity
import com.rooster.farmerhome.data.local.model.ProductionSummaryEntity
import com.rooster.farmerhome.data.local.model.ProductionSummaryWithMetrics
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionSummaryDao {

    @Transaction // Ensures this is run as a single transaction
    @Query("SELECT * FROM production_summary WHERE farmId = :farmId")
    fun getProductionSummaryWithMetrics(farmId: String): Flow<ProductionSummaryWithMetrics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductionSummary(summary: ProductionSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetricItems(metricItems: List<ProductionMetricItemEntity>)

    @Transaction
    suspend fun insertSummaryAndMetrics(summary: ProductionSummaryEntity, metrics: List<ProductionMetricItemEntity>) {
        insertProductionSummary(summary)
        // Delete old metrics for this summary before inserting new ones to avoid stale data
        // if metrics can change for a summary (e.g. different items over time)
        deleteMetricItemsForFarm(summary.farmId)
        insertMetricItems(metrics)
    }

    @Query("DELETE FROM production_summary WHERE farmId = :farmId")
    suspend fun deleteProductionSummary(farmId: String)

    @Query("DELETE FROM production_metric_item WHERE summaryFarmId = :farmId")
    suspend fun deleteMetricItemsForFarm(farmId: String)

    @Transaction
    suspend fun deleteSummaryAndMetricsForFarm(farmId: String) {
        deleteMetricItemsForFarm(farmId)
        deleteProductionSummary(farmId)
    }
}
