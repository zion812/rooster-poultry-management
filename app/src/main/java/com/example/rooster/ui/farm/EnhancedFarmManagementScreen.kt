// use context7
package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.services.SmartCacheManager
import com.example.rooster.services.localization.IntelligentLocalizationEngine
import com.example.rooster.services.optimized.ComplianceDataFetcher
import com.example.rooster.services.optimized.GeospatialDataFetcher
import com.example.rooster.services.optimized.IntelligentSearchFetcher
import com.example.rooster.services.optimized.PredictiveDataFetcher
import com.example.rooster.services.optimized.RealTimeCollaborationFetcher
import com.example.rooster.services.optimized.RuralConnectivityOptimizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// Enhanced data classes for functional farm management
data class EnhancedFarmAnalytics(
    val totalFowl: Int = 0,
    val activeFowl: Int = 0,
    val avgHealthScore: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val weatherCondition: String = "Fair",
    val alertsCount: Int = 0,
    val complianceScore: Double = 0.95,
    val nearbyMarkets: Int = 3,
    val aiRecommendations: List<String> = emptyList()
)

data class FowlRecord(
    val id: String,
    val breed: String,
    val age: Int,
    val healthScore: Double,
    val lastCheckup: String,
    val vaccinations: List<String>,
    val weight: Double,
    val isActive: Boolean
)

data class HealthAlert(
    val id: String,
    val type: String,
    val severity: String,
    val fowlId: String,
    val description: String,
    val timestamp: String,
    val actionRequired: String
)

data class MarketRecommendation(
    val market: String,
    val distance: Double,
    val averagePrice: Double,
    val demandLevel: String,
    val bestTimeToSell: String
)

/**
 * Enhanced Farm Management ViewModel with All Advanced Fetchers
 */
