package com.example.rooster.admin.systemmonitoring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data classes to mirror Python structure ---
data class ServerHealthData(
    val name: String,
    val status: String,
    val cpuUsage: String,
    val memoryUsage: String
)

data class ApiPerformanceData(
    val endpoint: String,
    val avgResponseTime: String,
    val errorRate: String,
    val requestsPerMinute: Int
)

data class ErrorLogData(
    val timestamp: Date,
    val errorCode: Int,
    val message: String,
    val source: String
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }
}

// --- ViewModel to hold and manage UI state ---
// In a real app, HiltViewModel would be used for DI
class SystemMonitoringViewModel : ViewModel() {
    // Mock data similar to Python script
    private val _serverHealth = MutableStateFlow<List<ServerHealthData>>(emptyList())
    val serverHealth: StateFlow<List<ServerHealthData>> = _serverHealth

    private val _apiPerformance = MutableStateFlow<List<ApiPerformanceData>>(emptyList())
    val apiPerformance: StateFlow<List<ApiPerformanceData>> = _apiPerformance

    private val _errorTracking = MutableStateFlow<List<ErrorLogData>>(emptyList())
    val errorTracking: StateFlow<List<ErrorLogData>> = _errorTracking

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _serverHealth.value = listOf(
            ServerHealthData("server1", "online", "15%", "45GB/64GB"),
            ServerHealthData("server2", "online", "25%", "50GB/64GB"),
            ServerHealthData("server3", "offline", "N/A", "N/A")
        )

        _apiPerformance.value = listOf(
            ApiPerformanceData("/api/users", "120ms", "0.5%", 1200),
            ApiPerformanceData("/api/pets", "150ms", "1.2%", 800),
            ApiPerformanceData("/api/consultations", "200ms", "0.2%", 500)
        )

        _errorTracking.value = listOf(
            ErrorLogData(Date(System.currentTimeMillis() - 5 * 60000), 500, "Internal Server Error on /api/payments", "server2"),
            ErrorLogData(Date(System.currentTimeMillis() - 15 * 60000), 404, "Resource not found on /api/users/unknown", "server1"),
            ErrorLogData(Date(System.currentTimeMillis() - 30 * 60000), 403, "Forbidden access to /api/admin/config", "server1")
        )
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemMonitoringScreen(viewModel: SystemMonitoringViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val serverHealthData by viewModel.serverHealth.collectAsState()
    val apiPerformanceData by viewModel.apiPerformance.collectAsState()
    val errorTrackingData by viewModel.errorTracking.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Monitoring") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray // Example color
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item {
                Text("Server Health", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(serverHealthData) { server ->
                ServerHealthCard(server)
            }

            item {
                Text("API Performance", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(apiPerformanceData) { api ->
                ApiPerformanceCard(api)
            }

            item {
                Text("Error Tracking", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(errorTrackingData) { errorLog ->
                ErrorLogCard(errorLog)
            }
        }
    }
}

@Composable
fun ServerHealthCard(data: ServerHealthData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Server: ${data.name}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Status: ${data.status}", color = if (data.status == "online") Color.Green else Color.Red)
            Text("CPU Usage: ${data.cpuUsage}")
            Text("Memory: ${data.memoryUsage}")
        }
    }
}

@Composable
fun ApiPerformanceCard(data: ApiPerformanceData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Endpoint: ${data.endpoint}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Avg Response: ${data.avgResponseTime}")
            Text("Error Rate: ${data.errorRate}")
            Text("RPM: ${data.requestsPerMinute}")
        }
    }
}

@Composable
fun ErrorLogCard(data: ErrorLogData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Error: ${data.errorCode} - ${data.message}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Source: ${data.source}")
            Text("Timestamp: ${data.getFormattedTimestamp()}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSystemMonitoringScreen() {
    // You might need to provide a mock ViewModel or use hardcoded data for previews
    // if the default viewModel() call doesn't work well in previews without Hilt setup.
    // For simplicity, we'll assume the default viewModel() works or use a simple instance.
    SystemMonitoringScreen(viewModel = SystemMonitoringViewModel())
}
package com.example.rooster.admin.systemmonitoring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data classes to mirror Python structure ---
data class ServerHealthData(
    val name: String,
    val status: String,
    val cpuUsage: String,
    val memoryUsage: String
)

data class ApiPerformanceData(
    val endpoint: String,
    val avgResponseTime: String,
    val errorRate: String,
    val requestsPerMinute: Int
)

data class ErrorLogData(
    val timestamp: Date,
    val errorCode: Int,
    val message: String,
    val source: String
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }
}

// --- ViewModel to hold and manage UI state ---
// In a real app, HiltViewModel would be used for DI
class SystemMonitoringViewModel : ViewModel() {
    // Mock data similar to Python script
    private val _serverHealth = MutableStateFlow<List<ServerHealthData>>(emptyList())
    val serverHealth: StateFlow<List<ServerHealthData>> = _serverHealth

