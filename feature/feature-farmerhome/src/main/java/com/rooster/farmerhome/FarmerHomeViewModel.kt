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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.rooster.core.common.util.DataState // Corrected Import
import javax.inject.Inject

data class FarmerHomeUiState(
    val weatherState: DataState<WeatherData?> = DataState.Loading(null),
    val healthAlertsState: DataState<List<FarmHealthAlert>> = DataState.Loading(null),
    val productionSummaryState: DataState<ProductionSummary?> = DataState.Loading(null),
    val farmInfoState: DataState<FarmBasicInfo?> = DataState.Loading(null),
    val transientUserMessage: String? = null,
    val messageId: java.util.UUID? = null,
    val isRefreshing: Boolean = false, // Added for pull-to-refresh
    val isOffline: Boolean = false // Added for network status
)

@HiltViewModel
class FarmerHomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val farmHealthAlertRepository: FarmHealthAlertRepository,
    private val productionMetricsRepository: ProductionMetricsRepository,
    private val farmDataRepository: FarmDataRepository,
    private val connectivityRepository: com.example.rooster.core.common.connectivity.ConnectivityRepository // Fully qualified name
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerHomeUiState())
    val uiState: StateFlow<FarmerHomeUiState> = _uiState.asStateFlow()

    init {
        fetchAllData()
        observeNetworkStatus() // Call new function
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityRepository.observeNetworkStatus().collect { status ->
                _uiState.value = _uiState.value.copy(
                    isOffline = status != com.example.rooster.core.common.connectivity.NetworkStatus.Available
                )
            }
        }
    }

    private fun fetchAllData(forceRefresh: Boolean = false) {
        // Determine farmId and location safely, potentially from a user session or saved preferences
        // For now, using defaults if farmInfo isn't loaded yet.
        val currentFarmInfo = _uiState.value.farmInfoState.getUnderlyingData()
        val farmId = currentFarmInfo?.farmId ?: "farm123" // Default or placeholder
        val farmLocation = currentFarmInfo?.location ?: "Krishna District Center" // Default or placeholder

        // Respect isOffline flag for actual network calls if needed,
        // but DataState pattern should handle emitting cached data.
        // The 'forceRefresh' could bypass staleness checks if true.

        fetchWeatherForFarm(farmLocation) // Repositories handle DataState
        fetchHealthAlerts(farmId)
        fetchProductionSummary(farmId)
        fetchFarmBasicInfo(farmId)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            fetchAllData()
            // Ideally, wait for all fetches to complete or use a counter
            // For simplicity now, just setting isRefreshing to false after launching fetches.
            // Proper implementation would involve tracking completion of all launched coroutines.
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }


    fun fetchWeatherForFarm(farmLocation: String) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherForFarm(farmLocation)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(weatherState = dataState)
                }
        }
    }

    fun fetchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(weatherState = dataState)
                }
        }
    }

    fun fetchHealthAlerts(farmId: String) {
        viewModelScope.launch {
            farmHealthAlertRepository.getHealthAlertsForFarm(farmId)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(healthAlertsState = dataState)
                }
        }
    }

    fun markAlertAsRead(farmId: String, alertId: String) {
        viewModelScope.launch {
            if (farmId.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    transientUserMessage = "Cannot mark alert: Farm ID missing.",
                    messageId = java.util.UUID.randomUUID()
                )
                return@launch
            }
            val result = farmHealthAlertRepository.markAlertAsRead(farmId, alertId)
            if (!result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    transientUserMessage = "Failed to sync 'Mark as Read': ${result.exceptionOrNull()?.message}",
                    messageId = java.util.UUID.randomUUID()
                )
            }
            // Data will refresh via the Flow from the local data source
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
                }
        }
    }

    fun fetchFarmBasicInfo(farmId: String) {
        viewModelScope.launch {
            farmDataRepository.getFarmBasicInfo(farmId)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(farmInfoState = dataState)
                }
        }
    }
}
