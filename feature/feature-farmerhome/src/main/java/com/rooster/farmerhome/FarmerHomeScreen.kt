package com.rooster.farmerhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import com.rooster.farmerhome.domain.model.MetricTrend
import com.rooster.farmerhome.domain.model.ProductionMetricItem
import com.rooster.farmerhome.domain.model.ProductionSummary
import com.rooster.farmerhome.domain.model.WeatherData
import com.example.rooster.core.common.util.DataState // Corrected import

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel(),
    navController: NavHostController // Added NavController
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
        val pullRefreshState = rememberPullRefreshState(
            refreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh
        )

        Box(
            modifier = Modifier
                .padding(scaffoldPadding) // Apply scaffold padding first
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Make the content column scrollable
                    .padding(16.dp), // Then apply original screen padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isOffline) {
                    Text(
                        stringResource(id = R.string.offline_banner_message), // TODO: Define R.string.offline_banner_message
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(stringResource(id = R.string.farmer_home_title), style = MaterialTheme.typography.headlineMedium) // TODO: Define R.string.farmer_home_title
                Spacer(modifier = Modifier.height(16.dp))

                FarmInfoSection(
                    farmInfoState = uiState.farmInfoState,
                    onRetry = { viewModel.fetchFarmBasicInfo("farm123") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                WeatherSection(
                    weatherState = uiState.weatherState,
                    onRetry = {
                        val location = uiState.farmInfoState.getUnderlyingData()?.location ?: "Krishna District Center"
                        viewModel.fetchWeatherForFarm(location)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FarmHealthAlertsSection(
                    healthAlertsState = uiState.healthAlertsState,
                    onMarkAsRead = { alert -> viewModel.markAlertAsRead(alert.farmId, alert.id) },
                    onRetry = { viewModel.fetchHealthAlerts(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                QuickActionsSection(
                    onLogMortalityClick = { navController.navigate("log_mortality_route") },
                    onRecordFeedingClick = { navController.navigate("record_feeding_route") },
                    onCheckMarketPricesClick = { navController.navigate("market_prices_route") },
                    onAddNewFlockClick = { navController.navigate("add_new_flock_route") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProductionMetricsSection(
                    productionSummaryState = uiState.productionSummaryState,
                    onRetry = { viewModel.fetchProductionSummary(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
                )
            } // End of Column content

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
            Spacer(modifier = Modifier.height(16.dp))

            FarmInfoSection(
                farmInfoState = uiState.farmInfoState,
                onRetry = { viewModel.fetchFarmBasicInfo("farm123") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            WeatherSection(
                weatherState = uiState.weatherState,
                onRetry = {
                    val location = uiState.farmInfoState.getUnderlyingData()?.location ?: "Krishna District Center"
                    viewModel.fetchWeatherForFarm(location)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FarmHealthAlertsSection(
                healthAlertsState = uiState.healthAlertsState,
                onMarkAsRead = { alert -> viewModel.markAlertAsRead(alert.farmId, alert.id) },
                onRetry = { viewModel.fetchHealthAlerts(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickActionsSection(
                onLogMortalityClick = { navController.navigate("log_mortality_route") },
                onRecordFeedingClick = { navController.navigate("record_feeding_route") },
                onCheckMarketPricesClick = { navController.navigate("market_prices_route") },
                onAddNewFlockClick = { navController.navigate("add_new_flock_route") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProductionMetricsSection(
                productionSummaryState = uiState.productionSummaryState,
                onRetry = { viewModel.fetchProductionSummary(uiState.farmInfoState.getUnderlyingData()?.farmId ?: "farm123") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherSection(
    weatherState: DataState<WeatherData?>,
    onRetry: () -> Unit
) {
    val weatherData = weatherState.getUnderlyingData()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.current_weather_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.current_weather_title
            Spacer(modifier = Modifier.height(8.dp))

            when (weatherState) {
                is DataState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    weatherData?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.weather_updating_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.weather_updating_stale
                        WeatherInfoDisplay(staleData)
                    }
                }
                is DataState.Success -> {
                    weatherState.data?.let {
                        if (weatherState.isFromCache && weatherState.isStale) {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(id = R.string.weather_possibly_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.weather_possibly_stale
                                Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                            }
                        } else if (weatherState.isFromCache) {
                            Text(stringResource(id = R.string.weather_cached), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.weather_cached
                        }
                        WeatherInfoDisplay(it)
                    } ?: Text(stringResource(id = R.string.weather_no_data)) // TODO: Define R.string.weather_no_data
                }
                is DataState.Error -> {
                    Text(stringResource(id = R.string.error_prefix) + " ${weatherState.message ?: weatherState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error) // TODO: Define R.string.error_prefix
                    weatherData?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.weather_failed_update_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.weather_failed_update_stale
                        WeatherInfoDisplay(staleData)
                    }
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(id = R.string.retry_button)) // TODO: Define R.string.retry_button
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInfoDisplay(weatherData: WeatherData) {
    weatherData.location?.let { Text(stringResource(id = R.string.weather_location_prefix) + " $it") } // TODO: Define R.string.weather_location_prefix
    Text(stringResource(id = R.string.weather_temperature_prefix) + " ${weatherData.temperature}") // TODO: Define R.string.weather_temperature_prefix
    Text(stringResource(id = R.string.weather_humidity_prefix) + " ${weatherData.humidity}") // TODO: Define R.string.weather_humidity_prefix
    Text(stringResource(id = R.string.weather_precipitation_prefix) + " ${weatherData.precipitation}") // TODO: Define R.string.weather_precipitation_prefix
    Text(stringResource(id = R.string.weather_wind_speed_prefix) + " ${weatherData.windSpeed}") // TODO: Define R.string.weather_wind_speed_prefix
    Text(stringResource(id = R.string.weather_description_prefix) + " ${weatherData.description}") // TODO: Define R.string.weather_description_prefix
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmHealthAlertsSection(
    healthAlertsState: DataState<List<FarmHealthAlert>>,
    onMarkAsRead: (alert: FarmHealthAlert) -> Unit,
    onRetry: () -> Unit
) {
    val alerts = healthAlertsState.getUnderlyingData() ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {
        val unreadCount = if (alerts.isNotEmpty() && healthAlertsState is DataState.Success) alerts.count { !it.isRead } else 0
        val titleSuffix = if (unreadCount > 0) " ($unreadCount ${stringResource(id = R.string.alerts_unread_suffix)})" else "" // TODO: Define R.string.alerts_unread_suffix
        Text(stringResource(id = R.string.farm_health_alerts_title) + titleSuffix, style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.farm_health_alerts_title
        Spacer(modifier = Modifier.height(8.dp))

        when (healthAlertsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (alerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.alerts_updating_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.alerts_updating_stale
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is DataState.Success -> {
                if (alerts.isEmpty()) {
                    Text(stringResource(id = R.string.alerts_no_active)) // TODO: Define R.string.alerts_no_active
                } else {
                    if (healthAlertsState.isFromCache && healthAlertsState.isStale) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(id = R.string.alerts_possibly_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.alerts_possibly_stale
                            Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                        }
                    } else if (healthAlertsState.isFromCache) {
                         Text(stringResource(id = R.string.alerts_cached), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.alerts_cached
                    }
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${healthAlertsState.message ?: healthAlertsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (alerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.alerts_failed_update_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.alerts_failed_update_stale
                    alerts.forEach { alert ->
                        FarmHealthAlertItem(alert = alert, onMarkAsRead = onMarkAsRead)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text(stringResource(id = R.string.retry_button))
                }
            }
        }
    }
}

@Composable
fun FarmHealthAlertItem(alert: FarmHealthAlert, onMarkAsRead: (FarmHealthAlert) -> Unit) {
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
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(alert.title, style = MaterialTheme.typography.titleSmall)
            Text(alert.description, style = MaterialTheme.typography.bodyMedium)
            alert.recommendedAction?.let {
                Text(stringResource(id = R.string.alert_recommendation_prefix) + " $it", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.alert_recommendation_prefix
            }
            Text(stringResource(id = R.string.alert_severity_prefix) + " ${alert.severity}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.alert_severity_prefix
            Text(stringResource(id = R.string.alert_time_prefix) + " ${java.text.SimpleDateFormat("dd/MM/yy HH:mm", java.util.Locale.getDefault()).format(alert.alertDate)}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.alert_time_prefix
            if (!alert.isRead) {
                Button(
                    onClick = { onMarkAsRead(alert) },
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                ) {
                    Text(stringResource(id = R.string.alert_mark_as_read_button)) // TODO: Define R.string.alert_mark_as_read_button
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmInfoSection(
    farmInfoState: DataState<FarmBasicInfo?>,
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
            Text(stringResource(id = R.string.farm_info_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.farm_info_title
            Spacer(modifier = Modifier.height(8.dp))

            when (farmInfoState) {
                is DataState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    farmInfo?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.farm_info_updating_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.farm_info_updating_stale
                        FarmInfoDisplay(staleData)
                    }
                }
                is DataState.Success -> {
                    farmInfo?.let {
                        if (farmInfoState.isFromCache && farmInfoState.isStale) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(id = R.string.farm_info_possibly_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.farm_info_possibly_stale
                                Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                            }
                        } else if (farmInfoState.isFromCache) {
                            Text(stringResource(id = R.string.farm_info_cached), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.farm_info_cached
                        }
                        FarmInfoDisplay(it)
                    } ?: Text(stringResource(id = R.string.farm_info_no_data)) // TODO: Define R.string.farm_info_no_data
                }
                is DataState.Error -> {
                    Text(stringResource(id = R.string.error_prefix) + " ${farmInfoState.message ?: farmInfoState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                    farmInfo?.let { staleData ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.farm_info_failed_update_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.farm_info_failed_update_stale
                        FarmInfoDisplay(staleData)
                    }
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(id = R.string.retry_button))
                    }
                }
            }
        }
    }
}

@Composable
fun FarmInfoDisplay(farmInfo: FarmBasicInfo) {
    Text(stringResource(R.string.farm_info_name_prefix) + " ${farmInfo.farmName}", style = MaterialTheme.typography.bodyLarge) // TODO: Define R.string.farm_info_name_prefix
    Text(stringResource(R.string.farm_info_location_prefix) + " ${farmInfo.location}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.farm_info_location_prefix
    Text(stringResource(R.string.farm_info_owner_prefix) + " ${farmInfo.ownerName}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.farm_info_owner_prefix
    Text(stringResource(R.string.farm_info_active_flocks_prefix) + " ${farmInfo.activeFlockCount}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.farm_info_active_flocks_prefix
    Text(stringResource(R.string.farm_info_total_capacity_prefix) + " ${farmInfo.totalCapacity} " + stringResource(id = R.string.farm_info_birds_suffix), style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.farm_info_total_capacity_prefix and R.string.farm_info_birds_suffix
    farmInfo.lastHealthCheckDate?.let {
        Text(stringResource(R.string.farm_info_last_health_check_prefix) + " $it", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.farm_info_last_health_check_prefix
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionMetricsSection(
    productionSummaryState: DataState<ProductionSummary?>,
    onRetry: () -> Unit
) {
    val summary = productionSummaryState.getUnderlyingData()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.production_metrics_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.production_metrics_title
        Spacer(modifier = Modifier.height(8.dp))

        when (productionSummaryState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                summary?.let { staleData ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.metrics_updating_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.metrics_updating_stale
                    ProductionSummaryDisplay(staleData)
                }
            }
            is DataState.Success -> {
                summary?.let {
                    if (productionSummaryState.isFromCache && productionSummaryState.isStale) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(id = R.string.metrics_possibly_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.metrics_possibly_stale
                            Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                        }
                    } else if (productionSummaryState.isFromCache) {
                        Text(stringResource(id = R.string.metrics_cached), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.metrics_cached
                    }
                    ProductionSummaryDisplay(it)
                } ?: Text(stringResource(id = R.string.metrics_no_data)) // TODO: Define R.string.metrics_no_data
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${productionSummaryState.message ?: productionSummaryState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                summary?.let { staleData ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.metrics_failed_update_stale), style = MaterialTheme.typography.labelSmall) // TODO: Define R.string.metrics_failed_update_stale
                    ProductionSummaryDisplay(staleData)
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text(stringResource(id = R.string.retry_button))
                }
            }
        }
    }
}

@Composable
fun ProductionSummaryDisplay(summary: ProductionSummary) {
    Column {
        Text(stringResource(id = R.string.production_overall_summary_title), style = MaterialTheme.typography.titleSmall) // TODO: Define R.string.production_overall_summary_title
        Text(stringResource(id = R.string.production_total_flocks_prefix) + " ${summary.totalFlocks}") // TODO: Define R.string.production_total_flocks_prefix
        Text(stringResource(id = R.string.production_active_birds_prefix) + " ${summary.activeBirds}") // TODO: Define R.string.production_active_birds_prefix
        Text(stringResource(id = R.string.production_eggs_today_prefix) + " ${summary.overallEggProductionToday} " + stringResource(id = R.string.production_eggs_suffix)) // TODO: Define R.string.production_eggs_today_prefix and R.string.production_eggs_suffix
        Text(stringResource(id = R.string.production_mortality_rate_prefix) + " ${String.format("%.2f%%", summary.weeklyMortalityRate)}") // TODO: Define R.string.production_mortality_rate_prefix

        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(id = R.string.production_detailed_metrics_title), style = MaterialTheme.typography.titleSmall) // TODO: Define R.string.production_detailed_metrics_title
        if (summary.metrics.isEmpty()) {
            Text(stringResource(id = R.string.production_no_detailed_metrics)) // TODO: Define R.string.production_no_detailed_metrics
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                summary.metrics.forEach { metric ->
                    ProductionMetricCard(metric = metric, modifier = Modifier.weight(1f))
                }
            }
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
                fontWeight = FontWeight.Bold
            )
            metric.period?.let {
                Text(stringResource(id = R.string.metric_period_prefix) + " $it", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.metric_period_prefix
            }
            metric.trend?.let {
                Text(stringResource(id = R.string.metric_trend_prefix) + " $it", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.metric_trend_prefix
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.quick_actions_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.quick_actions_title
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            QuickActionItem(
                textResId = R.string.quick_action_log_mortality, // TODO: Define R.string.quick_action_log_mortality
                onClick = onLogMortalityClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                textResId = R.string.quick_action_record_feeding, // TODO: Define R.string.quick_action_record_feeding
                onClick = onRecordFeedingClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                textResId = R.string.quick_action_market_prices, // TODO: Define R.string.quick_action_market_prices
                onClick = onCheckMarketPricesClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                textResId = R.string.quick_action_add_flock, // TODO: Define R.string.quick_action_add_flock
                onClick = onAddNewFlockClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionItem(
    textResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(id = textResId), textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmerHomeScreenPreview() {
    MaterialTheme {
        FarmerHomeScreen(
            viewModel = PreviewFarmerHomeViewModel(),
            navController = rememberNavController() // Add NavController for preview
        )
    }
}

class PreviewFarmerHomeViewModel : FarmerHomeViewModel(
    weatherRepository = object : com.rooster.farmerhome.domain.repository.WeatherRepository {
        override fun getCurrentWeather(latitude: Double, longitude: Double) =
            kotlinx.coroutines.flow.flowOf(
                DataState.Success(WeatherData("25°C", "60%", "0mm", "10km/h", "Sunny", "Preview Location (Coords)"))
            )

        override fun getCurrentWeatherForFarm(farmLocation: String) =
            kotlinx.coroutines.flow.flowOf(
                DataState.Success(WeatherData("28°C", "55%", "0.2mm", "12km/h", "Partly Cloudy", farmLocation))
            )
    },
    farmHealthAlertRepository = object : com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository {
        private val mockAlerts = listOf(
            FarmHealthAlert("1", "flockA", "farm123", "High Temp", "Temp high", com.rooster.farmerhome.domain.model.AlertSeverity.HIGH, System.currentTimeMillis() - 100000),
            FarmHealthAlert("2", "flockB", "farm123", "Low Feed", "Feed low", com.rooster.farmerhome.domain.model.AlertSeverity.MEDIUM, System.currentTimeMillis() - 200000, isRead = true)
        )
        override fun getHealthAlertsForFarm(farmId: String) = kotlinx.coroutines.flow.flowOf(DataState.Success(mockAlerts))
        override suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit> = Result.success(Unit)
    },
    productionMetricsRepository = object : com.rooster.farmerhome.domain.repository.ProductionMetricsRepository {
        override fun getProductionSummary(farmId: String) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(ProductionSummary(
                totalFlocks = 3,
                activeBirds = 1250,
                overallEggProductionToday = 980,
                weeklyMortalityRate = 0.75,
                metrics = listOf(
                    ProductionMetricItem("Eggs (7d)", "7200", "eggs", MetricTrend.UP, "Last 7 Days"),
                    ProductionMetricItem("Avg. Weight", "58.2", "g", MetricTrend.STABLE, "Current Batch")
                )
            ))
        )
    },
    farmDataRepository = object : com.rooster.farmerhome.domain.repository.FarmDataRepository {
        override fun getFarmBasicInfo(farmId: String) = kotlinx.coroutines.flow.flowOf(
            DataState.Success(FarmBasicInfo(
                farmId = "farm123-preview",
                farmName = "Preview Farm Deluxe",
                location = "Previewville, State",
                ownerName = "Mr. Previewer",
                activeFlockCount = 4,
                totalCapacity = 3000,
                lastHealthCheckDate = "15 Oct 2023"
            ))
        )
    }
) {
    init {
         fetchHealthAlerts("farm123")
         fetchProductionSummary("farm123")
         fetchFarmBasicInfo("farm123")
    }
}
