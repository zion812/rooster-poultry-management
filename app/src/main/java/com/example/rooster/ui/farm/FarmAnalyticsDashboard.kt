package com.example.rooster.ui.farm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rooster.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAnalyticsDashboard(
    farmerId: String? = null,
    onNavigateBack: () -> Unit = {},
) {
    var farmData by remember { mutableStateOf<FarmManagementData?>(null) }
    var financialData by remember { mutableStateOf<FinancialAnalytics?>(null) }
    var vaccinationRecords by remember { mutableStateOf<List<VaccinationRecord>>(emptyList()) }
    var breedingRecords by remember { mutableStateOf<List<FarmBreedingRecord>>(emptyList()) }
    var expenseBreakdown by remember { mutableStateOf<List<ExpenseCategory>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(farmerId) {
        loading = true

        // Fetch all farm data concurrently
        fetchFarmManagementData(
            farmerId = farmerId,
            onResult = { farmData = it },
            onError = { error = it },
            setLoading = { },
        )

        fetchFinancialAnalytics(
            farmerId = farmerId,
            onResult = { financialData = it },
            onError = { },
            setLoading = { },
        )

        fetchVaccinationSchedule(
            farmerId = farmerId,
            onResult = { vaccinationRecords = it },
            onError = { },
            setLoading = { },
        )

        fetchBreedingProgram(
            farmerId = farmerId,
            onResult = { breedingRecords = it },
            onError = { },
            setLoading = { },
        )

        fetchExpenseBreakdown(
            farmerId = farmerId,
            onResult = { expenseBreakdown = it },
            onError = { },
            setLoading = { loading = false },
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
                    "Farm Analytics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF5722),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFFFF5722))
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Farm Overview
                farmData?.let { data ->
                    item {
                        FarmOverviewSection(data)
                    }
                }

                // Financial Summary
                financialData?.let { data ->
                    item {
                        FinancialSummarySection(data)
                    }
                }

                // Expense Breakdown
                if (expenseBreakdown.isNotEmpty()) {
                    item {
                        ExpenseBreakdownSection(expenseBreakdown)
                    }
                }

                // Health & Vaccination Status
                item {
                    HealthVaccinationSection(vaccinationRecords)
                }

                // Breeding Program
                if (breedingRecords.isNotEmpty()) {
                    item {
                        BreedingProgramSection(breedingRecords)
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmOverviewSection(data: FarmManagementData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Farm Overview",
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
                        FarmStatItem(
                            "Total Fowl",
                            data.totalFowl.toString(),
                            Icons.Default.Pets,
                            Color(0xFF4CAF50),
                        ),
                        FarmStatItem(
                            "Healthy",
                            data.healthyFowl.toString(),
                            Icons.Default.Favorite,
                            Color(0xFF2196F3),
                        ),
                        FarmStatItem(
                            "Sick",
                            data.sickFowl.toString(),
                            Icons.Default.Warning,
                            Color(0xFFFF9800),
                        ),
                        FarmStatItem(
                            "Efficiency",
                            "${data.productionEfficiency.toInt()}%",
                            Icons.AutoMirrored.Filled.TrendingUp,
                            Color(0xFF9C27B0),
                        ),
                    ),
                ) { stat ->
                    FarmStatCard(stat)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Feed Consumed (30 days)", style = MaterialTheme.typography.bodyMedium)
                    Text("${data.totalFeedConsumed.toInt()} kg", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Avg Feed/Fowl", style = MaterialTheme.typography.bodyMedium)
                    Text("${data.averageFeedPerFowl.format(1)} kg", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Health Issues", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${data.recentHealthIssues}",
                        fontWeight = FontWeight.Bold,
                        color = if (data.recentHealthIssues > 0) Color.Red else Color.Green,
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmStatCard(stat: FarmStatItem) {
    Card(
        modifier =
            Modifier
                .width(120.dp)
                .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = stat.color.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                stat.icon,
                contentDescription = null,
                tint = stat.color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stat.value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = stat.color,
            )
            Text(
                stat.label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FinancialSummarySection(data: FinancialAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Financial Summary",
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
                        FinancialStatItem(
                            "Revenue",
                            NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                .format(data.totalRevenue),
                            Icons.AutoMirrored.Filled.TrendingUp,
                            Color(0xFF4CAF50),
                        ),
                        FinancialStatItem(
                            "Expenses",
                            NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                .format(data.totalExpenses),
                            Icons.AutoMirrored.Filled.TrendingDown,
                            Color(0xFFFF5722),
                        ),
                        FinancialStatItem(
                            "Profit",
                            NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                .format(data.netProfit),
                            Icons.Default.AccountBalance,
                            if (data.netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFFF5722),
                        ),
                        FinancialStatItem(
                            "Margin",
                            "${data.profitMargin.format(1)}%",
                            Icons.Default.Percent,
                            if (data.profitMargin >= 0) Color(0xFF2196F3) else Color(0xFFFF9800),
                        ),
                    ),
                ) { stat ->
                    FinancialStatCard(stat)
                }
            }
        }
    }
}

@Composable
private fun FinancialStatCard(stat: FinancialStatItem) {
    Card(
        modifier =
            Modifier
                .width(140.dp)
                .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = stat.color.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                stat.icon,
                contentDescription = null,
                tint = stat.color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stat.value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = stat.color,
                textAlign = TextAlign.Center,
            )
            Text(
                stat.label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ExpenseBreakdownSection(expenses: List<ExpenseCategory>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Expense Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            expenses.take(5).forEach { expense ->
                ExpenseItem(expense)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: ExpenseCategory) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                expense.category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                "${expense.percentage.format(1)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFF5722),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (expense.percentage / 100).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFF5722),
            trackColor = Color(0xFFFF5722).copy(alpha = 0.2f),
        )
        Text(
            NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(expense.amount),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
    }
}

@Composable
private fun HealthVaccinationSection(records: List<VaccinationRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Health & Vaccination",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Text(
                    "No vaccination records available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                val completedCount = records.count { it.isCompleted }
                val pendingCount = records.size - completedCount

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    VaccinationStatItem("Completed", completedCount.toString(), Color(0xFF4CAF50))
                    VaccinationStatItem("Pending", pendingCount.toString(), Color(0xFFFF9800))
                    VaccinationStatItem("Total", records.size.toString(), Color(0xFF2196F3))
                }
            }
        }
    }
}

@Composable
private fun VaccinationStatItem(
    label: String,
    value: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
    }
}

@Composable
private fun BreedingProgramSection(records: List<FarmBreedingRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Breeding Program",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Text(
                    "No breeding records available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                val avgHatchRate =
                    records.filter { it.hatchingRate > 0 }.map { it.hatchingRate }.average()
                val totalEggs = records.sumOf { it.eggsLaid }
                val totalHatched = records.sumOf { it.eggsHatched }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    BreedingStatItem(
                        "Avg Hatch Rate",
                        "${avgHatchRate.format(1)}%",
                        Color(0xFF4CAF50),
                    )
                    BreedingStatItem("Total Eggs", totalEggs.toString(), Color(0xFF2196F3))
                    BreedingStatItem("Hatched", totalHatched.toString(), Color(0xFFFF9800))
                }
            }
        }
    }
}

@Composable
private fun BreedingStatItem(
    label: String,
    value: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

// Helper data classes
private data class FarmStatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
)

private data class FinancialStatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
)

// Extension function for formatting doubles
private fun Double.format(digits: Int) = "%.${digits}f".format(this)
