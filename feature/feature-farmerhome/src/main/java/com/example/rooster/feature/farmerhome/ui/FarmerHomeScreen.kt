package com.example.rooster.feature.farmerhome.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1
 main
 main
package com.example.rooster.feature.farmerhome.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.R // Assuming R class from core-common for string resources
import com.example.rooster.ui.theme.RoosterTheme // Assuming your app theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel(),
    onNavigateToMyFarms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.navigateToFarmList) {
        if (uiState.navigateToFarmList) {
            onNavigateToMyFarms()
            viewModel.navigationToFarmListComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.farmer_dashboard_title)) } // New String
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage ?: stringResource(id = R.string.error_generic_loading_failed), // New String
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            FarmerDashboardContent(
                uiState = uiState,
                onMyFarmsClicked = { viewModel.onMyFarmsClicked() },
                onRefreshClicked = { viewModel.loadDashboardData() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun FarmerDashboardContent(
    uiState: FarmerHomeUiState,
    onMyFarmsClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the whole dashboard scrollable
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Message
        Text(
            text = stringResource(id = R.string.farmer_dashboard_welcome, uiState.userName), // New String: "Welcome, %1$s!"
            style = MaterialTheme.typography.headlineSmall
        )

        // Farm Overview Card
        DashboardCard(title = stringResource(id = R.string.farmer_dashboard_farm_overview)) { // New String
            StatItem(label = stringResource(id = R.string.farmer_dashboard_farm_count), value = uiState.farmCount.toString()) // New String
            StatItem(label = stringResource(id = R.string.farmer_dashboard_total_flocks), value = uiState.totalFlocks.toString()) // New String
            StatItem(label = stringResource(id = R.string.farmer_dashboard_total_birds), value = uiState.totalBirds.toString()) // New String
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onMyFarmsClicked, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Agriculture, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(id = R.string.farmer_dashboard_button_my_farms)) // New String
            }
        }

        // Alerts Section
        if (uiState.alerts.isNotEmpty()) {
            DashboardCard(title = stringResource(id = R.string.farmer_dashboard_alerts), icon = Icons.Filled.WarningAmber) { // New String
                uiState.alerts.forEach { alert ->
                    Text(text = alert, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }

        // Health Tips Section
        if (uiState.healthTips.isNotEmpty()) {
            DashboardCard(title = stringResource(id = R.string.farmer_dashboard_health_tips), icon = Icons.Filled.Lightbulb) { // New String
                uiState.healthTips.forEach { tip ->
                    Text(text = tip, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }

        // Quick Actions (Placeholder)
        DashboardCard(title = stringResource(id = R.string.farmer_dashboard_quick_actions)) { // New String
            Text("Placeholder for Quick Actions (e.g., Add Flock, Record Mortality, Market Prices)", style = MaterialTheme.typography.bodyMedium)
            // Example: Button(onClick = { /* TODO */ }) { Text("Add New Flock") }
        }

        // Refresh Button (optional)
        OutlinedButton(onClick = onRefreshClicked, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(id = R.string.action_refresh)) // New String
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector? = null, // Optional icon for the card title
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(imageVector = it, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(4.dp))
}


@Preview(showBackground = true, name = "Farmer Home Screen - Populated")
@Composable
fun FarmerHomeScreenPreviewPopulated() {
    RoosterTheme {
        // This preview won't use a real ViewModel, so we pass a sample UiState
        // For a more interactive preview, you'd need a fake ViewModel or pass lambdas.
        FarmerDashboardContent(
            uiState = FarmerHomeUiState(
                isLoading = false,
                userName = "Suresh P.",
                farmCount = 3,
                totalFlocks = 12,
                totalBirds = 650,
                alerts = listOf("Flock C needs deworming.", "Check Farm Beta water supply."),
                healthTips = listOf("Rotate pastures to reduce parasites.", "Provide grit for digestion.")
            ),
            onMyFarmsClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Farmer Home Screen - Empty/Loading")
@Composable
fun FarmerHomeScreenPreviewEmpty() {
    RoosterTheme {
        FarmerDashboardContent(
            uiState = FarmerHomeUiState(isLoading = false, userName = "New Farmer"),
            onMyFarmsClicked = {},
            onRefreshClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Farmer Home Screen - Error")
@Composable
fun FarmerHomeScreenPreviewError() {
    RoosterTheme {
        // Simulate how the main screen would show error
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Failed to load dashboard data. Please try again.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// New Strings to add:
// R.string.farmer_dashboard_title ("Farmer Dashboard")
// R.string.error_generic_loading_failed ("Failed to load data. Please try again.")
// R.string.farmer_dashboard_welcome ("Welcome, %1$s!")
// R.string.farmer_dashboard_farm_overview ("Farm Overview")
// R.string.farmer_dashboard_farm_count ("Total Farms")
// R.string.farmer_dashboard_total_flocks ("Total Flocks")
// R.string.farmer_dashboard_total_birds ("Total Birds")
// R.string.farmer_dashboard_button_my_farms ("Manage My Farms")
// R.string.farmer_dashboard_alerts ("Important Alerts")
// R.string.farmer_dashboard_health_tips ("Health & Management Tips")
// R.string.farmer_dashboard_quick_actions ("Quick Actions")
// R.string.action_refresh ("Refresh")
 feat/login-screen-v1

 feat/login-screen-v1


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rooster.core.common.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.tab_home)) }) // Assuming R.string.tab_home
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Farmer Home Screen (Dashboard) Placeholder")
        }
    }
}
 main
 main
 main
