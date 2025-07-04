package com.example.rooster.veterinary.prescriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
data class MedicationCatalogItem(
    val name: String,
    val unit: String,
    val requiresVetAuth: Boolean
)

data class Prescription(
    val prescriptionId: String,
    val vetId: String,
    val vetName: String,
    val patientId: String,
    val patientName: String,
    val userId: String, // Owner
    val dateIssued: Date,
    val medicationName: String,
    var dosage: String,
    var frequency: String,
    var duration: String,
    var quantityDispensed: String? = null,
    var dispensingPharmacyId: String? = null,
    var instructions: String,
    var refillsAllowed: Int,
    var refillsRemaining: Int,
    var status: String, // issued, dispensed, cancelled, expired, recommended
    var notes: String?,
    var dateDispensed: Date? = null,
    var lastUpdatedBy: String? = null,
    var lastUpdatedAt: Date? = null
) {
    fun getFormattedDate(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) } ?: "N/A"
}

// --- ViewModel ---
class PrescriptionViewModel : ViewModel() {
    private val _prescriptions = MutableStateFlow<List<Prescription>>(emptyList())
    val prescriptions: StateFlow<List<Prescription>> = _prescriptions

    val medicationCatalog = listOf(
        MedicationCatalogItem("Amoxicillin 250mg tablets", "tablet", true),
        MedicationCatalogItem("Meloxicam 1mg/ml Oral Suspension", "ml", true),
        MedicationCatalogItem("Ivermectin Pour-On", "ml", true),
        MedicationCatalogItem("Saline Eye Wash", "bottle", false),
        MedicationCatalogItem("Medicated Shampoo", "bottle", false),
        MedicationCatalogItem("Fenbendazole Granules 22.2%", "gram", true)
    )
    val knownVetIds = (1..3).map { "vet${it.toString().padStart(3,'0')}" }
    val knownPatientIds = (1..5).map { "pet${it.toString().padStart(3,'0')}" }
    val knownUserIds = (1..5).map { "farmer${it.toString().padStart(3,'0')}" }
    val prescriptionStatuses = listOf("issued", "dispensed", "cancelled", "expired", "recommended")


    init {
        loadMockPrescriptions()
    }

    private fun loadMockPrescriptions(count: Int = 5) {
        val now = Date()
        val tempPrescriptions = mutableListOf<Prescription>()
        for (i in 0 until count) {
            val medInfo = medicationCatalog.random()
            val issueDate = Date(now.time - Random.nextLong(0, 90 * 86400000L))
            val status = if (medInfo.requiresVetAuth) listOf("issued", "dispensed", "cancelled").random() else listOf("recommended", "dispensed").random()
            val p = Prescription(
                prescriptionId = "rx_${issueDate.time}_$i",
                vetId = knownVetIds.random(),
                vetName = "Dr. Mock Vet", // Simplified
                patientId = knownPatientIds.random(),
                patientName = "Mock Patient", // Simplified
                userId = knownUserIds.random(),
                dateIssued = issueDate,
                medicationName = medInfo.name,
                dosage = "${Random.nextInt(1, 3)} ${medInfo.unit}(s)",
                frequency = listOf("Once daily", "Twice daily", "As needed").random(),
                duration = if (medInfo.requiresVetAuth) "${Random.nextInt(3,14)} days" else "Until symptoms resolve",
                instructions = "Follow directions carefully.",
                refillsAllowed = if (medInfo.requiresVetAuth) Random.nextInt(0, 3) else 0,
                refillsRemaining = 0, // Will be set
                status = status,
                notes = if (medInfo.requiresVetAuth) "Ensure full course." else "OTC recommendation."
            )
            p.refillsRemaining = p.refillsAllowed
            if (status == "dispensed") {
                p.quantityDispensed = "${Random.nextInt(10,50)} ${medInfo.unit}(s)"
                p.dispensingPharmacyId = "pharm${Random.nextInt(1,3)}"
                p.dateDispensed = Date(issueDate.time + Random.nextLong(0, 2 * 86400000L))
                if(p.refillsRemaining > 0) p.refillsRemaining--
            }
            tempPrescriptions.add(p)
        }
        _prescriptions.value = tempPrescriptions.sortedByDescending { it.dateIssued }
    }

