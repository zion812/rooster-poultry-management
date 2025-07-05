package com.example.rooster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

// Dashboard data loading states
sealed class DashboardLoadingState {
    object Loading : DashboardLoadingState()

    object Success : DashboardLoadingState()

    data class Error(val message: String) : DashboardLoadingState()

    object RefreshingData : DashboardLoadingState()
}

// Dashboard metric cards
data class DashboardMetric(
    val title: String,
    val titleTelugu: String,
    val value: String,
    val trend: String,
    val trendDirection: TrendDirection,
    val icon: ImageVector,
    val color: Color,
    val onClick: (() -> Unit)? = null,
)

enum class TrendDirection {
    UP,
    DOWN,
    STABLE,
}

// Role-specific dashboard configurations
enum class DashboardRole(
    val displayName: String,
    val displayNameTelugu: String,
) {
    FARMER("Farmer", "రైతు"),
    VET("Veterinarian", "పశువైద్యుడు"),
    BUYER("Buyer", "కొనుగోలుదారు"),
    ADMIN("Administrator", "నిర్వాహకుడు"),
}

// Dashboard widgets
data class DashboardWidget(
    val id: String,
    val title: String,
    val titleTelugu: String,
    val widgetType: WidgetType,
    val data: Any,
    val refreshInterval: Long = 300000, // 5 minutes
    val isEnabled: Boolean = true,
    val priority: Int = 0,
)

enum class WidgetType {
    METRIC_CARD,
    CHART,
    LIST,
    ALERT,
    QUICK_ACTION,
    RECENT_ACTIVITY,
}

// Alert types for dashboard
data class DashboardAlert(
    val id: String,
    val title: String,
    val titleTelugu: String,
    val message: String,
    val messageTelugu: String,
    val severity: AlertSeverity,
    val timestamp: Date,
    val isRead: Boolean = false,
    val actionRequired: Boolean = false,
    val actionUrl: String? = null,
)

enum class AlertSeverity(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: ImageVector,
) {
    CRITICAL(
        "Critical",
        "క్రిటికల్",
        Color(0xFFDC2626),
        Icons.Default.Error,
    ),
    WARNING(
        "Warning",
        "హెచ్చరిక",
        Color(0xFFF59E0B),
        Icons.Default.Warning,
    ),
    INFO(
        "Information",
        "సమాచారం",
        Color(0xFF3B82F6),
        Icons.Default.Info,
    ),
    SUCCESS(
        "Success",
        "విజయం",
        Color(0xFF059669),
        Icons.Default.CheckCircle,
    ),
}

// Dashboard helper for role-specific content
object DashboardHelper {
    fun getDefaultMetrics(
        role: DashboardRole,
        isTeluguMode: Boolean,
    ): List<DashboardMetric> {
        return when (role) {
            DashboardRole.FARMER -> getFarmerMetrics(isTeluguMode)
            DashboardRole.VET -> getVetMetrics(isTeluguMode)
            DashboardRole.BUYER -> getBuyerMetrics(isTeluguMode)
            DashboardRole.ADMIN -> getAdminMetrics(isTeluguMode)
        }
    }

