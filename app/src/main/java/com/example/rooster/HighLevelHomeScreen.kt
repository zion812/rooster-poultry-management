package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import android.content.Context
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import com.example.rooster.models.*

suspend fun fetchHighLevelDashboardData(
    onResult: (HighLevelDashboardData) -> Unit,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit
) {
    try {
        delay(1000) // Simulate network delay
        val mockData = HighLevelDashboardData(
            overviewStats = OverviewStats(1250, 850, 15200, 450),
            performanceMetrics = DashboardMetrics(420, 15.0, 45.0, 8.0, 125000.0, 12.0),
            traceabilityMetrics = TraceabilityMetrics(25, 180, 12, 3, 94.5, 6.0),
            analyticsMetrics = AnalyticsMetrics(18.0, 2500.0, 12.5, 2, 98.5, 97.8),
            fraudAlerts = emptyList(),
            farmVerifications = emptyList(),
            userVerifications = emptyList(),
            topFarmers = generateTopFarmers(),
            recentActivities = generateRecentActivities(),
            systemHealth = SystemHealth("Healthy", "Healthy")
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
    onLanguageToggle: () -> Unit
) {
    var dashboardData by remember { mutableStateOf<HighLevelDashboardData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.GOOD) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        networkQuality = assessNetworkQualitySafely(context)
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
                    text = "Admin Dashboard",
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
                text = "Loading dashboard data...",
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
                    text = "Error loading dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
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
                    Text(text = "Retry", color = Color.White)
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
                text = "Overview",
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
                text = stat.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = stat.color,
            )
            Text(
                text = stat.label,
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
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MetricItem(
                    label = "Daily Active Users",
                    value = metrics.dailyActiveUsers.toString(),
                    trend = if (metrics.dauTrend > 0) "↗" else "↘",
                    trendColor = if (metrics.dauTrend > 0) Color.Green else Color.Red,
                )
                MetricItem(
                    label = "Avg Session Time",
                    value = "${metrics.avgSessionMinutes}m",
                    trend = if (metrics.sessionTrend > 0) "↗" else "↘",
                    trendColor = if (metrics.sessionTrend > 0) Color.Green else Color.Red,
                )
                MetricItem(
                    label = "Marketplace Sales",
                    value =
                        NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                            .format(metrics.marketplaceSales),
                    trend = if (metrics.salesTrend > 0) "↗" else "↘",
                    trendColor = if (metrics.salesTrend > 0) Color.Green else Color.Red,
                )
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = trend,
                color = trendColor,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
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
                text = "Transfer Traceability",
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
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
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
                text = "Analytics Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AnalyticsItem(
                    label = "Transfer Velocity",
                    value = "${metrics.transferVelocity}/hr",
                    icon = Icons.Default.SwapVerticalCircle,
                    color = Color(0xFF2196F3),
                )
                AnalyticsItem(
                    label = "Avg Price",
                    value =
                        NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                            .format(metrics.averagePrice),
                    icon = Icons.Default.MonetizationOn,
                    color = Color(0xFF4CAF50),
                )
                AnalyticsItem(
                    label = "Price Variance",
                    value = "${metrics.priceVariance}%",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = Color(0xFFFF9800),
                )
                AnalyticsItem(
                    label = "Suspicious Patterns",
                    value = metrics.suspiciousPatterns.toString(),
                    icon = Icons.Default.Warning,
                    color = Color(0xFF9C27B0),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnalyticsItem(
                    label = "Network Health",
                    value = "${metrics.networkHealth}%",
                    icon = Icons.Default.SignalWifiStatusbar4Bar,
                    color =
                        if (metrics.networkHealth > 90.0) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFFF9800)
                        },
                )
                AnalyticsItem(
                    label = "Data Integrity",
                    value = "${metrics.dataIntegrity}%",
                    icon = Icons.Default.Verified,
                    color =
                        if (metrics.dataIntegrity >= 95.0) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFFF9800)
                        },
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
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
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
                text = "Fraud Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (fraudAlerts.isEmpty()) {
                Text(
                    text = "No fraud alerts",
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
                    text = "Alert: ${alert.alertType} - Entity: ${alert.relatedEntity}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Status: ${alert.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Text(
                text = SimpleDateFormat("HH:mm", Locale.US).format(Date(alert.timestamp)),
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
                text = "Farm Verifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (farmVerifications.isEmpty()) {
                Text(
                    text = "No farm verification records",
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
                            when (verification.riskLevel) {
                                "High" -> Color.Red
                                "Medium" -> Color.Yellow
                                else -> Color.Green
                            },
                        ),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Farm: ${verification.farmName} - Owner: ${verification.ownerName}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Status: ${verification.verificationStatus}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Text(
                text = SimpleDateFormat("dd/MM", Locale.US).format(Date(verification.submittedDate)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
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
                text = "User Verifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (userVerifications.isEmpty()) {
                Text(
                    text = "No user verification records",
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
                            when (verification.verificationLevel) {
                                "High" -> Color.Red
                                "Medium" -> Color.Yellow
                                else -> Color.Green
                            },
                        ),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "User: ${verification.userName} - Type: ${verification.userType}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Progress: ${verification.verificationProgress}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Text(
                text = SimpleDateFormat("dd/MM HH:mm", Locale.US).format(Date(verification.lastActivity)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
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
                text = "Top Performing Farmers",
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
                text = farmer.rank.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = farmer.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${farmer.location} • ${farmer.fowlCount} fowl",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }

        Text(
            text = "${farmer.score}%",
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
                text = "Recent Activities",
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
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = activity.timeAgo,
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
                text = "System Health",
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
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = status,
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
                text = "Quick Actions",
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
                            color = Color(0xFF2196F3),
                        ),
                        QuickAction(
                            title = "User Management",
                            titleTe = "వినియోగదారు నిర్వహణ",
                            icon = Icons.Default.ManageAccounts,
                            route = "user_management",
                            color = Color(0xFF4CAF50),
                        ),
                        QuickAction(
                            title = "Reports",
                            titleTe = "నివేదికలు",
                            icon = Icons.Default.Assessment,
                            route = "reports",
                            color = Color(0xFFFF9800)
                        ),
                        QuickAction(
                            title = "Settings",
                            titleTe = "సెట్టింగులు",
                            icon = Icons.Default.Settings,
                            route = "settings",
                            color = Color(0xFF9C27B0)
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
                text = action.title,
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
        TopFarmer(id = "1", rank = 1, name = "Ravi Kumar", location = "Hyderabad, Telangana", fowlCount = 45, score = 95.0),
        TopFarmer(id = "2", rank = 2, name = "Priya Sharma", location = "Vijayawada, AP", fowlCount = 38, score = 92.0),
        TopFarmer(id = "3", rank = 3, name = "Suresh Reddy", location = "Mysore, Karnataka", fowlCount = 12, score = 94.5),
        TopFarmer(id = "4", rank = 4, name = "Lakshmi Devi", location = "Chennai, Tamil Nadu", fowlCount = 41, score = 87.0),
        TopFarmer(id = "5", rank = 5, name = "Venkat Rao", location = "Warangal, Telangana", fowlCount = 35, score = 85.0),
    )
}

fun generateRecentActivities(): List<RecentActivity> {
    return listOf(
        RecentActivity("user_registration", "New farmer joined from Karimnagar", "2 minutes ago"),
        RecentActivity("marketplace_sale", "Premium rooster sold for ₹8,500", "15 minutes ago"),
        RecentActivity("fowl_added", "Batch of 12 chicks added to lineage", "32 minutes ago"),
        RecentActivity(
            "transfer_completed",
            "Ownership transfer verified successfully",
            "1 hour ago",
        ),
        RecentActivity("user_registration", "High-level user registered", "2 hours ago"),
        RecentActivity("marketplace_sale", "Group buying completed - 25 fowl", "3 hours ago"),
        RecentActivity("fowl_added", "Heritage breed documentation added", "4 hours ago"),
        RecentActivity("transfer_completed", "Cross-state transfer completed", "5 hours ago"),
    )
}
