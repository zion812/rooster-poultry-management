package com.example.rooster.feature.farm.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val sensorData by viewModel.sensorData.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadSensorData()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (sensorData.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                items(sensorData) { data ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Device: ${data.deviceId}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        data.temperature?.let { temp ->
                            Text(
                                text = "Temperature: ${temp}°C",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        data.humidity?.let { humidity ->
                            Text(
                                text = "Humidity: ${humidity}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = data.timestamp.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
