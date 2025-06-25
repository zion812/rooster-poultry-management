package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rooster.models.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

suspend fun fetchHighLevelDashboardData(
    onResult: (HighLevelDashboardData) -> Unit,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        delay(1000) // Simulate network delay
        val mockData =
            HighLevelDashboardData(
                overviewStats = OverviewStats(1250, 850, 15200, 450),
                performanceMetrics = DashboardMetrics(420, 15.0, 45.0, 8.0, 125000.0, 12.0),
                traceabilityMetrics = TraceabilityMetrics(25, 180, 12, 3, 94.5, 6.0),
                analyticsMetrics = AnalyticsMetrics(18.0, 2500.0, 12.5, 2, 98.5, 97.8),
                fraudAlerts = emptyList(),
                farmVerifications = emptyList(),
                userVerifications = emptyList(),
                topFarmers = generateTopFarmers(),
                recentActivities = generateRecentActivities(),
                systemHealth = SystemHealth("Healthy", "Healthy"),
            )
        onResult(mockData)
        setLoading(false)
    } catch (e: Exception) {
        onError(e.message ?: "Unknown error occurred")
        setLoading(false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighLevelHomeScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    var dashboardData by remember { mutableStateOf<HighLevelDashboardData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.GOOD) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // --- FIX: Remove unresolved assessNetworkQualitySafely usage ---
        // networkQuality = assessNetworkQualitySafely(context)
        fetchHighLevelDashboardData(
            onResult = { dashboardData = it },
            onError = { error = it },
            setLoading = { loading = it },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Admin Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF5722),
                    titleContentColor = Color.White,
                ),
        )

        if (loading) {
            LoadingContent()
        } else if (error != null) {
            ErrorContent(error = error!!) {
                error = null
                loading = true
                // Retry logic here
            }
        } else if (dashboardData != null) {
            DashboardContent(
                data = dashboardData!!,
                networkQuality = networkQuality,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFFFF5722),
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Loading dashboard data...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Error loading dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5722),
                        ),
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: HighLevelDashboardData,
    networkQuality: NetworkQualityLevel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Overview Stats
        item {
            OverviewStatsSection(data.overviewStats)
        }

        // Performance Metrics
        item {
            DashboardMetricsSection(data.performanceMetrics)
        }

        // Traceability Metrics
        item {
            TraceabilityMetricsSection(data.traceabilityMetrics)
        }

        // Analytics Metrics
        item {
            AnalyticsMetricsSection(data.analyticsMetrics)
        }

        // Fraud Alerts
        item {
            FraudAlertsSection(data.fraudAlerts)
        }

        // Farm Verifications
        item {
            FarmVerificationsSection(data.farmVerifications)
        }

        // User Verifications
        item {
            UserVerificationsSection(data.userVerifications)
        }

        // Top Farmers
        item {
            TopFarmersSection(data.topFarmers)
        }

        // Recent Activities
        item {
            RecentActivitiesSection(data.recentActivities)
        }

        // System Health
        item {
            SystemHealthSection(data.systemHealth, networkQuality)
        }

        // Quick Actions
        item {
            QuickActionsSection()
        }
    }
}

