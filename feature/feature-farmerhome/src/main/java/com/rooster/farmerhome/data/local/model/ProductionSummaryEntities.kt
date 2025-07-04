package com.rooster.farmerhome.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.rooster.farmerhome.domain.model.MetricTrend

@Entity(tableName = "production_summary")
data class ProductionSummaryEntity(
    @PrimaryKey
    val farmId: String, // Assuming one summary per farm, so farmId is PK
    val totalFlocks: Int,
    val activeBirds: Int,
    val overallEggProductionToday: Int,
    val weeklyMortalityRate: Double,
    val timestamp: Long // For cache staleness
)

@Entity(
    tableName = "production_metric_item",
    foreignKeys = [ForeignKey(
        entity = ProductionSummaryEntity::class,
        parentColumns = ["farmId"],
        childColumns = ["summaryFarmId"],
        onDelete = ForeignKey.CASCADE // If summary is deleted, its metrics are deleted
    )],
    primaryKeys = ["summaryFarmId", "name"] // Composite primary key to ensure unique metric name per summary
)
data class ProductionMetricItemEntity(
    val summaryFarmId: String, // Foreign key to ProductionSummaryEntity
    val name: String,
    val value: String,
    val unit: String,
    @ColumnInfo(name = "metric_trend") // Avoid conflict with potential keywords
    val trend: MetricTrend?,
    val period: String?,
)

// Wrapper for querying ProductionSummary with its list of ProductionMetricItems
data class ProductionSummaryWithMetrics(
    @Embedded val summary: ProductionSummaryEntity,
    @Relation(
        parentColumn = "farmId",
        entityColumn = "summaryFarmId"
    )
    val metrics: List<ProductionMetricItemEntity>
)

// Mapper functions
fun ProductionSummaryWithMetrics.toDomain(): com.rooster.farmerhome.domain.model.ProductionSummary {
    return com.rooster.farmerhome.domain.model.ProductionSummary(
        totalFlocks = summary.totalFlocks,
        activeBirds = summary.activeBirds,
        overallEggProductionToday = summary.overallEggProductionToday,
        weeklyMortalityRate = summary.weeklyMortalityRate,
        metrics = metrics.map { it.toDomain() }
    )
}

fun ProductionMetricItemEntity.toDomain(): com.rooster.farmerhome.domain.model.ProductionMetricItem {
    return com.rooster.farmerhome.domain.model.ProductionMetricItem(
        name = name,
        value = value,
        unit = unit,
        trend = trend,
        period = period
    )
}

// For converting domain to entities (summary and its items separately for insertion)
fun com.rooster.farmerhome.domain.model.ProductionSummary.toSummaryEntity(currentFarmId: String): ProductionSummaryEntity {
    return ProductionSummaryEntity(
        farmId = currentFarmId, // Use the passed farmId
        totalFlocks = totalFlocks,
        activeBirds = activeBirds,
        overallEggProductionToday = overallEggProductionToday,
        weeklyMortalityRate = weeklyMortalityRate,
        timestamp = System.currentTimeMillis()
    )
}

fun com.rooster.farmerhome.domain.model.ProductionMetricItem.toEntity(summaryOwnerFarmId: String): ProductionMetricItemEntity {
    return ProductionMetricItemEntity(
        summaryFarmId = summaryOwnerFarmId,
        name = name,
        value = value,
        unit = unit,
        trend = trend,
        period = period
    )
}
