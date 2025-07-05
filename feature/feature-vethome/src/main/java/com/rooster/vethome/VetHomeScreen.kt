package com.rooster.vethome

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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.layout.Box
import com.rooster.vethome.ui.components.ConsultationQueueItemCard
import com.rooster.vethome.ui.components.PatientSummaryCard
import com.rooster.vethome.ui.components.VetHealthAlertCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetHomeScreen(
    viewModel: VetHomeViewModel = hiltViewModel()
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
                        stringResource(id = R.string.offline_banner_message), // TODO: Define R.string.offline_banner_message
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
                Text(stringResource(id = R.string.vet_home_title), style = MaterialTheme.typography.headlineMedium) // TODO: Define R.string.vet_home_title
                Spacer(modifier = Modifier.height(16.dp))

                ConsultationQueueSection(
            } // End of Column content

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
                queueState = uiState.consultationQueueState,
                onRetry = { viewModel.fetchConsultationQueue() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            RecentPatientsSection(
                patientsState = uiState.recentPatientsState,
                onRetry = { viewModel.fetchRecentPatientSummaries() }
            )
            Spacer(modifier = Modifier.height(16.dp))

            VetHealthAlertsSection(
                alertsState = uiState.healthAlertsState,
                onRetry = { viewModel.fetchActiveHealthAlerts() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationQueueSection(
    queueState: DataState<List<com.rooster.vethome.domain.model.ConsultationQueueItem>>,
    onRetry: () -> Unit
) {
    val queue = queueState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        val pendingCount = queue.filter { it.status == com.rooster.vethome.domain.model.ConsultationRequestStatus.PENDING }.size
        Text(stringResource(R.string.consultation_queue_title, pendingCount), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.consultation_queue_title (e.g., "Consultation Queue (%d pending)")
        Spacer(modifier = Modifier.height(8.dp))

        when (queueState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (queue.isNotEmpty()) {
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    queue.take(3).forEach { item -> ConsultationQueueItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
            }
            is DataState.Success -> {
                if (queue.isEmpty()) {
                    Text(stringResource(id = R.string.consultation_queue_empty)) // TODO: Define R.string.consultation_queue_empty
                } else {
                    if (queueState.isFromCache && queueState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    queue.take(3).forEach { item -> ConsultationQueueItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${queueState.message ?: queueState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (queue.isNotEmpty()) {
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    queue.take(3).forEach { item -> ConsultationQueueItemCard(item = item) }
                    if (queue.size > 3) Text(stringResource(R.string.and_n_more, queue.size - 3))
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

// Removed standalone ConsultationQueueItemCard as it's now imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentPatientsSection(
    patientsState: DataState<List<com.rooster.vethome.domain.model.PatientHistorySummary>>,
    onRetry: () -> Unit
) {
    val patients = patientsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.recent_patients_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.recent_patients_title
        Spacer(modifier = Modifier.height(8.dp))
        when (patientsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (patients.isNotEmpty()) {
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    patients.forEach { patient -> PatientSummaryCard(patient = patient) }
                }
            }
            is DataState.Success -> {
                if (patients.isEmpty()) {
                    Text(stringResource(id = R.string.patients_no_recent)) // TODO: Define R.string.patients_no_recent
                } else {
                    if (patientsState.isFromCache && patientsState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    patients.forEach { patient -> PatientSummaryCard(patient = patient) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${patientsState.message ?: patientsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (patients.isNotEmpty()) {
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    patients.forEach { patient -> PatientSummaryCard(patient = patient) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

// Removed standalone PatientSummaryItemCard as it's now imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetHealthAlertsSection(
    alertsState: DataState<List<com.rooster.vethome.domain.model.VetHealthAlert>>,
    onRetry: () -> Unit
) {
    val alerts = alertsState.getUnderlyingData() ?: emptyList()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.vet_health_alerts_title), style = MaterialTheme.typography.titleMedium) // TODO: Define R.string.vet_health_alerts_title
        Spacer(modifier = Modifier.height(8.dp))
        when (alertsState) {
            is DataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                if (alerts.isNotEmpty()) {
                    Text(stringResource(id = R.string.updating_cached_data), style = MaterialTheme.typography.labelSmall)
                    alerts.forEach { alert -> VetHealthAlertCard(alert = alert) }
                }
            }
            is DataState.Success -> {
                if (alerts.isEmpty()) {
                    Text(stringResource(id = R.string.vet_alerts_no_active)) // TODO: Define R.string.vet_alerts_no_active
                } else {
                     if (alertsState.isFromCache && alertsState.isStale) {
                        Badge { Text("!") }
                        Text(stringResource(id = R.string.data_possibly_stale), style = MaterialTheme.typography.labelSmall)
                    }
                    alerts.forEach { alert -> VetHealthAlertCard(alert = alert) }
                }
            }
            is DataState.Error -> {
                Text(stringResource(id = R.string.error_prefix) + " ${alertsState.message ?: alertsState.exception.localizedMessage}", color = MaterialTheme.colorScheme.error)
                if (alerts.isNotEmpty()) {
                    Text(stringResource(id = R.string.failed_update_showing_cached), style = MaterialTheme.typography.labelSmall)
                    alerts.forEach { alert -> VetHealthAlertCard(alert = alert) }
                }
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) { Text(stringResource(id = R.string.retry_button)) }
            }
        }
    }
}

// Removed standalone VetHealthAlertItemCard as it's now imported
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VetHomeScreenPreview() {
    MaterialTheme {
        VetHomeScreen(viewModel = PreviewVetHomeViewModel())
    }
}

// ViewModel for Preview
class PreviewVetHomeViewModel: VetHomeViewModel(
    consultationRepository = object : com.rooster.vethome.domain.repository.VetConsultationRepository {
        override fun getConsultationQueue(vetId: String) = kotlinx.coroutines.flow.flowOf(
            com.example.rooster.testing.MockDataProvider.VetHome.consultationQueue(vetId, 2)
        )
    },
    patientRepository = object : com.rooster.vethome.domain.repository.VetPatientRepository {
        override fun getRecentPatientSummaries(vetId: String, count: Int) = kotlinx.coroutines.flow.flowOf(
            com.example.rooster.testing.MockDataProvider.VetHome.recentPatientSummaries(vetId, 2)
        )
    },
    healthAlertRepository = object : com.rooster.vethome.domain.repository.VetHealthAlertRepository {
        override fun getActiveHealthAlerts(vetId: String, count: Int) = kotlinx.coroutines.flow.flowOf(
            com.example.rooster.testing.MockDataProvider.VetHome.activeHealthAlerts(vetId, 1)
        )
    }
) {
    init { }
}
