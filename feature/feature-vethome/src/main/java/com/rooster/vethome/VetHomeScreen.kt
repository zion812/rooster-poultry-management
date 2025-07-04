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
fun VetHomeScreen(
    viewModel: VetHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Veterinarian Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        ConsultationQueueSection(
            queue = uiState.consultationQueue,
            isLoading = uiState.isLoadingConsultationQueue,
            error = uiState.consultationQueueError,
            onRetry = { viewModel.fetchConsultationQueue() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        RecentPatientsSection(
            patients = uiState.recentPatients,
            isLoading = uiState.isLoadingRecentPatients,
            error = uiState.recentPatientsError,
            onRetry = { viewModel.fetchRecentPatientSummaries() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        VetHealthAlertsSection(
            alerts = uiState.healthAlerts,
            isLoading = uiState.isLoadingHealthAlerts,
            error = uiState.healthAlertsError,
            onRetry = { viewModel.fetchActiveHealthAlerts() }
        )
    }
}

@Composable
fun ConsultationQueueSection(
    queue: List<com.rooster.vethome.domain.model.ConsultationQueueItem>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Consultation Queue (${queue.filter { it.status == com.rooster.vethome.domain.model.ConsultationRequestStatus.PENDING }.size} pending)", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (queue.isEmpty()) {
            Text("Consultation queue is empty.")
        } else {
            queue.take(3).forEach { item -> // Show a few items
                // TODO: Create proper ConsultationQueueItemCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${item.farmerName} - ${item.farmLocation}", style = MaterialTheme.typography.titleSmall)
                        Text("Issue: ${item.issueSummary}", style = MaterialTheme.typography.bodyMedium)
                        Text("Status: ${item.status} (Priority: ${item.priority})", style = MaterialTheme.typography.bodySmall)
                        Text("Requested: ${java.text.SimpleDateFormat("dd MMM yy HH:mm", java.util.Locale.getDefault()).format(item.requestTime)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (queue.size > 3) Text("...and ${queue.size - 3} more.")
        }
    }
}

@Composable
fun RecentPatientsSection(
    patients: List<com.rooster.vethome.domain.model.PatientHistorySummary>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recent Patients", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (patients.isEmpty()) {
            Text("No recent patient data.")
        } else {
            patients.forEach { patient ->
                // TODO: Create proper PatientSummaryCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${patient.farmName} - ${patient.species}", style = MaterialTheme.typography.titleSmall)
                        patient.lastVisitDate?.let { Text("Last Visit: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(it)}", style = MaterialTheme.typography.bodySmall) }
                        patient.briefDiagnosis?.let { Text("Last Diagnosis: $it", style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
        }
    }
}

@Composable
fun VetHealthAlertsSection(
    alerts: List<com.rooster.vethome.domain.model.VetHealthAlert>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Active Health Alerts", style = MaterialTheme.typography.titleMedium)
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (alerts.isEmpty()) {
            Text("No active health alerts for your attention.")
        } else {
            alerts.forEach { alert ->
                 // TODO: Create proper VetHealthAlertCard
                androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text(alert.title, style = MaterialTheme.typography.titleSmall, color = if (alert.severity >= com.rooster.vethome.domain.model.VetAlertSeverity.URGENT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                        Text("${alert.farmName}: ${alert.description}", style = MaterialTheme.typography.bodyMedium)
                        Text("Severity: ${alert.severity}", style = MaterialTheme.typography.bodySmall)
                        Text("Reported: ${java.text.SimpleDateFormat("dd MMM yy HH:mm", java.util.Locale.getDefault()).format(alert.timestamp)}", style = MaterialTheme.typography.bodySmall)
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
