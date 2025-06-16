package com.example.rooster.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rooster.performance.*
import com.example.rooster.ui.components.StandardScreenLayout

/**
 * Performance Testing Screen for Rural Optimization
 *
 * Features:
 * - Real-time performance monitoring
 * - Network speed testing
 * - Memory usage tracking
 * - Device capability detection
 * - Optimization recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceTestScreen(
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val performanceOptimizer = rememberPerformanceOptimizer()
    val performanceMetrics by performanceOptimizer.MonitorPerformance()

    var testResults by remember { mutableStateOf<List<TestResult>>(emptyList()) }
    var isRunningTests by remember { mutableStateOf(false) }

    StandardScreenLayout(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "పనితీరు పరీక్ష" else "Performance Test",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        modifier = modifier,
        scrollable = false,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Current Performance Metrics
            item {
                CurrentPerformanceCard(
                    metrics = performanceMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Quick Test Actions
            item {
                TestActionsCard(
                    isRunningTests = isRunningTests,
                    onRunTests = {
                        isRunningTests = true
                        // Run performance tests
                    },
                    isTeluguMode = isTeluguMode,
                )
            }

            // Optimization Recommendations
            item {
                OptimizationRecommendationsCard(
                    metrics = performanceMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Test Results
            if (testResults.isNotEmpty()) {
                item {
                    Text(
                        text = if (isTeluguMode) "పరీక్ష ఫలితాలు" else "Test Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(testResults) { result ->
                    TestResultCard(
                        result = result,
                        isTeluguMode = isTeluguMode,
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentPerformanceCard(
    metrics: PerformanceMetrics,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = getPerformanceColor(metrics.networkSpeed).copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "ప్రస్తుత పనితీరు" else "Current Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Performance metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PerformanceMetricItem(
                    icon = Icons.Default.NetworkCheck,
                    label = if (isTeluguMode) "నెట్‌వర్క్" else "Network",
                    value = "${metrics.networkType.name} (${metrics.networkSpeed.name})",
                    modifier = Modifier.weight(1f),
                )

                PerformanceMetricItem(
                    icon = Icons.Default.Memory,
                    label = if (isTeluguMode) "మెమరీ" else "Memory",
                    value = "${metrics.memoryUsage.memoryPercentage.toInt()}%",
                    modifier = Modifier.weight(1f),
                )

                PerformanceMetricItem(
                    icon = Icons.Default.PhoneAndroid,
                    label = if (isTeluguMode) "పరికరం" else "Device",
                    value = metrics.deviceType.name,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TestActionsCard(
    isRunningTests: Boolean,
    onRunTests: () -> Unit,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "పనితీరు పరీక్షలు" else "Performance Tests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onRunTests,
                    enabled = !isRunningTests,
                    modifier = Modifier.weight(1f),
                ) {
                    if (isRunningTests) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text =
                            if (isRunningTests) {
                                if (isTeluguMode) "పరీక్షిస్తోంది..." else "Testing..."
                            } else {
                                if (isTeluguMode) "పరీక్షలు ప్రారంభించు" else "Run Tests"
                            },
                    )
                }

                OutlinedButton(
                    onClick = { /* Export results */ },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTeluguMode) "ఎగుమతి" else "Export",
                    )
                }
            }
        }
    }
}

@Composable
private fun OptimizationRecommendationsCard(
    metrics: PerformanceMetrics,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val recommendations = generateRecommendations(metrics, isTeluguMode)

    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "ఆప్టిమైజేషన్ సిఫార్సులు" else "Optimization Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            recommendations.forEach { recommendation ->
                RecommendationItem(
                    recommendation = recommendation,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PerformanceMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RecommendationItem(
    recommendation: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.TipsAndUpdates,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = recommendation,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TestResultCard(
    result: TestResult,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (result.passed) {
                        Color.Green.copy(alpha = 0.1f)
                    } else {
                        Color.Red.copy(alpha = 0.1f)
                    },
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (result.passed) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (result.passed) Color.Green else Color.Red,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.testName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = result.description,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = "${result.duration}ms",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

// Helper functions
private fun getPerformanceColor(networkSpeed: NetworkSpeed): Color {
    return when (networkSpeed) {
        NetworkSpeed.EXCELLENT -> Color.Green
        NetworkSpeed.GOOD -> Color.Blue
        NetworkSpeed.FAIR -> Color.Yellow
        NetworkSpeed.POOR -> Color.Red
        NetworkSpeed.OFFLINE -> Color.Gray
    }
}

private fun generateRecommendations(
    metrics: PerformanceMetrics,
    isTeluguMode: Boolean,
): List<String> {
    val recommendations = mutableListOf<String>()

    // Network-based recommendations
    when (metrics.networkSpeed) {
        NetworkSpeed.POOR, NetworkSpeed.OFFLINE -> {
            recommendations.add(
                if (isTeluguMode) {
                    "చిత్రాల నాణ్యతను తగ్గించండి"
                } else {
                    "Reduce image quality for faster loading"
                },
            )
            recommendations.add(
                if (isTeluguMode) {
                    "ఆఫ్‌లైన్ మోడ్ ఉపయోగించండి"
                } else {
                    "Use offline mode when possible"
                },
            )
        }

        NetworkSpeed.FAIR -> {
            recommendations.add(
                if (isTeluguMode) {
                    "చిత్రాలను ముందుగా లోడ్ చేయవద్దు"
                } else {
                    "Disable image preloading"
                },
            )
        }

        else -> { // Good network, no specific recommendations
        }
    }

    // Memory-based recommendations
    if (metrics.memoryUsage.memoryPercentage > 80) {
        recommendations.add(
            if (isTeluguMode) {
                "అనవసరమైన యాప్‌లను మూసివేయండి"
            } else {
                "Close unnecessary apps to free memory"
            },
        )
    }

    // Device-based recommendations
    when (metrics.deviceType) {
        DeviceType.LOW_END, DeviceType.VERY_LOW_END -> {
            recommendations.add(
                if (isTeluguMode) {
                    "యానిమేషన్‌లను నిలిపివేయండి"
                } else {
                    "Disable animations for better performance"
                },
            )
            recommendations.add(
                if (isTeluguMode) {
                    "తక్కువ వస్తువులను లోడ్ చేయండి"
                } else {
                    "Load fewer items at once"
                },
            )
        }

        else -> { // Higher-end device, fewer restrictions
        }
    }

    if (recommendations.isEmpty()) {
        recommendations.add(
            if (isTeluguMode) {
                "మీ పరికరం బాగా పనిచేస్తుంది!"
            } else {
                "Your device is performing well!"
            },
        )
    }

    return recommendations
}

// Data classes
data class TestResult(
    val testName: String,
    val description: String,
    val passed: Boolean,
    val duration: Long,
)
