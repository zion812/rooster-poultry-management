package com.example.rooster.feature.iot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.iot.data.model.AlertInfo
import com.example.rooster.feature.iot.data.model.DeviceInfo
import com.example.rooster.feature.iot.data.model.FeedLevelReading
import com.example.rooster.feature.iot.data.model.HumidityReading
import com.example.rooster.feature.iot.data.model.LightLevelReading
import com.example.rooster.feature.iot.data.model.TemperatureReading
import com.example.rooster.feature.iot.data.model.WaterConsumptionReading
import com.example.rooster.feature.iot.data.repository.IoTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.example.rooster.feature.iot.utils.CsvExporter
import java.time.LocalDate
import java.time.ZoneId

// Separate state for historical data to keep IoTDashboardUiState cleaner
// and manage its loading/error states independently.
/**
 * UI state for historical data display.
 *
 * @property temperatureReadings List of historical temperature readings.
 * @property humidityReadings List of historical humidity readings.
 * @property feedLevelReadings List of historical feed level readings.
 * @property waterConsumptionReadings List of historical water consumption readings.
 * @property lightLevelReadings List of historical light level readings.
 * @property isLoading True if historical data is currently being fetched.
 * @property errorMessage Optional error message if fetching failed.
 * @property lastFetchedDeviceId The device ID for which data was last fetched.
 * @property lastFetchedStartTime The start time of the last fetched data range.
 * @property lastFetchedEndTime The end time of the last fetched data range.
 */
data class HistoricalDataState(
    val temperatureReadings: List<TemperatureReading> = emptyList(),
    val humidityReadings: List<HumidityReading> = emptyList(),
    val feedLevelReadings: List<FeedLevelReading> = emptyList(),
    val waterConsumptionReadings: List<WaterConsumptionReading> = emptyList(),
    val lightLevelReadings: List<LightLevelReading> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastFetchedDeviceId: String? = null,
    val lastFetchedStartTime: Long? = null,
    val lastFetchedEndTime: Long? = null
)

/**
 * UI state for advanced analytics display.
 *
 * @property productionForecasts List of production forecasts.
 * @property performancePredictions List of performance predictions.
 * @property feedRecommendations List of feed optimization recommendations.
 * @property healthTrends List of health trends.
 * @property isLoading True if analytics data is currently being fetched.
 * @property errorMessage Optional error message if fetching failed.
 * @property lastFetchedFarmId The farm ID for which analytics were last fetched.
 * @property lastFetchedFlockId The flock ID for which analytics were last fetched.
 */
data class AdvancedAnalyticsUiState(
    val productionForecasts: List<ProductionForecast> = emptyList(),
    val performancePredictions: List<PerformancePrediction> = emptyList(),
    val feedRecommendations: List<FeedOptimizationRecommendation> = emptyList(),
    val healthTrends: List<HealthTrend> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastFetchedFarmId: String? = null,
    val lastFetchedFlockId: String? = null
)

/**
 * UI state for smart automation controls.
 *
 * @property feedingSchedules List of current feeding schedules.
 * @property climateSettings List of current climate settings.
 * @property maintenanceReminders List of maintenance reminders.
 * @property isLoading True if automation settings are currently being fetched.
 * @property errorMessage Optional error message if fetching failed.
 * @property lastFetchedFarmId The farm ID for which settings were last fetched.
 * @property lastFetchedFlockId The flock ID for which settings were last fetched.
 * @property lastFetchedDeviceIdForReminders The device ID for which reminders were last fetched.
 */
data class SmartAutomationControlsUiState(
    val feedingSchedules: List<FeedingSchedule> = emptyList(),
    val climateSettings: List<ClimateSettings> = emptyList(),
    val maintenanceReminders: List<MaintenanceReminder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastFetchedFarmId: String? = null,
    val lastFetchedFlockId: String? = null,
    val lastFetchedDeviceIdForReminders: String? = null
)

/**
 * Overall UI state for the IoT Dashboard screen.
 *
 * @property devices List of available IoT devices.
 * @property selectedDeviceId ID of the currently selected device for real-time data.
 * @property currentFarmIdForAnalytics Current farm ID context for analytics and some automation settings.
 * @property currentFlockIdForAnalytics Current flock ID context for analytics and some automation settings.
 * @property currentShedIdForAnalytics Current shed ID context for climate settings.
 * @property activeAlerts List of currently active (unacknowledged) alerts.
 * @property isLoading True if the initial list of devices is loading.
 * @property isLoadingDeviceData True if real-time data for the selected device is loading.
 * @property errorMessage Optional general error message for the screen.
 */
