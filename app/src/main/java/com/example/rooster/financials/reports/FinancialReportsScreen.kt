package com.example.rooster.financials.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.random.Random

// --- Data Classes ---
// Using Map<String, Double> for simplicity, similar to Python. Could be more structured.
data class FinancialReportData(
    val year: Int,
    val month: Int,
    val revenue: Map<String, Double>,
    val totalRevenue: Double,
    val costOfGoodsSold: Map<String, Double>,
    val totalCogs: Double,
    val grossProfit: Double,
    val operatingExpenses: Map<String, Double>,
    val totalOperatingExpenses: Double,
    val operatingIncome: Double, // EBIT
    val nonOperating: Map<String, Double>, // Interest, Taxes
    val netIncome: Double,
    // Balance Sheet parts
    val currentAssets: Map<String, Double>,
    val totalCurrentAssets: Double,
    val nonCurrentAssets: Map<String, Double>,
    val totalNonCurrentAssets: Double,
    val totalAssets: Double,
    val currentLiabilities: Map<String, Double>,
    val totalCurrentLiabilities: Double,
    val nonCurrentLiabilities: Map<String, Double>,
    val totalNonCurrentLiabilities: Double,
    val totalLiabilities: Double,
    val equity: Map<String, Double>, // Placeholder for actual equity components
    val totalEquity: Double
)

// --- ViewModel ---
class FinancialReportsViewModel : ViewModel() {
    private val _financialReportData = MutableStateFlow<FinancialReportData?>(null)
    val financialReportData: StateFlow<FinancialReportData?> = _financialReportData

    init {
        loadMockFinancialData()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadMockFinancialData() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-indexed

        val revenueMap = mapOf(
            "consultation_fees" to Random.nextDouble(50000.0, 150000.0),
            "marketplace_commissions" to Random.nextDouble(30000.0, 100000.0),
            "subscription_fees" to Random.nextDouble(10000.0, 50000.0),
            "other_income" to Random.nextDouble(1000.0, 5000.0)
        )
        val totalRevenue = revenueMap.values.sum()

        val cogsMap = mapOf(
            "platform_service_costs" to totalRevenue * Random.nextDouble(0.05, 0.10)
        )
        val totalCogs = cogsMap.values.sum()
        val grossProfit = totalRevenue - totalCogs

        val opExpensesMap = mapOf(
            "salaries_and_wages" to Random.nextDouble(20000.0, 60000.0),
            "marketing_and_advertising" to Random.nextDouble(5000.0, 20000.0),
            "software_and_tools" to Random.nextDouble(2000.0, 10000.0),
            "rent_and_utilities" to Random.nextDouble(1000.0, 5000.0),
            "payment_processing_fees" to totalRevenue * Random.nextDouble(0.01, 0.03),
            "customer_support" to Random.nextDouble(3000.0, 8000.0),
            "other_operating_expenses" to Random.nextDouble(1000.0, 4000.0)
        )
        val totalOpExpenses = opExpensesMap.values.sum()
        val operatingIncome = grossProfit - totalOpExpenses

        val nonOpMap = mapOf(
            "interest_expense" to (if (operatingIncome > 0) operatingIncome * Random.nextDouble(0.0, 0.02) else 0.0),
            "taxes" to (if (operatingIncome > 0) operatingIncome * Random.nextDouble(0.1, 0.2) else 0.0)
        )
        val netIncome = operatingIncome - nonOpMap.values.sum()

        // Balance Sheet
        val currentAssetsMap = mapOf(
            "cash_and_equivalents" to Random.nextDouble(100000.0, 500000.0),
            "accounts_receivable" to totalRevenue * Random.nextDouble(0.1, 0.2),
            "prepaid_expenses" to Random.nextDouble(5000.0, 20000.0)
        )
        val totalCurrentAssets = currentAssetsMap.values.sum()
        val nonCurrentAssetsMap = mapOf(
            "property_plant_equipment_net" to Random.nextDouble(20000.0, 100000.0),
            "intangible_assets_net" to Random.nextDouble(10000.0, 50000.0)
        )
        val totalNonCurrentAssets = nonCurrentAssetsMap.values.sum()
        val totalAssets = totalCurrentAssets + totalNonCurrentAssets

        val currentLiabilitiesMap = mapOf(
            "accounts_payable" to totalOpExpenses * Random.nextDouble(0.1, 0.15),
            "deferred_revenue" to revenueMap.getOrDefault("subscription_fees", 0.0) * Random.nextDouble(0.2, 0.4),
            "accrued_expenses" to Random.nextDouble(3000.0, 15000.0)
        )
        val totalCurrentLiabilities = currentLiabilitiesMap.values.sum()
        val nonCurrentLiabilitiesMap = mapOf(
            "long_term_debt" to Random.nextDouble(0.0, 50000.0)
        )
        val totalNonCurrentLiabilities = nonCurrentLiabilitiesMap.values.sum()
        val totalLiabilities = totalCurrentLiabilities + totalNonCurrentLiabilities

        val totalEquity = totalAssets - totalLiabilities
        val equityMap = mapOf(
            "retained_earnings_placeholder" to totalEquity - 50000.0, // Assuming 50k common stock
            "common_stock_placeholder" to 50000.0
        )


        _financialReportData.value = FinancialReportData(
            year, month, revenueMap, totalRevenue, cogsMap, totalCogs, grossProfit,
            opExpensesMap, totalOpExpenses, operatingIncome, nonOpMap, netIncome,
            currentAssetsMap, totalCurrentAssets, nonCurrentAssetsMap, totalNonCurrentAssets, totalAssets,
            currentLiabilitiesMap, totalCurrentLiabilities, nonCurrentLiabilitiesMap, totalNonCurrentLiabilities, totalLiabilities,
            equityMap, totalEquity
        )
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportsScreen(viewModel: FinancialReportsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val reportData by viewModel.financialReportData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF42A5F5)) // Blue
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            reportData?.let { data ->
                item { ProfitAndLossStatement(data) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { BalanceSheetSummary(data) }
            } ?: item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

val decimalFormat = DecimalFormat("$ #,##0.00;($ #,##0.00)") // Positive;Negative format

@Composable
fun ReportRow(label: String, amount: Double?, isTotal: Boolean = false, indentLevel: Int = 0) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = (indentLevel * 16).dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.replace("_", " ").replaceFirstChar { it.titlecase() },
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isTotal) 15.sp else 14.sp
        )
        Text(
            text = amount?.let { decimalFormat.format(it) } ?: "-",
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isTotal) 15.sp else 14.sp
        )
    }
}

