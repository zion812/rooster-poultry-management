package com.example.rooster.admin.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.DecimalFormat
import java.util.Date

// --- Data Classes ---
// Using Maps and Any for flexibility, mirroring Python's dictionary structure.
// For a production app, more specific data classes would be better.

data class KpiData(val metrics: Map<String, Any>)
data class UserBehaviorData(val metrics: Map<String, Any>)
data class RevenueTrackingData(val metrics: Map<String, Any>)
data class SystemPerformanceSummaryData(val metrics: Map<String, Any>)
data class ContentEngagementData(val metrics: Map<String, Any>)
data class TimeSeriesDataPoint(val date: String, val value: Number) // For revenue or user counts
data class TimeSeriesChartData(val name: String, val points: List<TimeSeriesDataPoint>)


// --- ViewModel ---
class AnalyticsDashboardViewModel : ViewModel() {
    private val _kpis = MutableStateFlow<KpiData?>(null)
    val kpis: StateFlow<KpiData?> = _kpis

    private val _userBehavior = MutableStateFlow<UserBehaviorData?>(null)
    val userBehavior: StateFlow<UserBehaviorData?> = _userBehavior

    private val _revenueTracking = MutableStateFlow<RevenueTrackingData?>(null)
    val revenueTracking: StateFlow<RevenueTrackingData?> = _revenueTracking

    private val _systemPerformance = MutableStateFlow<SystemPerformanceSummaryData?>(null)
    val systemPerformance: StateFlow<SystemPerformanceSummaryData?> = _systemPerformance

    private val _contentEngagement = MutableStateFlow<ContentEngagementData?>(null)
    val contentEngagement: StateFlow<ContentEngagementData?> = _contentEngagement

    private val _revenueOverTime = MutableStateFlow<TimeSeriesChartData?>(null)
    val revenueOverTime: StateFlow<TimeSeriesChartData?> = _revenueOverTime

    private val _newUsersOverTime = MutableStateFlow<TimeSeriesChartData?>(null)
    val newUsersOverTime: StateFlow<TimeSeriesChartData?> = _newUsersOverTime

