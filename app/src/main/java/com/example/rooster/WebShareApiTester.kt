package com.example.rooster

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Web Share API Testing Data Models
data class ShareTestResult(
    val testName: String,
    val testNameTelugu: String,
    val isSupported: Boolean,
    val executionTime: Long,
    val dataSize: String,
    val errorMessage: String? = null,
    val deviceCompatibility: DeviceCompatibility = DeviceCompatibility.UNKNOWN,
)

enum class DeviceCompatibility(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
) {
    EXCELLENT("Excellent Support", "అద్భుతమైన మద్దతు", Color(0xFF4CAF50)),
    GOOD("Good Support", "మంచి మద్దతు", Color(0xFF8BC34A)),
    FAIR("Limited Support", "పరిమిత మద్దతు", Color(0xFFFF9800)),
    POOR("Poor Support", "దుర్బల మద్దతు", Color(0xFFFF5722)),
    UNKNOWN("Unknown", "తెలియదు", Color(0xFF757575)),
}

data class ShareContent(
    val title: String,
    val text: String,
    val url: String,
    val estimatedSize: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebShareApiTester() {
    var testResults by remember { mutableStateOf<List<ShareTestResult>>(emptyList()) }
    var isTestingInProgress by remember { mutableStateOf(false) }
    var showTelugu by remember { mutableStateOf(false) }
    var overallCompatibility by remember { mutableStateOf(DeviceCompatibility.UNKNOWN) }
    var selectedContent by remember { mutableStateOf<ShareContent?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Sample content for testing
    val shareContents =
        remember {
            listOf(
                ShareContent(
                    title = if (showTelugu) "కోడిపెంపకం టిప్స్" else "Poultry Farming Tips",
                    text = if (showTelugu) "గ్రామీణ రైతుల కోసం కోడిపెంపకం మార్గదర్శకం" else "Poultry farming guide for rural farmers",
                    url = "https://example.com/poultry-tips",
                    estimatedSize = "~0.5KB",
                ),
                ShareContent(
                    title = if (showTelugu) "మార్కెట్ లిస్టింగ్" else "Marketplace Listing",
                    text = if (showTelugu) "కడక్నాత్ కోడిపిల్లలు అమ్మకానికి" else "Kadaknath chicks for sale",
                    url = "https://example.com/listing/123",
                    estimatedSize = "~0.3KB",
                ),
                ShareContent(
                    title = if (showTelugu) "ఆరోగ్య గుర్తు" else "Health Reminder",
                    text = if (showTelugu) "మీ కోడిపిల్లలకు టీకా వేయాల్సిన సమయం" else "Time to vaccinate your chicks",
                    url = "https://example.com/health-reminder",
                    estimatedSize = "~0.2KB",
                ),
                ShareContent(
                    title = if (showTelugu) "సాంస్కృతిక కార్యక్రమం" else "Cultural Event",
                    text = if (showTelugu) "మకర సంక్రాంతి కోడిపోట్లు పోటీ" else "Makar Sankranti Cockfight Competition",
                    url = "https://example.com/cultural-event",
                    estimatedSize = "~0.4KB",
                ),
            )
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // Header with language toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (showTelugu) "వెబ్ షేర్ API పరీక్ష" else "Web Share API Test",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Switch(
                checked = showTelugu,
                onCheckedChange = { showTelugu = it },
                thumbContent = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device compatibility card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = overallCompatibility.color.copy(alpha = 0.1f),
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector =
                            when (overallCompatibility) {
                                DeviceCompatibility.EXCELLENT -> Icons.Default.CheckCircle
                                DeviceCompatibility.GOOD -> Icons.Default.Check
                                DeviceCompatibility.FAIR -> Icons.Default.Warning
                                DeviceCompatibility.POOR -> Icons.Default.Error
                                DeviceCompatibility.UNKNOWN -> Icons.AutoMirrored.Filled.Help
                            },
                        contentDescription = null,
                        tint = overallCompatibility.color,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showTelugu) "పరికర అనుకూలత" else "Device Compatibility",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (showTelugu) overallCompatibility.displayNameTelugu else overallCompatibility.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = overallCompatibility.color,
                    fontWeight = FontWeight.Medium,
                )

                if (overallCompatibility != DeviceCompatibility.UNKNOWN) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text =
                            if (showTelugu) {
                                when (overallCompatibility) {
                                    DeviceCompatibility.EXCELLENT -> "మీ పరికరం పూర్తిగా వెబ్ షేర్ APIని మద్దతు చేస్తుంది"
                                    DeviceCompatibility.GOOD -> "మీ పరికరం వెబ్ షేర్ APIని బాగా మద్దతు చేస్తుంది"
                                    DeviceCompatibility.FAIR -> "మీ పరికరం పరిమిత వెబ్ షేర్ API మద్దతును కలిగి ఉంది"
                                    DeviceCompatibility.POOR -> "మీ పరికరం వెబ్ షేర్ APIని బాగా మద్దతు చేయదు"
                                    else -> ""
                                }
                            } else {
                                when (overallCompatibility) {
                                    DeviceCompatibility.EXCELLENT -> "Your device fully supports Web Share API"
                                    DeviceCompatibility.GOOD -> "Your device has good Web Share API support"
                                    DeviceCompatibility.FAIR -> "Your device has limited Web Share API support"
                                    DeviceCompatibility.POOR -> "Your device has poor Web Share API support"
                                    else -> ""
                                }
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = {
                    scope.launch {
                        isTestingInProgress = true
                        testResults = emptyList()

                        // Run comprehensive tests
                        testResults = runWebShareTests(context, shareContents, showTelugu)

                        // Calculate overall compatibility
                        overallCompatibility = calculateOverallCompatibility(testResults)

                        isTestingInProgress = false
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isTestingInProgress,
            ) {
                if (isTestingInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isTestingInProgress) {
                        if (showTelugu) "పరీక్షిస్తున్నాం..." else "Testing..."
                    } else {
                        if (showTelugu) "పరీక్ష ప్రారంభించండి" else "Start Tests"
                    },
                )
            }

            OutlinedButton(
                onClick = {
                    testResults = emptyList()
                    overallCompatibility = DeviceCompatibility.UNKNOWN
                },
                enabled = !isTestingInProgress,
            ) {
                Text(if (showTelugu) "క్లియర్" else "Clear")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test results
        if (testResults.isNotEmpty()) {
            Text(
                text = if (showTelugu) "పరీక్ష ఫలితాలు" else "Test Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(testResults) { result ->
                    TestResultCard(
                        result = result,
                        showTelugu = showTelugu,
                        onTestContent = { content ->
                            selectedContent = content
                            // Perform actual share test
                            scope.launch {
                                performActualShare(context, content)
                            }
                        },
                    )
                }
            }
        } else if (!isTestingInProgress) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (showTelugu) "వెబ్ షేర్ API పరీక్ష ప్రారంభించండి" else "Start Web Share API Test",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text =
                            if (showTelugu) {
                                "మీ పరికరంలో వెబ్ షేర్ మద్దతును పరీక్షించండి"
                            } else {
                                "Test Web Share support on your device"
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun TestResultCard(
    result: ShareTestResult,
    showTelugu: Boolean,
    onTestContent: (ShareContent) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (result.isSupported) {
                        Color(0xFFE8F5E8)
                    } else {
                        Color(0xFFFFEBEE)
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (result.isSupported) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (result.isSupported) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showTelugu) result.testNameTelugu else result.testName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Text(
                    text = "${result.executionTime}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text =
                        if (showTelugu) {
                            "డేటా పరిమాణం: ${result.dataSize}"
                        } else {
                            "Data Size: ${result.dataSize}"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = if (showTelugu) result.deviceCompatibility.displayNameTelugu else result.deviceCompatibility.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = result.deviceCompatibility.color,
                    fontWeight = FontWeight.Medium,
                )
            }

            result.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F),
                )
            }
        }
    }
}

