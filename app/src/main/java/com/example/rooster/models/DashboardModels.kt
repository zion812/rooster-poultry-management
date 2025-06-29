package com.example.rooster.models

/**
 * Dashboard-related data models consolidated from various locations
 */

data class HighLevelDashboardData(
    val overviewStats: OverviewStats = OverviewStats(),
    val performanceMetrics: DashboardMetrics = DashboardMetrics(),
    val traceabilityMetrics: TraceabilityMetrics = TraceabilityMetrics(),
    val analyticsMetrics: AnalyticsMetrics = AnalyticsMetrics(),
    val fraudAlerts: List<FraudAlert> = emptyList(),
    val farmVerifications: List<FarmVerification> = emptyList(),
    val userVerifications: List<UserVerification> = emptyList(),
    val topFarmers: List<TopFarmer> = emptyList(),
    val recentActivities: List<RecentActivity> = emptyList(),
    val systemHealth: SystemHealth = SystemHealth(),
)

data class OverviewStats(
    val totalUsers: Int = 0,
    val activeFarmers: Int = 0,
    val totalFowl: Int = 0,
    val marketplaceItems: Int = 0,
)

data class DashboardMetrics(
    val dailyActiveUsers: Int = 0,
    val dauTrend: Double = 0.0,
    val avgSessionMinutes: Double = 0.0,
    val sessionTrend: Double = 0.0,
    val marketplaceSales: Double = 0.0,
    val salesTrend: Double = 0.0,
)

data class TraceabilityMetrics(
    val activeTransfersCount: Int = 0,
    val completedTransfersCount: Int = 0,
    val pendingVerifications: Int = 0,
    val fraudAlertsCount: Int = 0,
    val verificationSuccessRate: Double = 0.0,
    val avgTransferTime: Double = 0.0,
    val activeTransfers: Int = 0, // Legacy compatibility
    val completedTransfers: Int = 0, // Legacy compatibility
)

data class AnalyticsMetrics(
    val transferVelocity: Double = 0.0,
    val averagePrice: Double = 0.0,
    val priceVariance: Double = 0.0,
    val suspiciousPatterns: Int = 0,
    val networkHealth: Double = 100.0,
    val dataIntegrity: Double = 100.0,
)

data class SystemHealth(
    val serverStatus: String = "HEALTHY",
    val databaseStatus: String = "HEALTHY",
)

data class FraudAlert(
    val alertId: String = "",
    val severity: String = "LOW",
    val alertType: String = "",
    val relatedEntity: String = "",
    val status: String = "PENDING",
    val timestamp: Long = System.currentTimeMillis(),
)

data class FarmVerification(
    val verificationId: String = "",
    val farmName: String = "",
    val ownerName: String = "",
    val verificationStatus: String = "PENDING",
    val riskLevel: String = "LOW",
    val submittedDate: Long = System.currentTimeMillis(),
)

data class UserVerification(
    val verificationId: String = "",
    val userName: String = "",
    val userType: String = "",
    val verificationProgress: Double = 0.0,
    val verificationLevel: String = "BASIC",
    val lastActivity: Long = System.currentTimeMillis(),
    val documentsSubmitted: Int = 0,
)

data class TopFarmer(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val fowlCount: Int = 0,
    val score: Double = 0.0,
    val rank: Int = 0,
)

data class RecentActivity(
    val id: String = "",
    val type: String = "",
    val description: String = "",
    val timeAgo: String = "",
)

// Additional models referenced in UI
data class StatItem(
    val label: String,
    val value: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
)

data class QuickAction(
    val title: String,
    val titleTe: String = "",
    val description: String = "",
    val descriptionTe: String = "",
    val route: String = "",
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color,
)

// Add missing NetworkQualityLevel enum
enum class NetworkQualityLevel {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    VERY_POOR
}