    private fun getFarmerMetrics(isTeluguMode: Boolean): List<DashboardMetric> =
        listOf(
            DashboardMetric(
                title = "Total Birds",
                titleTelugu = "మొత్తం పక్షులు",
                value = "247",
                trend = "+12 this week",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.Pets,
                color = Color(0xFF059669),
            ),
            DashboardMetric(
                title = "Active Listings",
                titleTelugu = "క్రియాశీల లిస్టింగులు",
                value = "18",
                trend = "+3 today",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.Store,
                color = Color(0xFF3B82F6),
            ),
            DashboardMetric(
                title = "This Month Revenue",
                titleTelugu = "ఈ నెల ఆదాయం",
                value = "₹25,400",
                trend = "+18% vs last month",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF059669),
            ),
            DashboardMetric(
                title = "Health Alerts",
                titleTelugu = "ఆరోగ్య హెచ్చరికలు",
                value = "3",
                trend = "2 urgent",
                trendDirection = TrendDirection.DOWN,
                icon = Icons.Default.LocalHospital,
                color = Color(0xFFDC2626),
            ),
        )

    private fun getVetMetrics(isTeluguMode: Boolean): List<DashboardMetric> =
        listOf(
            DashboardMetric(
                title = "Consultations Today",
                titleTelugu = "నేటి సంప్రదింపులు",
                value = "12",
                trend = "3 pending",
                trendDirection = TrendDirection.STABLE,
                icon = Icons.Default.MedicalServices,
                color = Color(0xFF3B82F6),
            ),
            DashboardMetric(
                title = "Emergency Cases",
                titleTelugu = "అత్యవసర కేసులు",
                value = "2",
                trend = "High priority",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.Emergency,
                color = Color(0xFFDC2626),
            ),
        )

    private fun getBuyerMetrics(isTeluguMode: Boolean): List<DashboardMetric> =
        listOf(
            DashboardMetric(
                title = "Active Orders",
                titleTelugu = "క్రియాశీల ఆర్డర్లు",
                value = "3",
                trend = "1 in transit",
                trendDirection = TrendDirection.STABLE,
                icon = Icons.Default.ShoppingCart,
                color = Color(0xFF3B82F6),
            ),
            DashboardMetric(
                title = "Saved Items",
                titleTelugu = "సేవ్ చేసిన వస్తువులు",
                value = "28",
                trend = "+5 this week",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.BookmarkBorder,
                color = Color(0xFF8B5CF6),
            ),
        )

    private fun getAdminMetrics(isTeluguMode: Boolean): List<DashboardMetric> =
        listOf(
            DashboardMetric(
                title = "Total Users",
                titleTelugu = "మొత్తం వినియోగదారులు",
                value = "1,247",
                trend = "+48 this week",
                trendDirection = TrendDirection.UP,
                icon = Icons.Default.People,
                color = Color(0xFF059669),
            ),
            DashboardMetric(
                title = "Pending Verifications",
                titleTelugu = "పెండింగ్ ధృవీకరణలు",
                value = "15",
                trend = "5 urgent",
                trendDirection = TrendDirection.DOWN,
                icon = Icons.Default.VerifiedUser,
                color = Color(0xFFF59E0B),
            ),
        )

    fun getRecentAlerts(
        role: DashboardRole,
        isTeluguMode: Boolean,
    ): List<DashboardAlert> {
        // Mock data - replace with actual backend calls
        return listOf(
            DashboardAlert(
                id = "alert_1",
                title = "Vaccination Due",
                titleTelugu = "టీకా గడువు",
                message = "5 birds need vaccination within 3 days",
                messageTelugu = "5 పక్షులకు 3 రోజుల్లో టీకా అవసరం",
                severity = AlertSeverity.WARNING,
                timestamp = Date(),
                actionRequired = true,
                actionUrl = "health_management",
            ),
            DashboardAlert(
                id = "alert_2",
                title = "Order Update",
                titleTelugu = "ఆర్డర్ అప్డేట్",
                message = "Your order #12345 has been shipped",
                messageTelugu = "మీ ఆర్డర్ #12345 రవాణా చేయబడింది",
                severity = AlertSeverity.INFO,
                timestamp = Date(),
            ),
        )
    }
}

// Dashboard metric card composable
@Composable
fun DashboardMetricCard(
    metric: DashboardMetric,
    isTeluguMode: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = metric.color,
                modifier = Modifier.size(32.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTeluguMode) metric.titleTelugu else metric.title,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                )

                Text(
                    text = metric.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector =
                            when (metric.trendDirection) {
                                TrendDirection.UP -> Icons.Default.TrendingUp
                                TrendDirection.DOWN -> Icons.Default.TrendingDown
                                TrendDirection.STABLE -> Icons.Default.TrendingFlat
                            },
                        contentDescription = null,
                        tint =
                            when (metric.trendDirection) {
                                TrendDirection.UP -> Color(0xFF059669)
                                TrendDirection.DOWN -> Color(0xFFDC2626)
                                TrendDirection.STABLE -> Color(0xFF6B7280)
                            },
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = metric.trend,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                    )
                }
            }
        }
    }
}

// Dashboard alerts section
@Composable
fun DashboardAlerts(
    alerts: List<DashboardAlert>,
    isTeluguMode: Boolean = false,
    onAlertClick: (DashboardAlert) -> Unit = {},
) {
    if (alerts.isEmpty()) return

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = if (isTeluguMode) "హెచ్చరికలు" else "Alerts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 300.dp),
        ) {
            items(alerts.sortedByDescending { it.timestamp }) { alert ->
                DashboardAlertCard(
                    alert = alert,
                    isTeluguMode = isTeluguMode,
                    onClick = { onAlertClick(alert) },
                )
            }
        }
    }
}

@Composable
private fun DashboardAlertCard(
    alert: DashboardAlert,
    isTeluguMode: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = alert.severity.color.copy(alpha = 0.1f),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = alert.severity.icon,
                contentDescription = null,
                tint = alert.severity.color,
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTeluguMode) alert.titleTelugu else alert.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827),
                )

                Text(
                    text = if (isTeluguMode) alert.messageTelugu else alert.message,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                )

                if (alert.actionRequired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTeluguMode) "చర్య అవసరం" else "Action Required",
                        fontSize = 10.sp,
                        color = alert.severity.color,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            if (!alert.isRead) {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .background(
                                alert.severity.color,
                                RoundedCornerShape(4.dp),
                            ),
                )
            }
        }
    }
}
