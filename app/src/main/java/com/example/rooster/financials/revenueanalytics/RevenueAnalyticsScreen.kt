package com.example.rooster.financials.revenueanalytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.util.Calendar
import kotlin.random.Random

// --- Data Classes ---
// Using Map<String, Any> for flexibility, mirroring Python's dictionary structure.
// For a production app, more specific data classes would be better.
data class RevenueSummary(val metrics: Map<String, Any>)
data class RevenueBySource(val periodData: Map<String, Map<String, Double>>) // MTD/YTD -> Source -> Amount
data class RevenueTrendPoint(val label: String, val revenue: Double) // Date or Month string
data class RevenueTrend(val granularity: String, val points: List<RevenueTrendPoint>)
data class ClvData(val segments: Map<String, Double>)
data class TopRevenueItem(val name: String, val type: String, val revenueMtd: Double)

// --- ViewModel ---
class RevenueAnalyticsViewModel : ViewModel() {
    private val _summary = MutableStateFlow<RevenueSummary?>(null)
    val summary: StateFlow<RevenueSummary?> = _summary

    private val _revenueBySource = MutableStateFlow<RevenueBySource?>(null)
    val revenueBySource: StateFlow<RevenueBySource?> = _revenueBySource

    private val _dailyTrend = MutableStateFlow<RevenueTrend?>(null)
    val dailyTrend: StateFlow<RevenueTrend?> = _dailyTrend

    private val _monthlyTrend = MutableStateFlow<RevenueTrend?>(null)
    val monthlyTrend: StateFlow<RevenueTrend?> = _monthlyTrend

    private val _clv = MutableStateFlow<ClvData?>(null)
    val clv: StateFlow<ClvData?> = _clv

    private val _topItems = MutableStateFlow<List<TopRevenueItem>>(emptyList())
    val topItems: StateFlow<List<TopRevenueItem>> = _topItems