    init {
        loadMockMetrics()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadMockMetrics() {
        // Mock data similar to Python script
        _kpis.value = KpiData(mapOf(
            "total_users" to 15230,
            "active_users_monthly" to 8500,
            "active_users_daily" to 1200,
            "new_signups_last_30_days" to 750,
            "user_retention_rate" to "65%"
        ))

        _userBehavior.value = UserBehaviorData(mapOf(
            "average_session_duration" to "15 minutes",
            "pages_per_session" to 5.2,
            "most_visited_feature" to "Veterinary Consultation",
            "feature_engagement_rate" to mapOf(
                "Consultations" to "40%",
                "Marketplace" to "30%",
                "Educational Content" to "25%",
                "Forum" to "15%"
            ),
            "user_demographics_summary" to mapOf(
                "top_country" to "USA (60%)",
                "user_types" to "Farmers (70%), Veterinarians (30%)"
            )
        ))

        _revenueTracking.value = RevenueTrackingData(mapOf(
            "total_revenue_mtd" to 12500.75,
            "total_revenue_ytd" to 150200.50,
            "average_revenue_per_user_arpu" to 9.86,
            "revenue_by_source" to mapOf(
                "Consultation Fees" to 75000.00,
                "Marketplace Commissions" to 60200.50,
                "Subscription Fees" to 15000.00
            ),
            "recent_transactions_count_last_24h" to 85
        ))

        _systemPerformance.value = SystemPerformanceSummaryData(mapOf(
            "overall_uptime" to "99.98%",
            "average_api_response_time" to "140ms",
            "critical_errors_last_24h" to 2
        ))

        _contentEngagement.value = ContentEngagementData(mapOf(
            "new_articles_published_last_30d" to 25,
            "article_views_last_30d" to 15000,
            "average_time_on_page_articles" to "3 min 45 sec",
            "most_popular_article_id" to "article_vet_health_basics"
        ))

        // Mock time series data
        val today = java.util.Calendar.getInstance()
        val revenuePoints = (0..29).map { i ->
            val day = today.clone() as java.util.Calendar
            day.add(java.util.Calendar.DATE, -i)
            TimeSeriesDataPoint(
                date = "${day.get(java.util.Calendar.YEAR)}-${day.get(java.util.Calendar.MONTH) + 1}-${day.get(java.util.Calendar.DAY_OF_MONTH)}",
                value = (300..800).random().toDouble()
            )
        }.reversed()
        _revenueOverTime.value = TimeSeriesChartData("Revenue Over Time (Daily)", revenuePoints)

        val newUsersPoints = (0..29).map { i ->
            val day = today.clone() as java.util.Calendar
            day.add(java.util.Calendar.DATE, -i)
            TimeSeriesDataPoint(
                date = "${day.get(java.util.Calendar.YEAR)}-${day.get(java.util.Calendar.MONTH) + 1}-${day.get(java.util.Calendar.DAY_OF_MONTH)}",
                value = (10..50).random()
            )
        }.reversed()
        _newUsersOverTime.value = TimeSeriesChartData("New Users Over Time (Daily)", newUsersPoints)
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(viewModel: AnalyticsDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val kpis by viewModel.kpis.collectAsState()
    val userBehavior by viewModel.userBehavior.collectAsState()
    val revenueTracking by viewModel.revenueTracking.collectAsState()
    val systemPerformance by viewModel.systemPerformance.collectAsState()
    val contentEngagement by viewModel.contentEngagement.collectAsState()
    val revenueOverTime by viewModel.revenueOverTime.collectAsState()
    val newUsersOverTime by viewModel.newUsersOverTime.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Yellow) // Example
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            kpis?.let { data -> item { AnalyticsSectionCard("Key Performance Indicators", data.metrics) } }
            userBehavior?.let { data -> item { AnalyticsSectionCard("User Behavior", data.metrics) } }
            revenueTracking?.let { data -> item { AnalyticsSectionCard("Revenue Tracking", data.metrics) } }
            systemPerformance?.let { data -> item { AnalyticsSectionCard("System Performance Summary", data.metrics) } }
            contentEngagement?.let { data -> item { AnalyticsSectionCard("Content Engagement", data.metrics) } }

            revenueOverTime?.let { data -> item { TimeSeriesSection("Revenue Over Last 30 Days (Sample)", data.points) } }
            newUsersOverTime?.let { data -> item { TimeSeriesSection("New Users Over Last 30 Days (Sample)", data.points) } }
        }
    }
}

@Composable
fun AnalyticsSectionCard(title: String, metrics: Map<String, Any>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            DisplayMetrics(metrics)
        }
    }
}

@Composable
fun DisplayMetrics(metrics: Map<String, Any>, indentLevel: Int = 0) {
    val indent = "  ".repeat(indentLevel)
    val decimalFormat = DecimalFormat("#,##0.00")

    metrics.forEach { (key, value) ->
        val displayKey = key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        when (value) {
            is Map<*, *> -> {
                Text("$indent$displayKey:")
                DisplayMetrics(value as Map<String, Any>, indentLevel + 1)
            }
            is Double -> Text("$indent$displayKey: $${decimalFormat.format(value)}")
            is Number -> Text("$indent$displayKey: $value")
            else -> Text("$indent$displayKey: $value")
        }
    }
}

@Composable
fun TimeSeriesSection(title: String, dataPoints: List<TimeSeriesDataPoint>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            // Displaying top 5 for brevity, similar to Python's print output
            dataPoints.take(5).forEach { point ->
                Text("  Date: ${point.date}, Value: ${point.value}")
            }
            if (dataPoints.size > 5) {
                Text("  ... and more data points")
            }
            // Placeholder for chart:
            // Text("Chart would be displayed here using MPAndroidChart or similar.",
            //      modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            //      textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAnalyticsDashboardScreen() {
    MaterialTheme {
        AnalyticsDashboardScreen(viewModel = AnalyticsDashboardViewModel())
    }
}
package com.example.rooster.admin.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.DecimalFormat
import java.util.Date

