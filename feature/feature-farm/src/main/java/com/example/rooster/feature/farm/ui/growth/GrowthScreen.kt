package com.example.rooster.feature.farm.ui.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GrowthScreen(
    viewModel: GrowthViewModel = hiltViewModel()
) {
    val growthData by viewModel.growthData.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadGrowthMetrics()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        growthData.forEach { (type, dataPoints) ->
            Text(text = type, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (dataPoints.isNotEmpty()) {
                // Simple chart placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Chart: ${dataPoints.size} data points",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(text = "No data for $type")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
