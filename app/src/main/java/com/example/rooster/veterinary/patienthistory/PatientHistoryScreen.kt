package com.example.rooster.veterinary.patienthistory

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
data class MedicationPrescribed(
    val medication: String,
    val dosage: String,
    val duration: String
)

data class VaccinationGiven(
    val vaccineName: String,
    val batchNumber: String,
    val nextDue: String? // ISO Date String
)

data class MedicalRecord(
    val recordId: String,
    val visitDate: Date,
    val recordType: String, // check-up, vaccination, illness, injury, procedure
    val attendingVetName: String,
    val presentingComplaint: String,
    val diagnosis: String,
    val treatmentPlan: String,
    val medicationsPrescribed: List<MedicationPrescribed> = emptyList(),
    val vaccinationsGiven: List<VaccinationGiven> = emptyList(),
    val weightKg: Double?,
    val temperatureCelsius: Double?,
    val notes: String,
    val followUpNeeded: Boolean
) {
    fun getFormattedVisitDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(visitDate)
}

data class PatientDetails(
    val patientId: String,
    val name: String,
    val species: String,
    val breed: String,
    val dob: String, // ISO Date String
    val ownerId: String,
    val gender: String?,
    val color: String? = null,
    val microchipId: String? = null
)

data class PatientFullHistory(
    val details: PatientDetails,
    val records: List<MedicalRecord>
)

// --- ViewModel ---
class PatientHistoryViewModel : ViewModel() {
    private val _patientHistory = MutableStateFlow<PatientFullHistory?>(null)
    val patientHistory: StateFlow<PatientFullHistory?> = _patientHistory

    private var currentPatientId: String = "pet001" // Default patient to display

    // Mock data for dropdowns in Add Record Dialog
    val recordTypes = listOf("check_up", "vaccination", "illness", "injury", "procedure")
    val commonVets = listOf("Dr. Alice Smith", "Dr. Bob Johnson", "Dr. Carol White", "Dr. Eve Foster")


    init {
        loadPatientData(currentPatientId)
    }

    fun loadPatientData(patientId: String) {
        currentPatientId = patientId
        // In a real app, this would fetch from a database or API
        // For now, generate mock data for the requested patientId
        _patientHistory.value = generateMockPatientFullHistory(patientId)
    }

    private fun generateMockPatientFullHistory(patientId: String): PatientFullHistory {
        val details = PatientDetails( // Simplified for direct use
            patientId = patientId,
            name = when(patientId) {
                "pet001" -> "Bessie"
                "pet002" -> "Charlie"
                "pet003" -> "Daisy"
                else -> "Unknown Pet"
            },
            species = if (patientId == "pet002") "Dog" else "Cow",
            breed = if (patientId == "pet002") "Labrador" else "Holstein",
            dob = if (patientId == "pet002") "2020-07-22" else "2018-03-15",
            ownerId = "farmer${Random.nextInt(1,5).toString().padStart(3,'0')}",
            gender = if (Random.nextBoolean()) "Female" else "Male"
        )

        val records = mutableListOf<MedicalRecord>()
        val numRecords = Random.nextInt(1, 5)
        for (i in 0 until numRecords) {
            val visitDate = Date(System.currentTimeMillis() - Random.nextLong(10, 730) * 86400000L)
            records.add(
                MedicalRecord(
                    recordId = "rec_${visitDate.time}_$i",
                    visitDate = visitDate,
                    recordType = recordTypes.random(),
                    attendingVetName = commonVets.random(),
                    presentingComplaint = "Symptom $i",
                    diagnosis = "Diagnosis $i",
                    treatmentPlan = "Treatment plan $i",
                    medicationsPrescribed = if (Random.nextBoolean()) listOf(MedicationPrescribed("Med A", "1 tab BID", "7 days")) else emptyList(),
                    vaccinationsGiven = if (Random.nextBoolean()) listOf(VaccinationGiven("Vaccine X", "B123", "2024-12-31")) else emptyList(),
                    weightKg = Random.nextDouble(5.0, (if(details.species == "Cow") 600.0 else 50.0)),
                    temperatureCelsius = Random.nextDouble(37.5, 39.5),
                    notes = "Notes for visit $i.",
                    followUpNeeded = Random.nextBoolean()
                )
            )
        }
        return PatientFullHistory(details, records.sortedByDescending { it.visitDate })
    }

