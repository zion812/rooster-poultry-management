package com.rooster.farmerhome.data.repository

import com.example.rooster.core.common.util.DataState
import com.example.rooster.core.network.api.FarmDataApiService
import com.example.rooster.core.network.api.WeatherApiService
import com.rooster.farmerhome.domain.model.*
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import com.rooster.farmerhome.domain.repository.WeatherRepository
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation that integrates with Python backend APIs
 * Handles data conversion between API DTOs and domain models
 */
@Singleton
class FarmApiRepositoryImpl @Inject constructor(
    private val farmDataApiService: FarmDataApiService,
    private val weatherApiService: WeatherApiService
) : FarmDataRepository, WeatherRepository, FarmHealthAlertRepository, ProductionMetricsRepository {

    // FarmDataRepository implementation
    override fun getFarmBasicInfo(farmId: String): Flow<DataState<FarmBasicInfo?>> = flow {
        emit(DataState.Loading(null))
        try {
            val response = farmDataApiService.getFarmBasicInfo(farmId)
            if (response.isSuccessful) {
                val apiData = response.body()
                val domainData = apiData?.let { api ->
                    FarmBasicInfo(
                        farmId = api.farmId,
                        farmName = api.farmName,
                        location = api.location,
                        ownerName = api.ownerName,
                        activeFlockCount = api.activeFlockCount,
                        totalCapacity = api.totalCapacity,
                        lastHealthCheckDate = api.lastHealthCheckDate
                    )
                }
                emit(DataState.Success(domainData))
            } else {
                emit(DataState.Error(Exception("Failed to fetch farm info: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    // WeatherRepository implementation
    override fun getCurrentWeather(latitude: Double, longitude: Double): Flow<DataState<WeatherData?>> = flow {
        emit(DataState.Loading(null))
        try {
            val response = weatherApiService.getCurrentWeatherByCoords(latitude, longitude)
            if (response.isSuccessful) {
                val apiData = response.body()
                val domainData = apiData?.let { api ->
                    WeatherData(
                        temperature = api.temperature,
                        humidity = api.humidity,
                        precipitation = api.precipitation,
                        windSpeed = api.windSpeed,
                        description = api.description,
                        location = api.location
                    )
                }
                emit(DataState.Success(domainData))
            } else {
                emit(DataState.Error(Exception("Failed to fetch weather: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override fun getCurrentWeatherForFarm(farmLocation: String): Flow<DataState<WeatherData?>> = flow {
        emit(DataState.Loading(null))
        try {
            val response = weatherApiService.getCurrentWeatherByLocation(farmLocation)
            if (response.isSuccessful) {
                val apiData = response.body()
                val domainData = apiData?.let { api ->
                    WeatherData(
                        temperature = api.temperature,
                        humidity = api.humidity,
                        precipitation = api.precipitation,
                        windSpeed = api.windSpeed,
                        description = api.description,
                        location = api.location
                    )
                }
                emit(DataState.Success(domainData))
            } else {
                emit(DataState.Error(Exception("Failed to fetch weather: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    // FarmHealthAlertRepository implementation
    override fun getHealthAlertsForFarm(farmId: String): Flow<DataState<List<FarmHealthAlert>>> = flow {
        emit(DataState.Loading(emptyList()))
        try {
            val response = farmDataApiService.getFarmHealthAlerts(farmId)
            if (response.isSuccessful) {
                val apiData = response.body() ?: emptyList()
                val domainData = apiData.map { api ->
                    FarmHealthAlert(
                        id = api.id,
                        flockId = api.flockId,
                        farmId = api.farmId,
                        title = api.title,
                        description = api.description,
                        severity = parseAlertSeverity(api.severity),
                        alertDate = Date(api.timestamp),
                        recommendedAction = api.recommendedAction,
                        isRead = api.isRead
                    )
                }
                emit(DataState.Success(domainData))
            } else {
                emit(DataState.Error(Exception("Failed to fetch health alerts: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit> {
        return try {
            val response = farmDataApiService.markAlertAsRead(farmId, alertId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark alert as read: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProductionMetricsRepository implementation
    override fun getProductionSummary(farmId: String): Flow<DataState<ProductionSummary?>> = flow {
        emit(DataState.Loading(null))
        try {
            val response = farmDataApiService.getProductionSummary(farmId)
            if (response.isSuccessful) {
                val apiData = response.body()
                val domainData = apiData?.let { api ->
                    ProductionSummary(
                        totalFlocks = api.totalFlocks,
                        activeBirds = api.activeBirds,
                        overallEggProductionToday = api.overallEggProductionToday,
                        weeklyMortalityRate = api.weeklyMortalityRate,
                        metrics = api.metrics.map { metric ->
                            ProductionMetricItem(
                                name = metric.name,
                                value = metric.value,
                                unit = metric.unit,
                                trend = parseMetricTrend(metric.trend),
                                period = metric.period
                            )
                        }
                    )
                }
                emit(DataState.Success(domainData))
            } else {
                emit(DataState.Error(Exception("Failed to fetch production summary: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    // Helper functions for enum parsing
    private fun parseAlertSeverity(severity: String): AlertSeverity {
        return when (severity.uppercase()) {
            "LOW" -> AlertSeverity.LOW
            "MEDIUM" -> AlertSeverity.MEDIUM
            "HIGH" -> AlertSeverity.HIGH
            "CRITICAL" -> AlertSeverity.CRITICAL
            else -> AlertSeverity.LOW
        }
    }

    private fun parseMetricTrend(trend: String?): MetricTrend? {
        return when (trend?.uppercase()) {
            "UP" -> MetricTrend.UP
            "DOWN" -> MetricTrend.DOWN
            "STABLE" -> MetricTrend.STABLE
            else -> null
        }
    }
}