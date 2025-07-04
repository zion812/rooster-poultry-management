package com.rooster.farmerhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rooster.farmerhome.domain.model.WeatherData
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect


@OptIn(ExperimentalMaterial3Api::class) // Added for Scaffold & SnackbarHost if not already present
@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messageId) {
        uiState.transientUserMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearTransientMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding) // Apply scaffold padding
                .padding(16.dp), // Then apply original screen padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Farmer Home Screen", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            FarmInfoSection(
            farmInfoState = uiState.farmInfoState, // Pass DataState
            onRetry = { viewModel.fetchFarmBasicInfo("farm123") } // Consider dynamic farmId
        )
        Spacer(modifier = Modifier.height(16.dp))

        WeatherSection(
            weatherState = uiState.weatherState, // Pass the DataState object
            onRetry = {
                // Retry with current farm's location if available, else default
                val location = uiState.farmBasicInfo?.location ?: "Krishna District Center"
                viewModel.fetchWeatherForFarm(location)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FarmHealthAlertsSection(
            healthAlertsState = uiState.healthAlertsState, // Pass DataState
            onMarkAsRead = { alert -> viewModel.markAlertAsRead(alert.farmId, alert.id) },
            onRetry = { viewModel.fetchHealthAlerts(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickActionsSection(
            onLogMortalityClick = { /* TODO: Navigate or show dialog */ },
            onRecordFeedingClick = { /* TODO: Navigate or show dialog */ },
            onCheckMarketPricesClick = { /* TODO: Navigate to market screen */ },
            onAddNewFlockClick = { /* TODO: Navigate to add flock screen */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProductionMetricsSection(
            productionSummaryState = uiState.productionSummaryState, // Pass DataState
            onRetry = { viewModel.fetchProductionSummary(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
        )

        // TODO: Add other sections if any
    }
}

import com.rooster.farmerhome.core.common.util.DataState // Ensure DataState is imported
import androidx.compose.material3.Button // For Retry button
import androidx.compose.material3.Badge // For stale data indication
import androidx.compose.material3.ExperimentalMaterial3Api // For Badge
import androidx.compose.foundation.layout.Row // For Badge layout
import androidx.compose.foundation.layout.size // For icon size in badge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherSection(
    weatherState: DataState<WeatherData?>, // Updated parameter
    onRetry: () -> Unit
) {
    val weatherData = weatherState.getUnderlyingData() // Helper to get data from any state

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Current Weather", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            when (weatherState) {
                is DataState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    weatherData?.let { staleData -> // Show stale data if available while loading
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Updating weather (showing last known)...", style = MaterialTheme.typography.labelSmall)
                        WeatherInfoDisplay(staleData)
                    }
                }
                is DataState.Success -> {
                    weatherState.data?.let {
                        if (weatherState.isFromCache && weatherState.isStale) {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Weather (possibly stale)", style = MaterialTheme.typography.labelSmall)
                                Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                            }
                        } else if (weatherState.isFromCache) {
                            Text("Weather (cached)", style = MaterialTheme.typography.labelSmall)
                        }
                        WeatherInfoDisplay(it)
                    } ?: Text("No weather data available.")
                }
                is DataState.Error -> {
                    Text("Error: ${weatherState.message ?: weatherState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                    weatherData?.let { staleData -> // Show stale data if available on error
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Failed to update (showing last known):", style = MaterialTheme.typography.labelSmall)
                        WeatherInfoDisplay(staleData)
                    }
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoDisplay(weatherData: WeatherData) {
    weatherData.location?.let { Text("Location: $it") }
    Text("Temperature: ${weatherData.temperature}")
    Text("Humidity: ${weatherData.humidity}")
                Text("Precipitation: ${weatherData.precipitation}")
                Text("Wind Speed: ${weatherData.windSpeed}")
                Text("Description: ${weatherData.description}")
            } else {
                Text("No weather data available.")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // For Badge
@Composable
fun FarmHealthAlertsSection(
    healthAlertsState: DataState<List<FarmHealthAlert>>, // Updated parameter
    onMarkAsRead: (alert: FarmHealthAlert) -> Unit,
    onRetry: () -> Unit
) {
    val alerts = healthAlertsState.getUnderlyingData() ?: emptyList()

    Column {
        val titleSuffix = if (alerts.isNotEmpty() && healthAlertsState is DataState.Success) " (${alerts.count { !it.isRead }} unread)" else ""
        Text("Farm Health Alerts$titleSuffix", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (healthAlertsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (alerts.isNotEmpty()) { // Show stale data if available
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Updating alerts (showing last known)...", style = MaterialTheme.typography.labelSmall)
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is DataState.Success -> {
                if (alerts.isEmpty()) {
                    Text("No active health alerts.")
                } else {
                    if (healthAlertsState.isFromCache && healthAlertsState.isStale) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Alerts (possibly stale)", style = MaterialTheme.typography.labelSmall)
                            Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                        }
                    } else if (healthAlertsState.isFromCache) {
                         Text("Alerts (cached)", style = MaterialTheme.typography.labelSmall)
                    }
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is DataState.Error -> {
                Text("Error: ${healthAlertsState.message ?: healthAlertsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (alerts.isNotEmpty()) { // Show stale data if available
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Failed to update alerts (showing last known):", style = MaterialTheme.typography.labelSmall)
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun FarmHealthAlertItem(alert: FarmHealthAlert, onMarkAsRead: (FarmHealthAlert) -> Unit) { // Changed parameter type
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isRead) MaterialTheme.colorScheme.surfaceVariant else {
                when (alert.severity) {
                    com.rooster.farmerhome.domain.model.AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    com.rooster.farmerhome.domain.model.AlertSeverity.HIGH -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    com.rooster.farmerhome.domain.model.AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                }
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(alert.title, style = MaterialTheme.typography.titleSmall)
            Text(alert.description, style = MaterialTheme.typography.bodyMedium)
            alert.recommendedAction?.let {
                Text("Recommendation: $it", style = MaterialTheme.typography.bodySmall)
            }
            Text("Severity: ${alert.severity}", style = MaterialTheme.typography.bodySmall)
            Text("Time: ${java.text.SimpleDateFormat("dd/MM/yy HH:mm", java.util.Locale.getDefault()).format(alert.alertDate)}", style = MaterialTheme.typography.bodySmall)
            if (!alert.isRead) {
                androidx.compose.material3.Button(
                    onClick = { onMarkAsRead(alert) }, // Pass the whole alert object
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                ) {
                    Text("Mark as Read")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FarmerHomeScreenPreview() {
    // Wrap in a Theme for preview if not already provided by a higher-level preview
    MaterialTheme {
        FarmerHomeScreen(viewModel = PreviewFarmerHomeViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class) // For Badge
@Composable
fun FarmInfoSection(
    farmInfoState: DataState<FarmBasicInfo?>, // Updated parameter
    onRetry: () -> Unit
) {
    val farmInfo = farmInfoState.getUnderlyingData()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text("My Farm", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            when (farmInfoState) {
                is DataState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    farmInfo?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Updating farm info (showing last known)...", style = MaterialTheme.typography.labelSmall)
                        FarmInfoDisplay(staleData)
                    }
                }
                is DataState.Success -> {
                    farmInfo?.let {
                        if (farmInfoState.isFromCache && farmInfoState.isStale) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Farm Info (possibly stale)", style = MaterialTheme.typography.labelSmall)
                                Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                            }
                        } else if (farmInfoState.isFromCache) {
                            Text("Farm Info (cached)", style = MaterialTheme.typography.labelSmall)
                        }
                        FarmInfoDisplay(it)
                    } ?: Text("Farm information not available.")
                }
                is DataState.Error -> {
                    Text("Error: ${farmInfoState.message ?: farmInfoState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                    farmInfo?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Failed to update (showing last known):", style = MaterialTheme.typography.labelSmall)
                        FarmInfoDisplay(staleData)
                    }
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun FarmInfoDisplay(farmInfo: FarmBasicInfo) {
    Text("Name: ${farmInfo.farmName}", style = MaterialTheme.typography.bodyLarge)
    Text("Location: ${farmInfo.location}", style = MaterialTheme.typography.bodyMedium)
    Text("Owner: ${farmInfo.ownerName}", style = MaterialTheme.typography.bodyMedium)
                Text("Active Flocks: ${farmInfo.activeFlockCount}", style = MaterialTheme.typography.bodySmall)
                Text("Total Capacity: ${farmInfo.totalCapacity} birds", style = MaterialTheme.typography.bodySmall)
                farmInfo.lastHealthCheckDate?.let {
                    Text("Last Health Check: $it", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Text("Farm information not available.")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // For Badge
@Composable
fun ProductionMetricsSection(
    productionSummaryState: DataState<ProductionSummary?>, // Updated parameter
    onRetry: () -> Unit
) {
    val summary = productionSummaryState.getUnderlyingData()

    Column {
        Text("Production Metrics", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (productionSummaryState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                summary?.let { staleData ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Updating metrics (showing last known)...", style = MaterialTheme.typography.labelSmall)
                    ProductionSummaryDisplay(staleData)
                }
            }
            is DataState.Success -> {
                summary?.let {
                    if (productionSummaryState.isFromCache && productionSummaryState.isStale) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Metrics (possibly stale)", style = MaterialTheme.typography.labelSmall)
                            Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                        }
                    } else if (productionSummaryState.isFromCache) {
                        Text("Metrics (cached)", style = MaterialTheme.typography.labelSmall)
                    }
                    ProductionSummaryDisplay(it)
                } ?: Text("No production metrics available.")
            }
            is DataState.Error -> {
                Text("Error: ${productionSummaryState.message ?: productionSummaryState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                summary?.let { staleData ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Failed to update metrics (showing last known):", style = MaterialTheme.typography.labelSmall)
                    ProductionSummaryDisplay(staleData)
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun ProductionSummaryDisplay(summary: ProductionSummary) {
    Column {
        Text("Overall Summary:", style = MaterialTheme.typography.titleSmall)
        Text("Total Flocks: ${summary.totalFlocks}")
                Text("Active Birds: ${summary.activeBirds}")
                Text("Egg Production (Today): ${summary.overallEggProductionToday} eggs")
                Text("Weekly Mortality Rate: ${String.format("%.2f%%", summary.weeklyMortalityRate)}")

                Spacer(modifier = Modifier.height(12.dp))
                Text("Detailed Metrics:", style = MaterialTheme.typography.titleSmall)
                if (summary.metrics.isEmpty()) {
                    Text("No detailed metrics available.")
                } else {
                    // Use a FlowRow or LazyRow for better layout of multiple cards
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        summary.metrics.forEach { metric ->
                            ProductionMetricCard(metric = metric, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Text("No production metrics available.")
        }
    }
}

@Composable
fun ProductionMetricCard(metric: ProductionMetricItem, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Text(metric.name, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${metric.value} ${metric.unit}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            metric.period?.let {
                Text("Period: $it", style = MaterialTheme.typography.bodySmall)
            }
            metric.trend?.let {
                Text("Trend: $it", style = MaterialTheme.typography.bodySmall) // TODO: Add icons for trend
            }
        }
    }
}


@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun QuickActionsSection(
    onLogMortalityClick: () -> Unit,
    onRecordFeedingClick: () -> Unit,
    onCheckMarketPricesClick: () -> Unit,
    onAddNewFlockClick: () -> Unit
) {
    Column {
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Using a simple Row for now, can be converted to LazyVerticalGrid for more items
        androidx.compose.foundation.layout.FlowRow( // Use FlowRow for wrapping items
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2 // Adjust as needed, or remove for full flow
        ) {
            QuickActionItem(
                text = "Log Mortality",
                // icon = painterResource(id = R.drawable.ic_log_mortality), // TODO: Add actual icons
                onClick = onLogMortalityClick,
                modifier = Modifier.weight(1f) // Make items share space
            )
            QuickActionItem(
                text = "Record Feeding",
                // icon = painterResource(id = R.drawable.ic_record_feeding), // TODO: Add actual icons
                onClick = onRecordFeedingClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                text = "Market Prices",
                // icon = painterResource(id = R.drawable.ic_market_prices), // TODO: Add actual icons
                onClick = onCheckMarketPricesClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                text = "Add New Flock",
                // icon = painterResource(id = R.drawable.ic_add_flock), // TODO: Add actual icons
                onClick = onAddNewFlockClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionItem(
    text: String,
    // icon: androidx.compose.ui.graphics.painter.Painter, // TODO: Enable when icons are ready
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button( // Using Button for simplicity, can be a Card or custom layout
        onClick = onClick,
        modifier = modifier
            .height(IntrinsicSize.Min) // Ensure buttons in a row have similar height
            .fillMaxWidth() // Allow button to take width within its weighted space
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Icon(painter = icon, contentDescription = text, modifier = Modifier.size(24.dp)) // TODO: Enable icon
            // Spacer(modifier = Modifier.height(4.dp))
            Text(text, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}


// A ViewModel for preview purposes, providing static data
class PreviewFarmerHomeViewModel : FarmerHomeViewModel(
    weatherRepository = object : com.rooster.farmerhome.domain.repository.WeatherRepository {
        override fun getCurrentWeather(latitude: Double, longitude: Double) =
            kotlinx.coroutines.flow.flowOf( // Simulate DataState for preview
                DataState.Success(WeatherData("25°C", "60%", "0mm", "10km/h", "Sunny", "Preview Location (Coords)"))
            )

        override fun getCurrentWeatherForFarm(farmLocation: String) =
            kotlinx.coroutines.flow.flowOf( // Simulate DataState for preview
                DataState.Success(WeatherData("28°C", "55%", "0.2mm", "12km/h", "Partly Cloudy", farmLocation))
            )
    },
    farmHealthAlertRepository = object : com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository {
        private val mockAlerts = listOf(
            FarmHealthAlert("1", "flockA", "farm123", "High Temp", "Temp high", com.rooster.farmerhome.domain.model.AlertSeverity.HIGH, System.currentTimeMillis() - 100000),
            FarmHealthAlert("2", "flockB", "farm123", "Low Feed", "Feed low", com.rooster.farmerhome.domain.model.AlertSeverity.MEDIUM, System.currentTimeMillis() - 200000, isRead = true)
        )
        override fun getHealthAlertsForFarm(farmId: String) = kotlinx.coroutines.flow.flowOf(DataState.Success(mockAlerts))
        override suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit> = Result.success(Unit) // Added farmId
    },
    productionMetricsRepository = object : com.rooster.farmerhome.domain.repository.ProductionMetricsRepository {
        override fun getProductionSummary(farmId: String) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(ProductionSummary( // Simulate DataState
                totalFlocks = 3,
                activeBirds = 1250,
                overallEggProductionToday = 980,
                weeklyMortalityRate = 0.75,
                metrics = listOf(
                    ProductionMetricItem("Eggs (7d)", "7200", "eggs", MetricTrend.UP, "Last 7 Days"),
                    ProductionMetricItem("Avg. Weight", "58.2", "g", MetricTrend.STABLE, "Current Batch")
                )
            )
        )
    },
    farmDataRepository = object : com.rooster.farmerhome.domain.repository.FarmDataRepository {
        override fun getFarmBasicInfo(farmId: String) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(FarmBasicInfo( // Simulate DataState for preview
                farmId = "farm123-preview",
                farmName = "Preview Farm Deluxe",
                location = "Previewville, State",
                ownerName = "Mr. Previewer",
                activeFlockCount = 4,
                totalCapacity = 3000,
                lastHealthCheckDate = "15 Oct 2023"
            )
        )
    }
) {
    init {
        // Override init block if necessary for preview state
         fetchHealthAlerts("farm123") // Ensure preview model also fetches alerts
         fetchProductionSummary("farm123") // Ensure preview model also fetches summary
         fetchFarmBasicInfo("farm123") // Ensure preview model also fetches farm info
    }
}