    init {
        loadMockRevenueData()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadMockRevenueData() {
        val sources = listOf("Consultation Fees", "Marketplace Commissions", "Subscription Fees", "Premium Content Sales")
        val revenueBySourceMtd = sources.associateWith { Random.nextDouble(1000.0, 10000.0) }
        val revenueBySourceYtd = revenueBySourceMtd.mapValues { it.value * Random.nextDouble(8.0, 12.0) }

        val totalRevenueMtd = revenueBySourceMtd.values.sum()
        val totalRevenueYtd = revenueBySourceYtd.values.sum()

        _summary.value = RevenueSummary(mapOf(
            "total_revenue_mtd" to totalRevenueMtd,
            "total_revenue_ytd" to totalRevenueYtd,
            "average_revenue_per_paying_user_arppu_monthly" to totalRevenueMtd / (1000 + Random.nextInt(-100, 100)),
            "churn_rate_monthly" to "${Random.nextDouble(1.5, 5.0).format(2)}%"
        ))

        _revenueBySource.value = RevenueBySource(mapOf("mtd" to revenueBySourceMtd, "ytd" to revenueBySourceYtd))

        val today = Calendar.getInstance()
        val dailyPoints = (0..29).map { i ->
            val day = today.clone() as Calendar
            day.add(Calendar.DATE, -i)
            RevenueTrendPoint(
                label = "${day.get(Calendar.YEAR)}-${day.get(Calendar.MONTH) + 1}-${day.get(Calendar.DAY_OF_MONTH)}",
                revenue = sources.sumOf { Random.nextDouble(50.0, 200.0) }
            )
        }.reversed()
        _dailyTrend.value = RevenueTrend("Daily", dailyPoints)

        val monthlyPoints = (0..11).map { i ->
            val currentMonthCal = today.clone() as Calendar
            currentMonthCal.add(Calendar.MONTH, -i)
            RevenueTrendPoint(
                label = "${currentMonthCal.get(Calendar.YEAR)}-${currentMonthCal.get(Calendar.MONTH) + 1}",
                revenue = sources.sumOf { Random.nextDouble(5000.0, 20000.0) }
            )
        }.reversed()
        _monthlyTrend.value = RevenueTrend("Monthly", monthlyPoints)

        _clv.value = ClvData(mapOf(
            "farmers_standard" to Random.nextDouble(50.0, 150.0),
            "farmers_premium" to Random.nextDouble(150.0, 400.0),
            "veterinarians_basic" to Random.nextDouble(100.0, 250.0),
            "veterinarians_pro" to Random.nextDouble(250.0, 600.0),
            "overall_average" to (List(4) { Random.nextDouble(50.0, 600.0) }).average()
        ))

        _topItems.value = listOf(
            TopRevenueItem("consult_specialist_A", "Consultation", Random.nextDouble(500.0, 1500.0)),
            TopRevenueItem("product_feed_B", "Marketplace", Random.nextDouble(300.0, 1000.0)),
            TopRevenueItem("subscription_vet_pro", "Subscription", Random.nextDouble(1000.0, 2000.0))
        )
    }
    // Helper extension for formatting
    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueAnalyticsScreen(viewModel: RevenueAnalyticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val summary by viewModel.summary.collectAsState()
    val revenueBySource by viewModel.revenueBySource.collectAsState()
    val dailyTrend by viewModel.dailyTrend.collectAsState()
    val monthlyTrend by viewModel.monthlyTrend.collectAsState()
    val clv by viewModel.clv.collectAsState()
    val topItems by viewModel.topItems.collectAsState()

    val decimalFormat = remember { DecimalFormat("$ #,##0.00") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revenue Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFAB47BC)) // Purple
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            summary?.let { item { RevenueSectionCard("Revenue Summary", it.metrics, decimalFormat) } }
            revenueBySource?.let {
                item { RevenueSectionCard("Revenue by Source (MTD)", it.periodData["mtd"] ?: emptyMap(), decimalFormat) }
                item { RevenueSectionCard("Revenue by Source (YTD)", it.periodData["ytd"] ?: emptyMap(), decimalFormat) }
            }
            dailyTrend?.let { item { RevenueTrendCard("Daily Revenue Trend (Last 5 Days)", it.points.takeLast(5), decimalFormat) } }
            monthlyTrend?.let { item { RevenueTrendCard("Monthly Revenue Trend (Last 3 Months)", it.points.takeLast(3), decimalFormat) } }
            clv?.let { item { RevenueSectionCard("Customer Lifetime Value (CLV)", it.segments, decimalFormat) } }
            if (topItems.isNotEmpty()) {
                item { TopRevenueItemsCard(topItems, decimalFormat) }
            }
        }
    }
}

@Composable
fun RevenueSectionCard(title: String, metrics: Map<String, Any>, formatter: DecimalFormat) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            DisplayRevenueMetrics(metrics, formatter)
        }
    }
}

@Composable
fun DisplayRevenueMetrics(metrics: Map<String, Any>, formatter: DecimalFormat, indentLevel: Int = 0) {
    val indent = "  ".repeat(indentLevel)
    metrics.forEach { (key, value) ->
        val displayKey = key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        when (value) {
            is Map<*, *> -> {
                Text("$indent$displayKey:")
                DisplayRevenueMetrics(value as Map<String, Any>, formatter, indentLevel + 1)
            }
            is Double -> Text("$indent$displayKey: ${formatter.format(value)}")
            is Number -> Text("$indent$displayKey: $value") // For counts or non-currency numbers
            else -> Text("$indent$displayKey: $value")
        }
    }
}

@Composable
fun RevenueTrendCard(title: String, points: List<RevenueTrendPoint>, formatter: DecimalFormat) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            points.forEach { point ->
                Text("  ${point.label}: ${formatter.format(point.revenue)}")
            }
            // Placeholder for chart
            // Text("Chart would be here", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top=8.dp))
        }
    }
}

@Composable
fun TopRevenueItemsCard(items: List<TopRevenueItem>, formatter: DecimalFormat) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Top Revenue Generating Items (MTD)", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            items.forEach { item ->
                Text("  ${item.name.replace("_", " ").titlecase()} (${item.type}): ${formatter.format(item.revenueMtd)}")
            }
        }
    }
}

// String.titlecase() helper if not available or for specific locale
fun String.titlecase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


@Preview(showBackground = true)
@Composable
fun PreviewRevenueAnalyticsScreen() {
    MaterialTheme {
        RevenueAnalyticsScreen(viewModel = RevenueAnalyticsViewModel())
    }
}
