package com.example.rooster.financials

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.financials.commission.CommissionManagementScreen
import com.example.rooster.financials.reports.FinancialReportsScreen
import com.example.rooster.financials.payouts.PayoutSystemScreen
import com.example.rooster.financials.revenueanalytics.RevenueAnalyticsScreen
import com.example.rooster.financials.transactions.TransactionMonitoringScreen

object FinancialsDestinations {
    const val DASHBOARD = "financials_dashboard"
    const val COMMISSION_MANAGEMENT = "financials_commission_management"
    const val FINANCIAL_REPORTS = "financials_reports"
    const val PAYOUT_SYSTEM = "financials_payout_system"
    const val REVENUE_ANALYTICS = "financials_revenue_analytics"
    const val TRANSACTION_MONITORING = "financials_transaction_monitoring"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialsDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financials Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF26A69A)) // Teal
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { FinancialsDashboardButton(navController, "Commission Management", FinancialsDestinations.COMMISSION_MANAGEMENT) }
            item { FinancialsDashboardButton(navController, "Financial Reports", FinancialsDestinations.FINANCIAL_REPORTS) }
            item { FinancialsDashboardButton(navController, "Payout System", FinancialsDestinations.PAYOUT_SYSTEM) }
            item { FinancialsDashboardButton(navController, "Revenue Analytics", FinancialsDestinations.REVENUE_ANALYTICS) }
            item { FinancialsDashboardButton(navController, "Transaction Monitoring", FinancialsDestinations.TRANSACTION_MONITORING) }
        }
    }
}

@Composable
fun FinancialsDashboardButton(navController: NavController, text: String, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
    ) {
        Text(text)
    }
}

@Composable
fun FinancialsFeatureNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FinancialsDestinations.DASHBOARD) {
        composable(FinancialsDestinations.DASHBOARD) { FinancialsDashboardScreen(navController) }
        composable(FinancialsDestinations.COMMISSION_MANAGEMENT) { CommissionManagementScreen() }
        composable(FinancialsDestinations.FINANCIAL_REPORTS) { FinancialReportsScreen() }
        composable(FinancialsDestinations.PAYOUT_SYSTEM) { PayoutSystemScreen() }
        composable(FinancialsDestinations.REVENUE_ANALYTICS) { RevenueAnalyticsScreen() }
        composable(FinancialsDestinations.TRANSACTION_MONITORING) { TransactionMonitoringScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFinancialsDashboardScreen() {
    MaterialTheme {
        FinancialsFeatureNavigator()
    }
}
