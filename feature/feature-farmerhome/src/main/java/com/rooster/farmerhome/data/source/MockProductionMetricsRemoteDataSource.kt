package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.MetricTrend
import com.rooster.farmerhome.domain.model.ProductionMetricItem
import com.rooster.farmerhome.domain.model.ProductionSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MockProductionMetricsRemoteDataSource @Inject constructor() : ProductionMetricsRemoteDataSource {
    override fun getProductionSummary(farmId: String): Flow<ProductionSummary> = flow {
        delay(800) // Simulate network delay

        val summary = ProductionSummary(
            totalFlocks = Random.nextInt(2, 6),
            activeBirds = Random.nextInt(500, 2000),
            overallEggProductionToday = Random.nextInt(300, 1500),
            weeklyMortalityRate = Random.nextDouble(0.1, 2.5),
            metrics = listOf(
                ProductionMetricItem(
                    name = "Total Eggs (Last 7 Days)",
                    value = Random.nextInt(2000, 10000).toString(),
                    unit = "eggs",
                    trend = MetricTrend.values().random(),
                    period = "Last 7 Days"
                ),
                ProductionMetricItem(
                    name = "Avg. Egg Weight",
                    value = String.format("%.1f", Random.nextDouble(50.0, 65.0)),
                    unit = "g",
                    trend = MetricTrend.STABLE,
                    period = "Last Batch"
                ),
                ProductionMetricItem(
                    name = "Feed Conversion Ratio (FCR)",
                    value = String.format("%.2f", Random.nextDouble(1.8, 2.5)),
                    unit = "", // Ratio, no unit
                    trend = if (Random.nextBoolean()) MetricTrend.UP else MetricTrend.DOWN, // For FCR, lower is better, so UP is bad.
                    period = "Current Broiler Flock"
                ),
                ProductionMetricItem(
                    name = "Water Consumption (Today)",
                    value = Random.nextInt(100, 500).toString(),
                    unit = "liters",
                    period = "Today"
                )
            )
        )
        emit(summary)
    }
}
