package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.*
import com.example.rooster.viewmodel.AdminDashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighLevelDashboardScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: AdminDashboardViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()

    // Collect dashboard data
    val verificationMetrics by viewModel.verificationMetrics.collectAsState()
    val revenueMetrics by viewModel.revenueMetrics.collectAsState()
    val disputeMetrics by viewModel.disputeMetrics.collectAsState()
    val pendingActions by viewModel.pendingActions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Load data on first composition
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "అడ్మిన్ డాష్‌బోర్డ్" else "High-Level Dashboard")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.refreshDashboard()
                            }
                        },
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Loading indicator
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Key Metrics Overview
            item {
                KeyMetricsOverviewCard(
                    verificationMetrics = verificationMetrics,
                    revenueMetrics = revenueMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Verification Metrics Section
            item {
                VerificationMetricsCard(
                    metrics = verificationMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Revenue Analytics Section
            item {
                RevenueAnalyticsCard(
                    metrics = revenueMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Dispute & Fraud Monitoring
            item {
                DisputeMonitoringCard(
                    metrics = disputeMetrics,
                    isTeluguMode = isTeluguMode,
                )
            }

            // Verification Actions Widget
            item {
                VerificationActionsCard(
                    pendingActions = pendingActions,
                    isTeluguMode = isTeluguMode,
                    onApprove = { actionId ->
                        scope.launch {
                            viewModel.approveVerification(actionId)
                        }
                    },
                    onReject = { actionId ->
                        scope.launch {
                            viewModel.rejectVerification(actionId)
                        }
                    },
                    onRemind = { actionId ->
                        scope.launch {
                            viewModel.sendReminder(actionId)
                        }
                    },
                )
            }

            // Error Display
            errorMessage?.let { error ->
                item {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyMetricsOverviewCard(
    verificationMetrics: VerificationMetrics?,
    revenueMetrics: RevenueMetrics?,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "ముఖ్య మెట్రిక్స్" else "Key Metrics Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Verification Queue Size
                item {
                    MetricCard(
                        title = if (isTeluguMode) "వెరిఫికేషన్ క్యూ" else "Verification Queue",
                        value = "${verificationMetrics?.queueSize ?: 0}",
                        icon = Icons.Default.Queue,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                // Today's Revenue
                item {
                    MetricCard(
                        title = if (isTeluguMode) "నేటి ఆదాయం" else "Today's Revenue",
                        value = "₹${String.format("%.0f", revenueMetrics?.dailyRevenue ?: 0.0)}",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = Color.Green,
                    )
                }

                // Active Disputes
                item {
                    MetricCard(
                        title = if (isTeluguMode) "చురుకైన వివాదాలు" else "Active Disputes",
                        value = "${verificationMetrics?.pendingDisputes ?: 0}",
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                // Success Rate
                item {
                    MetricCard(
                        title = if (isTeluguMode) "విజయ రేటు" else "Success Rate",
                        value = "${
                            String.format(
                                "%.1f",
                                verificationMetrics?.successRate ?: 0.0,
                            )
                        }%",
                        icon = Icons.Default.CheckCircle,
                        color = Color.Green,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
) {
    Surface(
        modifier = Modifier.width(120.dp),
        shape = MaterialTheme.shapes.medium,
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    }
}

@Composable
private fun VerificationMetricsCard(
    metrics: VerificationMetrics?,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వెరిఫికేషన్ మెట్రిక్స్" else "Verification Metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            metrics?.let { m ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MetricItem(
                        label = if (isTeluguMode) "క్యూ సైజ్" else "Queue Size",
                        value = "${m.queueSize}",
                        isTeluguMode = isTeluguMode,
                    )
                    MetricItem(
                        label = if (isTeluguMode) "సగటు TAT" else "Avg TAT",
                        value = "${m.averageTurnaroundTime}h",
                        isTeluguMode = isTeluguMode,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MetricItem(
                        label = if (isTeluguMode) "విఫలత రేటు" else "Failure Rate",
                        value = "${String.format("%.1f", m.failureRate)}%",
                        isTeluguMode = isTeluguMode,
                    )
                    MetricItem(
                        label = if (isTeluguMode) "పెండింగ్ డిస్ప్యూట్‌లు" else "Pending Disputes",
                        value = "${m.pendingDisputes}",
                        isTeluguMode = isTeluguMode,
                    )
                }

                // Progress indicators
                Text(
                    text =
                        if (isTeluguMode) {
                            "విజయ రేటు: ${String.format("%.1f", m.successRate)}%"
                        } else {
                            "Success Rate: ${String.format("%.1f", m.successRate)}%"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                )
                LinearProgressIndicator(
                    progress = { (m.successRate / 100.0).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RevenueAnalyticsCard(
    metrics: RevenueMetrics?,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "రెవెన్యూ అనలిటిక్స్" else "Revenue Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            metrics?.let { m ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = if (isTeluguMode) "నెలవారీ రెవెన్యూ" else "Monthly Revenue",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "₹${String.format("%.0f", m.monthlyRevenue)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                        )
                    }
                    Column {
                        Text(
                            text = if (isTeluguMode) "వార్షిక రెవెన్యూ" else "Annual Revenue",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "₹${String.format("%.0f", m.annualRevenue)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = if (isTeluguMode) "ప్రాంతవారీ కమిషన్లు" else "Commissions by Region",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                m.commissionsByRegion.forEach { (region, amount) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = region,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "₹${String.format("%.0f", amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisputeMonitoringCard(
    metrics: DisputeMetrics?,
    isTeluguMode: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "వివాద & మోసం మానిటరింగ్" else "Dispute & Fraud Monitoring",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            metrics?.let { m ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MetricItem(
                        label = if (isTeluguMode) "ఓపెన్ కేసులు" else "Open Cases",
                        value = "${m.openCases}",
                        isTeluguMode = isTeluguMode,
                    )
                    MetricItem(
                        label = if (isTeluguMode) "సగటు రిజల్యూషన్ టైమ్" else "Avg Resolution Time",
                        value = "${m.averageResolutionTime}h",
                        isTeluguMode = isTeluguMode,
                    )
                }

                Text(
                    text =
                        if (isTeluguMode) {
                            "అధిక రిస్క్ లెన్నింగ్‌లు: ${m.highRiskTransactions}"
                        } else {
                            "High Risk Transactions: ${m.highRiskTransactions}"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )

                if (m.flaggedUsers.isNotEmpty()) {
                    Text(
                        text = if (isTeluguMode) "ఫ్లాగ్ చేయబడిన వినియోగదారులు:" else "Flagged Users:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    m.flaggedUsers.take(3).forEach { user ->
                        Text(
                            text = "• $user",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationActionsCard(
    pendingActions: List<VerificationAction>,
    isTeluguMode: Boolean,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onRemind: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (isTeluguMode) "వెరిఫికేషన్ చర్యలు" else "Verification Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            if (pendingActions.isEmpty()) {
                Text(
                    text = if (isTeluguMode) "పెండింగ్ చర్యలు లేవు" else "No pending actions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            } else {
                pendingActions.take(5).forEach { action ->
                    VerificationActionItem(
                        action = action,
                        isTeluguMode = isTeluguMode,
                        onApprove = { onApprove(action.id) },
                        onReject = { onReject(action.id) },
                        onRemind = { onRemind(action.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationActionItem(
    action: VerificationAction,
    isTeluguMode: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRemind: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = action.userDisplayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isTeluguMode) "రకం: ${action.verificationType}" else "Type: ${action.verificationType}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text =
                            if (isTeluguMode) {
                                "వేచి ఉన్న రోజులు: ${action.daysPending}"
                            } else {
                                "Days Pending: ${action.daysPending}"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (action.daysPending > 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Green,
                        ),
                ) {
                    Text(if (isTeluguMode) "ఆమోదించు" else "Approve")
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                ) {
                    Text(if (isTeluguMode) "తిరస్కరించు" else "Reject")
                }

                TextButton(
                    onClick = onRemind,
                ) {
                    Text(if (isTeluguMode) "రిమైండ్" else "Remind")
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    isTeluguMode: Boolean,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
