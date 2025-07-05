package com.example.rooster.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.viewmodel.EnthusiastViewModel // This ViewModel might need to be replaced by a specific FarmViewModel later

// Assuming a simple route for flock detail for now
const val FLOCK_DETAIL_ROUTE_PREFIX = "flock_detail"

@Composable
fun FlockDashboardScreen(
    navController: NavController,
    vm: EnthusiastViewModel = viewModel(), // TODO: Replace with a Hilt injected ViewModel from feature-farm if available
) {
    val flock by vm.flock.collectAsState() // This data source will need to be updated for real farm data

    // TODO: Add state for growth stats and chart data, fetched from ViewModel

    Scaffold(
        topBar = { TopAppBar(title = { Text("Flock Dashboard") }) }, // TODO: Localize title
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Placeholder for Growth Stats
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Growth Statistics", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Average Weight: TODO kg")
                    Text("Feed Conversion Ratio: TODO")
                    Text("Mortality Rate: TODO %")
                }
            }

            // Placeholder for Charts
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Growth Chart / Health Trends (Placeholder)", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp),
                    ) {
                        Text("Chart Area - MPAndroidChart to be integrated here", Modifier.align(Alignment.Center))
                    }
                }
            }

            Text("Your Flocks", style = MaterialTheme.typography.titleMedium)

            if (flock.isEmpty()) {
                Text("No flocks to display.") // TODO: Localize
            } else {
                flock.forEach { entry ->
                    // Assuming 'entry' has an 'id' or unique identifier
                    val flockId = entry.name // Replace with actual ID if available, e.g. entry.id
                    ListItem(
                        headlineContent = { Text(entry.name) },
                        supportingContent = { Text("Count: ${entry.count}") }, // TODO: Localize "Count"
                        modifier =
                            Modifier.clickable {
                                navController.navigate("$FLOCK_DETAIL_ROUTE_PREFIX/$flockId")
                            },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
