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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.rooster.core.common.util.DataState
import androidx.compose.foundation.background
import androidx.compose.ui.res.stringResource
import com.rooster.core.R // Assuming R class for string resources is in core
import com.rooster.adminhome.ui.components.ContentModerationItemCard
import com.rooster.adminhome.ui.components.FinancialHighlightItemCard
import com.rooster.adminhome.ui.components.SystemMetricItemCard
import com.rooster.adminhome.ui.components.UserManagementInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messageId) {
        uiState.transientUserMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearTransientMessage()
        }
    }

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messageId) {
        uiState.transientUserMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearTransientMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { scaffoldPadding ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh
        )

        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isOffline) {
                    Text(
                        stringResource(id = R.string.offline_banner_message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(stringResource(id = R.string.admin_home_title), style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                SystemMonitoringSection(
                metricsState = uiState.systemMetricsState,
                onRetry = { viewModel.fetchSystemMetrics() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            UserManagementSummarySection(
                userInfoState = uiState.userManagementInfoState,
                onRetry = { viewModel.fetchUserManagementSummary() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            FinancialHighlightsSection(
                highlightsState = uiState.financialHighlightsState,
                onRetry = { viewModel.fetchFinancialHighlights() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ContentModerationQueueSection(
                queueState = uiState.moderationQueueState,
                onRetry = { viewModel.fetchModerationQueue() }
            )
            } // End of Column content

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        } // End of Box
    } // End of Scaffold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemMonitoringSection(
    metricsState: DataState<List<com.rooster.adminhome.domain.model.SystemMetric>>,
    onRetry: () -> Unit
) {
    val metrics = metricsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.system_status_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (metricsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (metrics.isNotEmpty()) { // Show stale data if available
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally))
                    metrics.forEach { metric -> SystemMetricItemCard(metric) }
                } else {
                    // Potentially a full-section shimmer/placeholder if no stale data
                }
            }
            is DataState.Success -> {
                if (metrics.isEmpty()) {
                    Text(stringResource(id = R.string.system_metrics_no_data), modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (metricsState.isFromCache && metricsState.isStale) {
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                            Badge(modifier = Modifier.padding(start = 4.dp)) { Text("!") }
                        }
                    } else if (metricsState.isFromCache) {
                        Text(stringResource(id = R.string.metrics_cached), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    metrics.forEach { metric -> SystemMetricItemCard(metric) }
                }
            }
            is DataState.Error -> {
                Text(
                    stringResource(id = R.string.error_prefix) + " ${metricsState.message ?: metricsState.exception.localizedMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (metrics.isNotEmpty()) { // Show stale data if available during error
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterHorizontally))
                    metrics.forEach { metric -> SystemMetricItemCard(metric) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRetry, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(id = R.string.retry_button))
                }
            }
        }
    }
}

@Composable
fun SystemMetricItemCard(metric: com.rooster.adminhome.domain.model.SystemMetric) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text("${metric.name}: ${metric.value} (${metric.status})", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementSummarySection(
    userInfoState: DataState<com.rooster.adminhome.domain.model.UserManagementInfo?>,
    onRetry: () -> Unit
) {
    val userInfo = userInfoState.getUnderlyingData()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.user_overview_title), style = MaterialTheme.typography.titleMedium) // Already done, good.
        Spacer(modifier = Modifier.height(8.dp))

        when (userInfoState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                userInfo?.let { staleData ->
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    UserManagementInfoCard(userInfo = staleData)
                }
            }
            is DataState.Success -> {
                userInfo?.let {
                    if (userInfoState.isFromCache && userInfoState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    UserManagementInfoCard(userInfo = it)
                } ?: Text(stringResource(id = R.string.user_info_no_data)) // TODO: Define R.string.user_info_no_data
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${userInfoState.message ?: userInfoState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                userInfo?.let { staleData ->
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    UserManagementInfoCard(userInfo = staleData)
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

@Composable
fun UserManagementInfoCard(userInfo: com.rooster.adminhome.domain.model.UserManagementInfo) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text(stringResource(R.string.user_info_total_users_prefix) + " ${userInfo.totalUsers}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.user_info_total_users_prefix
            Text(stringResource(R.string.user_info_new_today_prefix) + " ${userInfo.newUsersToday}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.user_info_new_today_prefix
            Text(stringResource(R.string.user_info_active_users_prefix) + " ${userInfo.activeUsers}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.user_info_active_users_prefix
            Text(stringResource(R.string.user_info_pending_verification_prefix) + " ${userInfo.pendingVerifications}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.user_info_pending_verification_prefix
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialHighlightsSection(
    highlightsState: DataState<List<com.rooster.adminhome.domain.model.FinancialAnalyticHighlight>>,
    onRetry: () -> Unit
) {
    val highlights = highlightsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.financial_snapshot_title), style = MaterialTheme.typography.titleMedium) // Already done, good.
        Spacer(modifier = Modifier.height(8.dp))
        when (highlightsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (highlights.isNotEmpty()) {
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    highlights.forEach { highlight -> FinancialHighlightItemCard(highlight = highlight) }
                }
            }
            is DataState.Success -> {
                if (highlights.isEmpty()) {
                    Text(stringResource(id = R.string.financial_highlights_no_data)) // TODO: Define R.string.financial_highlights_no_data
                } else {
                    if (highlightsState.isFromCache && highlightsState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    highlights.forEach { highlight -> FinancialHighlightItemCard(highlight = highlight) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${highlightsState.message ?: highlightsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                 if (highlights.isNotEmpty()) {
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    highlights.forEach { highlight -> FinancialHighlightItemCard(highlight = highlight) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

@Composable
fun FinancialHighlightItemCard(highlight: com.rooster.adminhome.domain.model.FinancialAnalyticHighlight) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text("${highlight.title} (${highlight.period}): ${highlight.value}", style = MaterialTheme.typography.bodyMedium) // Title, period, value are dynamic
            highlight.trendPercentage?.let {
                Text(stringResource(R.string.financial_trend_prefix) + " ${String.format("%.1f%%", it)}", color = if (it >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) // TODO: Define R.string.financial_trend_prefix
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentModerationQueueSection(
    queueState: DataState<List<com.rooster.adminhome.domain.model.ContentModerationItem>>,
    onRetry: () -> Unit
) {
    val queue = queueState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.moderation_queue_title) + " (${queue.size} " + stringResource(id = R.string.moderation_queue_pending_suffix) + ")", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when (queueState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (queue.isNotEmpty()) {
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    queue.take(3).forEach { item -> ContentModerationItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
            }
            is DataState.Success -> {
                if (queue.isEmpty()) {
                    Text(stringResource(id = R.string.moderation_queue_clear))
                } else {
                    if (queueState.isFromCache && queueState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    queue.take(3).forEach { item -> ContentModerationItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${queueState.message ?: queueState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (queue.isNotEmpty()) {
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    queue.take(3).forEach { item -> ContentModerationItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

@Composable
fun ContentModerationItemCard(item: com.rooster.adminhome.domain.model.ContentModerationItem) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Text("${item.contentType.name.lowercase(java.util.Locale.getDefault()).replaceFirstChar { it.titlecase(java.util.Locale.getDefault()) }}: \"${item.contentSnippet}...\"", style = MaterialTheme.typography.bodyMedium) // ContentType might need localization if displayed directly
            item.reasonForFlag?.let { Text(stringResource(R.string.moderation_reason_prefix) + " $it", style = MaterialTheme.typography.bodySmall) } // TODO: Define R.string.moderation_reason_prefix
            Text(stringResource(R.string.moderation_submitted_prefix) + " ${java.text.SimpleDateFormat("dd MMM yy HH:mm", java.util.Locale.getDefault()).format(item.submissionDate)}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.moderation_submitted_prefix
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