// --- Data Classes ---
// Using Maps and Any for flexibility, mirroring Python's dictionary structure.
// For a production app, more specific data classes would be better.

data class KpiData(val metrics: Map<String, Any>)
data class UserBehaviorData(val metrics: Map<String, Any>)
data class RevenueTrackingData(val metrics: Map<String, Any>)
data class SystemPerformanceSummaryData(val metrics: Map<String, Any>)
data class ContentEngagementData(val metrics: Map<String, Any>)
data class TimeSeriesDataPoint(val date: String, val value: Number) // For revenue or user counts
data class TimeSeriesChartData(val name: String, val points: List<TimeSeriesDataPoint>)


// --- ViewModel ---
class AnalyticsDashboardViewModel : ViewModel() {
    private val _kpis = MutableStateFlow<KpiData?>(null)
    val kpis: StateFlow<KpiData?> = _kpis

    private val _userBehavior = MutableStateFlow<UserBehaviorData?>(null)
    val userBehavior: StateFlow<UserBehaviorData?> = _userBehavior

    private val _revenueTracking = MutableStateFlow<RevenueTrackingData?>(null)
    val revenueTracking: StateFlow<RevenueTrackingData?> = _revenueTracking

    private val _systemPerformance = MutableStateFlow<SystemPerformanceSummaryData?>(null)
    val systemPerformance: StateFlow<SystemPerformanceSummaryData?> = _systemPerformance

    private val _contentEngagement = MutableStateFlow<ContentEngagementData?>(null)
    val contentEngagement: StateFlow<ContentEngagementData?> = _contentEngagement

    private val _revenueOverTime = MutableStateFlow<TimeSeriesChartData?>(null)
    val revenueOverTime: StateFlow<TimeSeriesChartData?> = _revenueOverTime

    private val _newUsersOverTime = MutableStateFlow<TimeSeriesChartData?>(null)
    val newUsersOverTime: StateFlow<TimeSeriesChartData?> = _newUsersOverTime

