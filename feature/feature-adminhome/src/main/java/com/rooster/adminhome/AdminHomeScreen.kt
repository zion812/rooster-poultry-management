package com.rooster.adminhome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        SystemMonitoringSection(
            metrics = uiState.systemMetrics,
            isLoading = uiState.isLoadingSystemMetrics,
            error = uiState.systemMetricsError,
            onRetry = { viewModel.fetchSystemMetrics() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        UserManagementSummarySection(
            userInfo = uiState.userManagementInfo,
            isLoading = uiState.isLoadingUserManagementInfo,
            error = uiState.userManagementInfoError,
            onRetry = { viewModel.fetchUserManagementSummary() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        FinancialHighlightsSection(
            highlights = uiState.financialHighlights,
            isLoading = uiState.isLoadingFinancialHighlights,
            error = uiState.financialHighlightsError,
            onRetry = { viewModel.fetchFinancialHighlights() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        ContentModerationQueueSection(
            queue = uiState.moderationQueue,
            isLoading = uiState.isLoadingModerationQueue,
            error = uiState.moderationQueueError,
            onRetry = { viewModel.fetchModerationQueue() }
        )
    }
}

@Composable
fun SystemMonitoringSection(
    metrics: List<com.rooster.adminhome.domain.model.SystemMetric>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("System Status", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (metrics.isEmpty()){
            Text("No system metrics available.")
        } else {
            metrics.forEach { metric ->
                // TODO: Create proper SystemMetricCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)){
                        Text("${metric.name}: ${metric.value} (${metric.status})", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun UserManagementSummarySection(
    userInfo: com.rooster.adminhome.domain.model.UserManagementInfo?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("User Overview", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (userInfo == null) {
            Text("User information not available.")
        } else {
            // TODO: Create proper UserInfoCard
            androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(8.dp)){
                    Text("Total Users: ${userInfo.totalUsers}", style = MaterialTheme.typography.bodyMedium)
                    Text("New Today: ${userInfo.newUsersToday}", style = MaterialTheme.typography.bodyMedium)
                    Text("Active Users: ${userInfo.activeUsers}", style = MaterialTheme.typography.bodyMedium)
                    Text("Pending Verification: ${userInfo.pendingVerifications}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun FinancialHighlightsSection(
    highlights: List<com.rooster.adminhome.domain.model.FinancialAnalyticHighlight>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Financial Snapshot", style = MaterialTheme.typography.titleMedium)
         if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (highlights.isEmpty()){
            Text("No financial highlights available.")
        } else {
            highlights.forEach { highlight ->
                // TODO: Create proper FinancialHighlightCard
                 androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)){
                        Text("${highlight.title} (${highlight.period}): ${highlight.value}", style = MaterialTheme.typography.bodyMedium)
                        highlight.trendPercentage?.let {
                            Text(String.format("Trend: %.1f%%", it), color = if(it >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentModerationQueueSection(
    queue: List<com.rooster.adminhome.domain.model.ContentModerationItem>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Moderation Queue (${queue.size} pending)", style = MaterialTheme.typography.titleMedium)
         if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (queue.isEmpty()){
            Text("Moderation queue is clear.")
        } else {
            queue.take(3).forEach { item -> // Show a few items on home screen
                // TODO: Create proper ModerationItemCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                     Column(Modifier.padding(8.dp)){
                        Text("${item.contentType}: \"${item.contentSnippet}...\"", style = MaterialTheme.typography.bodyMedium)
                        item.reasonForFlag?.let { Text("Reason: $it", style = MaterialTheme.typography.bodySmall) }
                         Text("Submitted: ${java.text.SimpleDateFormat("dd MMM yy HH:mm", java.util.Locale.getDefault()).format(item.submissionDate)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if(queue.size > 3) Text("...and ${queue.size - 3} more.")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    MaterialTheme {
        AdminHomeScreen(viewModel = PreviewAdminHomeViewModel())
    }
}

// ViewModel for Preview
class PreviewAdminHomeViewModel: AdminHomeViewModel(
    systemRepository = object : com.rooster.adminhome.domain.repository.AdminSystemRepository {
        override fun getCurrentSystemMetrics() = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.adminhome.domain.model.SystemMetric("prev_api", "API Health", "OK", com.rooster.adminhome.domain.model.SystemStatus.OPERATIONAL, java.util.Date()))
        )
    },
    userRepository = object : com.rooster.adminhome.domain.repository.AdminUserRepository {
        override fun getUserManagementSummary() = kotlinx.coroutines.flow.flowOf(
            com.rooster.adminhome.domain.model.UserManagementInfo(100,5,80,2)
        )
    },
    financialRepository = object : com.rooster.adminhome.domain.repository.AdminFinancialRepository {
        override fun getFinancialHighlights() = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.adminhome.domain.model.FinancialAnalyticHighlight("Revenue", "10K", 2.5, "MTD"))
        )
    },
    moderationRepository = object : com.rooster.adminhome.domain.repository.AdminContentModerationRepository {
        override fun getPendingModerationItems(count: Int) = kotlinx.coroutines.flow.flowOf(
            listOf(com.rooster.adminhome.domain.model.ContentModerationItem("mod1", com.rooster.adminhome.domain.model.ContentType.POST, "Preview content...", null, "Spam", java.util.Date(), com.rooster.adminhome.domain.model.ModerationStatus.PENDING_REVIEW))
        )
    }
) {
    init { }
}
