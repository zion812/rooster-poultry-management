package com.example.rooster.iot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

// ---------------------------  Data models  ---------------------------

data class IoTDevice(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // e.g. "THERMO_HUMID"
    val location: String,
    val lastSync: Long = System.currentTimeMillis(),
)

data class SensorReading(
    val deviceId: String,
    val type: String, // e.g. "TEMP", "HUMID"
    val value: Float,
    val timestamp: Long = System.currentTimeMillis(),
)

// ---------------------------  Mock gateway  ---------------------------

object SensorGateway {
    private val _readings = MutableStateFlow<SensorReading?>(null)
    val readings: Flow<SensorReading?> = _readings.asStateFlow()

    /** Starts emitting random temp & humidity every 5 s for the given device. */
    fun start(device: IoTDevice) {
        if (_emitting) return
        _emitting = true
        kotlinx.coroutines.GlobalScope.launch {
            while (_emitting) {
                val temp = (25..38).random() + (0..99).random() / 100f
                _readings.emit(
                    SensorReading(device.id, "TEMP", temp),
                )
                val humid = (40..95).random() + (0..99).random() / 100f
                _readings.emit(
                    SensorReading(device.id, "HUMID", humid),
                )
                delay(5_000)
            }
        }
    }

    private var _emitting = false
}

// ---------------------------  Dashboard UI  ---------------------------

@Composable
fun IoTDashboardScreen(device: IoTDevice) {
    val scope = rememberCoroutineScope()
    var temp by remember { mutableStateOf<Float?>(null) }
    var humid by remember { mutableStateOf<Float?>(null) }

    // Start gateway when UI first composed
    LaunchedEffect(device.id) {
        SensorGateway.start(device)
        scope.launch {
            SensorGateway.readings.collect { r ->
                if (r?.deviceId == device.id) {
                    when (r.type) {
                        "TEMP" -> temp = r.value
                        "HUMID" -> humid = r.value
                    }
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = device.name, style = MaterialTheme.typography.titleLarge)
        SensorCard(label = "Temperature (°C)", value = temp)
        SensorCard(label = "Humidity (%)", value = humid)
    }
}

@Composable
private fun SensorCard(
    label: String,
    value: Float?,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = value?.let { String.format(Locale.ROOT, "%.2f", it) } ?: "--",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}
