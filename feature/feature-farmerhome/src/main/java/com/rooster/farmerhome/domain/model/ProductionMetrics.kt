package com.rooster.farmerhome.domain.model

// Represents a single type of production metric
data class ProductionMetricItem(
    val name: String, // e.g., "Total Eggs Laid", "Average Egg Weight", "Feed Conversion Ratio"
    val value: String, // Current value, formatted as a string
    val unit: String, // e.g., "eggs", "grams", "%"
    val trend: MetricTrend? = null, // Optional: to show if the metric is trending up, down, or stable
    val period: String? = null // Optional: e.g., "Last 7 Days", "Current Flock"
)

enum class MetricTrend {
    UP, DOWN, STABLE
}

// Represents an overall summary of production
data class ProductionSummary(
    val totalFlocks: Int,
    val activeBirds: Int,
    val overallEggProductionToday: Int,
    val weeklyMortalityRate: Double, // As a percentage
    val metrics: List<ProductionMetricItem> = emptyList() // Detailed list of metrics
)
