package com.example.rooster.veterinary.consultations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class VetProfile(
    val vetId: String,
    val name: String,
    val specialty: String,
    var availability: Map<String, List<String>>, // Date string to list of time slots (e.g., "HH:mm")
    val consultationFee: Double
)

data class Appointment(
    val appointmentId: String,
    val vetId: String,
    val vetName: String,
    val userId: String,
    val patientName: String,
    val appointmentDateTime: Date,
    val reason: String,
    var status: String // booked, completed, cancelled, in_progress
) {
    fun getFormattedDateTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(appointmentDateTime)
}

data class PatientQueueEntry(
    val queueId: String,
    val userId: String,
    val patientName: String,
    val arrivalTime: Date,
    val reason: String,
    var assignedVetId: String? = null,
    var assignedVetName: String? = null, // For display
    var status: String = "waiting" // waiting, assigned, in_consultation
) {
    fun getFormattedArrivalTime(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(arrivalTime)
}

// --- ViewModel ---
class VetConsultationViewModel : ViewModel() {
    private val _vets = MutableStateFlow<List<VetProfile>>(emptyList())
    val vets: StateFlow<List<VetProfile>> = _vets

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    private val _patientQueue = MutableStateFlow<List<PatientQueueEntry>>(emptyList())
    val patientQueue: StateFlow<List<PatientQueueEntry>> = _patientQueue

    val specialties = listOf("General Practice", "Surgery", "Dermatology", "Livestock Specialist", "Cardiology")
    val mockPatientNames = listOf("Bessie", "Charlie", "Daisy", "Rocky", "Lucy", "Max", "Speckles")
    val mockReasons = listOf("General check-up", "Vaccination", "Skin issue", "Limping", "Not eating", "Urgent concern")


    init {
        loadMockData()
    }

    private fun generateMockAvailability(numDays: Int = 7): Map<String, List<String>> {
        val availability = mutableMapOf<String, List<String>>()
        val today = Calendar.getInstance()
        for (i in 0 until numDays) {
            val dayCal = today.clone() as Calendar
            dayCal.add(Calendar.DATE, i)
            if (dayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || dayCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) continue

            val timeSlots = mutableListOf<String>()
            for (j in 0..4) { // 5 slots per day
                val hour = 9 + j
                if (Random.nextBoolean()) { // Slot available?
                    timeSlots.add(String.format("%02d:00", hour))
                }
            }
            if (timeSlots.isNotEmpty()) {
                availability[SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCal.time)] = timeSlots
            }
        }
        return availability
    }

    private fun loadMockData() {
        val vetNames = listOf("Dr. Alice Smith", "Dr. Bob Johnson", "Dr. Carol White", "Dr. David Brown")
        val tempVets = vetNames.mapIndexed { i, name ->
            VetProfile(
                vetId = "vet${(i + 1).toString().padStart(3, '0')}",
                name = name,
                specialty = specialties.random(),
                availability = generateMockAvailability(),
                consultationFee = Random.nextDouble(50.0, 150.0)
            )
        }
        _vets.value = tempVets

        val tempAppointments = mutableListOf<Appointment>()
        // Create some mock appointments
        _appointments.value = tempAppointments.sortedBy { it.appointmentDateTime }

        val tempQueue = (1..3).map { i ->
            PatientQueueEntry(
                queueId = "q_${Date().time}_$i",
                userId = "farmer${Random.nextInt(1,5).toString().padStart(3,'0')}",
                patientName = mockPatientNames.random(),
                arrivalTime = Date(System.currentTimeMillis() - Random.nextLong(5, 30) * 60000L),
                reason = mockReasons.random()
            )
        }.toMutableList()
        _patientQueue.value = tempQueue.sortedBy { it.arrivalTime }
    }

    fun bookAppointment(vetId: String, userId: String, patientName: String, dateStr: String, timeStr: String, reason: String): Boolean {
        val vet = _vets.value.find { it.vetId == vetId } ?: return false
        val slotsForDate = vet.availability[dateStr] ?: return false
        if (!slotsForDate.contains(timeStr)) return false

        // Remove slot (simplified)
        val updatedAvailability = vet.availability.toMutableMap()
        updatedAvailability[dateStr] = slotsForDate.filterNot { it == timeStr }
        _vets.update { list -> list.map { if (it.vetId == vetId) it.copy(availability = updatedAvailability) else it } }

        val appointmentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse("$dateStr $timeStr") ?: Date()
        val newAppointment = Appointment(
            appointmentId = "appt_${Date().time}", vetId = vetId, vetName = vet.name, userId = userId,
            patientName = patientName, appointmentDateTime = appointmentDateTime, reason = reason, status = "booked"
        )
        _appointments.update { (it + newAppointment).sortedBy { appt -> appt.appointmentDateTime } }
        return true
    }

    fun addToQueue(userId: String, patientName: String, reason: String) {
        val newEntry = PatientQueueEntry(
            queueId = "q_${Date().time}_${Random.nextInt(100)}", userId = userId, patientName = patientName,
            arrivalTime = Date(), reason = reason
        )
        _patientQueue.update { (it + newEntry).sortedBy { entry -> entry.arrivalTime } }
    }

    fun assignQueuedPatient(queueId: String, vetId: String) {
        val vet = _vets.value.find { it.vetId == vetId } ?: return
        _patientQueue.update { queue ->
            queue.map {
                if (it.queueId == queueId && it.assignedVetId == null)
                    it.copy(assignedVetId = vetId, assignedVetName = vet.name, status = "assigned")
                else it
            }
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetConsultationScreen(viewModel: VetConsultationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val vets by viewModel.vets.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val patientQueue by viewModel.patientQueue.collectAsState()

    var showBookingDialog by remember { mutableStateOf(false) }
    var vetForBooking by remember { mutableStateOf<VetProfile?>(null) }
    var showAddToQueueDialog by remember { mutableStateOf(false) }

    if (showBookingDialog && vetForBooking != null) {
        BookingDialog(vet = vetForBooking!!, viewModel = viewModel, onDismiss = { showBookingDialog = false; vetForBooking = null })
    }
    if(showAddToQueueDialog) {
        AddToQueueDialog(viewModel = viewModel, onDismiss = { showAddToQueueDialog = false })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vet Consultation") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00ACC1))) }, // Cyan
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddToQueueDialog = true }) {
                Icon(Icons.Filled.PersonAdd, "Add to Walk-in Queue")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item { SectionTitle("Available Veterinarians") }
            items(vets, key = {it.vetId}) { vet -> VetCard(vet) { selectedVet -> vetForBooking = selectedVet; showBookingDialog = true } }

            item { SectionTitle("Upcoming Appointments (${appointments.filter{it.status == "booked" && it.appointmentDateTime >= Date()}.size})") }
            val upcomingAppointments = appointments.filter{it.status == "booked" && it.appointmentDateTime >= Date()}.take(5)
            if(upcomingAppointments.isEmpty()) item { EmptyState("No upcoming appointments.")}
            items(upcomingAppointments, key = {it.appointmentId}) { appt -> AppointmentCard(appt) }

            item { SectionTitle("Patient Queue (${patientQueue.size})") }
             if(patientQueue.isEmpty()) item { EmptyState("Patient queue is empty.")}
            items(patientQueue, key = {it.queueId}) { entry -> PatientQueueCard(entry, vets, viewModel) }
        }
    }
}

@Composable
fun SectionTitle(title: String) = Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
@Composable
fun EmptyState(message: String) = Text(message, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)


@Composable
fun VetCard(vet: VetProfile, onBookClick: (VetProfile) -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("${vet.name} - ${vet.specialty}", style = MaterialTheme.typography.titleMedium)
            Text("Fee: $${"%.2f".format(vet.consultationFee)}")
            // Display first few available slots as example
            vet.availability.entries.firstOrNull()?.let { (date, slots) ->
                if (slots.isNotEmpty()) Text("Next Available: $date at ${slots.first()}", style = MaterialTheme.typography.bodySmall)
                else Text("No immediate slots shown", style = MaterialTheme.typography.bodySmall)
            } ?: Text("Availability not shown", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { onBookClick(vet) }, modifier = Modifier.align(Alignment.End).padding(top=4.dp)) { Text("Book Appointment") }
        }
    }
}