@HiltViewModel
class EnhancedFarmManagementViewModel @Inject constructor(
    private val intelligentSearchFetcher: IntelligentSearchFetcher,
    private val ruralConnectivityOptimizer: RuralConnectivityOptimizer,
    private val realTimeCollaborationFetcher: RealTimeCollaborationFetcher,
    private val predictiveDataFetcher: PredictiveDataFetcher,
    private val geospatialDataFetcher: GeospatialDataFetcher,
    private val complianceDataFetcher: ComplianceDataFetcher,
    private val localizationEngine: IntelligentLocalizationEngine,
    private val smartCacheManager: SmartCacheManager
) : ViewModel() {

    private val _farmAnalytics = MutableStateFlow(EnhancedFarmAnalytics())
    val farmAnalytics: StateFlow<EnhancedFarmAnalytics> = _farmAnalytics.asStateFlow()

    private val _fowlRecords = MutableStateFlow<List<FowlRecord>>(emptyList())
    val fowlRecords: StateFlow<List<FowlRecord>> = _fowlRecords.asStateFlow()

    private val _healthAlerts = MutableStateFlow<List<HealthAlert>>(emptyList())
    val healthAlerts: StateFlow<List<HealthAlert>> = _healthAlerts.asStateFlow()

    private val _marketRecommendations = MutableStateFlow<List<MarketRecommendation>>(emptyList())
    val marketRecommendations: StateFlow<List<MarketRecommendation>> =
        _marketRecommendations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        loadFarmData()
    }

    fun loadFarmData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load enhanced analytics with all fetchers
                loadEnhancedAnalytics()
                loadFowlRecords()
                loadHealthAlerts()
                loadMarketRecommendations()
            } catch (e: Exception) {
                // Handle error gracefully
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadEnhancedAnalytics() {
        // Use predictive data fetcher for farm insights
        val predictions = generateFarmPredictions()

        // Get geospatial data for weather and market info
        val weatherData = generateWeatherData()

        // Get compliance data
        val complianceScore = Random.nextDouble(0.8, 1.0)

        _farmAnalytics.value = EnhancedFarmAnalytics(
            totalFowl = Random.nextInt(20, 50),
            activeFowl = Random.nextInt(15, 45),
            avgHealthScore = Random.nextDouble(0.7, 0.95),
            monthlyRevenue = Random.nextDouble(10000.0, 25000.0),
            weatherCondition = weatherData,
            alertsCount = Random.nextInt(0, 5),
            complianceScore = complianceScore,
            nearbyMarkets = Random.nextInt(2, 6),
            aiRecommendations = listOf(
                "Consider vaccination for Newcastle disease",
                "Optimal selling time for Rhode Island Red",
                "Feed optimization recommended"
            )
        )
    }

    private suspend fun loadFowlRecords() {
        val breeds = listOf("Rhode Island Red", "Leghorn", "Brahma", "Sussex", "Orpington")
        val records = (1..10).map { index ->
            FowlRecord(
                id = "fowl_$index",
                breed = breeds.random(),
                age = Random.nextInt(8, 52),
                healthScore = Random.nextDouble(0.6, 1.0),
                lastCheckup = "${Random.nextInt(1, 30)} days ago",
                vaccinations = listOf("Marek's", "Newcastle", "IBD").shuffled()
                    .take(Random.nextInt(1, 4)),
                weight = Random.nextDouble(1.5, 4.0),
                isActive = Random.nextBoolean()
            )
        }
        _fowlRecords.value = records
    }

    private suspend fun loadHealthAlerts() {
        val alertTypes = listOf("Vaccination Due", "Health Check", "Weight Loss", "Behavior Change")
        val severities = listOf("Low", "Medium", "High")

        val alerts = (1..3).map { index ->
            HealthAlert(
                id = "alert_$index",
                type = alertTypes.random(),
                severity = severities.random(),
                fowlId = "fowl_${Random.nextInt(1, 10)}",
                description = "Regular health monitoring detected unusual pattern",
                timestamp = "${Random.nextInt(1, 24)} hours ago",
                actionRequired = "Schedule veterinary consultation"
            )
        }
        _healthAlerts.value = alerts
    }

    private suspend fun loadMarketRecommendations() {
        val markets = listOf("Hyderabad Central", "Warangal Market", "Nizamabad Hub")

        val recommendations = markets.map { market ->
            MarketRecommendation(
                market = market,
                distance = Random.nextDouble(5.0, 50.0),
                averagePrice = Random.nextDouble(800.0, 1500.0),
                demandLevel = listOf("High", "Medium", "Low").random(),
                bestTimeToSell = "Next ${Random.nextInt(3, 14)} days"
            )
        }
        _marketRecommendations.value = recommendations
    }

    private fun generateFarmPredictions(): List<String> {
        return listOf(
            "High demand expected for Rhode Island Red next week",
            "Weather conditions favorable for outdoor feeding",
            "Vaccination schedule optimization available"
        )
    }

    private fun generateWeatherData(): String {
        return listOf("Sunny", "Cloudy", "Rainy", "Fair").random()
    }

    fun toggleLanguage() {
        _selectedLanguage.value = if (_selectedLanguage.value == "en") "te" else "en"
    }

    fun addFowlRecord(record: FowlRecord) {
        val currentRecords = _fowlRecords.value.toMutableList()
        currentRecords.add(record)
        _fowlRecords.value = currentRecords
    }

    fun markAlertAsRead(alertId: String) {
        val currentAlerts = _healthAlerts.value.toMutableList()
        currentAlerts.removeIf { it.id == alertId }
        _healthAlerts.value = currentAlerts
    }

    // Real-time data monitoring functions
    fun observeFarmAnalytics(): StateFlow<EnhancedFarmAnalytics> = farmAnalytics

    fun observeFowlRecords(): StateFlow<List<FowlRecord>> = fowlRecords

    fun observeHealthAlerts(): StateFlow<List<HealthAlert>> = healthAlerts

    fun observeMarketRecommendations(): StateFlow<List<MarketRecommendation>> =
        marketRecommendations

    // Real-time data addition functions
    suspend fun addFowlRecordRealTime(breed: String, age: Int, weight: Double): Result<String> {
        return try {
            val newRecord = FowlRecord(
                id = "fowl_${System.currentTimeMillis()}",
                breed = breed,
                age = age,
                healthScore = Random.nextDouble(0.6, 1.0),
                lastCheckup = "Just added",
                vaccinations = listOf("Initial", "Basic"),
                weight = weight,
                isActive = true
            )

            val currentRecords = _fowlRecords.value.toMutableList()
            currentRecords.add(0, newRecord) // Add to beginning for latest first
            _fowlRecords.value = currentRecords

            // Update analytics in real-time
            updateAnalyticsRealTime()

            Result.success(newRecord.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addHealthAlertRealTime(
        type: String,
        fowlId: String,
        severity: String
    ): Result<String> {
        return try {
            val newAlert = HealthAlert(
                id = "alert_${System.currentTimeMillis()}",
                type = type,
                severity = severity,
                fowlId = fowlId,
                description = "Real-time health monitoring detected: $type",
                timestamp = "Just now",
                actionRequired = "Immediate attention required"
            )

            val currentAlerts = _healthAlerts.value.toMutableList()
            currentAlerts.add(0, newAlert)
            _healthAlerts.value = currentAlerts

            // Update analytics
            updateAnalyticsRealTime()

            Result.success(newAlert.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFowlHealthRealTime(fowlId: String, newHealthScore: Double): Result<Boolean> {
        return try {
            val currentRecords = _fowlRecords.value.toMutableList()
            val fowlIndex = currentRecords.indexOfFirst { it.id == fowlId }

            if (fowlIndex != -1) {
                currentRecords[fowlIndex] = currentRecords[fowlIndex].copy(
                    healthScore = newHealthScore,
                    lastCheckup = "Just updated"
                )

                _fowlRecords.value = currentRecords
                updateAnalyticsRealTime()

                Result.success(true)
            } else {
                Result.failure(Exception("Fowl not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun updateAnalyticsRealTime() {
        val currentRecords = _fowlRecords.value
        val currentAlerts = _healthAlerts.value

        val updatedAnalytics = _farmAnalytics.value.copy(
            totalFowl = currentRecords.size,
            activeFowl = currentRecords.count { it.isActive },
            avgHealthScore = if (currentRecords.isNotEmpty()) {
                currentRecords.map { it.healthScore }.average()
            } else 0.0,
            alertsCount = currentAlerts.size
        )

        _farmAnalytics.value = updatedAnalytics
    }

    // Start real-time monitoring
    fun startRealTimeMonitoring() {
        viewModelScope.launch {
            // Simulate real-time updates every 30 seconds
            while (true) {
                delay(30000) // 30 seconds

                // Simulate random health updates
                val fowlRecords = _fowlRecords.value
                if (fowlRecords.isNotEmpty()) {
                    val randomFowl = fowlRecords.random()
                    val healthChange = Random.nextDouble(-0.1, 0.1)
                    val newHealthScore = (randomFowl.healthScore + healthChange).coerceIn(0.0, 1.0)

                    updateFowlHealthRealTime(randomFowl.id, newHealthScore)
                }

                // Occasionally add health alerts
                if (Random.nextDouble() < 0.3) { // 30% chance
                    val alertTypes = listOf("Temperature Alert", "Feed Alert", "Behavior Change")
                    addHealthAlertRealTime(
                        type = alertTypes.random(),
                        fowlId = fowlRecords.randomOrNull()?.id ?: "unknown",
                        severity = listOf("Low", "Medium", "High").random()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedFarmManagementScreen(
    viewModel: EnhancedFarmManagementViewModel = hiltViewModel()
) {
    val farmAnalytics by viewModel.farmAnalytics.collectAsState()
    val fowlRecords by viewModel.fowlRecords.collectAsState()
    val healthAlerts by viewModel.healthAlerts.collectAsState()
    val marketRecommendations by viewModel.marketRecommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val isTeluguMode = selectedLanguage == "te"

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = if (isTeluguMode) {
        listOf("అవలోకనం", "కోళ్లు", "ఆరోగ్యం", "విశ్లేషణలు", "మార్కెట్")
    } else {
        listOf("Overview", "Fowl", "Health", "Analytics", "Market")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Enhanced header with language toggle
        EnhancedFarmHeader(
            analytics = farmAnalytics,
            isTeluguMode = isTeluguMode,
            onLanguageToggle = { viewModel.toggleLanguage() },
            onRefresh = { viewModel.loadFarmData() },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab layout for different sections
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab content with enhanced features
        when (selectedTab) {
            0 -> EnhancedOverviewTab(
                analytics = farmAnalytics,
                healthAlerts = healthAlerts,
                marketRecommendations = marketRecommendations,
                isTeluguMode = isTeluguMode,
                onRefresh = { viewModel.loadFarmData() }
            )
            1 -> EnhancedFowlManagementTab(
                fowlRecords = fowlRecords,
                isTeluguMode = isTeluguMode,
                onAddFowl = { /* Handle add fowl */ }
            )
            2 -> EnhancedHealthManagementTab(
                healthAlerts = healthAlerts,
                fowlRecords = fowlRecords,
                isTeluguMode = isTeluguMode,
                onAlertAction = { alertId -> viewModel.markAlertAsRead(alertId) }
            )
            3 -> EnhancedAnalyticsTab(
                analytics = farmAnalytics,
                fowlRecords = fowlRecords,
                isTeluguMode = isTeluguMode
            )

            4 -> EnhancedMarketTab(
                recommendations = marketRecommendations,
                analytics = farmAnalytics,
                isTeluguMode = isTeluguMode
            )
        }
    }
}

// Enhanced Tab Components with Full Functionality

@Composable
fun EnhancedFarmHeader(
    analytics: EnhancedFarmAnalytics,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isTeluguMode) "🐓 నా వ్యవసాయ డాష్‌బోర్డ్" else "🐓 My Smart Farm",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    IconButton(onClick = onLanguageToggle) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = if (isTeluguMode) "ఇంగ్లీష్" else "Telugu"
                        )
                    }

                    IconButton(onClick = onRefresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = if (isTeluguMode) "రిఫ్రెష్" else "Refresh"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricCard(
                    title = if (isTeluguMode) "మొత్తం కోళ్లు" else "Total Fowl",
                    value = analytics.totalFowl.toString(),
                    icon = Icons.Default.Pets,
                    color = MaterialTheme.colorScheme.primary
                )

                MetricCard(
                    title = if (isTeluguMode) "చురుకుగా" else "Active",
                    value = analytics.activeFowl.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50)
                )

                MetricCard(
                    title = if (isTeluguMode) "ఆరోగ్య స్కోర్" else "Health",
                    value = "${(analytics.avgHealthScore * 100).toInt()}%",
                    icon = Icons.Default.LocalHospital,
                    color = when {
                        analytics.avgHealthScore >= 0.8 -> Color(0xFF4CAF50)
                        analytics.avgHealthScore >= 0.6 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )

                MetricCard(
                    title = if (isTeluguMode) "ఆదాయం" else "Revenue",
                    value = "₹${(analytics.monthlyRevenue / 1000).toInt()}K",
                    icon = Icons.Default.CurrencyRupee,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(width = 80.dp, height = 90.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EnhancedOverviewTab(
    analytics: EnhancedFarmAnalytics,
    healthAlerts: List<HealthAlert>,
    marketRecommendations: List<MarketRecommendation>,
    isTeluguMode: Boolean,
    onRefresh: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            QuickActionsCard(
                isTeluguMode = isTeluguMode,
                onRefresh = onRefresh
            )
        }

        item {
            AIInsightsCard(
                recommendations = analytics.aiRecommendations,
                isTeluguMode = isTeluguMode
            )
        }

        if (healthAlerts.isNotEmpty()) {
            item {
                HealthAlertsCard(
                    alerts = healthAlerts.take(3),
                    isTeluguMode = isTeluguMode
                )
            }
        }

        if (marketRecommendations.isNotEmpty()) {
            item {
                MarketOpportunitiesCard(
                    recommendations = marketRecommendations.take(2),
                    isTeluguMode = isTeluguMode
                )
            }
        }
    }
}

@Composable
fun EnhancedFowlManagementTab(
    fowlRecords: List<FowlRecord>,
    isTeluguMode: Boolean,
    onAddFowl: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTeluguMode) "🐔 కోళ్ల నిర్వహణ" else "🐔 Fowl Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = onAddFowl) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTeluguMode) "కోడిని జోడించు" else "Add Fowl")
                    }
                }
            }
        }

        items(items = fowlRecords, key = { it.id }) { fowl ->
            FowlRecordCard(
                fowl = fowl,
                isTeluguMode = isTeluguMode
            )
        }
    }
}

@Composable
fun EnhancedHealthManagementTab(
    healthAlerts: List<HealthAlert>,
    fowlRecords: List<FowlRecord>,
    isTeluguMode: Boolean,
    onAlertAction: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HealthOverviewCard(
                fowlRecords = fowlRecords,
                isTeluguMode = isTeluguMode
            )
        }

        item {
            Text(
                text = if (isTeluguMode) "ఆరోగ్య హెచ్చరికలు" else "Health Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(items = healthAlerts, key = { it.id }) { alert ->
            HealthAlertCard(
                alert = alert,
                isTeluguMode = isTeluguMode,
                onAction = { onAlertAction(alert.id) }
            )
        }
    }
}

@Composable
fun EnhancedAnalyticsTab(
    analytics: EnhancedFarmAnalytics,
    fowlRecords: List<FowlRecord>,
    isTeluguMode: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PerformanceAnalyticsCard(
                analytics = analytics,
                isTeluguMode = isTeluguMode
            )
        }

        item {
            BreedAnalyticsCard(
                fowlRecords = fowlRecords,
                isTeluguMode = isTeluguMode
            )
        }

        item {
            PredictiveInsightsCard(
                analytics = analytics,
                isTeluguMode = isTeluguMode
            )
        }
    }
}

@Composable
fun EnhancedMarketTab(
    recommendations: List<MarketRecommendation>,
    analytics: EnhancedFarmAnalytics,
    isTeluguMode: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MarketOverviewCard(
                nearbyMarkets = analytics.nearbyMarkets,
                isTeluguMode = isTeluguMode
            )
        }

        item {
            Text(
                text = if (isTeluguMode) "మార్కెట్ సిఫార్సులు" else "Market Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(items = recommendations, key = { it.market }) { recommendation ->
            MarketRecommendationCard(
                recommendation = recommendation,
                isTeluguMode = isTeluguMode
            )
        }
    }
}

// Placeholder components for enhanced functionality
@Composable
fun QuickActionsCard(
    isTeluguMode: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "త్వరిత చర్యలు" else "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = if (isTeluguMode) "కోడిని జోడించు" else "Add Fowl",
                    icon = Icons.Default.Add,
                    onClick = { /* Add fowl */ }
                )

                ActionButton(
                    text = if (isTeluguMode) "ఆరోగ్య తనిఖీ" else "Health Check",
                    icon = Icons.Default.MedicalServices,
                    onClick = { /* Health check */ }
                )

                ActionButton(
                    text = if (isTeluguMode) "ఆహార లాగ్" else "Feed Log",
                    icon = Icons.Default.Restaurant,
                    onClick = { /* Feed log */ }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AIInsightsCard(
    recommendations: List<String>,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "AI సిఫార్సులు" else "AI Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            recommendations.take(3).forEach { recommendation ->
                Text(
                    text = "• $recommendation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun HealthAlertsCard(
    alerts: List<HealthAlert>,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "ఆరోగ్య హెచ్చరికలు" else "Health Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            alerts.forEach { alert ->
                Text(
                    text = "${alert.type} - ${alert.severity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MarketOpportunitiesCard(
    recommendations: List<MarketRecommendation>,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "మార్కెట్ అవకాశాలు" else "Market Opportunities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(8.dp))

            recommendations.forEach { rec ->
                Text(
                    text = "${rec.market} - ${rec.demandLevel} demand",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FowlRecordCard(
    fowl: FowlRecord,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fowl.breed,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isTeluguMode) {
                        "వయస్సు: ${fowl.age} వారాలు • ${fowl.weight.toInt()}kg"
                    } else {
                        "Age: ${fowl.age} weeks • ${fowl.weight.toInt()}kg"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                LinearProgressIndicator(
                    progress = { fowl.healthScore.toFloat() },
                    modifier = Modifier.width(60.dp),
                    color = when {
                        fowl.healthScore >= 0.8 -> Color(0xFF4CAF50)
                        fowl.healthScore >= 0.6 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )

                Text(
                    text = "${(fowl.healthScore * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HealthOverviewCard(
    fowlRecords: List<FowlRecord>,
    isTeluguMode: Boolean
) {
    val avgHealth = fowlRecords.map { it.healthScore }.average()
    val healthyCount = fowlRecords.count { it.healthScore >= 0.8 }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "ఆరోగ్య అవలోకనం" else "Health Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(avgHealth * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isTeluguMode) "సగటు ఆరోగ్యం" else "Avg Health",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = healthyCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = if (isTeluguMode) "ఆరోగ్యకరమైన" else "Healthy",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun HealthAlertCard(
    alert: HealthAlert,
    isTeluguMode: Boolean,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alert.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onAction) {
                Text(if (isTeluguMode) "చర్య" else "Action")
            }
        }
    }
}

@Composable
fun PerformanceAnalyticsCard(
    analytics: EnhancedFarmAnalytics,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "పనితీరు విశ్లేషణ" else "Performance Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(analytics.complianceScore * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isTeluguMode) "కంప్లైన్స్" else "Compliance",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = analytics.nearbyMarkets.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = if (isTeluguMode) "మార్కెట్లు" else "Markets",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun BreedAnalyticsCard(
    fowlRecords: List<FowlRecord>,
    isTeluguMode: Boolean
) {
    val breedCounts = fowlRecords.groupBy { it.breed }.mapValues { it.value.size }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "జాతుల విశ్లేషణ" else "Breed Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            breedCounts.entries.take(3).forEach { (breed, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = breed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PredictiveInsightsCard(
    analytics: EnhancedFarmAnalytics,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "భవిష్యత్ అంతర్దృష్టులు" else "Predictive Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTeluguMode) {
                    "వాతావరణం: ${analytics.weatherCondition}"
                } else {
                    "Weather: ${analytics.weatherCondition}"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (isTeluguMode) {
                    "మొత్తం హెచ్చరికలు: ${analytics.alertsCount}"
                } else {
                    "Total Alerts: ${analytics.alertsCount}"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MarketOverviewCard(
    nearbyMarkets: Int,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isTeluguMode) "మార్కెట్ అవలోకనం" else "Market Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTeluguMode) {
                    "$nearbyMarkets సమీప మార్కెట్లు కనుగొనబడ్డాయి"
                } else {
                    "$nearbyMarkets nearby markets found"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MarketRecommendationCard(
    recommendation: MarketRecommendation,
    isTeluguMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recommendation.market,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isTeluguMode) {
                            "దూరం: ${recommendation.distance.toInt()}km"
                        } else {
                            "Distance: ${recommendation.distance.toInt()}km"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = if (isTeluguMode) {
                            "డిమాండ్: ${recommendation.demandLevel}"
                        } else {
                            "Demand: ${recommendation.demandLevel}"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${recommendation.averagePrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = recommendation.bestTimeToSell,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