    init {
        loadMockMetrics()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadMockMetrics() {
        // Mock data similar to Python script
        _kpis.value = KpiData(mapOf(
            "total_users" to 15230,
            "active_users_monthly" to 8500,
            "active_users_daily" to 1200,
            "new_signups_last_30_days" to 750,
            "user_retention_rate" to "65%"
        ))

        _userBehavior.value = UserBehaviorData(mapOf(
            "average_session_duration" to "15 minutes",
            "pages_per_session" to 5.2,
            "most_visited_feature" to "Veterinary Consultation",
            "feature_engagement_rate" to mapOf(
                "Consultations" to "40%",
                "Marketplace" to "30%",
                "Educational Content" to "25%",
                "Forum" to "15%"
            ),
            "user_demographics_summary" to mapOf(
                "top_country" to "USA (60%)",
                "user_types" to "Farmers (70%), Veterinarians (30%)"
            )
        ))

        _revenueTracking.value = RevenueTrackingData(mapOf(
            "total_revenue_mtd" to 12500.75,
            "total_revenue_ytd" to 150200.50,
            "average_revenue_per_user_arpu" to 9.86,
            "revenue_by_source" to mapOf(
                "Consultation Fees" to 75000.00,
                "Marketplace Commissions" to 60200.50,
                "Subscription Fees" to 15000.00
            ),
            "recent_transactions_count_last_24h" to 85
        ))

        _systemPerformance.value = SystemPerformanceSummaryData(mapOf(
            "overall_uptime" to "99.98%",
            "average_api_response_time" to "140ms",
            "critical_errors_last_24h" to 2
        ))

        _contentEngagement.value = ContentEngagementData(mapOf(
            "new_articles_published_last_30d" to 25,
            "article_views_last_30d" to 15000,
            "average_time_on_page_articles" to "3 min 45 sec",
            "most_popular_article_id" to "article_vet_health_basics"
        ))

        // Mock time series data
        val today = java.util.Calendar.getInstance()
        val revenuePoints = (0..29).map { i ->
            val day = today.clone() as java.util.Calendar
            day.add(java.util.Calendar.DATE, -i)
            TimeSeriesDataPoint(
                date = "${day.get(java.util.Calendar.YEAR)}-${day.get(java.util.Calendar.MONTH) + 1}-${day.get(java.util.Calendar.DAY_OF_MONTH)}",
                value = (300..800).random().toDouble()
            )
        }.reversed()
        _revenueOverTime.value = TimeSeriesChartData("Revenue Over Time (Daily)", revenuePoints)

        val newUsersPoints = (0..29).map { i ->
            val day = today.clone() as java.util.Calendar
            day.add(java.util.Calendar.DATE, -i)
            TimeSeriesDataPoint(
                date = "${day.get(java.util.Calendar.YEAR)}-${day.get(java.util.Calendar.MONTH) + 1}-${day.get(java.util.Calendar.DAY_OF_MONTH)}",
                value = (10..50).random()
            )
        }.reversed()
        _newUsersOverTime.value = TimeSeriesChartData("New Users Over Time (Daily)", newUsersPoints)
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(viewModel: AnalyticsDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val kpis by viewModel.kpis.collectAsState()
    val userBehavior by viewModel.userBehavior.collectAsState()
    val revenueTracking by viewModel.revenueTracking.collectAsState()
    val systemPerformance by viewModel.systemPerformance.collectAsState()
    val contentEngagement by viewModel.contentEngagement.collectAsState()
    val revenueOverTime by viewModel.revenueOverTime.collectAsState()
    val newUsersOverTime by viewModel.newUsersOverTime.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Yellow) // Example
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            kpis?.let { data -> item { AnalyticsSectionCard("Key Performance Indicators", data.metrics) } }
            userBehavior?.let { data -> item { AnalyticsSectionCard("User Behavior", data.metrics) } }
            revenueTracking?.let { data -> item { AnalyticsSectionCard("Revenue Tracking", data.metrics) } }
            systemPerformance?.let { data -> item { AnalyticsSectionCard("System Performance Summary", data.metrics) } }
            contentEngagement?.let { data -> item { AnalyticsSectionCard("Content Engagement", data.metrics) } }

            revenueOverTime?.let { data -> item { TimeSeriesSection("Revenue Over Last 30 Days (Sample)", data.points) } }
            newUsersOverTime?.let { data -> item { TimeSeriesSection("New Users Over Last 30 Days (Sample)", data.points) } }
        }
    }
}

@Composable
fun AnalyticsSectionCard(title: String, metrics: Map<String, Any>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            DisplayMetrics(metrics)
        }
    }
}

@Composable
fun DisplayMetrics(metrics: Map<String, Any>, indentLevel: Int = 0) {
    val indent = "  ".repeat(indentLevel)
    val decimalFormat = DecimalFormat("#,##0.00")

    metrics.forEach { (key, value) ->
        val displayKey = key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        when (value) {
            is Map<*, *> -> {
                Text("$indent$displayKey:")
                DisplayMetrics(value as Map<String, Any>, indentLevel + 1)
            }
            is Double -> Text("$indent$displayKey: $${decimalFormat.format(value)}")
            is Number -> Text("$indent$displayKey: $value")
            else -> Text("$indent$displayKey: $value")
        }
    }
}

@Composable
fun TimeSeriesSection(title: String, dataPoints: List<TimeSeriesDataPoint>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            // Displaying top 5 for brevity, similar to Python's print output
            dataPoints.take(5).forEach { point ->
                Text("  Date: ${point.date}, Value: ${point.value}")
            }
            if (dataPoints.size > 5) {
                Text("  ... and more data points")
            }
            // Placeholder for chart:
            // Text("Chart would be displayed here using MPAndroidChart or similar.",
            //      modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            //      textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAnalyticsDashboardScreen() {
    MaterialTheme {
        AnalyticsDashboardScreen(viewModel = AnalyticsDashboardViewModel())
    }
}
