package com.rooster.farmerhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooster.farmerhome.domain.model.WeatherData
import com.rooster.farmerhome.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

package com.rooster.farmerhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import com.rooster.farmerhome.domain.model.ProductionSummary
import com.rooster.farmerhome.domain.model.WeatherData
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import com.rooster.farmerhome.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState // Import DataState
import javax.inject.Inject

data class FarmerHomeUiState(
    val weatherState: DataState<WeatherData?> = DataState.Loading(null),
    val healthAlertsState: DataState<List<FarmHealthAlert>> = DataState.Loading(null),
    // isLoadingAlerts and alertsError will be derived
    val productionSummaryState: DataState<ProductionSummary?> = DataState.Loading(null),
    val farmInfoState: DataState<FarmBasicInfo?> = DataState.Loading(null),
    val transientUserMessage: String? = null,
    val messageId: java.util.UUID? = null // To trigger recomposition for same message

import javax.inject.Inject

data class FarmerHomeUiState(
    val weatherData: WeatherData? = null,
    val isLoadingWeather: Boolean = false,
    val weatherError: String? = null,
    val farmHealthAlerts: List<FarmHealthAlert> = emptyList(),
    val isLoadingAlerts: Boolean = false,
    val alertsError: String? = null,
    val productionSummary: ProductionSummary? = null,
    val isLoadingProductionSummary: Boolean = false,
    val productionSummaryError: String? = null,
    val farmBasicInfo: FarmBasicInfo? = null,
    val isLoadingFarmInfo: Boolean = false,
    val farmInfoError: String? = null,
 main
)

@HiltViewModel
class FarmerHomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val farmHealthAlertRepository: FarmHealthAlertRepository,
    private val productionMetricsRepository: ProductionMetricsRepository,
    private val farmDataRepository: FarmDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerHomeUiState())
    val uiState: StateFlow<FarmerHomeUiState> = _uiState.asStateFlow()

    init {
        // Example: Fetch weather for a default location or a user's farm location
        // This location would ideally come from user preferences or farm data
        fetchWeatherForFarm("Krishna District Center")
        // Example: Fetch alerts for a specific farmId. This ID should be dynamic.
        fetchHealthAlerts("farm123")
        // Example: Fetch production summary for a specific farmId.
        fetchProductionSummary("farm123")
        // Example: Fetch basic farm info. The farmId should be determined dynamically.
        fetchFarmBasicInfo("farm123")
    }

    fun fetchWeatherForFarm(farmLocation: String) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherForFarm(farmLocation)
 feature/dashboard-scaffolding-and-weather-api
                // .onStart and .catch are now handled within the repository's DataState flow
                .collect { dataState ->
                    // The WeatherData from DataState could be null if error occurs before any data is loaded/cached.
                    // The WeatherData model itself also has an 'error' field for API-level errors within a successful fetch.
                    _uiState.value = _uiState.value.copy(weatherState = dataState)

                .onStart {
                    _uiState.value = _uiState.value.copy(isLoadingWeather = true, weatherError = null, alertsError = _uiState.value.alertsError)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        weatherError = "Failed to load weather: ${e.message}"
                    )
                }
                .collect { weatherData ->
                    if (weatherData.error != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoadingWeather = false,
                            weatherError = weatherData.error,
                            weatherData = null // Clear previous data on error
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoadingWeather = false,
                            weatherData = weatherData,
                            weatherError = null
                        )
                    }
 main
                }
        }
    }

    fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude)
 feature/dashboard-scaffolding-and-weather-api
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(weatherState = dataState)

                .onStart {
                    _uiState.value = _uiState.value.copy(isLoadingWeather = true, weatherError = null, alertsError = _uiState.value.alertsError)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingWeather = false,
                        weatherError = "Failed to load weather: ${e.message}"
                    )
                }
                .collect { weatherData ->
                     if (weatherData.error != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoadingWeather = false,
                            weatherError = weatherData.error,
                            weatherData = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoadingWeather = false,
                            weatherData = weatherData,
                            weatherError = null
                        )
                    }
 main
                }
        }
    }

    fun fetchHealthAlerts(farmId: String) {
        viewModelScope.launch {
            farmHealthAlertRepository.getHealthAlertsForFarm(farmId)
 feature/dashboard-scaffolding-and-weather-api
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(healthAlertsState = dataState)
                }
        }
    }

    fun markAlertAsRead(farmId: String, alertId: String) {
        viewModelScope.launch {
            if (farmId.isBlank()) {
                // Update UI state with a specific error for this action if needed,
                // or rely on healthAlertsState to eventually show an error if fetch fails.
                // For now, just logging, actual error display would be through healthAlertsState.
                // _uiState.value = _uiState.value.copy(healthAlertsState = DataState.Error(Exception("Farm ID missing for marking alert.")))
                _uiState.value = _uiState.value.copy(
                    transientUserMessage = "Cannot mark alert: Farm ID missing.",
                    messageId = java.util.UUID.randomUUID()
                )
                return@launch
            }
            val result = farmHealthAlertRepository.markAlertAsRead(farmId, alertId)
            // The list will refresh due to DAO changes triggering the Flow.
            // If the remote operation failed, the repository reverts the local change,
            // and the list re-emission will show the original unread state.
            // We can show a specific message for the action's success/failure.
            if (!result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    transientUserMessage = "Failed to sync 'Mark as Read': ${result.exceptionOrNull()?.message}",
                    messageId = java.util.UUID.randomUUID()
                )
            } else {
                // Optionally, show a success message, or just let the UI update.
                // _uiState.value = _uiState.value.copy(
                //     transientUserMessage = "Alert updated.",
                //     messageId = java.util.UUID.randomUUID()
                // )
            }
        }
    }

    fun clearTransientMessage() {
        _uiState.value = _uiState.value.copy(transientUserMessage = null, messageId = null)
    }

    fun fetchProductionSummary(farmId: String) {
        viewModelScope.launch {
            productionMetricsRepository.getProductionSummary(farmId)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(productionSummaryState = dataState)

                .onStart {
                    _uiState.value = _uiState.value.copy(isLoadingAlerts = true, alertsError = null, weatherError = _uiState.value.weatherError)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingAlerts = false,
                        alertsError = "Failed to load health alerts: ${e.message}"
                    )
                }
                .collect { alerts ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingAlerts = false,
                        farmHealthAlerts = alerts,
                        alertsError = null
                    )
                }
        }
    }

    fun markAlertAsRead(alertId: String) {
        viewModelScope.launch {
            val result = farmHealthAlertRepository.markAlertAsRead(alertId)
            if (result.isSuccess) {
                // Refresh alerts or update the specific alert in the list
                val currentFarmId = _uiState.value.farmHealthAlerts.firstOrNull()?.farmId ?: "farm123" // Or get from a better source
                fetchHealthAlerts(currentFarmId) // Simplest way to refresh
            } else {
                // Handle error, e.g., show a toast
                _uiState.value = _uiState.value.copy(alertsError = "Failed to mark alert as read: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun fetchProductionSummary(farmId: String) {
        viewModelScope.launch {
            productionMetricsRepository.getProductionSummary(farmId)
                .onStart {
                    _uiState.value = _uiState.value.copy(isLoadingProductionSummary = true, productionSummaryError = null)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingProductionSummary = false,
                        productionSummaryError = "Failed to load production summary: ${e.message}"
                    )
                }
                .collect { summary ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingProductionSummary = false,
                        productionSummary = summary,
                        productionSummaryError = null
                    )
 main
                }
        }
    }

    fun fetchFarmBasicInfo(farmId: String) {
        viewModelScope.launch {
            farmDataRepository.getFarmBasicInfo(farmId)
 feature/dashboard-scaffolding-and-weather-api
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(farmInfoState = dataState)

                .onStart {
                    _uiState.value = _uiState.value.copy(isLoadingFarmInfo = true, farmInfoError = null)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingFarmInfo = false,
                        farmInfoError = "Failed to load farm info: ${e.message}"
                    )
                }
                .collect { farmInfo ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingFarmInfo = false,
                        farmBasicInfo = farmInfo,
                        farmInfoError = if (farmInfo == null) "Farm data not found." else null
                    )
 main
                }
        }
    }

    // TODO: Add functions for other farmer home screen actions
}
