package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

/**
 * UAT Testing Management Screen
 * Provides tools for UAT preparation and testing data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UATTestingScreen() {
    val crashlytics = FirebaseCrashlytics.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("🎯 UAT Testing Environment Ready") }
    var showResult by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "🧪 UAT Testing Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Data Population & Testing Tools",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Text(
                text = statusMessage,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Actions
        Text(
            text = "📊 Data Population",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Populate UAT Test Data
        UATActionCard(
            title = "Populate UAT Test Data",
            description = "Create comprehensive test data including users, fowls, marketplace listings, and social content",
            icon = Icons.Default.PlayArrow,
            buttonText = "Create Test Data",
            isLoading = isLoading,
            onAction = {
                scope.launch {
                    isLoading = true
                    crashlytics.log("UAT Action: Populate UAT Test Data started")
                    statusMessage = "🔄 Creating UAT test data..."
                    try {
                        val result = TestDataPopulator.populateUATTestData()
                        if (result.isSuccess) {
                            crashlytics.log("UAT Action: Populate UAT Test Data succeeded")
                            statusMessage = "✅ UAT test data created successfully!"
                            resultText = result.getOrNull() ?: "Success"
                            showResult = true
                        } else {
                            crashlytics.log("UAT Action: Populate UAT Test Data failed: ${result.exceptionOrNull()?.message}")
                            statusMessage =
                                "❌ Failed to create test data: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        crashlytics.log("UAT Action: Populate UAT Test Data exception: ${e.message}")
                        statusMessage = "❌ Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Create UAT Accounts
        UATActionCard(
            title = "Create UAT Test Accounts",
            description = "Generate specific test accounts for UAT team (farmer, general, admin)",
            icon = Icons.Default.Settings,
            buttonText = "Create Accounts",
            isLoading = isLoading,
            onAction = {
                scope.launch {
                    isLoading = true
                    crashlytics.log("UAT Action: Create UAT Test Accounts started")
                    statusMessage = "🔄 Creating UAT test accounts..."
                    try {
                        val result = TestDataPopulator.createUATTestAccounts()
                        if (result.isSuccess) {
                            crashlytics.log("UAT Action: Create UAT Test Accounts succeeded")
                            statusMessage = "✅ UAT test accounts created!"
                            resultText = result.getOrNull() ?: "Success"
                            showResult = true
                        } else {
                            crashlytics.log("UAT Action: Create UAT Test Accounts failed: ${result.exceptionOrNull()?.message}")
                            statusMessage =
                                "❌ Failed to create accounts: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        crashlytics.log("UAT Action: Create UAT Test Accounts exception: ${e.message}")
                        statusMessage = "❌ Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stress Test Data
        UATActionCard(
            title = "Generate Stress Test Data",
            description = "Create additional data for performance testing (1000+ concurrent users)",
            icon = Icons.Default.Refresh,
            buttonText = "Create Stress Data",
            isLoading = isLoading,
            onAction = {
                scope.launch {
                    isLoading = true
                    crashlytics.log("UAT Action: Generate Stress Test Data started")
                    statusMessage = "🔄 Generating stress test data..."
                    try {
                        val result = TestDataPopulator.createStressTestData()
                        if (result.isSuccess) {
                            crashlytics.log("UAT Action: Generate Stress Test Data succeeded")
                            statusMessage = "✅ Stress test data created!"
                            resultText = result.getOrNull() ?: "Success"
                            showResult = true
                        } else {
                            crashlytics.log("UAT Action: Generate Stress Test Data failed: ${result.exceptionOrNull()?.message}")
                            statusMessage =
                                "❌ Failed to create stress data: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        crashlytics.log("UAT Action: Generate Stress Test Data exception: ${e.message}")
                        statusMessage = "❌ Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cleanup Actions
        Text(
            text = "🧹 Cleanup Tools",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cleanup Test Data
        UATActionCard(
            title = "Cleanup Test Data",
            description = "Archive test data and prepare for fresh UAT session",
            icon = Icons.Default.Refresh,
            buttonText = "Cleanup Data",
            isLoading = isLoading,
            onAction = {
                scope.launch {
                    isLoading = true
                    crashlytics.log("UAT Action: Cleanup Test Data started")
                    statusMessage = "🔄 Cleaning up test data..."
                    try {
                        val result = UATDataCleaner.cleanupTestData()
                        if (result.isSuccess) {
                            crashlytics.log("UAT Action: Cleanup Test Data succeeded")
                            statusMessage = "✅ Test data cleaned up!"
                            resultText = result.getOrNull() ?: "Success"
                            showResult = true
                        } else {
                            crashlytics.log("UAT Action: Cleanup Test Data failed: ${result.exceptionOrNull()?.message}")
                            statusMessage = "❌ Cleanup failed: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        crashlytics.log("UAT Action: Cleanup Test Data exception: ${e.message}")
                        statusMessage = "❌ Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // UAT Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📋 UAT Testing Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        """
                        1. Create UAT test data first
                        2. Test all user roles (Farmer, General, High-Level)
                        3. Validate core features:
                           • User authentication and navigation
                           • Fowl management and milestone tracking
                           • Marketplace listings and transfers
                           • Traditional markets integration
                           • Cultural events and social features
                        4. Test on 2G network conditions
                        5. Validate Telugu language support
                        6. Monitor Firebase Crashlytics
                        """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    // Result Dialog
    if (showResult) {
        AlertDialog(
            onDismissRequest = { showResult = false },
            title = { Text("Operation Result") },
            text = {
                Text(
                    text = resultText,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                TextButton(onClick = { showResult = false }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun UATActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    isLoading: Boolean,
    onAction: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAction,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                } else {
                    Text(buttonText)
                }
            }
        }
    }
}