    fun addMedicalRecord(patientId: String, record: MedicalRecord) {
        _patientHistory.update { currentHistory ->
            currentHistory?.copy(records = (listOf(record) + currentHistory.records).sortedByDescending { it.visitDate })
        }
        // In a real app, persist this record.
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHistoryScreen(viewModel: PatientHistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val patientHistory by viewModel.patientHistory.collectAsState()

    // State for Add Record Dialog
    var showAddRecordDialog by remember { mutableStateOf(false) }

    if (showAddRecordDialog && patientHistory != null) {
        AddMedicalRecordDialog(
            patientId = patientHistory!!.details.patientId,
            viewModel = viewModel,
            onDismiss = { showAddRecordDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient History: ${patientHistory?.details?.name ?: "Loading..."}") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF795548)) // Brown
            )
        },
        floatingActionButton = {
            if (patientHistory != null) { // Show FAB only when patient data is loaded
                FloatingActionButton(onClick = { showAddRecordDialog = true }) {
                    Icon(Icons.Filled.Add, "Add Medical Record")
                }
            }
        }
    ) { paddingValues ->
        patientHistory?.let { history ->
            LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                item { PatientDetailsCard(history.details) }
                item {
                    Text(
                        "Medical Records (${history.records.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                if (history.records.isEmpty()) {
                    item { Text("No medical records found for this patient.") }
                } else {
                    items(history.records, key = { it.recordId }) { record ->
                        MedicalRecordCard(record)
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            // Show a loading indicator or allow patient selection
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Enter Patient ID to load history:")
                var patientIdInput by remember { mutableStateOf("pet001") }
                OutlinedTextField(
                    value = patientIdInput,
                    onValueChange = { patientIdInput = it },
                    label = { Text("Patient ID") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(onClick = { viewModel.loadPatientData(patientIdInput.trim()) }) {
                    Text("Load Patient")
                }
            }
        }
    }
}

@Composable
fun PatientDetailsCard(details: PatientDetails) {
    Card(Modifier.fillMaxWidth().padding(bottom = 16.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Patient: ${details.name} (ID: ${details.patientId})", style = MaterialTheme.typography.titleLarge)
            Text("Species: ${details.species}, Breed: ${details.breed}")
            Text("DOB: ${details.dob}, Gender: ${details.gender ?: "N/A"}")
            Text("Owner ID: ${details.ownerId}")
            details.color?.let { Text("Color: $it") }
            details.microchipId?.let { Text("Microchip: $it") }
        }
    }
}

@Composable
fun MedicalRecordCard(record: MedicalRecord) {
    val df = remember { DecimalFormat("#0.0") }
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Visit: ${record.getFormattedVisitDate()} - ${record.recordType.replaceFirstChar { it.titlecase() }}", style = MaterialTheme.typography.titleMedium)
            Text("Vet: ${record.attendingVetName}", style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Complaint: ${record.presentingComplaint}")
            Text("Diagnosis: ${record.diagnosis}", fontWeight = FontWeight.Bold)
            Text("Treatment: ${record.treatmentPlan}")
            if (record.medicationsPrescribed.isNotEmpty()) {
                Text("Medications:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top=4.dp))
                record.medicationsPrescribed.forEach { med ->
                    Text("  - ${med.medication} (${med.dosage}, ${med.duration})")
                }
            }
            if (record.vaccinationsGiven.isNotEmpty()) {
                Text("Vaccinations:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top=4.dp))
                record.vaccinationsGiven.forEach { vacc ->
                    Text("  - ${vacc.vaccineName} (Batch: ${vacc.batchNumber}, Next Due: ${vacc.nextDue ?: "N/A"})")
                }
            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                record.weightKg?.let { Text("Weight: ${df.format(it)} kg", modifier = Modifier.weight(1f)) }
                record.temperatureCelsius?.let { Text("Temp: ${df.format(it)} °C", modifier = Modifier.weight(1f)) }
            }
            Text("Notes: ${record.notes}")
            Text("Follow-up Needed: ${if (record.followUpNeeded) "Yes" else "No"}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicalRecordDialog(patientId: String, viewModel: PatientHistoryViewModel, onDismiss: () -> Unit) {
    var visitDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var recordType by remember { mutableStateOf(viewModel.recordTypes.first()) }
    var attendingVet by remember { mutableStateOf(viewModel.commonVets.first()) }
    var complaint by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var followUp by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medical Record for $patientId") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { // For scrollability if form is long
                item { OutlinedTextField(value = visitDate, onValueChange = { visitDate = it }, label = { Text("Visit Date (YYYY-MM-DD)") }) }
                item { ExposedDropdownMenuForOptions(label = "Record Type", options = viewModel.recordTypes, selectedOption = recordType, onOptionSelected = { recordType = it }) }
                item { ExposedDropdownMenuForOptions(label = "Attending Vet", options = viewModel.commonVets, selectedOption = attendingVet, onOptionSelected = { attendingVet = it }) }
                item { OutlinedTextField(value = complaint, onValueChange = { complaint = it }, label = { Text("Presenting Complaint") }) }
                item { OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Diagnosis") }) }
                item { OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text("Treatment Plan") }) }
                item { OutlinedTextField(value = weight, onValueChange = { weight = it.filter{c->c.isDigit()||c=='.'} }, label = { Text("Weight (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
                item { OutlinedTextField(value = temp, onValueChange = { temp = it.filter{c->c.isDigit()||c=='.'} }, label = { Text("Temperature (°C)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
                item { OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, maxLines = 3) }
                item { Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = followUp, onCheckedChange = { followUp = it })
                    Text("Follow-up Needed")
                }}
            }
        },
        confirmButton = {
            Button(onClick = {
                val dateParsed = try { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(visitDate) } catch (e: Exception) { Date() }
                val newRecord = MedicalRecord(
                    recordId = "rec_${System.currentTimeMillis()}",
                    visitDate = dateParsed ?: Date(),
                    recordType = recordType,
                    attendingVetName = attendingVet,
                    presentingComplaint = complaint,
                    diagnosis = diagnosis,
                    treatmentPlan = treatment,
                    notes = notes,
                    weightKg = weight.toDoubleOrNull(),
                    temperatureCelsius = temp.toDoubleOrNull(),
                    followUpNeeded = followUp
                    // Medications and Vaccinations can be added via a more complex UI later
                )
                viewModel.addMedicalRecord(patientId, newRecord)
                onDismiss()
            }) { Text("Add Record") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

// Re-using ExposedDropdownMenuForOptions from HealthAlertSystemScreen, assuming it's accessible
// If not, it should be defined here or in a shared UI module.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions( // Copied from HealthAlertSystemScreen for standalone preview
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


@Preview(showBackground = true, heightDp = 1000)
@Composable
fun PreviewPatientHistoryScreen() {
    MaterialTheme {
        PatientHistoryScreen(viewModel = PatientHistoryViewModel())
    }
}