    fun createPrescription(vetId: String, patientId: String, userId: String, medName: String, dosage: String, freq: String, duration: String, instr: String, refills: Int, notes: String?) {
        val medInfo = medicationCatalog.find { it.name == medName } ?: return // Should not happen if UI uses catalog
        val newRx = Prescription(
            prescriptionId = "rx_${Date().time}_${Random.nextInt(1000)}",
            vetId = vetId, vetName = "Dr. Current User", patientId = patientId, patientName = "Patient $patientId", userId = userId,
            dateIssued = Date(), medicationName = medName, dosage = dosage, frequency = freq, duration = duration,
            instructions = instr, refillsAllowed = refills, refillsRemaining = refills, status = "issued", notes = notes
        )
        _prescriptions.update { (listOf(newRx) + it).sortedByDescending { rx -> rx.dateIssued } }
    }

    fun updatePrescriptionStatus(rxId: String, newStatus: String, updatedBy: String, qtyDispensed: String? = null, pharmacyId: String? = null) {
        _prescriptions.update { list ->
            list.map {
                if (it.prescriptionId == rxId) {
                    it.copy(
                        status = newStatus,
                        lastUpdatedAt = Date(),
                        lastUpdatedBy = updatedBy,
                        quantityDispensed = if (newStatus == "dispensed") qtyDispensed ?: it.quantityDispensed else it.quantityDispensed,
                        dispensingPharmacyId = if (newStatus == "dispensed") pharmacyId ?: it.dispensingPharmacyId else it.dispensingPharmacyId,
                        dateDispensed = if (newStatus == "dispensed") Date() else it.dateDispensed,
                        refillsRemaining = if (newStatus == "dispensed" && it.refillsRemaining > 0) it.refillsRemaining -1 else it.refillsRemaining
                    )
                } else it
            }
        }
    }
}


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionManagementScreen(viewModel: PrescriptionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val prescriptions by viewModel.prescriptions.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showUpdateStatusDialog by remember { mutableStateOf(false) }
    var selectedPrescription by remember { mutableStateOf<Prescription?>(null) }

    if (showCreateDialog) {
        CreatePrescriptionDialog(viewModel = viewModel, onDismiss = { showCreateDialog = false })
    }
    if (showUpdateStatusDialog && selectedPrescription != null) {
        UpdatePrescriptionStatusDialog(
            prescription = selectedPrescription!!,
            viewModel = viewModel,
            onDismiss = { showUpdateStatusDialog = false; selectedPrescription = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescription Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF26A69A)) // Teal
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, "Create New Prescription")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (prescriptions.isEmpty()) {
                item { Text("No prescriptions found.") }
            } else {
                items(prescriptions, key = { it.prescriptionId }) { rx ->
                    PrescriptionCard(rx) {
                        selectedPrescription = rx
                        showUpdateStatusDialog = true
                    }
                }
            }
        }
    }
}