@Composable
private fun OverviewStatsSection(stats: OverviewStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        StatItem(
                            "Total Users",
                            stats.totalUsers.toString(),
                            Icons.Default.People,
                            Color(0xFF4CAF50),
                        ),
                        StatItem(
                            "Active Farmers",
                            stats.activeFarmers.toString(),
                            Icons.Default.Agriculture,
                            Color(0xFF2196F3),
                        ),
                        StatItem(
                            "Total Fowl",
                            stats.totalFowl.toString(),
                            Icons.Default.Pets,
                            Color(0xFFFF9800),
                        ),
                        StatItem(
                            "Marketplace Items",
                            stats.marketplaceItems.toString(),
                            Icons.Default.Store,
                            Color(0xFF9C27B0),
                        ),
                    ),
                ) { statItem ->
                    StatCard(statItem)
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: StatItem) {
    Card(
        modifier =
            Modifier
                .width(140.dp)
                .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = stat.color.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                stat.icon,
                contentDescription = null,
                tint = stat.color,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stat.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = stat.color,
            )
            Text(
                stat.label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DashboardMetricsSection(metrics: DashboardMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Performance Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            val items =
                listOf(
                    MetricItemData("DAU", metrics.dailyActiveUsers.toString(), metrics.dauTrend),
                    MetricItemData("Avg Session", "${String.format("%.1f", metrics.avgSessionMinutes)}m", metrics.sessionTrend),
                    MetricItemData("Sales (24h)", "₹${String.format("%.0f", metrics.marketplaceSales)}", metrics.salesTrend),
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEach { item ->
                    MetricItem(
                        label = item.label,
                        value = item.value,
                        trend = if (item.trend > 0) "↗" else "↘",
                        trendColor = if (item.trend > 0) Color.Green else Color.Red,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    trend: String,
    trendColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = trend,
                color = trendColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TraceabilityMetricsSection(metrics: TraceabilityMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Transfer Traceability",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TraceabilityItem(
                    label = "Active Transfers",
                    value = metrics.activeTransfers.toString(),
                    icon = Icons.Default.SwapHoriz,
                    color = Color(0xFF2196F3),
                )
                TraceabilityItem(
                    label = "Completed Transfers",
                    value = metrics.completedTransfers.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                )
                TraceabilityItem(
                    label = "Verification Success",
                    value = "${metrics.verificationSuccessRate}%",
                    icon = Icons.Default.Verified,
                    color = Color(0xFFFF9800),
                )
                TraceabilityItem(
                    label = "Avg Transfer Time",
                    value = "${metrics.avgTransferTime}h",
                    icon = Icons.Default.Timer,
                    color = Color(0xFF9C27B0),
                )
            }
        }
    }
}

@Composable
private fun TraceabilityItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun AnalyticsMetricsSection(metrics: AnalyticsMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Analytics Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            val items =
                listOf(
                    MetricItemData("Transfer Velocity", "${String.format("%.2f", metrics.transferVelocity)}", 0.0),
                    MetricItemData("Avg Price", "₹${String.format("%.2f", metrics.averagePrice)}", metrics.priceVariance),
                    MetricItemData("Suspicious Patterns", metrics.suspiciousPatterns.toString(), 0.0),
                    MetricItemData("Network Health", "${String.format("%.1f", metrics.networkHealth)}%", 0.0),
                    MetricItemData("Data Integrity", "${String.format("%.1f", metrics.dataIntegrity)}%", 0.0),
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEach { item ->
                    MetricItem(
                        label = item.label,
                        value = item.value,
                        trend = if (item.trend > 0) "↗" else "↘",
                        trendColor = if (item.trend > 0) Color.Green else Color.Red,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricItem(
                    label = "Network Health",
                    value = "${String.format("%.1f", metrics.networkHealth)}%",
                    trend = if (metrics.networkHealth > 90.0) "↗" else "↘",
                    trendColor = if (metrics.networkHealth > 90.0) Color.Green else Color.Red,
                )
                MetricItem(
                    label = "Data Integrity",
                    value = "${String.format("%.1f", metrics.dataIntegrity)}%",
                    trend = if (metrics.dataIntegrity >= 95.0) "↗" else "↘",
                    trendColor = if (metrics.dataIntegrity >= 95.0) Color.Green else Color.Red,
                )
            }
        }
    }
}

@Composable
private fun AnalyticsItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun FraudAlertsSection(fraudAlerts: List<FraudAlert>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Fraud Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (fraudAlerts.isEmpty()) {
                Text(
                    "No fraud alerts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(160.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(fraudAlerts) { alert ->
                        FraudAlertItem(alert)
                    }
                }
            }
        }
    }
}

@Composable
private fun FraudAlertItem(alert: FraudAlert) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (alert.severity) {
                                "High" -> Color.Red
                                "Medium" -> Color.Yellow
                                else -> Color.Green
                            },
                        ),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Alert: ${alert.alertType} - Entity: ${alert.relatedEntity}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Status: ${alert.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Text(
                SimpleDateFormat("HH:mm", Locale.US).format(Date(alert.timestamp)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }
    }
}