@Composable
fun AppointmentCard(appt: Appointment) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Patient: ${appt.patientName} with ${appt.vetName}", style = MaterialTheme.typography.titleMedium)
            Text("On: ${appt.getFormattedDateTime()} for ${appt.reason}")
            Text("Status: ${appt.status}", color = if (appt.status == "booked") Color.Blue else Color.DarkGray)
        }
    }
}

@Composable
fun PatientQueueCard(entry: PatientQueueEntry, vets: List<VetProfile>, viewModel: VetConsultationViewModel) {
    var showAssignVetDialog by remember { mutableStateOf(false) }

    if (showAssignVetDialog) {
        AssignVetDialog(entry, vets, viewModel, onDismiss = { showAssignVetDialog = false })
    }

    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Patient: ${entry.patientName} (User: ${entry.userId})", style = MaterialTheme.typography.titleMedium)
            Text("Arrived: ${entry.getFormattedArrivalTime()}, Reason: ${entry.reason}")
            Text("Status: ${entry.status}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            entry.assignedVetName?.let { Text("Assigned to: $it") }
            if (entry.assignedVetId == null) {
                Button(onClick = { showAssignVetDialog = true }, modifier = Modifier.align(Alignment.End).padding(top=4.dp)) {
                    Text("Assign Vet")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(vet: VetProfile, viewModel: VetConsultationViewModel, onDismiss: () -> Unit) {
    var selectedDate by remember { mutableStateOf(vet.availability.keys.firstOrNull() ?: "") }
    var selectedTime by remember { mutableStateOf(vet.availability[selectedDate]?.firstOrNull() ?: "") }
    var patientName by remember { mutableStateOf(viewModel.mockPatientNames.first()) }
    var reason by remember { mutableStateOf(viewModel.mockReasons.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book with ${vet.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuForOptions("Date", vet.availability.keys.toList(), selectedDate) { date ->
                    selectedDate = date
                    selectedTime = vet.availability[date]?.firstOrNull() ?: ""
                }
                if (selectedDate.isNotEmpty()) {
                    ExposedDropdownMenuForOptions("Time", vet.availability[selectedDate] ?: emptyList(), selectedTime) { selectedTime = it }
                }
                ExposedDropdownMenuForOptions("Patient", viewModel.mockPatientNames, patientName) { patientName = it }
                ExposedDropdownMenuForOptions("Reason", viewModel.mockReasons, reason) { reason = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                    viewModel.bookAppointment(vet.vetId, "user_currentUser", patientName, selectedDate, selectedTime, reason)
                }
                onDismiss()
            }, enabled = selectedDate.isNotBlank() && selectedTime.isNotBlank()) { Text("Book") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToQueueDialog(viewModel: VetConsultationViewModel, onDismiss: () -> Unit) {
    var patientName by remember { mutableStateOf(viewModel.mockPatientNames.first()) }
    var reason by remember { mutableStateOf(viewModel.mockReasons.first()) }
    var userId by remember { mutableStateOf("farmer_walkin_user") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Patient to Queue")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                 ExposedDropdownMenuForOptions("Patient", viewModel.mockPatientNames, patientName) { patientName = it }
                 ExposedDropdownMenuForOptions("Reason", viewModel.mockReasons, reason) { reason = it }
                 OutlinedTextField(value = userId, onValueChange = {userId = it}, label = {Text("User ID (Optional)")})
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.addToQueue(userId.ifBlank { "anonymous_user" }, patientName, reason)
            onDismiss()
        }) {Text("Add to Queue")}},
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")}}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignVetDialog(entry: PatientQueueEntry, vets: List<VetProfile>, viewModel: VetConsultationViewModel, onDismiss: () -> Unit){
    var selectedVetId by remember { mutableStateOf(vets.firstOrNull()?.vetId ?: "")}
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {Text("Assign Vet for ${entry.patientName}")},
        text = {
            ExposedDropdownMenuForOptions("Select Vet", vets.map { "${it.name} (${it.vetId})" }, vets.find{it.vetId == selectedVetId}?.let{"${it.name} (${it.vetId})"} ?: "Select Vet") {
                selectedVetId = it.substringAfterLast("(").substringBeforeLast(")") // Extract ID
            }
        },
        confirmButton = { Button(onClick = {
            if(selectedVetId.isNotBlank()) viewModel.assignQueuedPatient(entry.queueId, selectedVetId)
            onDismiss()
        }, enabled = selectedVetId.isNotBlank()) {Text("Assign")}},
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")}}
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

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun PreviewVetConsultationScreen() {
    MaterialTheme {
        VetConsultationScreen(viewModel = VetConsultationViewModel())
    }
}