@Composable
fun PrescriptionCard(rx: Prescription, onCardClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onCardClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${rx.medicationName} (ID: ${rx.prescriptionId.takeLast(6)})", style = MaterialTheme.typography.titleMedium)
            Text("Patient: ${rx.patientName} (ID: ${rx.patientId}) - Owner: ${rx.userId}")
            Text("Vet: ${rx.vetName} (ID: ${rx.vetId})")
            Text("Issued: ${rx.getFormattedDate(rx.dateIssued)}")
            Text("Dosage: ${rx.dosage}, ${rx.frequency} for ${rx.duration}")
            Text("Instructions: ${rx.instructions}")
            Text("Refills: ${rx.refillsRemaining}/${rx.refillsAllowed}")
            Text("Status: ${rx.status}", color = when(rx.status){
                "issued" -> Color.Blue
                "dispensed" -> Color.Green
                "cancelled", "expired" -> Color.Red
                else -> Color.DarkGray
            })
            rx.notes?.let { Text("Notes: $it") }
            if (rx.status == "dispensed") {
                Text("Dispensed: ${rx.quantityDispensed ?: ""} by ${rx.dispensingPharmacyId ?: ""} on ${rx.getFormattedDate(rx.dateDispensed)}")
            }
            rx.lastUpdatedAt?.let { Text("Last Update: ${rx.getFormattedDate(it)} by ${rx.lastUpdatedBy ?: ""}", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
fun CreatePrescriptionDialog(viewModel: PrescriptionViewModel, onDismiss: () -> Unit) {
    // Basic fields for simplicity, can be expanded
    var patientId by remember { mutableStateOf(viewModel.knownPatientIds.first()) }
    var medicationName by remember { mutableStateOf(viewModel.medicationCatalog.first().name) }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var refills by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Prescription") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { ExposedDropdownMenuForOptions(label = "Patient ID", options = viewModel.knownPatientIds, selectedOption = patientId, onOptionSelected = { patientId = it }) }
                item { ExposedDropdownMenuForOptions(label = "Medication", options = viewModel.medicationCatalog.map { it.name }, selectedOption = medicationName, onOptionSelected = { medicationName = it }) }
                item { OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g., 1 tablet)") }) }
                item { OutlinedTextField(value = frequency, onValueChange = { frequency = it }, label = { Text("Frequency (e.g., Twice daily)") }) }
                item { OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (e.g., 7 days)") }) }
                item { OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Instructions") }) }
                item { OutlinedTextField(value = refills, onValueChange = { refills = it.filter(Char::isDigit) }, label = { Text("Refills Allowed") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
                item { OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (Optional)") }) }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.createPrescription(
                    vetId = "vet_current_user", // Placeholder
                    patientId = patientId,
                    userId = "farmer_current_user", // Placeholder
                    medName = medicationName,
                    dosage = dosage, freq = frequency, duration = duration, instr = instructions,
                    refills = refills.toIntOrNull() ?: 0, notes = notes.ifBlank { null }
                )
                onDismiss()
            }) { Text("Create") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun UpdatePrescriptionStatusDialog(prescription: Prescription, viewModel: PrescriptionViewModel, onDismiss: () -> Unit) {
    var newStatus by remember { mutableStateOf(prescription.status) }
    var qtyDispensed by remember { mutableStateOf(prescription.quantityDispensed ?: "") }
    var pharmacyId by remember { mutableStateOf(prescription.dispensingPharmacyId ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Rx: ${prescription.prescriptionId.takeLast(6)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Medication: ${prescription.medicationName}")
                ExposedDropdownMenuForOptions(label = "New Status", options = viewModel.prescriptionStatuses, selectedOption = newStatus, onOptionSelected = {newStatus = it})
                if (newStatus == "dispensed") {
                    OutlinedTextField(value = qtyDispensed, onValueChange = {qtyDispensed = it}, label = {Text("Quantity Dispensed")})
                    OutlinedTextField(value = pharmacyId, onValueChange = {pharmacyId = it}, label = {Text("Dispensing Pharmacy ID")})
                }
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.updatePrescriptionStatus(prescription.prescriptionId, newStatus, "admin_compose", if(newStatus == "dispensed") qtyDispensed else null, if(newStatus == "dispensed") pharmacyId else null)
            onDismiss()
        }) {Text("Update Status")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}

// Re-using ExposedDropdownMenuForOptions, assuming it's accessible
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions( // Copied for standalone preview if needed
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewPrescriptionManagementScreen() {
    MaterialTheme {
        PrescriptionManagementScreen(viewModel = PrescriptionViewModel())
    }
}