// Test execution functions
suspend fun runWebShareTests(
    context: Context,
    shareContents: List<ShareContent>,
    showTelugu: Boolean,
): List<ShareTestResult> {
    val results = mutableListOf<ShareTestResult>()

    shareContents.forEach { content ->
        delay(500) // Simulate testing delay

        val startTime = System.currentTimeMillis()
        val testResult = performShareTest(context, content, showTelugu)
        val endTime = System.currentTimeMillis()

        results.add(
            testResult.copy(executionTime = endTime - startTime),
        )
    }

    // Add general API availability test
    delay(300)
    val startTime = System.currentTimeMillis()
    val apiTest = testWebShareApiAvailability(context, showTelugu)
    val endTime = System.currentTimeMillis()

    results.add(0, apiTest.copy(executionTime = endTime - startTime))

    return results
}

private fun performShareTest(
    context: Context,
    content: ShareContent,
    showTelugu: Boolean,
): ShareTestResult {
    return try {
        // Simulate Web Share API test
        val isSupported = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M

        ShareTestResult(
            testName = "Share: ${content.title}",
            testNameTelugu = "షేర్: ${content.title}",
            isSupported = isSupported,
            executionTime = 0, // Will be set by caller
            dataSize = content.estimatedSize,
            deviceCompatibility = if (isSupported) DeviceCompatibility.GOOD else DeviceCompatibility.POOR,
        )
    } catch (e: Exception) {
        ShareTestResult(
            testName = "Share: ${content.title}",
            testNameTelugu = "షేర్: ${content.title}",
            isSupported = false,
            executionTime = 0,
            dataSize = content.estimatedSize,
            errorMessage = e.message,
            deviceCompatibility = DeviceCompatibility.POOR,
        )
    }
}

