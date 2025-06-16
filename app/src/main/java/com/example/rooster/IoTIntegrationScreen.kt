package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoTIntegrationScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
    ) {
        // Header
        IoTHeader(
            isTeluguMode = isTeluguMode,
            onLanguageToggle = onLanguageToggle,
        )

        // Tab Row
        IoTTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            isTeluguMode = isTeluguMode,
        )

        // Content based on selected tab
        when (selectedTab) {
            0 -> FarmOverviewTab(isTeluguMode = isTeluguMode)
            1 -> SensorDataTab(isTeluguMode = isTeluguMode)
            2 -> DeviceManagementTab(isTeluguMode = isTeluguMode)
            3 -> AlertsTab(isTeluguMode = isTeluguMode)
        }
    }
}

@Composable
private fun IoTHeader(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = if (isTeluguMode) "స్మార్ట్ ఫార్మింగ్" else "Smart Farming",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            // Language toggle
            TextButton(onClick = onLanguageToggle) {
                Text(
                    text = if (isTeluguMode) "EN" else "తె",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFF5722),
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
    )
}

@Composable
private fun IoTTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isTeluguMode: Boolean,
) {
    val tabs =
        listOf(
            Triple(Icons.Default.Dashboard, if (isTeluguMode) "ఓవర్వ్యూ" else "Overview", "overview"),
            Triple(Icons.Default.Sensors, if (isTeluguMode) "సెన్సర్లు" else "Sensors", "sensors"),
            Triple(Icons.Default.DeviceHub, if (isTeluguMode) "పరికరాలు" else "Devices", "devices"),
            Triple(Icons.Default.Warning, if (isTeluguMode) "అలర్ట్లు" else "Alerts", "alerts"),
        )

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = Color(0xFFFF5722),
    ) {
        tabs.forEachIndexed { index, (icon, label, _) ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                },
            )
        }
    }
}

// ===============================
// FARM OVERVIEW TAB
// ===============================

@Composable
private fun FarmOverviewTab(isTeluguMode: Boolean) {
    var farmConfig by remember { mutableStateOf<IoTFarmConfiguration?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Mock farm configuration for demonstration
        farmConfig =
            IoTFarmConfiguration(
                farmId = "FARM_001",
                farmName = if (isTeluguMode) "రవి గారి ఫార్మ్" else "Ravi's Farm",
                location = "Karimnagar, Telangana",
                totalDevices = 8,
                activeDevices = 6,
                alertThresholds =
                    mapOf(
                        SensorType.TEMPERATURE to Pair(15.0, 35.0),
                        SensorType.HUMIDITY to Pair(40.0, 80.0),
                        SensorType.FEED_LEVEL to Pair(20.0, 100.0),
                    ),
                notificationSettings =
                    IoTNotificationSettings(
                        enableEmailAlerts = true,
                        enableSMSAlerts = false,
                        enablePushNotifications = true,
                        quietHoursStart = "22:00",
                        quietHoursEnd = "06:00",
                        alertSeverityThreshold = AlertLevel.MEDIUM,
                    ),
                lastUpdated = Date(),
            )
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "ఫార్మ్ ఓవర్వ్యూ" else "Farm Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        if (farmConfig != null) {
            item {
                FarmStatusCard(
                    config = farmConfig!!,
                    isTeluguMode = isTeluguMode,
                )
            }

            item {
                QuickStatsRow(
                    config = farmConfig!!,
                    isTeluguMode = isTeluguMode,
                )
            }

            item {
                RecentSensorReadings(isTeluguMode = isTeluguMode)
            }

            item {
                DeviceStatusOverview(
                    config = farmConfig!!,
                    isTeluguMode = isTeluguMode,
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        }
    }
}

@Composable
private fun FarmStatusCard(
    config: IoTFarmConfiguration,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = config.farmName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = config.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )
                }

                // Status indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (config.activeDevices >= config.totalDevices * 0.8) {
                                        Color(0xFF4CAF50)
                                    } else {
                                        Color(0xFFFF9800)
                                    },
                                ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text =
                            if (isTeluguMode) {
                                if (config.activeDevices >= config.totalDevices * 0.8) "సాధారణం" else "దృష్టి అవసరం"
                            } else {
                                if (config.activeDevices >= config.totalDevices * 0.8) "Healthy" else "Needs Attention"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatusMetric(
                    label = if (isTeluguMode) "మొత్తం పరికరాలు" else "Total Devices",
                    value = config.totalDevices.toString(),
                    icon = Icons.Default.DeviceHub,
                    color = Color(0xFF2196F3),
                )

                StatusMetric(
                    label = if (isTeluguMode) "క్రియాశీల" else "Active",
                    value = config.activeDevices.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                )

                StatusMetric(
                    label = if (isTeluguMode) "ఆఫ్లైన్" else "Offline",
                    value = (config.totalDevices - config.activeDevices).toString(),
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFF9800),
                )
            }
        }
    }
}

