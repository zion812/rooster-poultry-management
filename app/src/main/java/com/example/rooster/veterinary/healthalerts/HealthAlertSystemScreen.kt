package com.example.rooster.veterinary.healthalerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class GeoCoordinates(val latitude: Double, val longitude: Double)
data class CaseUpdate(val timestamp: Date, val note: String, val updatedBy: String? = null) {
    fun getFormattedTimestamp(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timestamp)
}

data class ReportedCase(
    val caseId: String,
    var diseaseName: String,
    var location: String,
    var geoCoordinates: GeoCoordinates,
    var affectedRadiusKm: Double,
    val dateReported: Date,
    var numberOfConfirmedCases: Int,
    var numberOfSuspectedCases: Int,
    var affectedSpecies: List<String>,
    var severity: String, // Low, Medium, High, Critical
    var sourceOfReport: String,
    var status: String, // under_investigation, monitoring, contained, resolved
    var updates: MutableList<CaseUpdate> = mutableListOf()
) {
    fun getFormattedDateReported(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(dateReported)
}

data class HealthAlert(
    val alertId: String,
    val basedOnCaseId: String,
    val diseaseName: String,
    val locationSummary: String,
    val severity: String,
    val dateIssued: Date,
    val targetAudience: String,
    val alertMessage: String,
    val preventativeMeasuresLink: String,
    var status: String // active, superseded, cancelled
) {
    fun getFormattedDateIssued(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(dateIssued)
}


// --- ViewModel ---
class HealthAlertViewModel : ViewModel() {
    private val _reportedCases = MutableStateFlow<List<ReportedCase>>(emptyList())
    val reportedCases: StateFlow<List<ReportedCase>> = _reportedCases

    private val _activeAlerts = MutableStateFlow<List<HealthAlert>>(emptyList())
    val activeAlerts: StateFlow<List<HealthAlert>> = _activeAlerts

    // Mock data for dropdowns etc.
    val knownDiseases = listOf("Avian Influenza", "Foot and Mouth Disease", "Rabies", "Swine Flu", "Bluetongue")
    val severities = listOf("Low", "Medium", "High", "Critical")
    val caseStatuses = listOf("under_investigation", "monitoring", "contained", "resolved")
    val speciesList = listOf("Cattle", "Poultry", "Swine", "Sheep", "Goats", "Horses", "Dogs", "Cats")


    init {
        loadMockData()
    }

    private fun loadMockData() {
        val now = Date()
        val initialCases = mutableListOf<ReportedCase>()
        for (i in 1..3) {
            val reportDate = Date(now.time - Random.nextLong(1, 30) * 86400000L)
            initialCases.add(
                ReportedCase(
                    caseId = "case_${reportDate.time}_$i",
                    diseaseName = knownDiseases.random(),
                    location = "Location $i, ST",
                    geoCoordinates = GeoCoordinates(Random.nextDouble(30.0, 40.0), Random.nextDouble(-90.0, -80.0)),
                    affectedRadiusKm = Random.nextDouble(10.0, 50.0),
                    dateReported = reportDate,
                    numberOfConfirmedCases = Random.nextInt(1, 20),
                    numberOfSuspectedCases = Random.nextInt(5, 50),
                    affectedSpecies = speciesList.shuffled().take(Random.nextInt(1,3)),
                    severity = severities.random(),
                    sourceOfReport = "Vet Clinic $i",
                    status = caseStatuses.random(),
                    updates = mutableListOf(CaseUpdate(reportDate, "Initial report"))
                )
            )
        }
        _reportedCases.value = initialCases.sortedByDescending { it.dateReported }
        generateAlertsFromCases()
    }

    private fun generateAlertsFromCases() {
        val newAlerts = mutableListOf<HealthAlert>()
        _reportedCases.value.forEach { case ->
            if (case.severity in listOf("High", "Critical") && case.status !in listOf("resolved", "contained")) {
                if (_activeAlerts.value.none { it.basedOnCaseId == case.caseId && it.status == "active"}) {
                     newAlerts.add(
                        HealthAlert(
                            alertId = "alert_${case.caseId}",
                            basedOnCaseId = case.caseId,
                            diseaseName = case.diseaseName,
                            locationSummary = case.location,
                            severity = case.severity,
                            dateIssued = Date(case.dateReported.time + Random.nextLong(1, 6) * 3600000L), // Issued a bit after report
                            targetAudience = "Users within ${case.affectedRadiusKm.toInt()}km",
                            alertMessage = "WARNING: ${case.severity} risk of ${case.diseaseName} near ${case.location}. Affects: ${case.affectedSpecies.joinToString()}. Take precautions.",
                            preventativeMeasuresLink = "https://example.com/alerts/${case.diseaseName.lowercase().replace(" ", "-")}",
                            status = "active"
                        )
                    )
                }
            }
        }
        _activeAlerts.update { (it + newAlerts).distinctBy { alert -> alert.alertId }.sortedByDescending { alert -> alert.dateIssued } }
    }

    fun reportNewCase(disease: String, location: String, lat: Double, lon: Double, radius: Double, confirmed: Int, suspected: Int, species: List<String>, severity: String, source: String, notes: String, reporterId: String) {
        val reportDate = Date()
        val newCase = ReportedCase(
            caseId = "case_${reportDate.time}_${Random.nextInt(1000)}",
            diseaseName = disease, location = location, geoCoordinates = GeoCoordinates(lat, lon),
            affectedRadiusKm = radius, dateReported = reportDate, numberOfConfirmedCases = confirmed,
            numberOfSuspectedCases = suspected, affectedSpecies = species, severity = severity,
            sourceOfReport = source, status = "under_investigation",
            updates = mutableListOf(CaseUpdate(reportDate, notes, reporterId))
        )
        _reportedCases.update { (listOf(newCase) + it).sortedByDescending { c -> c.dateReported } }
        if (severity in listOf("High", "Critical")) {
            generateAlertsFromCases() // Re-evaluate alerts
        }
    }

    fun updateCaseStatus(caseId: String, newStatus: String, notes: String, adminId: String) {
        _reportedCases.update { cases ->
            cases.map {
                if (it.caseId == caseId) {
                    it.copy(status = newStatus, updates = (it.updates + CaseUpdate(Date(), notes, adminId)).toMutableList())
                } else it
            }
        }
        generateAlertsFromCases() // Status change might affect alerts
    }
     fun createAlertFromCase(caseId: String, customMessage: String?, adminId: String){
         val case = _reportedCases.value.find{ it.caseId == caseId} ?: return
         if (_activeAlerts.value.any { it.basedOnCaseId == case.caseId && it.status == "active" }) return // Avoid duplicate

         val alert = HealthAlert(
            alertId = "alert_manual_${case.caseId}_${Date().time}",
            basedOnCaseId = case.caseId,
            diseaseName = case.diseaseName,
            locationSummary = case.location,
            severity = case.severity,
            dateIssued = Date(),
            targetAudience = "Users within ${case.affectedRadiusKm.toInt()}km (Manual Alert by $adminId)",
            alertMessage = customMessage ?: "MANUAL ALERT by $adminId: ${case.severity} risk of ${case.diseaseName} near ${case.location}. Affects: ${case.affectedSpecies.joinToString()}.",
            preventativeMeasuresLink = "https://example.com/alerts/${case.diseaseName.lowercase().replace(" ", "-")}",
            status = "active"
        )
        _activeAlerts.update { (listOf(alert) + it).distinctBy {a -> a.alertId}.sortedByDescending { a -> a.dateIssued } }
    }
}


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAlertSystemScreen(viewModel: HealthAlertViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val cases by viewModel.reportedCases.collectAsState()
    val alerts by viewModel.activeAlerts.collectAsState()

    var showReportCaseDialog by remember { mutableStateOf(false) }
    var showUpdateStatusDialog by remember { mutableStateOf(false) }
    var showCreateAlertDialog by remember { mutableStateOf(false) }
    var selectedCaseForOps by remember { mutableStateOf<ReportedCase?>(null) }


    if (showReportCaseDialog) {
        ReportNewCaseDialog(
            viewModel = viewModel,
            onDismiss = { showReportCaseDialog = false }
        )
    }
    if (showUpdateStatusDialog && selectedCaseForOps != null) {
        UpdateCaseStatusDialog(
            case = selectedCaseForOps!!,
            viewModel = viewModel,
            onDismiss = { showUpdateStatusDialog = false; selectedCaseForOps = null }
        )
    }
    if (showCreateAlertDialog && selectedCaseForOps != null) {
        CreateAlertDialog(
            case = selectedCaseForOps!!,
            viewModel = viewModel,
            onDismiss = { showCreateAlertDialog = false; selectedCaseForOps = null }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Alert System") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF06292)) // Pink
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showReportCaseDialog = true }) {
                Icon(Icons.Filled.Add, "Report New Case")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item { SectionTitle("Active Health Alerts (${alerts.size})") }
            if(alerts.isEmpty()) item { EmptyState("No active health alerts.")}
            items(alerts, key = { it.alertId }) { alert -> ActiveAlertCard(alert) }

            item { SectionTitle("Recently Reported Cases (${cases.size})") }
            if(cases.isEmpty()) item { EmptyState("No cases reported yet.")}
            items(cases, key = { it.caseId }) { case ->
                ReportedCaseCard(case,
                    onUpdateStatus = { selectedCaseForOps = it; showUpdateStatusDialog = true },
                    onCreateAlert = { selectedCaseForOps = it; showCreateAlertDialog = true }
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}
@Composable
fun EmptyState(message: String) {
    Text(message, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
}


@Composable
fun ActiveAlertCard(alert: HealthAlert) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("ALERT: ${alert.diseaseName} (${alert.severity})", style = MaterialTheme.typography.titleMedium, color = if(alert.severity in listOf("High", "Critical")) Color.Red else MaterialTheme.colorScheme.onSurface)
            Text("Location: ${alert.locationSummary}", style = MaterialTheme.typography.bodyMedium)
            Text(alert.alertMessage, style = MaterialTheme.typography.bodyMedium)
            Text("Issued: ${alert.getFormattedDateIssued()}", style = MaterialTheme.typography.bodySmall)
            Text("Target: ${alert.targetAudience}", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = { /* Open link */ }) { Text("More Info: ${alert.preventativeMeasuresLink}")}
        }
    }
}

@Composable
fun ReportedCaseCard(case: ReportedCase, onUpdateStatus: () -> Unit, onCreateAlert: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${case.diseaseName} at ${case.location}", style = MaterialTheme.typography.titleMedium)
            Text("Case ID: ${case.caseId}", style = MaterialTheme.typography.bodySmall)
            Text("Status: ${case.status} (${case.severity})", fontWeight = FontWeight.Bold)
            Text("Reported: ${case.getFormattedDateReported()} by ${case.sourceOfReport}")
            Text("Species: ${case.affectedSpecies.joinToString()}, Confirmed: ${case.numberOfConfirmedCases}, Suspected: ${case.numberOfSuspectedCases}")
            Text("Updates:", style = MaterialTheme.typography.labelMedium)
            case.updates.takeLast(2).forEach { update ->
                Text("- ${update.note} (${update.getFormattedTimestamp()}) ${update.updatedBy?.let{"by $it"} ?:""}", style = MaterialTheme.typography.bodySmall)
            }
            Row(Modifier.fillMaxWidth().padding(top=8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onUpdateStatus) { Text("Update Status") }
                Spacer(Modifier.width(8.dp))
                if (case.severity in listOf("High", "Critical") && case.status !in listOf("resolved", "contained")) {
                     Button(onClick = onCreateAlert, elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Create Alert", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Create Alert")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportNewCaseDialog(viewModel: HealthAlertViewModel, onDismiss: () -> Unit) {
    var disease by remember { mutableStateOf(viewModel.knownDiseases.first()) }
    var location by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("10") }
    var confirmed by remember { mutableStateOf("0") }
    var suspected by remember { mutableStateOf("1") }
    var selectedSpecies by remember { mutableStateOf(emptyList<String>()) } // For multi-select potentially
    var severity by remember { mutableStateOf(viewModel.severities.first()) }
    var source by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report New Health Case") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Use LazyColumn if many fields
                item { OutlinedTextField(value = disease, onValueChange = { disease = it }, label = { Text("Disease Name") }) } // Consider ExposedDropdownMenuBox
                item { OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location (e.g., City, Farm ID)") }) }
                item { Row {
                    OutlinedTextField(value = lat, onValueChange = { lat = it.filter{c->c.isDigit()||c=='.'||c=='-'} }, label = { Text("Latitude") }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = lon, onValueChange = { lon = it.filter{c->c.isDigit()||c=='.'||c=='-'} }, label = { Text("Longitude") }, modifier = Modifier.weight(1f))
                }}
                item { OutlinedTextField(value = radius, onValueChange = { radius = it.filter{c->c.isDigit()||c=='.'} }, label = { Text("Affected Radius (km)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
                item { Row {
                    OutlinedTextField(value = confirmed, onValueChange = { confirmed = it.filter{c->c.isDigit()} }, label = { Text("Confirmed Cases") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = suspected, onValueChange = { suspected = it.filter{c->c.isDigit()} }, label = { Text("Suspected Cases") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }}
                // Species selection could be more complex (e.g., multi-select checkboxes)
                item { OutlinedTextField(value = selectedSpecies.joinToString(), onValueChange = { selectedSpecies = it.split(",").map(String::trim).filter(String::isNotBlank) }, label = { Text("Affected Species (comma-sep)") }) }
                item { ExposedDropdownMenuForOptions(label = "Severity", options = viewModel.severities, selectedOption = severity, onOptionSelected = { severity = it }) }
                item { OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Source of Report") }) }
                item { OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Initial Notes") }, maxLines = 3) }
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.reportNewCase(disease, location, lat.toDoubleOrNull()?:0.0, lon.toDoubleOrNull()?:0.0, radius.toDoubleOrNull()?:10.0, confirmed.toIntOrNull()?:0, suspected.toIntOrNull()?:1, selectedSpecies, severity, source, notes, "user_android")
            onDismiss()
        }) { Text("Report Case") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun UpdateCaseStatusDialog(case: ReportedCase, viewModel: HealthAlertViewModel, onDismiss: () -> Unit) {
    var newStatus by remember { mutableStateOf(case.status) }
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status for Case: ${case.caseId.take(8)}...")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Current Status: ${case.status}")
                ExposedDropdownMenuForOptions(label = "New Status", options = viewModel.caseStatuses, selectedOption = newStatus, onOptionSelected = {newStatus = it})
                OutlinedTextField(value = notes, onValueChange = {notes = it}, label = { Text("Update Notes")})
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.updateCaseStatus(case.caseId, newStatus, notes, "admin_android")
            onDismiss()
        }) {Text("Update")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}

@Composable
fun CreateAlertDialog(case: ReportedCase, viewModel: HealthAlertViewModel, onDismiss: () -> Unit) {
    var customMessage by remember { mutableStateOf("") }
     AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Alert for Case: ${case.caseId.take(8)}...")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Disease: ${case.diseaseName}, Severity: ${case.severity}")
                OutlinedTextField(value = customMessage, onValueChange = {customMessage = it}, label = { Text("Custom Alert Message (Optional)")}, placeholder = {Text("Default message will be used if empty.")})
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.createAlertFromCase(case.caseId, customMessage.ifBlank { null }, "admin_android")
            onDismiss()
        }) {Text("Create Alert")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {}, readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true, heightDp = 1200)
@Composable
fun PreviewHealthAlertSystemScreen() {
    MaterialTheme {
        HealthAlertSystemScreen(viewModel = HealthAlertViewModel())
    }
}
