package com.rooster.farmerhome.domain.model

enum class MetricTrend {
    UP, STABLE, DOWN
}

data class ProductionMetricItem(
    val name: String,
    val value: String,
    val unit: String,
    val trend: MetricTrend?,
    val period: String?
)

data class ProductionSummary(
    val totalFlocks: Int,
    val activeBirds: Int,
    val overallEggProductionToday: Int,
    val weeklyMortalityRate: Double,
    val metrics: List<ProductionMetricItem>
)