@Composable
private fun StatusMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = color,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )
    }
}

@Composable
private fun QuickStatsRow(
    config: IoTFarmConfiguration,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వేగవంతమైన గణాంకాలు" else "Quick Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    listOf(
                        QuickStat(
                            "25°C",
                            if (isTeluguMode) "ఉష్ణోగ్రత" else "Temperature",
                            Icons.Default.Thermostat,
                            Color(0xFFFF5722),
                        ),
                        QuickStat(
                            "65%",
                            if (isTeluguMode) "తేమ" else "Humidity",
                            Icons.Default.WaterDrop,
                            Color(0xFF2196F3),
                        ),
                        QuickStat(
                            "80%",
                            if (isTeluguMode) "ఆహార స్థాయి" else "Feed Level",
                            Icons.Default.Restaurant,
                            Color(0xFF4CAF50),
                        ),
                        QuickStat(
                            "2",
                            if (isTeluguMode) "అలర్ట్లు" else "Alerts",
                            Icons.Default.Warning,
                            Color(0xFFFF9800),
                        ),
                    ),
                ) { stat ->
                    QuickStatCard(stat)
                }
            }
        }
    }
}

@Composable
private fun QuickStatCard(stat: QuickStat) {
    Card(
        modifier =
            Modifier
                .width(120.dp)
                .height(100.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = stat.color.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = stat.color,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = stat.color,
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                maxLines = 2,
            )
        }
    }
}

// ===============================
// SENSOR DATA TAB
// ===============================

