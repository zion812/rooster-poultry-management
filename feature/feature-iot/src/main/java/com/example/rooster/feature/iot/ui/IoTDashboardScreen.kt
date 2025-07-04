package com.example.rooster.feature.iot.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.feature.iot.data.model.AlertInfo
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.example.rooster.feature.iot.data.model.DeviceInfo
import com.example.rooster.feature.iot.data.model.FeedLevelReading
import com.example.rooster.feature.iot.data.model.HistoricalDataState
import com.example.rooster.feature.iot.data.model.HumidityReading
import com.example.rooster.feature.iot.data.model.LightLevelReading
import com.example.rooster.feature.iot.data.model.TemperatureReading
import com.example.rooster.feature.iot.data.model.WaterConsumptionReading
import com.example.rooster.feature.iot.data.model.AdvancedAnalyticsUiState
import com.example.rooster.feature.iot.data.model.SmartAutomationControlsUiState
import com.example.rooster.feature.iot.data.model.FeedingSchedule
import com.example.rooster.feature.iot.data.model.ClimateSettings
import com.example.rooster.feature.iot.data.model.MaintenanceReminder
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider // Required for sharing files

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoTDashboardScreen(
    viewModel: IoTViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val temperatureReadings by viewModel.temperatureReadings.collectAsState()
    val humidityReadings by viewModel.humidityReadings.collectAsState()
    val feedLevelReadings by viewModel.feedLevelReadings.collectAsState()
    val waterConsumptionReadings by viewModel.waterConsumptionReadings.collectAsState()
    val lightLevelReadings by viewModel.lightLevelReadings.collectAsState()

    val historicalDataState by viewModel.historicalDataState.collectAsState()
    val advancedAnalyticsState by viewModel.advancedAnalyticsState.collectAsState()
    val smartAutomationState by viewModel.smartAutomationState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val calendarStateStart = rememberSheetState()
    val calendarStateEnd = rememberSheetState()

    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    CalendarDialog(
        state = calendarStateStart,
        config = CalendarConfig(monthSelection = true, yearSelection = true),
        selection = CalendarSelection.Date { date -> selectedStartDate = date }
    )
    CalendarDialog(
        state = calendarStateEnd,
        config = CalendarConfig(monthSelection = true, yearSelection = true),
        selection = CalendarSelection.Date { date -> selectedEndDate = date }
    )

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearErrorMessage()
        }
    }
     LaunchedEffect(advancedAnalyticsState.errorMessage) {
        advancedAnalyticsState.errorMessage?.let {
            snackbarHostState.showSnackbar(message = "Analytics Error: $it", duration = SnackbarDuration.Long)
        }
    }
    LaunchedEffect(smartAutomationState.errorMessage) {
        smartAutomationState.errorMessage?.let {
            snackbarHostState.showSnackbar(message = "Automation Error: $it", duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("IoT Dashboard") },
                actions = {
                    if (uiState.isLoading || advancedAnalyticsState.isLoading || smartAutomationState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(onClick = {
                            uiState.selectedDeviceId?.let { viewModel.refreshDeviceData(it) }
                            viewModel.fetchAdvancedAnalyticsData(uiState.currentFarmIdForAnalytics, uiState.currentFlockIdForAnalytics)
                            viewModel.fetchSmartAutomationSettings(
                                farmId = uiState.currentFarmIdForAnalytics,
                                flockId = uiState.currentFlockIdForAnalytics,
                                shedId = uiState.currentShedIdForAnalytics,
                                deviceIdForReminders = uiState.selectedDeviceId
                            )
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh All")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)
        ) {
            item { DeviceSelectionArea(uiState = uiState, onDeviceSelected = { viewModel.selectDevice(it) }) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { AlertsSection(alerts = uiState.activeAlerts, onAcknowledge = { viewModel.acknowledgeAlert(it) }) }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (uiState.selectedDeviceId != null) {
                item { Text("Real-time Sensor Data for ${uiState.devices.find{it.deviceId == uiState.selectedDeviceId}?.name ?: "Device"}", style = MaterialTheme.typography.titleMedium) }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    if(uiState.isLoadingDeviceData) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        SensorDataGrid(
                            temperatureReadings = temperatureReadings,
                            humidityReadings = humidityReadings,
                            feedLevelReadings = feedLevelReadings,
                            waterConsumptionReadings = waterConsumptionReadings,
                            lightLevelReadings = lightLevelReadings,
                            isHistorical = false
                        )
                    }
                }
            } else if (uiState.isLoading && uiState.devices.isEmpty()) {
                 item { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (uiState.devices.isEmpty() && !uiState.isLoading) {
                item { Text("No IoT devices found or registered.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
            } else {
                 item { Text("Please select a device to view its data.", style = MaterialTheme.typography.bodyLarge) }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { HistoricalDataSection(
                selectedDeviceId = uiState.selectedDeviceId,
                selectedDeviceName = uiState.devices.find{it.deviceId == uiState.selectedDeviceId}?.name,
                selectedStartDate = selectedStartDate,
                selectedEndDate = selectedEndDate,
                onStartDateClick = { calendarStateStart.show() },
                onEndDateClick = { calendarStateEnd.show() },
                onFetchHistoricalData = { start, end ->
                    uiState.selectedDeviceId?.let { deviceId ->
                        viewModel.fetchHistoricalData(deviceId, start, end)
                    }
                },
                historicalDataState = historicalDataState,
                dateFormatter = dateFormatter,
                context = context,
                viewModel = viewModel
            ) }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { AdvancedAnalyticsDisplaySection(analyticsState = advancedAnalyticsState, dateTimeFormatter = dateTimeFormatter) }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { SmartAutomationControlsSection(
                automationState = smartAutomationState,
                viewModel = viewModel,
                dateTimeFormatter = dateTimeFormatter,
                dateFormatter = dateFormatter
            ) }
        }
    }
}

@Composable
fun SmartAutomationControlsSection(
    automationState: SmartAutomationControlsUiState,
    viewModel: IoTViewModel,
    dateTimeFormatter: SimpleDateFormat,
    dateFormatter: SimpleDateFormat
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        Text("Smart Automation Controls", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (automationState.isLoading) {
            CircularProgressIndicator()
            return
        }
        if (automationState.errorMessage != null) {
            Text("Error loading automation settings: ${automationState.errorMessage}", color = MaterialTheme.colorScheme.error)
        }

        Text("Feeding Schedules", style = MaterialTheme.typography.titleSmall)
        if (automationState.feedingSchedules.isEmpty() && !automationState.isLoading) {
            Text("No feeding schedules configured for Farm: ${automationState.lastFetchedFarmId ?: "N/A"}, Flock: ${automationState.lastFetchedFlockId ?: "N/A"}.")
        } else {
            automationState.feedingSchedules.forEach { schedule ->
                var editableSchedule by remember(schedule.scheduleId, schedule.lastUpdated) { mutableStateOf(schedule) }
                var showFeedingDialog by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showFeedingDialog = true }) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Schedule: ${editableSchedule.scheduleId}", style = MaterialTheme.typography.bodyMedium)
                        Text("Farm: ${editableSchedule.farmId ?: "N/A"}, Flock: ${editableSchedule.flockId ?: "N/A"}")
                        Text("Enabled: ${editableSchedule.isEnabled}")
                        Text("Times: ${editableSchedule.times.joinToString()}", style = MaterialTheme.typography.bodySmall)
                        Text("Amount: ${editableSchedule.amountPerFeedingKg} kg", style = MaterialTheme.typography.bodySmall)
                        Text("Last Updated: ${dateTimeFormatter.format(Date(editableSchedule.lastUpdated))}", style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (showFeedingDialog) {
                     AlertDialog(
                        onDismissRequest = { showFeedingDialog = false },
                        title = { Text("Edit Feeding Schedule") },
                        text = {
                               Column {
                                   Text("Schedule ID: ${editableSchedule.scheduleId}")
                                   Row(verticalAlignment = Alignment.CenterVertically) {
                                       Checkbox(checked = editableSchedule.isEnabled, onCheckedChange = { editableSchedule = editableSchedule.copy(isEnabled = it) })
                                       Text("Enabled")
                                   }
                                   Text("Current Amount: ${editableSchedule.amountPerFeedingKg} kg")
                                   Button(onClick = { editableSchedule = editableSchedule.copy(amountPerFeedingKg = editableSchedule.amountPerFeedingKg + 0.5) }) { Text("+0.5kg") }
                                   Button(onClick = { editableSchedule = editableSchedule.copy(amountPerFeedingKg = (editableSchedule.amountPerFeedingKg - 0.5).coerceAtLeast(0.0)) }) { Text("-0.5kg") }
                                   Text("Times: ${editableSchedule.times.joinToString()} (UI for time editing TBD)")
                               }
                        },
                        confirmButton = {
                            Button(onClick = {
                                coroutineScope.launch { viewModel.updateFeedingSchedule(editableSchedule) }
                                showFeedingDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = { Button(onClick = { showFeedingDialog = false }) { Text("Cancel") } }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Climate Settings", style = MaterialTheme.typography.titleSmall)
         if (automationState.climateSettings.isEmpty() && !automationState.isLoading) {
            Text("No climate settings configured for Farm: ${automationState.lastFetchedFarmId ?: "N/A"}, Shed: ${automationState.lastFetchedFlockId ?: "N/A"}.")
        } else {
            automationState.climateSettings.forEach { settings ->
                 var editableSettings by remember(settings.settingsId, settings.lastUpdated) { mutableStateOf(settings) }
                 var showClimateDialog by remember { mutableStateOf(false) }
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showClimateDialog = true }) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Settings: ${editableSettings.settingsId}", style = MaterialTheme.typography.bodyMedium)
                        Text("Farm: ${editableSettings.farmId ?: "N/A"}, Shed: ${editableSettings.shedId ?: "N/A"}")
                        Text("Target Temp: ${editableSettings.targetTemperature}°C, Target Humidity: ${editableSettings.targetHumidityPercentage}%")
                        Text("Fan Mode: ${editableSettings.fanControlMode}")
                        Text("Lighting: On ${editableSettings.lightingSchedule["on"]}, Off ${editableSettings.lightingSchedule["off"]}")
                        Text("Last Updated: ${dateTimeFormatter.format(Date(editableSettings.lastUpdated))}", style = MaterialTheme.typography.labelSmall)
                    }
                }
                 if (showClimateDialog) {
                     AlertDialog(
                        onDismissRequest = { showClimateDialog = false },
                        title = { Text("Edit Climate Settings") },
                        text = {
                            Column {
                                Text("Settings ID: ${editableSettings.settingsId}")
                                Text("Target Temp: ${editableSettings.targetTemperature}°C (UI TBD)")
                                Text("Target Humidity: ${editableSettings.targetHumidityPercentage}% (UI TBD)")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                coroutineScope.launch { viewModel.updateClimateSettings(editableSettings) }
                                showClimateDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = { Button(onClick = { showClimateDialog = false }) { Text("Cancel") } }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Maintenance Reminders", style = MaterialTheme.typography.titleSmall)
        if (automationState.maintenanceReminders.isEmpty() && !automationState.isLoading) {
            Text("No maintenance reminders for Device: ${automationState.lastFetchedDeviceIdForReminders ?: "All"} on Farm: ${automationState.lastFetchedFarmId ?: "N/A"}.")
        } else {
            automationState.maintenanceReminders.forEach { reminder ->
                var editableReminder by remember(reminder.reminderId, reminder.lastUpdated) { mutableStateOf(reminder) }
                var showReminderDialog by remember { mutableStateOf(false) }
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showReminderDialog = true }) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${reminder.equipmentType}: ${reminder.description}", style = MaterialTheme.typography.bodyMedium, color = if(editableReminder.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface)
                        Text("Due: ${dateFormatter.format(Date(editableReminder.dueDate))}, Device: ${editableReminder.deviceId ?: "N/A"}")
                        Text(if (editableReminder.isCompleted) "Status: Completed" else "Status: Pending", color = if(editableReminder.isCompleted) Color.Green else MaterialTheme.colorScheme.error)
                        editableReminder.notes?.let { Text("Notes: $it", style=MaterialTheme.typography.bodySmall) }
                    }
                }
                 if (showReminderDialog) {
                     AlertDialog(
                        onDismissRequest = { showReminderDialog = false },
                        title = { Text("Edit Reminder") },
                        text = {
                            Column {
                                Text("Reminder: ${editableReminder.description}")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = editableReminder.isCompleted, onCheckedChange = { editableReminder = editableReminder.copy(isCompleted = it) })
                                    Text("Completed")
                                }
                                OutlinedTextField(
                                    value = editableReminder.notes ?: "",
                                    onValueChange = { editableReminder = editableReminder.copy(notes = it) },
                                    label = { Text("Notes") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                coroutineScope.launch { viewModel.updateMaintenanceReminder(editableReminder) }
                                showReminderDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = { Button(onClick = { showReminderDialog = false }) { Text("Cancel") } }
                    )
                }
            }
        }
        Button(onClick = {
            val newReminder = MaintenanceReminder(
                equipmentType = "Feeder Unit",
                description = "Clean and Lubricate Auger",
                dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
            )
            coroutineScope.launch { viewModel.addMaintenanceReminder(newReminder) }
        }) { Text("Add Sample Reminder") }
    }
}

@Composable
fun AdvancedAnalyticsDisplaySection(analyticsState: AdvancedAnalyticsUiState, dateTimeFormatter: SimpleDateFormat) {
    Column {
        Text("Advanced Analytics & Recommendations", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (analyticsState.isLoading) {
            CircularProgressIndicator()
            return
        }
        if (analyticsState.errorMessage != null) {
            Text("Error loading analytics: ${analyticsState.errorMessage}", color = MaterialTheme.colorScheme.error)
        }

        if (analyticsState.productionForecasts.isNotEmpty()) {
            Text("Production Forecasts", style = MaterialTheme.typography.titleSmall)
            analyticsState.productionForecasts.forEach { forecast ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Forecast for: ${dateTimeFormatter.format(Date(forecast.forecastDate))}", style = MaterialTheme.typography.bodyMedium)
                        forecast.predictedEggCount?.let { Text("Predicted Eggs: $it") }
                        forecast.predictedWeightGainKg?.let { Text("Predicted Weight Gain: $it kg") }
                        forecast.confidenceScore?.let { Text("Confidence: ${"%.0f".format(it * 100)}%") }
                        forecast.notes?.let { Text("Notes: $it") }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (analyticsState.performancePredictions.isNotEmpty()) {
            Text("Performance Predictions", style = MaterialTheme.typography.titleSmall)
            analyticsState.performancePredictions.forEach { prediction ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Prediction for: ${dateTimeFormatter.format(Date(prediction.predictionDate))}", style = MaterialTheme.typography.bodyMedium)
                        prediction.feedConversionRatio?.let { Text("FCR: $it") }
                        prediction.mortalityRatePercentage?.let { Text("Mortality: $it%") }
                        prediction.overallScore?.let { Text("Overall: $it", color = if(it.contains("Attention")) MaterialTheme.colorScheme.error else Color.Unspecified) }
                        if(prediction.factors.isNotEmpty()) Text("Factors: ${prediction.factors.joinToString()}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (analyticsState.feedRecommendations.isNotEmpty()) {
            Text("Feed Optimization", style = MaterialTheme.typography.titleSmall)
            analyticsState.feedRecommendations.forEach { rec ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Recommendation (as of ${dateTimeFormatter.format(Date(rec.recommendationDate))})", style = MaterialTheme.typography.bodyMedium)
                        rec.currentFeedName?.let { Text("Current: $it") }
                        rec.recommendedFeedName?.let { Text("Recommended: $it", color = MaterialTheme.colorScheme.primary) }
                        rec.reason?.let { Text("Reason: $it") }
                        rec.estimatedSavingsPercentage?.let { Text("Est. Savings: $it%") }
                        rec.estimatedPerformanceGain?.let { Text("Est. Gain: $it") }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (analyticsState.healthTrends.isNotEmpty()) {
            Text("Health Trends", style = MaterialTheme.typography.titleSmall)
            analyticsState.healthTrends.forEach { trend ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${trend.trendType} Trend (${trend.trendDirection})", style = MaterialTheme.typography.bodyMedium)
                        Text("Period: ${dateFormatter.format(Date(trend.periodStartDate))} - ${dateFormatter.format(Date(trend.periodEndDate))}")
                        Text("Value: ${"%.2f".format(trend.trendValue)}")
                        trend.interpretation?.let { Text("Note: $it") }
                    }
                }
            }
        }

        if (analyticsState.productionForecasts.isEmpty() &&
            analyticsState.performancePredictions.isEmpty() &&
            analyticsState.feedRecommendations.isEmpty() &&
            analyticsState.healthTrends.isEmpty() &&
            analyticsState.errorMessage == null && !analyticsState.isLoading
        ) {
             Text("No advanced analytics data available for the current context (Farm: ${analyticsState.lastFetchedFarmId ?: "N/A"}, Flock: ${analyticsState.lastFetchedFlockId ?: "N/A"}).")
        }
    }
}


@Composable
fun HistoricalDataSection(
    selectedDeviceId: String?,
    selectedDeviceName: String?,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onFetchHistoricalData: (Long, Long) -> Unit,
    historicalDataState: HistoricalDataState,
    dateFormatter: SimpleDateFormat,
    context: Context,
    viewModel: IoTViewModel
) {
    Column {
        Text("Historical Data Analysis", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedDeviceId == null) {
            Text("Please select a device first to fetch historical data.")
            return
        }

        Text("Device: ${selectedDeviceName ?: selectedDeviceId}", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onStartDateClick, modifier = Modifier.weight(1f)) {
                Text(selectedStartDate?.let { dateFormatter.format(Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant())) } ?: "Select Start Date")
            }
            OutlinedButton(onClick = onEndDateClick, modifier = Modifier.weight(1f)) {
                Text(selectedEndDate?.let { dateFormatter.format(Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant())) } ?: "Select End Date")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (selectedStartDate != null && selectedEndDate != null) {
                        val startTime = selectedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val endTime = selectedEndDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() -1
                        onFetchHistoricalData(startTime, endTime)
                    }
                },
                enabled = selectedStartDate != null && selectedEndDate != null && selectedStartDate.isBefore(selectedEndDate.plusDays(1))
            ) {
                Text("Fetch Data")
            }
            Button(
                onClick = {
                    viewModel.exportHistoricalDataToCsv(context, selectedDeviceId, selectedStartDate, selectedEndDate)
                },
                enabled = historicalDataState.temperatureReadings.isNotEmpty() || // Enable if any data is loaded
                          historicalDataState.humidityReadings.isNotEmpty() ||
                          historicalDataState.feedLevelReadings.isNotEmpty() ||
                          historicalDataState.waterConsumptionReadings.isNotEmpty() ||
                          historicalDataState.lightLevelReadings.isNotEmpty()
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Export Data")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Export CSV")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (historicalDataState.isLoading) {
            CircularProgressIndicator()
        } else if (historicalDataState.errorMessage != null) {
            Text("Error: ${historicalDataState.errorMessage}", color = MaterialTheme.colorScheme.error)
        } else if (
            historicalDataState.temperatureReadings.isEmpty() &&
            historicalDataState.humidityReadings.isEmpty() &&
            historicalDataState.feedLevelReadings.isEmpty() &&
            historicalDataState.waterConsumptionReadings.isEmpty() &&
            historicalDataState.lightLevelReadings.isEmpty() &&
            (selectedStartDate != null || selectedEndDate != null)
        ) {
            Text("No historical data found for the selected period.")
        } else if (selectedStartDate != null || selectedEndDate != null) { // Only show grid if dates were picked
            SensorDataGrid(
                temperatureReadings = historicalDataState.temperatureReadings,
                humidityReadings = historicalDataState.humidityReadings,
                feedLevelReadings = historicalDataState.feedLevelReadings,
                waterConsumptionReadings = historicalDataState.waterConsumptionReadings,
                lightLevelReadings = historicalDataState.lightLevelReadings,
                isHistorical = true
            )
        }
    }
}


@Composable
fun DeviceSelectionArea(uiState: IoTDashboardUiState, onDeviceSelected: (String) -> Unit) {
    Column {
        Text("Select Device:", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        if (uiState.devices.isEmpty() && !uiState.isLoading) {
            Text("No devices available.")
        } else if (uiState.isLoading && uiState.devices.isEmpty()){
             CircularProgressIndicator()
        }
        else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.devices) { device ->
                    Button(
                        onClick = { onDeviceSelected(device.deviceId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (device.deviceId == uiState.selectedDeviceId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (device.deviceId == uiState.selectedDeviceId) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(device.name.ifEmpty { device.deviceId })
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsSection(alerts: List<AlertInfo>, onAcknowledge: (String) -> Unit) {
    if (alerts.isNotEmpty()) {
        Column {
            Text("Active Alerts", style = MaterialTheme.typography.titleMedium)
            alerts.take(3).forEach { alert ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(alert.message, style = MaterialTheme.typography.bodyMedium, color = if(alert.severity == "CRITICAL") MaterialTheme.colorScheme.error else Color.Unspecified)
                            Text("Type: ${alert.alertType}, Severity: ${alert.severity}", style = MaterialTheme.typography.bodySmall)
                            Text(SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(alert.timestamp)), style = MaterialTheme.typography.bodySmall)
                        }
                        if (!alert.acknowledged) {
                            Button(onClick = { onAcknowledge(alert.alertId) }) { Text("Ack") }
                        }
                    }
                }
            }
             if (alerts.size > 3) {
                Text("...and ${alerts.size - 3} more alerts.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}


@Composable
fun <T> SensorChartCard(
    title: String,
    readings: List<T>,
    valueExtractor: (T) -> Float,
    timestampExtractor: (T) -> Long,
    yAxisLabel: String = "Value",
    isHistorical: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            if (readings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available for $title")
                }
            } else {
                AndroidView(
                    factory = { context ->
                        LineChart(context).apply {
                            description.isEnabled = false
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.granularity = 1f
                            xAxis.valueFormatter = object : ValueFormatter() {
                                private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                                override fun getFormattedValue(value: Float): String {
                                    val reading = readings.getOrNull(value.toInt())
                                    return reading?.let {
                                        val date = Date(timestampExtractor(it))
                                        if (isHistorical) dateFormat.format(date) else timeFormat.format(date)
                                    } ?: ""
                                }
                            }
                            axisRight.isEnabled = false
                            axisLeft.setLabelCount(5, true)
                            legend.isEnabled = true

                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            isScaleXEnabled = true
                            isScaleYEnabled = true
                            setPinchZoom(true)
                            isDoubleTapToZoomEnabled = true

                            isHighlightPerTapEnabled = true
                            isHighlightPerDragEnabled = false
                        }
                    },
                    update = { chart ->
                        val entries = readings.mapIndexed { index, reading ->
                            Entry(index.toFloat(), valueExtractor(reading))
                        }
                        val dataSet = LineDataSet(entries, yAxisLabel).apply {
                            color = Color(0xFF00796B).hashCode()
                            valueTextColor = Color.Black.hashCode()
                            setDrawCircles(true)
                            setDrawCircleHole(false)
                            setCircleColor(Color(0xFF004D40).hashCode())
                            circleRadius = 3f
                            setDrawValues(false)
                            lineWidth = 2f
                            highlightLineWidth = 1.5f
                            highLightColor = android.graphics.Color.RED
                            setDrawHighlightIndicators(true)
                        }
                        chart.data = LineData(dataSet)
                        chart.setVisibleXRangeMaximum(if (isHistorical) 30f else 15f)
                        chart.moveViewToX(entries.size.toFloat())
                        chart.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SensorDataGrid(
    temperatureReadings: List<TemperatureReading>,
    humidityReadings: List<HumidityReading>,
    feedLevelReadings: List<FeedLevelReading>,
    waterConsumptionReadings: List<WaterConsumptionReading>,
    lightLevelReadings: List<LightLevelReading>,
    isHistorical: Boolean = false
) {
    val itemsToShow = if (isHistorical) Int.MAX_VALUE else 20
    SensorChartCard(
        title = "Temperature (°C)",
        readings = temperatureReadings.takeLast(itemsToShow),
        valueExtractor = { it.temperature.toFloat() },
        timestampExtractor = { it.timestamp },
        yAxisLabel = "Temp (°C)",
        isHistorical = isHistorical
    )
    SensorChartCard(
        title = "Humidity (%)",
        readings = humidityReadings.takeLast(itemsToShow),
        valueExtractor = { it.humidity.toFloat() },
        timestampExtractor = { it.timestamp },
        yAxisLabel = "Humidity (%)",
        isHistorical = isHistorical
    )
     SensorChartCard(
        title = "Feed Level (%)",
        readings = feedLevelReadings.takeLast(itemsToShow),
        valueExtractor = { it.levelPercentage.toFloat() },
        timestampExtractor = { it.timestamp },
        yAxisLabel = "Feed (%)",
        isHistorical = isHistorical
    )
    SensorChartCard(
        title = "Water Consumption (L)",
        readings = waterConsumptionReadings.takeLast(itemsToShow),
        valueExtractor = { it.volumeConsumed.toFloat() },
        timestampExtractor = { it.timestamp },
        yAxisLabel = "Water (L)",
        isHistorical = isHistorical
    )
    SensorChartCard(
        title = "Light Level (lux)",
        readings = lightLevelReadings.takeLast(itemsToShow),
        valueExtractor = { it.lux.toFloat() },
        timestampExtractor = { it.timestamp },
        yAxisLabel = "Light (lux)",
        isHistorical = isHistorical
    )

    if (!isHistorical) {
        Text("Device Management", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Text("List of devices, status, add/edit/remove device actions will go here.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DeviceList(devices: List<DeviceInfo>, onManageDevice: (DeviceInfo) -> Unit) {
    if (devices.isEmpty()) {
        Text("No devices to manage.")
        return
    }
    Column {
        devices.forEach { device ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onManageDevice(device) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(device.name, style = MaterialTheme.typography.bodyLarge)
                        Text("ID: ${device.deviceId}", style = MaterialTheme.typography.bodySmall)
                        Text("Type: ${device.type}, Location: ${device.location}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(device.status, color = if(device.status.equals("online", true)) Color.Green else Color.Gray)
                }
            }
        }
    }
}
