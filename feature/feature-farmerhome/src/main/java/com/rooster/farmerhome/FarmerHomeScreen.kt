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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rooster.farmerhome.domain.model.WeatherData

@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Farmer Home Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        FarmInfoSection(
            farmInfo = uiState.farmBasicInfo,
            isLoading = uiState.isLoadingFarmInfo,
            error = uiState.farmInfoError,
            onRetry = { viewModel.fetchFarmBasicInfo("farm123") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        WeatherSection(
            weatherData = uiState.weatherData,
            isLoading = uiState.isLoadingWeather,
            error = uiState.weatherError,
            onRetry = { viewModel.fetchWeatherForFarm("Krishna District Center") } // Example retry
        )

        Spacer(modifier = Modifier.height(16.dp))

        FarmHealthAlertsSection(
            alerts = uiState.farmHealthAlerts,
            isLoading = uiState.isLoadingAlerts,
            error = uiState.alertsError,
            onMarkAsRead = { alertId -> viewModel.markAlertAsRead(alertId) },
            onRetry = { viewModel.fetchHealthAlerts("farm123") } // Example retry
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
            summary = uiState.productionSummary,
            isLoading = uiState.isLoadingProductionSummary,
            error = uiState.productionSummaryError,
            onRetry = { viewModel.fetchProductionSummary("farm123") } // Example retry
        )

        // TODO: Add other sections if any
    }
}

@Composable
fun WeatherSection(
    weatherData: WeatherData?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
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

            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
                // Button("Retry", onClick = onRetry) // Optional retry button
            } else if (weatherData != null) {
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


@Composable
fun FarmHealthAlertsSection(
    alerts: List<FarmHealthAlert>,
    isLoading: Boolean,
    error: String?,
    onMarkAsRead: (String) -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Text("Farm Health Alerts", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
            // Button("Retry", onClick = onRetry) // Optional
        } else if (alerts.isEmpty()) {
            Text("No active health alerts.")
        } else {
            alerts.forEach { alert ->
                FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FarmHealthAlertItem(alert: FarmHealthAlert, onMarkAsRead: (String) -> Unit) {
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
                    onClick = { onMarkAsRead(alert.id) },
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

@Composable
fun FarmInfoSection(
    farmInfo: FarmBasicInfo?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text("My Farm", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
                // Button("Retry", onClick = onRetry) // Optional
            } else if (farmInfo != null) {
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


@Composable
fun ProductionMetricsSection(
    summary: ProductionSummary?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column {
        Text("Production Metrics", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
            // Button("Retry", onClick = onRetry) // Optional
        } else if (summary != null) {
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
            kotlinx.coroutines.flow.flowOf(
                WeatherData("25°C", "60%", "0mm", "10km/h", "Sunny", "Preview Location")
            )

        override fun getCurrentWeatherForFarm(farmLocation: String) =
            kotlinx.coroutines.flow.flowOf(
                WeatherData("28°C", "55%", "0.2mm", "12km/h", "Partly Cloudy", farmLocation)
            )
    },
    farmHealthAlertRepository = object : com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository {
        private val mockAlerts = listOf(
            FarmHealthAlert("1", "flockA", "farm123", "High Temp", "Temp high", com.rooster.farmerhome.domain.model.AlertSeverity.HIGH, System.currentTimeMillis() - 100000),
            FarmHealthAlert("2", "flockB", "farm123", "Low Feed", "Feed low", com.rooster.farmerhome.domain.model.AlertSeverity.MEDIUM, System.currentTimeMillis() - 200000, isRead = true)
        )
        override fun getHealthAlertsForFarm(farmId: String) = kotlinx.coroutines.flow.flowOf(mockAlerts)
        override suspend fun markAlertAsRead(alertId: String): Result<Unit> = Result.success(Unit)
    },
    productionMetricsRepository = object : com.rooster.farmerhome.domain.repository.ProductionMetricsRepository {
        override fun getProductionSummary(farmId: String) = kotlinx.coroutines.flow.flowOf(
            ProductionSummary(
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
            FarmBasicInfo(
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