@Composable
fun ReportSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}


@Composable
fun ProfitAndLossStatement(data: FinancialReportData) {
    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Profit and Loss Statement", style = MaterialTheme.typography.headlineSmall)
            Text("For Period Ending: ${data.month}/${data.year}", style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ReportSectionHeader("Revenue")
            data.revenue.forEach { (key, value) -> ReportRow(key, value, indentLevel = 1) }
            ReportRow("Total Revenue", data.totalRevenue, isTotal = true)

            ReportSectionHeader("Cost of Goods Sold")
            data.costOfGoodsSold.forEach { (key, value) -> ReportRow(key, value, indentLevel = 1) }
            ReportRow("Total COGS", data.totalCogs, isTotal = true)
            ReportRow("Gross Profit", data.grossProfit, isTotal = true, indentLevel = -1) // Negative indent to align with major totals

            ReportSectionHeader("Operating Expenses")
            data.operatingExpenses.forEach { (key, value) -> ReportRow(key, value, indentLevel = 1) }
            ReportRow("Total Operating Expenses", data.totalOperatingExpenses, isTotal = true)
            ReportRow("Operating Income (EBIT)", data.operatingIncome, isTotal = true, indentLevel = -1)

            ReportSectionHeader("Non-Operating Income/Expenses")
            data.nonOperating.forEach { (key, value) -> ReportRow(key, value, indentLevel = 1) }
            ReportRow("Net Income", data.netIncome, isTotal = true, indentLevel = -1)
        }
    }
}

@Composable
fun BalanceSheetSummary(data: FinancialReportData) {
    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Balance Sheet Summary", style = MaterialTheme.typography.headlineSmall)
            Text("As of: ${data.month}/${data.year}", style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ReportSectionHeader("Assets")
            ReportSectionHeader("Current Assets")
            data.currentAssets.forEach { (key, value) -> ReportRow(key, value, indentLevel = 2) }
            ReportRow("Total Current Assets", data.totalCurrentAssets, isTotal = true, indentLevel = 1)

            ReportSectionHeader("Non-Current Assets")
            data.nonCurrentAssets.forEach { (key, value) -> ReportRow(key, value, indentLevel = 2) }
            ReportRow("Total Non-Current Assets", data.totalNonCurrentAssets, isTotal = true, indentLevel = 1)
            ReportRow("Total Assets", data.totalAssets, isTotal = true)

            ReportSectionHeader("Liabilities")
            ReportSectionHeader("Current Liabilities")
            data.currentLiabilities.forEach { (key, value) -> ReportRow(key, value, indentLevel = 2) }
            ReportRow("Total Current Liabilities", data.totalCurrentLiabilities, isTotal = true, indentLevel = 1)

            ReportSectionHeader("Non-Current Liabilities")
            data.nonCurrentLiabilities.forEach { (key, value) -> ReportRow(key, value, indentLevel = 2) }
            ReportRow("Total Non-Current Liabilities", data.totalNonCurrentLiabilities, isTotal = true, indentLevel = 1)
            ReportRow("Total Liabilities", data.totalLiabilities, isTotal = true)

            ReportSectionHeader("Equity")
            data.equity.forEach { (key, value) -> ReportRow(key, value, indentLevel = 1) }
            ReportRow("Total Equity", data.totalEquity, isTotal = true)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            ReportRow("Total Liabilities and Equity", data.totalLiabilities + data.totalEquity, isTotal = true)

            // Sanity Check (visual cue, actual logic would be in VM or tests)
            if (kotlin.math.abs((data.totalLiabilities + data.totalEquity) - data.totalAssets) > 0.01) {
                Text("Error: Balance sheet does not balance!", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFinancialReportsScreen() {
    MaterialTheme {
        FinancialReportsScreen(viewModel = FinancialReportsViewModel())
    }
}