@Composable
private fun FarmVerificationsSection(farmVerifications: List<FarmVerification>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Farm Verifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (farmVerifications.isEmpty()) {
                Text(
                    "No farm verification records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(160.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(farmVerifications) { verification ->
                        FarmVerificationItem(verification)
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmVerificationItem(verification: FarmVerification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = "Farm Verification")
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(verification.farmName, fontWeight = FontWeight.Bold)
                Text("Owner: ${verification.ownerName}", style = MaterialTheme.typography.bodySmall)
            }
            Text(verification.verificationStatus, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun UserVerificationsSection(userVerifications: List<UserVerification>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "User Verifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (userVerifications.isEmpty()) {
                Text(
                    "No user verification records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(160.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(userVerifications) { verification ->
                        UserVerificationItem(verification)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserVerificationItem(verification: UserVerification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Person, contentDescription = "User Verification")
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(verification.userName, fontWeight = FontWeight.Bold)
                Text("ID: ${verification.verificationId}", style = MaterialTheme.typography.bodySmall)
            }
            Text(verification.verificationLevel, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TopFarmersSection(farmers: List<TopFarmer>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Top Performing Farmers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            farmers.take(5).forEach { farmer ->
                FarmerRankItem(farmer)
                if (farmer != farmers.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FarmerRankItem(farmer: TopFarmer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Rank Badge
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (farmer.rank) {
                            1 -> Color(0xFFFFD700) // Gold
                            2 -> Color(0xFFC0C0C0) // Silver
                            3 -> Color(0xFFCD7F32) // Bronze
                            else -> Color(0xFFFF5722)
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                farmer.rank.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                farmer.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                "${farmer.location} • ${farmer.fowlCount} fowl",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }

        Text(
            "${farmer.score}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722),
        )
    }
}

@Composable
private fun RecentActivitiesSection(activities: List<RecentActivity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Recent Activities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            activities.take(10).forEach { activity ->
                ActivityItem(activity)
                if (activity != activities.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: RecentActivity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            when (activity.type) {
                "user_registration" -> Icons.Default.PersonAdd
                "fowl_added" -> Icons.Default.Add
                "marketplace_sale" -> Icons.Default.ShoppingCart
                "transfer_completed" -> Icons.Default.SwapHoriz
                else -> Icons.Default.Info
            },
            contentDescription = null,
            tint = Color(0xFFFF5722),
            modifier = Modifier.size(20.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                activity.description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                activity.timeAgo,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun SystemHealthSection(
    health: SystemHealth,
    networkQuality: NetworkQualityLevel,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "System Health",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HealthIndicator(
                    label = "Server Status",
                    status = health.serverStatus,
                    isHealthy = health.serverStatus == "Healthy",
                )
                HealthIndicator(
                    label = "Database",
                    status = health.databaseStatus,
                    isHealthy = health.databaseStatus == "Healthy",
                )
                HealthIndicator(
                    label = "Network",
                    status = networkQuality.name,
                    isHealthy = networkQuality == NetworkQualityLevel.EXCELLENT || networkQuality == NetworkQualityLevel.GOOD,
                )
            }
        }
    }
}

@Composable
private fun HealthIndicator(
    label: String,
    status: String,
    isHealthy: Boolean,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier =
                Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isHealthy) Color.Green else Color.Red),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
        Text(
            status,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHealthy) Color.Green else Color.Red,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun QuickActionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        QuickAction(
                            title = "Send Notification",
                            titleTe = "నోటిఫికేషన్ పంపండి",
                            icon = Icons.Default.Notifications,
                            route = "notifications",
                            description = "Send alerts to farmers",
                            descriptionTe = "రైతులకు హెచ్చరికలు పంపండి",
                            color = Color(0xFF2196F3),
                        ),
                        QuickAction(
                            title = "User Management",
                            titleTe = "వినియోగదారు నిర్వహణ",
                            icon = Icons.Default.ManageAccounts,
                            route = "user_management",
                            description = "Manage user accounts",
                            descriptionTe = "వినియోగదారు ఖాతాలను నిర్వహించండి",
                            color = Color(0xFF4CAF50),
                        ),
                        QuickAction(
                            title = "Reports",
                            titleTe = "నివేదికలు",
                            icon = Icons.Default.Assessment,
                            route = "reports",
                            description = "Generate reports",
                            descriptionTe = "నివేదికలను రూపొందించండి",
                            color = Color(0xFFFF9800),
                        ),
                        QuickAction(
                            title = "Settings",
                            titleTe = "సెట్టింగులు",
                            icon = Icons.Default.Settings,
                            route = "settings",
                            description = "System settings",
                            descriptionTe = "సిస్టమ్ సెట్టింగులు",
                            color = Color(0xFF9C27B0),
                        ),
                    ),
                ) { action ->
                    QuickActionCard(action)
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction) {
    Card(
        modifier =
            Modifier
                .width(120.dp)
                .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = action.color.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                action.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

fun generateTopFarmers(): List<TopFarmer> {
    return listOf(
        TopFarmer(id = "1", name = "Ravi Kumar", location = "Hyderabad, Telangana", fowlCount = 45, score = 95.0, rank = 1),
        TopFarmer(id = "2", name = "Priya Sharma", location = "Vijayawada, AP", fowlCount = 38, score = 92.0, rank = 2),
        TopFarmer(id = "3", name = "Suresh Reddy", location = "Mysore, Karnataka", fowlCount = 52, score = 90.0, rank = 3),
        TopFarmer(id = "4", name = "Lakshmi Devi", location = "Chennai, Tamil Nadu", fowlCount = 41, score = 87.0, rank = 4),
        TopFarmer(id = "5", name = "Venkat Rao", location = "Warangal, Telangana", fowlCount = 35, score = 85.0, rank = 5),
    )
}

fun generateRecentActivities(): List<RecentActivity> {
    return listOf(
        RecentActivity(id = "1", type = "farm_verification", description = "New farm 'Green Meadows' verified", timeAgo = "Just now"),
        RecentActivity(id = "2", type = "user_update", description = "User 'Ramesh K.' updated profile", timeAgo = "5m ago"),
        RecentActivity(id = "3", type = "fraud_alert", description = "Fraud alert triggered for transaction #T12345", timeAgo = "15m ago"),
        RecentActivity(id = "4", type = "system_update", description = "System maintenance scheduled for 2 AM", timeAgo = "1h ago"),
        RecentActivity(
            id = "5",
            type = "marketplace_update",
            description = "New marketplace item 'Organic Feed' added",
            timeAgo = "3h ago",
        ),
    )
}

private data class MetricItemData(val label: String, val value: String, val trend: Double)