@Composable
private fun SensorDataTab(isTeluguMode: Boolean) {
    var sensorReadings by remember { mutableStateOf(listOf<SensorReading>()) }
    var selectedSensorType by remember { mutableStateOf<SensorType?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSensorType) {
        isLoading = true
        // Mock sensor data
        sensorReadings = generateMockSensorData(selectedSensorType)
        isLoading = false
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "సెన్సర్ డేటా" else "Sensor Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        item {
            SensorTypeFilter(
                selectedType = selectedSensorType,
                onTypeSelected = { selectedSensorType = it },
                isTeluguMode = isTeluguMode,
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        } else {
            items(sensorReadings) { reading ->
                SensorReadingCard(
                    reading = reading,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }
}

@Composable
private fun SensorTypeFilter(
    selectedType: SensorType?,
    onTypeSelected: (SensorType?) -> Unit,
    isTeluguMode: Boolean,
) {
    val sensorTypes =
        listOf(
            null to (if (isTeluguMode) "అన్నీ" else "All"),
            SensorType.TEMPERATURE to (if (isTeluguMode) "ఉష్ణోగ్రత" else "Temperature"),
            SensorType.HUMIDITY to (if (isTeluguMode) "తేమ" else "Humidity"),
            SensorType.FEED_LEVEL to (if (isTeluguMode) "ఆహార స్థాయి" else "Feed Level"),
            SensorType.WATER_LEVEL to (if (isTeluguMode) "నీటి స్థాయి" else "Water Level"),
        )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sensorTypes) { (type, label) ->
            FilterChip(
                onClick = { onTypeSelected(type) },
                label = { Text(label) },
                selected = selectedType == type,
            )
        }
    }
}

@Composable
private fun SensorReadingCard(
    reading: SensorReading,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Sensor icon
            val icon =
                when (reading.sensorType) {
                    SensorType.TEMPERATURE -> Icons.Default.Thermostat
                    SensorType.HUMIDITY -> Icons.Default.WaterDrop
                    SensorType.FEED_LEVEL -> Icons.Default.Restaurant
                    SensorType.WATER_LEVEL -> Icons.Default.WaterDrop
                    SensorType.LIGHT -> Icons.Default.LightMode
                    SensorType.MOTION -> Icons.AutoMirrored.Filled.DirectionsRun
                    SensorType.AIR_QUALITY -> Icons.Default.Air
                    SensorType.SOUND_LEVEL -> Icons.AutoMirrored.Filled.VolumeUp
                }

            val color =
                when (reading.alertLevel) {
                    AlertLevel.NORMAL -> Color(0xFF4CAF50)
                    AlertLevel.LOW, AlertLevel.MEDIUM -> Color(0xFFFF9800)
                    AlertLevel.HIGH, AlertLevel.CRITICAL -> Color(0xFFE53935)
                }

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = color,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getSensorTypeName(reading.sensorType, isTeluguMode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = reading.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
                Text(
                    text =
                        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                            .format(reading.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "${reading.value}${reading.unit}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )
                if (reading.alertLevel != AlertLevel.NORMAL) {
                    Text(
                        text = getAlertLevelName(reading.alertLevel, isTeluguMode),
                        style = MaterialTheme.typography.bodySmall,
                        color = color,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

// ===============================
// DEVICE MANAGEMENT TAB
// ===============================

@Composable
private fun DeviceManagementTab(isTeluguMode: Boolean) {
    var devices by remember { mutableStateOf(listOf<IoTDevice>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        devices = generateMockDevices()
        isLoading = false
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "పరికర నిర్వహణ" else "Device Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        } else {
            items(devices) { device ->
                IoTDeviceCard(
                    device = device,
                    isTeluguMode = isTeluguMode,
                )
            }
        }
    }
}

@Composable
private fun IoTDeviceCard(
    device: IoTDevice,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = device.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )
                }

                // Online status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (device.isOnline) Color(0xFF4CAF50) else Color(0xFFE53935),
                                ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text =
                            if (device.isOnline) {
                                if (isTeluguMode) "ఆన్లైన్" else "Online"
                            } else {
                                if (isTeluguMode) "ఆఫ్లైన్" else "Offline"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (device.isOnline) Color(0xFF4CAF50) else Color(0xFFE53935),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                DeviceMetric(
                    label = if (isTeluguMode) "బ్యాటరీ" else "Battery",
                    value = "${device.batteryLevel.toInt()}%",
                    color = getBatteryColor(device.batteryLevel),
                )

                DeviceMetric(
                    label = if (isTeluguMode) "సిగ్నల్" else "Signal",
                    value = "${device.signalStrength.toInt()}%",
                    color = getSignalColor(device.signalStrength),
                )

                DeviceMetric(
                    label = if (isTeluguMode) "రకం" else "Type",
                    value = getDeviceTypeName(device.deviceType, isTeluguMode),
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${if (isTeluguMode) "చివరిసారి చూసినది:" else "Last seen:"} ${
                    SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(device.lastSeen)
                }",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun DeviceMetric(
    label: String,
    value: String,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
    }
}

// ===============================
// ALERTS TAB
// ===============================

@Composable
private fun AlertsTab(isTeluguMode: Boolean) {
    var alerts by remember { mutableStateOf(listOf<IoTAlert>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        alerts = generateMockAlerts()
        isLoading = false
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = if (isTeluguMode) "అలర్ట్లు మరియు నోటిఫికేషన్లు" else "Alerts & Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722),
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                }
            }
        } else if (alerts.isEmpty()) {
            item {
                EmptyAlertsCard(isTeluguMode = isTeluguMode)
            }
        } else {
            items(alerts) { alert ->
                IoTAlertCard(
                    alert = alert,
                    isTeluguMode = isTeluguMode,
                    onAcknowledge = {
                        // Handle alert acknowledgment
                    },
                )
            }
        }
    }
}

@Composable
private fun IoTAlertCard(
    alert: IoTAlert,
    isTeluguMode: Boolean,
    onAcknowledge: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when (alert.severity) {
                        AlertLevel.CRITICAL -> Color(0xFFE53935).copy(alpha = 0.1f)
                        AlertLevel.HIGH -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        AlertLevel.MEDIUM -> Color(0xFFFFC107).copy(alpha = 0.1f)
                        AlertLevel.LOW -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        AlertLevel.NORMAL -> Color.White
                    },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                AlertSeverityChip(
                    severity = alert.severity,
                    isTeluguMode = isTeluguMode,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            if (alert.value != 0.0 && alert.threshold != 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        if (isTeluguMode) {
                            "విలువ: ${alert.value} (థ్రెషోల్డ్: ${alert.threshold})"
                        } else {
                            "Value: ${alert.value} (Threshold: ${alert.threshold})"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(alert.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                if (alert.isActive && !alert.isAcknowledged) {
                    Button(
                        onClick = onAcknowledge,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722),
                            ),
                    ) {
                        Text(
                            text = if (isTeluguMode) "అంగీకరించు" else "Acknowledge",
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertSeverityChip(
    severity: AlertLevel,
    isTeluguMode: Boolean,
) {
    val (text, color) =
        when (severity) {
            AlertLevel.CRITICAL ->
                (if (isTeluguMode) "క్రిటికల్" else "Critical") to Color(0xFFE53935)

            AlertLevel.HIGH ->
                (if (isTeluguMode) "హై" else "High") to Color(0xFFFF9800)

            AlertLevel.MEDIUM ->
                (if (isTeluguMode) "మీడియం" else "Medium") to Color(0xFFFFC107)

            AlertLevel.LOW ->
                (if (isTeluguMode) "లో" else "Low") to Color(0xFF4CAF50)

            AlertLevel.NORMAL ->
                (if (isTeluguMode) "సాధారణ" else "Normal") to Color(0xFF4CAF50)
        }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.2f),
            ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EmptyAlertsCard(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF4CAF50),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text =
                    if (isTeluguMode) {
                        "అలర్ట్లు లేవు! మీ ఫార్మ్ బాగా పని చేస్తోంది."
                    } else {
                        "No alerts! Your farm is running smoothly."
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ===============================
// HELPER FUNCTIONS & DATA
// ===============================

data class QuickStat(
    val value: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
)

@Composable
private fun RecentSensorReadings(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "ఇటీవలి రీడింగ్లు" else "Recent Readings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            val recentReadings =
                listOf(
                    Triple(
                        "25.3°C",
                        if (isTeluguMode) "కూప్ A ఉష్ణోగ్రత" else "Coop A Temperature",
                        Color(0xFFFF5722),
                    ),
                    Triple(
                        "68%",
                        if (isTeluguMode) "కూప్ B తేమ" else "Coop B Humidity",
                        Color(0xFF2196F3),
                    ),
                    Triple(
                        "85%",
                        if (isTeluguMode) "ఫీడర్ 1 స్థాయి" else "Feeder 1 Level",
                        Color(0xFF4CAF50),
                    ),
                )

            recentReadings.forEach { (value, label, color) ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceStatusOverview(
    config: IoTFarmConfiguration,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "పరికర స్థితి" else "Device Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = config.activeDevices.toFloat() / config.totalDevices.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color =
                    if (config.activeDevices >= config.totalDevices * 0.8) {
                        Color(0xFF4CAF50)
                    } else {
                        Color(0xFFFF9800)
                    },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text =
                    if (isTeluguMode) {
                        "${config.activeDevices}/${config.totalDevices} పరికరాలు ఆన్లైన్లో ఉన్నాయి"
                    } else {
                        "${config.activeDevices}/${config.totalDevices} devices online"
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}

// Mock data generation functions
private fun generateMockSensorData(type: SensorType?): List<SensorReading> {
    val types = type?.let { listOf(it) } ?: SensorType.values().toList()
    return types.flatMap { sensorType ->
        (1..3).map { index ->
            val value =
                when (sensorType) {
                    SensorType.TEMPERATURE -> 20.0 + (Math.random() * 15)
                    SensorType.HUMIDITY -> 40.0 + (Math.random() * 40)
                    SensorType.FEED_LEVEL -> 20.0 + (Math.random() * 80)
                    SensorType.WATER_LEVEL -> 30.0 + (Math.random() * 70)
                    SensorType.LIGHT -> 100.0 + (Math.random() * 900)
                    SensorType.MOTION -> Math.random() * 100
                    SensorType.AIR_QUALITY -> 30.0 + (Math.random() * 70)
                    SensorType.SOUND_LEVEL -> 30.0 + (Math.random() * 40)
                }

            SensorReading(
                readingId = "reading_${sensorType}_$index",
                farmId = "FARM_001",
                sensorType = sensorType,
                value = value,
                unit =
                    when (sensorType) {
                        SensorType.TEMPERATURE -> "°C"
                        SensorType.HUMIDITY -> "%"
                        SensorType.FEED_LEVEL, SensorType.WATER_LEVEL -> "%"
                        SensorType.LIGHT -> "lux"
                        SensorType.MOTION -> "%"
                        SensorType.AIR_QUALITY -> "AQI"
                        SensorType.SOUND_LEVEL -> "dB"
                    },
                location = "Coop ${('A' + index - 1)}",
                deviceId = "device_${sensorType}_$index",
                alertLevel =
                    when {
                        Math.random() < 0.1 -> AlertLevel.HIGH
                        Math.random() < 0.2 -> AlertLevel.MEDIUM
                        else -> AlertLevel.NORMAL
                    },
                timestamp = Date(System.currentTimeMillis() - (index * 3600000)),
                batteryLevel = 70.0 + (Math.random() * 30),
                signalStrength = 60.0 + (Math.random() * 40),
            )
        }
    }
}

private fun generateMockDevices(): List<IoTDevice> {
    return listOf(
        IoTDevice(
            deviceId = "temp_sensor_01",
            deviceName = "Temperature Sensor 1",
            deviceType = IoTDeviceType.SENSOR,
            farmId = "FARM_001",
            location = "Coop A",
            isOnline = true,
            batteryLevel = 85.0,
            signalStrength = 92.0,
            lastSeen = Date(),
            firmwareVersion = "1.2.3",
            configuration = "{}",
        ),
        IoTDevice(
            deviceId = "humidity_sensor_01",
            deviceName = "Humidity Sensor 1",
            deviceType = IoTDeviceType.SENSOR,
            farmId = "FARM_001",
            location = "Coop A",
            isOnline = true,
            batteryLevel = 72.0,
            signalStrength = 88.0,
            lastSeen = Date(System.currentTimeMillis() - 300000),
            firmwareVersion = "1.2.1",
            configuration = "{}",
        ),
        IoTDevice(
            deviceId = "feeder_01",
            deviceName = "Auto Feeder 1",
            deviceType = IoTDeviceType.FEEDER,
            farmId = "FARM_001",
            location = "Coop B",
            isOnline = false,
            batteryLevel = 15.0,
            signalStrength = 0.0,
            lastSeen = Date(System.currentTimeMillis() - 7200000),
            firmwareVersion = "2.1.0",
            configuration = "{}",
        ),
    )
}

private fun generateMockAlerts(): List<IoTAlert> {
    return listOf(
        IoTAlert(
            alertId = "alert_001",
            farmId = "FARM_001",
            deviceId = "temp_sensor_01",
            alertType = IoTAlertType.THRESHOLD_EXCEEDED,
            severity = AlertLevel.HIGH,
            title = "High Temperature Alert",
            description = "Temperature in Coop A has exceeded safe limits",
            value = 38.5,
            threshold = 35.0,
            isActive = true,
            isAcknowledged = false,
            createdAt = Date(System.currentTimeMillis() - 1800000),
            acknowledgedAt = null,
        ),
        IoTAlert(
            alertId = "alert_002",
            farmId = "FARM_001",
            deviceId = "feeder_01",
            alertType = IoTAlertType.DEVICE_OFFLINE,
            severity = AlertLevel.MEDIUM,
            title = "Device Offline",
            description = "Auto Feeder 1 has been offline for over 2 hours",
            value = 0.0,
            threshold = 0.0,
            isActive = true,
            isAcknowledged = false,
            createdAt = Date(System.currentTimeMillis() - 7200000),
            acknowledgedAt = null,
        ),
    )
}

// Helper functions for display names
private fun getSensorTypeName(
    type: SensorType,
    isTeluguMode: Boolean,
): String {
    return if (isTeluguMode) {
        when (type) {
            SensorType.TEMPERATURE -> "ఉష్ణోగ్రత"
            SensorType.HUMIDITY -> "తేమ"
            SensorType.FEED_LEVEL -> "ఆహార స్థాయి"
            SensorType.WATER_LEVEL -> "నీటి స్థాయి"
            SensorType.LIGHT -> "వెలుతురు"
            SensorType.MOTION -> "కదలిక"
            SensorType.AIR_QUALITY -> "గాలి నాణ్యత"
            SensorType.SOUND_LEVEL -> "శబ్ద స్థాయి"
        }
    } else {
        when (type) {
            SensorType.TEMPERATURE -> "Temperature"
            SensorType.HUMIDITY -> "Humidity"
            SensorType.FEED_LEVEL -> "Feed Level"
            SensorType.WATER_LEVEL -> "Water Level"
            SensorType.LIGHT -> "Light"
            SensorType.MOTION -> "Motion"
            SensorType.AIR_QUALITY -> "Air Quality"
            SensorType.SOUND_LEVEL -> "Sound Level"
        }
    }
}

private fun getAlertLevelName(
    level: AlertLevel,
    isTeluguMode: Boolean,
): String {
    return if (isTeluguMode) {
        when (level) {
            AlertLevel.CRITICAL -> "క్రిటికల్"
            AlertLevel.HIGH -> "హై"
            AlertLevel.MEDIUM -> "మీడియం"
            AlertLevel.LOW -> "లో"
            AlertLevel.NORMAL -> "సాధారణ"
        }
    } else {
        when (level) {
            AlertLevel.CRITICAL -> "Critical"
            AlertLevel.HIGH -> "High"
            AlertLevel.MEDIUM -> "Medium"
            AlertLevel.LOW -> "Low"
            AlertLevel.NORMAL -> "Normal"
        }
    }
}

private fun getDeviceTypeName(
    type: IoTDeviceType,
    isTeluguMode: Boolean,
): String {
    return if (isTeluguMode) {
        when (type) {
            IoTDeviceType.SENSOR -> "సెన్సర్"
            IoTDeviceType.ACTUATOR -> "అక్చుయేటర్"
            IoTDeviceType.CAMERA -> "కెమెరా"
            IoTDeviceType.FEEDER -> "ఫీడర్"
            IoTDeviceType.GATEWAY -> "గేట్వే"
        }
    } else {
        when (type) {
            IoTDeviceType.SENSOR -> "Sensor"
            IoTDeviceType.ACTUATOR -> "Actuator"
            IoTDeviceType.CAMERA -> "Camera"
            IoTDeviceType.FEEDER -> "Feeder"
            IoTDeviceType.GATEWAY -> "Gateway"
        }
    }
}

private fun getBatteryColor(level: Double): Color {
    return when {
        level >= 60 -> Color(0xFF4CAF50)
        level >= 30 -> Color(0xFFFF9800)
        else -> Color(0xFFE53935)
    }
}

private fun getSignalColor(strength: Double): Color {
    return when {
        strength >= 70 -> Color(0xFF4CAF50)
        strength >= 40 -> Color(0xFFFF9800)
        else -> Color(0xFFE53935)
    }
}