    private val _apiPerformance = MutableStateFlow<List<ApiPerformanceData>>(emptyList())
    val apiPerformance: StateFlow<List<ApiPerformanceData>> = _apiPerformance

    private val _errorTracking = MutableStateFlow<List<ErrorLogData>>(emptyList())
    val errorTracking: StateFlow<List<ErrorLogData>> = _errorTracking

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _serverHealth.value = listOf(
            ServerHealthData("server1", "online", "15%", "45GB/64GB"),
            ServerHealthData("server2", "online", "25%", "50GB/64GB"),
            ServerHealthData("server3", "offline", "N/A", "N/A")
        )

        _apiPerformance.value = listOf(
            ApiPerformanceData("/api/users", "120ms", "0.5%", 1200),
            ApiPerformanceData("/api/pets", "150ms", "1.2%", 800),
            ApiPerformanceData("/api/consultations", "200ms", "0.2%", 500)
        )

        _errorTracking.value = listOf(
            ErrorLogData(Date(System.currentTimeMillis() - 5 * 60000), 500, "Internal Server Error on /api/payments", "server2"),
            ErrorLogData(Date(System.currentTimeMillis() - 15 * 60000), 404, "Resource not found on /api/users/unknown", "server1"),
            ErrorLogData(Date(System.currentTimeMillis() - 30 * 60000), 403, "Forbidden access to /api/admin/config", "server1")
        )
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemMonitoringScreen(viewModel: SystemMonitoringViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val serverHealthData by viewModel.serverHealth.collectAsState()
    val apiPerformanceData by viewModel.apiPerformance.collectAsState()
    val errorTrackingData by viewModel.errorTracking.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Monitoring") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray // Example color
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item {
                Text("Server Health", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(serverHealthData) { server ->
                ServerHealthCard(server)
            }

            item {
                Text("API Performance", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(apiPerformanceData) { api ->
                ApiPerformanceCard(api)
            }

            item {
                Text("Error Tracking", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(errorTrackingData) { errorLog ->
                ErrorLogCard(errorLog)
            }
        }
    }
}

@Composable
fun ServerHealthCard(data: ServerHealthData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Server: ${data.name}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Status: ${data.status}", color = if (data.status == "online") Color.Green else Color.Red)
            Text("CPU Usage: ${data.cpuUsage}")
            Text("Memory: ${data.memoryUsage}")
        }
    }
}

@Composable
fun ApiPerformanceCard(data: ApiPerformanceData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Endpoint: ${data.endpoint}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Avg Response: ${data.avgResponseTime}")
            Text("Error Rate: ${data.errorRate}")
            Text("RPM: ${data.requestsPerMinute}")
        }
    }
}

@Composable
fun ErrorLogCard(data: ErrorLogData) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Error: ${data.errorCode} - ${data.message}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text("Source: ${data.source}")
            Text("Timestamp: ${data.getFormattedTimestamp()}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSystemMonitoringScreen() {
    // You might need to provide a mock ViewModel or use hardcoded data for previews
    // if the default viewModel() call doesn't work well in previews without Hilt setup.
    // For simplicity, we'll assume the default viewModel() works or use a simple instance.
    SystemMonitoringScreen(viewModel = SystemMonitoringViewModel())
}