data class IoTDashboardUiState(
    val devices: List<DeviceInfo> = emptyList(),
    val selectedDeviceId: String? = null,
    val currentFarmIdForAnalytics: String? = "default-farm-id",
    val currentFlockIdForAnalytics: String? = null,
    val currentShedIdForAnalytics: String? = "default-shed-id",
    val activeAlerts: List<AlertInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingDeviceData: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Defines thresholds for generating automated alerts.
 */
object AlertThresholds {
    const val MAX_TEMP = 35.0 // Celsius
    const val MIN_TEMP = 15.0 // Celsius
    const val MAX_HUMIDITY = 75.0 // Percent
    const val MIN_HUMIDITY = 30.0 // Percent
    const val MIN_FEED_LEVEL = 10.0 // Percent
}

/**
 * ViewModel for the IoT Dashboard screen.
 * Manages state for device data, sensor readings, alerts, analytics, and automation controls.
 *
 * @param repository The [IoTRepository] for data operations.
 */
@HiltViewModel
class IoTViewModel @Inject constructor(
    private val repository: IoTRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _isLoadingDeviceData = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _selectedDeviceId = MutableStateFlow<String?>(null)

    private val _historicalDataState = MutableStateFlow(HistoricalDataState())
    val historicalDataState: StateFlow<HistoricalDataState> = _historicalDataState.asStateFlow()

    private val _advancedAnalyticsState = MutableStateFlow(AdvancedAnalyticsUiState())
    val advancedAnalyticsState: StateFlow<AdvancedAnalyticsUiState> = _advancedAnalyticsState.asStateFlow()

    private val _smartAutomationState = MutableStateFlow(SmartAutomationControlsUiState())
    val smartAutomationState: StateFlow<SmartAutomationControlsUiState> = _smartAutomationState.asStateFlow()

    private val _currentFarmId = MutableStateFlow<String?>("default-farm-id")
    private val _currentFlockId = MutableStateFlow<String?>(null)
    private val _currentShedId = MutableStateFlow<String?>("default-shed-id")


    /**
     * Flow of available IoT devices.
     */
    val devices: StateFlow<Result<List<DeviceInfo>>> = repository.getAllDeviceInfos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

    /**
     * Combined UI state for the main dashboard elements.
     */
    val uiState: StateFlow<IoTDashboardUiState> = combine(
        devices,
        _selectedDeviceId,
        _currentFarmId,
        _currentFlockId,
        _currentShedId,
        repository.getUnacknowledgedAlerts().map { if (it is Result.Success) it.data else emptyList() },
        _isLoading,
        _isLoadingDeviceData,
        _errorMessage
    ) { devicesResult, selectedDevId, farmId, flockId, shedId, currentAlerts, generalLoading, deviceDataLoading, errorMsg ->

        val currentDevices = if (devicesResult is Result.Success) devicesResult.data else emptyList()
        if (selectedDevId == null && currentDevices.isNotEmpty() && _selectedDeviceId.value == null) {
            _selectedDeviceId.value = currentDevices.first().deviceId
        }

        IoTDashboardUiState(
            devices = currentDevices,
            selectedDeviceId = _selectedDeviceId.value,
            currentFarmIdForAnalytics = farmId,
            currentFlockIdForAnalytics = flockId,
            currentShedIdForAnalytics = shedId,
            activeAlerts = currentAlerts,
            isLoading = generalLoading || devicesResult is Result.Loading,
            isLoadingDeviceData = deviceDataLoading,
            errorMessage = errorMsg ?: (devicesResult as? Result.Error)?.exception?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IoTDashboardUiState(isLoading = true)
    )

    /** Flow of real-time temperature readings for the selected device. */
    val temperatureReadings: StateFlow<List<TemperatureReading>> = _selectedDeviceId.flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else repository.getTemperatureReadings(deviceId)
            .onEach { result -> if(result is Result.Loading) _isLoadingDeviceData.value = true else if (result is Result.Success || result is Result.Error) _isLoadingDeviceData.value = false }
            .map { if (it is Result.Success) it.data else emptyList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of real-time humidity readings for the selected device. */
    val humidityReadings: StateFlow<List<HumidityReading>> = _selectedDeviceId.flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else repository.getHumidityReadings(deviceId)
            .onEach { result -> if(result is Result.Loading) _isLoadingDeviceData.value = true else if (result is Result.Success || result is Result.Error) _isLoadingDeviceData.value = false }
            .map { if (it is Result.Success) it.data else emptyList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of real-time feed level readings for the selected device. */
    val feedLevelReadings: StateFlow<List<FeedLevelReading>> = _selectedDeviceId.flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else repository.getFeedLevelReadings(deviceId)
            .onEach { result -> if(result is Result.Loading) _isLoadingDeviceData.value = true else if (result is Result.Success || result is Result.Error) _isLoadingDeviceData.value = false }
            .map { if (it is Result.Success) it.data else emptyList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of real-time water consumption readings for the selected device. */
    val waterConsumptionReadings: StateFlow<List<WaterConsumptionReading>> = _selectedDeviceId.flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else repository.getWaterConsumptionReadings(deviceId)
            .onEach { result -> if(result is Result.Loading) _isLoadingDeviceData.value = true else if (result is Result.Success || result is Result.Error) _isLoadingDeviceData.value = false }
            .map { if (it is Result.Success) it.data else emptyList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Flow of real-time light level readings for the selected device. */
    val lightLevelReadings: StateFlow<List<LightLevelReading>> = _selectedDeviceId.flatMapLatest { deviceId ->
        if (deviceId == null) flowOf(emptyList())
        else repository.getLightLevelReadings(deviceId)
            .onEach { result -> if(result is Result.Loading) _isLoadingDeviceData.value = true else if (result is Result.Success || result is Result.Error) _isLoadingDeviceData.value = false }
            .map { if (it is Result.Success) it.data else emptyList() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        viewModelScope.launch {
            devices.filter { it is Result.Success && it.data.isNotEmpty() }
                .collect { devicesResult ->
                    if (_selectedDeviceId.value == null) {
                        val deviceList = (devicesResult as Result.Success).data
                        selectDevice(deviceList.first().deviceId)
                    }
                }
        }
        setupAlertMonitoring()
        fetchAdvancedAnalyticsData(_currentFarmId.value, _currentFlockId.value)
        fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, _selectedDeviceId.value)
    }

    /**
     * Selects a device for displaying its real-time data and device-specific automation settings.
     * @param deviceId The ID of the device to select.
     */
    fun selectDevice(deviceId: String) {
        if (_selectedDeviceId.value == deviceId && !_isLoadingDeviceData.value) return
        _selectedDeviceId.value = deviceId
        refreshDeviceData(deviceId)
        fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, deviceId)
    }

    /**
     * Refreshes real-time sensor data for the given device.
     * @param deviceId The ID of the device whose data needs refreshing.
     */
    fun refreshDeviceData(deviceId: String) {
        _isLoadingDeviceData.value = true
        viewModelScope.launch {
            repository.refreshTemperatureReadings(deviceId)
            repository.refreshHumidityReadings(deviceId)
            repository.refreshFeedLevelReadings(deviceId)
            repository.refreshWaterConsumptionReadings(deviceId)
            repository.refreshLightLevelReadings(deviceId)
        }
    }

    /**
     * Fetches historical sensor data for a given device and time range.
     * Updates the [historicalDataState].
     * @param deviceId The ID of the device.
     * @param startTime The start of the time range (epoch milliseconds).
     * @param endTime The end of the time range (epoch milliseconds).
     */
    fun fetchHistoricalData(deviceId: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            _historicalDataState.value = HistoricalDataState(isLoading = true, lastFetchedDeviceId = deviceId, lastFetchedStartTime = startTime, lastFetchedEndTime = endTime)
            try {
                val tempDataResult = repository.getTemperatureReadingsInRange(deviceId, startTime, endTime).first()
                val humidityDataResult = repository.getHumidityReadingsInRange(deviceId, startTime, endTime).first()
                val feedDataResult = repository.getFeedLevelReadingsInRange(deviceId, startTime, endTime).first()
                val waterDataResult = repository.getWaterConsumptionReadingsInRange(deviceId, startTime, endTime).first()
                val lightDataResult = repository.getLightLevelReadingsInRange(deviceId, startTime, endTime).first()

                val errors = listOf(tempDataResult, humidityDataResult, feedDataResult, waterDataResult, lightDataResult)
                    .filterIsInstance<Result.Error<*>>()

                if (errors.isNotEmpty()) {
                    _historicalDataState.value = _historicalDataState.value.copy(
                        isLoading = false,
                        errorMessage = errors.joinToString("\n") { it.exception.message ?: "Unknown error" }
                    )
                } else {
                     _historicalDataState.value = _historicalDataState.value.copy(
                        isLoading = false,
                        temperatureReadings = (tempDataResult as? Result.Success)?.data ?: emptyList(),
                        humidityReadings = (humidityDataResult as? Result.Success)?.data ?: emptyList(),
                        feedLevelReadings = (feedDataResult as? Result.Success)?.data ?: emptyList(),
                        waterConsumptionReadings = (waterDataResult as? Result.Success)?.data ?: emptyList(),
                        lightLevelReadings = (lightDataResult as? Result.Success)?.data ?: emptyList(),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _historicalDataState.value = _historicalDataState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to fetch historical data"
                )
            }
        }
    }

    /**
     * Fetches advanced analytics data (forecasts, predictions, recommendations, trends).
     * Updates the [advancedAnalyticsState].
     * @param farmId Optional ID of the farm for context.
     * @param flockId Optional ID of the flock for context.
     */
    fun fetchAdvancedAnalyticsData(farmId: String?, flockId: String?) {
        viewModelScope.launch {
            _advancedAnalyticsState.value = AdvancedAnalyticsUiState(isLoading = true, lastFetchedFarmId = farmId, lastFetchedFlockId = flockId)
            repository.refreshProductionForecasts(farmId, flockId)
            repository.refreshPerformancePredictions(farmId, flockId)
            repository.refreshFeedOptimizationRecommendations(farmId, flockId)
            repository.refreshHealthTrends(farmId, flockId)

            try {
                val forecasts = repository.getProductionForecasts(farmId, flockId).first()
                val predictions = repository.getPerformancePredictions(farmId, flockId).first()
                val recommendations = repository.getFeedOptimizationRecommendations(farmId, flockId).first()
                val trends = repository.getHealthTrends(farmId, flockId).first()

                val errors = listOf(forecasts, predictions, recommendations, trends)
                    .filterIsInstance<Result.Error<*>>()

                if (errors.isNotEmpty()) {
                     _advancedAnalyticsState.value = _advancedAnalyticsState.value.copy(
                        isLoading = false,
                        errorMessage = errors.joinToString("\n") { it.exception.message ?: "Analytics fetch error" }
                    )
                } else {
                    _advancedAnalyticsState.value = AdvancedAnalyticsUiState(
                        productionForecasts = (forecasts as? Result.Success)?.data ?: emptyList(),
                        performancePredictions = (predictions as? Result.Success)?.data ?: emptyList(),
                        feedRecommendations = (recommendations as? Result.Success)?.data ?: emptyList(),
                        healthTrends = (trends as? Result.Success)?.data ?: emptyList(),
                        isLoading = false,
                        lastFetchedFarmId = farmId,
                        lastFetchedFlockId = flockId
                    )
                }
            } catch (e: Exception) {
                 _advancedAnalyticsState.value = _advancedAnalyticsState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to fetch advanced analytics"
                )
            }
        }
    }

    /**
     * Updates the context (farmId, flockId) for fetching analytics and automation settings.
     * @param farmId New farm ID.
     * @param flockId New flock ID.
     */
    fun updateAnalyticsContext(farmId: String?, flockId: String?) {
        _currentFarmId.value = farmId
        _currentFlockId.value = flockId
        fetchAdvancedAnalyticsData(farmId, flockId)
        fetchSmartAutomationSettings(farmId, flockId, _currentShedId.value, _selectedDeviceId.value)
    }

    /**
     * Fetches smart automation settings (feeding schedules, climate settings, maintenance reminders).
     * Updates the [smartAutomationState].
     * @param farmId Optional farm ID.
     * @param flockId Optional flock ID.
     * @param shedId Optional shed ID (for climate).
     * @param deviceIdForReminders Optional device ID (for maintenance reminders).
     */
    fun fetchSmartAutomationSettings(farmId: String?, flockId: String?, shedId: String?, deviceIdForReminders: String?) {
         viewModelScope.launch {
            _smartAutomationState.value = SmartAutomationControlsUiState(isLoading = true, lastFetchedFarmId = farmId, lastFetchedFlockId = flockId, lastFetchedDeviceIdForReminders = deviceIdForReminders)
            repository.refreshFeedingSchedules(farmId, flockId)
            repository.refreshClimateSettings(farmId, shedId)
            repository.refreshMaintenanceReminders(farmId, deviceIdForReminders)

            try {
                val schedules = repository.getFeedingSchedules(farmId, flockId).first()
                val climates = repository.getClimateSettings(farmId, shedId).first()
                val reminders = repository.getMaintenanceReminders(farmId, deviceIdForReminders).first()

                val errors = listOf(schedules, climates, reminders).filterIsInstance<Result.Error<*>>()
                if (errors.isNotEmpty()) {
                    _smartAutomationState.value = _smartAutomationState.value.copy(
                        isLoading = false,
                        errorMessage = errors.joinToString("\n") { it.exception.message ?: "Automation settings fetch error" }
                    )
                } else {
                     _smartAutomationState.value = SmartAutomationControlsUiState(
                        feedingSchedules = (schedules as? Result.Success)?.data ?: emptyList(),
                        climateSettings = (climates as? Result.Success)?.data ?: emptyList(),
                        maintenanceReminders = (reminders as? Result.Success)?.data ?: emptyList(),
                        isLoading = false,
                        lastFetchedFarmId = farmId,
                        lastFetchedFlockId = flockId,
                        lastFetchedDeviceIdForReminders = deviceIdForReminders
                    )
                }
            } catch (e: Exception) {
                _smartAutomationState.value = _smartAutomationState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to fetch automation settings"
                )
            }
        }
    }

    /**
     * Updates a feeding schedule.
     * @param schedule The [FeedingSchedule] to update.
     * @return True if successful, false otherwise.
     */
    suspend fun updateFeedingSchedule(schedule: FeedingSchedule): Boolean {
        val result = repository.updateFeedingSchedule(schedule.copy(farmId = _currentFarmId.value, flockId = _currentFlockId.value))
        if (result is Result.Error) _errorMessage.value = result.exception.message ?: "Failed to update schedule"
        else fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, _selectedDeviceId.value)
        return result is Result.Success
    }

    /**
     * Updates climate settings.
     * @param settings The [ClimateSettings] to update.
     * @return True if successful, false otherwise.
     */
    suspend fun updateClimateSettings(settings: ClimateSettings): Boolean {
        val result = repository.updateClimateSettings(settings.copy(farmId = _currentFarmId.value, shedId = _currentShedId.value))
        if (result is Result.Error) _errorMessage.value = result.exception.message ?: "Failed to update climate settings"
        else fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, _selectedDeviceId.value)
        return result is Result.Success
    }

    /**
     * Updates a maintenance reminder (e.g., marks as complete).
     * @param reminder The [MaintenanceReminder] to update.
     * @return True if successful, false otherwise.
     */
    suspend fun updateMaintenanceReminder(reminder: MaintenanceReminder): Boolean {
        val result = repository.updateMaintenanceReminder(reminder.copy(farmId = _currentFarmId.value))
        if (result is Result.Error) _errorMessage.value = result.exception.message ?: "Failed to update reminder"
        else fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, _selectedDeviceId.value)
        return result is Result.Success
    }

    /**
     * Adds a new maintenance reminder.
     * @param reminder The [MaintenanceReminder] to add.
     * @return True if successful, false otherwise.
     */
     suspend fun addMaintenanceReminder(reminder: MaintenanceReminder): Boolean {
        val result = repository.addMaintenanceReminder(reminder.copy(farmId = _currentFarmId.value))
        if (result is Result.Error) _errorMessage.value = result.exception.message ?: "Failed to add reminder"
        else fetchSmartAutomationSettings(_currentFarmId.value, _currentFlockId.value, _currentShedId.value, _selectedDeviceId.value)
        return result is Result.Success
    }

    /**
     * Sets up monitoring for real-time sensor data to generate alerts.
     */
    private fun setupAlertMonitoring() {
        temperatureReadings
            .debounce(1000) // Process only one reading per second to avoid alert spam
            .onEach { readings ->
                val latest = readings.maxByOrNull { it.timestamp } ?: return@onEach
                val deviceId = latest.deviceId
                if (latest.temperature > AlertThresholds.MAX_TEMP) {
                    generateAlert(deviceId, "TEMPERATURE_HIGH", "CRITICAL", "Temperature too high: ${latest.temperature}°C")
                } else if (latest.temperature < AlertThresholds.MIN_TEMP) {
                    generateAlert(deviceId, "TEMPERATURE_LOW", "CRITICAL", "Temperature too low: ${latest.temperature}°C")
                }
            }.launchIn(viewModelScope)

        // Monitor Humidity
        humidityReadings
            .debounce(1000)
            .onEach { readings ->
                val latest = readings.maxByOrNull { it.timestamp } ?: return@onEach
                val deviceId = latest.deviceId
                if (latest.humidity > AlertThresholds.MAX_HUMIDITY) {
                    generateAlert(deviceId, "HUMIDITY_HIGH", "WARNING", "Humidity too high: ${latest.humidity}%")
                } else if (latest.humidity < AlertThresholds.MIN_HUMIDITY) {
                    generateAlert(deviceId, "HUMIDITY_LOW", "WARNING", "Humidity too low: ${latest.humidity}%")
                }
            }.launchIn(viewModelScope)

        // Monitor Feed Level
        feedLevelReadings
            .debounce(1000)
            .onEach { readings ->
                val latest = readings.maxByOrNull { it.timestamp } ?: return@onEach
                val deviceId = latest.deviceId
                if (latest.levelPercentage < AlertThresholds.MIN_FEED_LEVEL) {
                    generateAlert(deviceId, "FEED_LOW", "WARNING", "Feed level low: ${latest.levelPercentage}%")
                }
            }.launchIn(viewModelScope)
    }

    private suspend fun generateAlert(deviceId: String, type: String, severity: String, message: String) {
        // Check if a similar unacknowledged alert already exists to prevent duplicates
        val existingAlerts = uiState.value.activeAlerts
        val hasSimilarAlert = existingAlerts.any {
            it.deviceId == deviceId && it.alertType == type && !it.acknowledged
        }

        if (!hasSimilarAlert) {
            val newAlert = AlertInfo(
                deviceId = deviceId,
                alertType = type,
                severity = severity,
                message = message,
                timestamp = System.currentTimeMillis(),
                acknowledged = false
            )
            val result = repository.recordAlert(newAlert)
            if (result is Result.Error) {
                _errorMessage.value = result.exception.message ?: "Failed to record alert"
            }
            // UI will update via the uiState flow which includes alerts
        }
    }


    fun acknowledgeAlert(alertId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.acknowledgeAlert(alertId)
            if (result is Result.Error) {
                _errorMessage.value = result.exception.message ?: "Failed to acknowledge alert"
            }
            _isLoading.value = false
            // UI will update via the uiState flow
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun exportHistoricalDataToCsv(
        context: Context,
        deviceId: String?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ) {
        if (deviceId == null || startDate == null || endDate == null) {
            _errorMessage.value = "Device ID and date range must be selected for export."
            return
        }

        val currentHistoricalData = historicalDataState.value
        if (currentHistoricalData.isLoading ||
            currentHistoricalData.lastFetchedDeviceId != deviceId ||
            currentHistoricalData.lastFetchedStartTime != startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() ||
            currentHistoricalData.lastFetchedEndTime != endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() -1 ) {
            _errorMessage.value = "Please fetch the data for the selected range first."
            // Or trigger a fetch automatically:
            // fetchHistoricalData(deviceId, startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() -1)
            // and then perhaps observe for completion before exporting. For simplicity, requiring user to fetch first.
            return
        }

        var csvContent = ""
        val deviceName = devices.value.let { if (it is Result.Success) it.data.find { d -> d.deviceId == deviceId }?.name ?: deviceId else deviceId }


        if (currentHistoricalData.temperatureReadings.isNotEmpty()) {
            csvContent += CsvExporter.generateCsvContent(currentHistoricalData.temperatureReadings, "Temperature") + "\n\n"
        }
        if (currentHistoricalData.humidityReadings.isNotEmpty()) {
            csvContent += CsvExporter.generateCsvContent(currentHistoricalData.humidityReadings, "Humidity") + "\n\n"
        }
        if (currentHistoricalData.feedLevelReadings.isNotEmpty()) {
            csvContent += CsvExporter.generateCsvContent(currentHistoricalData.feedLevelReadings, "FeedLevel") + "\n\n"
        }
        if (currentHistoricalData.waterConsumptionReadings.isNotEmpty()) {
            csvContent += CsvExporter.generateCsvContent(currentHistoricalData.waterConsumptionReadings, "WaterConsumption") + "\n\n"
        }
        if (currentHistoricalData.lightLevelReadings.isNotEmpty()) {
            csvContent += CsvExporter.generateCsvContent(currentHistoricalData.lightLevelReadings, "LightLevel") + "\n\n"
        }

        if (csvContent.isNotBlank()) {
            CsvExporter.shareCsvFile(context, csvContent, "iot_data_${deviceName}")
        } else {
            _errorMessage.value = "No historical data available to export for the selected range."
        }
    }
}