private fun testWebShareApiAvailability(
    context: Context,
    showTelugu: Boolean,
): ShareTestResult {
    return try {
        // Test if standard Android sharing is available
        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Test")
            }

        val isSupported = shareIntent.resolveActivity(context.packageManager) != null

        ShareTestResult(
            testName = "Web Share API Availability",
            testNameTelugu = "వెబ్ షేర్ API అందుబాటు",
            isSupported = isSupported,
            executionTime = 0,
            dataSize = "~0.1KB",
            deviceCompatibility = if (isSupported) DeviceCompatibility.EXCELLENT else DeviceCompatibility.POOR,
        )
    } catch (e: Exception) {
        ShareTestResult(
            testName = "Web Share API Availability",
            testNameTelugu = "వెబ్ షేర్ API అందుబాటు",
            isSupported = false,
            executionTime = 0,
            dataSize = "~0.1KB",
            errorMessage = e.message,
            deviceCompatibility = DeviceCompatibility.POOR,
        )
    }
}

private fun calculateOverallCompatibility(results: List<ShareTestResult>): DeviceCompatibility {
    if (results.isEmpty()) return DeviceCompatibility.UNKNOWN

    val supportedCount = results.count { it.isSupported }
    val totalCount = results.size
    val supportRatio = supportedCount.toFloat() / totalCount

    return when {
        supportRatio >= 0.9f -> DeviceCompatibility.EXCELLENT
        supportRatio >= 0.7f -> DeviceCompatibility.GOOD
        supportRatio >= 0.5f -> DeviceCompatibility.FAIR
        else -> DeviceCompatibility.POOR
    }
}

private fun performActualShare(
    context: Context,
    content: ShareContent,
) {
    try {
        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, content.title)
                putExtra(Intent.EXTRA_TEXT, "${content.text}\n\n${content.url}")
            }

        val chooser = Intent.createChooser(shareIntent, "Share via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        // Handle error silently
    }
}
