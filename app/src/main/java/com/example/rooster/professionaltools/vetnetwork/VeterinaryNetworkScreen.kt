package com.example.rooster.professionaltools.vetnetwork

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class VetProfile(
    val vetId: String,
    val fullName: String,
    val licensedState: String,
    val licenseNumber: String,
    val primarySpecialty: String,
    val secondarySpecialties: List<String> = emptyList(),
    val clinicName: String,
    val clinicAddress: String,
    val contactEmail: String,
    val contactPhone: String,
    val yearsOfExperience: Int,
    val profileBio: String,
    var acceptingReferrals: Boolean,
    val areasOfInterest: List<String> = emptyList(),
    val publicationsLinks: List<String> = emptyList(),
    val profileVisibility: String = "all_vets", // all_vets, connections_only
    val lastActiveDate: Date
) {
    fun getFormattedLastActive(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastActiveDate)
}

data class ReferralRequest(
    val requestId: String,
    val sendingVetId: String,
    val sendingVetName: String,
    val receivingVetId: String,
    val receivingVetName: String,
    val patientSummary: String,
    val reasonForReferral: String,
    val dateSent: Date,
    var status: String, // pending_review, accepted, declined, information_requested
    var urgency: String = "Routine", // Routine, Urgent
    var notes: String? = null, // Notes from receiver
    var lastUpdatedBy: String? = null,
    var lastUpdatedAt: Date? = null
) {
    fun getFormattedDate(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) } ?: "N/A"
}

data class VetSearchFilters(
    val specialty: String? = null,
    val location: String? = null,
    val name: String? = null,
    val acceptingReferrals: Boolean? = null
)

// --- ViewModel ---
class VeterinaryNetworkViewModel(val currentVetId: String = "vet_net_0001") : ViewModel() { // Assume a current vet user
    private val _allVetProfiles = MutableStateFlow<List<VetProfile>>(emptyList())
    private val _allReferrals = MutableStateFlow<List<ReferralRequest>>(emptyList())

    private val _filters = MutableStateFlow(VetSearchFilters())
    val filters: StateFlow<VetSearchFilters> = _filters

    val filteredVetProfiles: StateFlow<List<VetProfile>> = combine(_allVetProfiles, _filters) { profiles, filters ->
        profiles.filter { vet ->
            (filters.specialty.isNullOrBlank() || vet.primarySpecialty.contains(filters.specialty, ignoreCase = true) || vet.secondarySpecialties.any { it.contains(filters.specialty,ignoreCase = true) }) &&
            (filters.location.isNullOrBlank() || vet.clinicAddress.contains(filters.location, ignoreCase = true)) &&
            (filters.name.isNullOrBlank() || vet.fullName.contains(filters.name, ignoreCase = true)) &&
            (filters.acceptingReferrals == null || vet.acceptingReferrals == filters.acceptingReferrals)
        }.sortedBy { it.fullName }
    }.stateIn(kotlinx.coroutines.MainScope(), kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val incomingReferrals: StateFlow<List<ReferralRequest>> = combine(_allReferrals, MutableStateFlow(currentVetId)) { referrals, currentId ->
        referrals.filter { it.receivingVetId == currentId && it.status == "pending_review" }.sortedByDescending { it.dateSent }
    }.stateIn(kotlinx.coroutines.MainScope(), kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val outgoingReferrals: StateFlow<List<ReferralRequest>> = combine(_allReferrals, MutableStateFlow(currentVetId)) { referrals, currentId ->
        referrals.filter { it.sendingVetId == currentId && it.status !in listOf("accepted", "declined") }.sortedByDescending { it.dateSent }
    }.stateIn(kotlinx.coroutines.MainScope(), kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())


    private val _selectedVetProfile = MutableStateFlow<VetProfile?>(null)
    val selectedVetProfile: StateFlow<VetProfile?> = _selectedVetProfile

    val specialtiesList = listOf("Any", "General Practice", "Cardiology", "Surgery", "Dermatology", "Oncology", "Neurology", "Livestock Health")


    init {
        loadMockVetProfiles()
        // Add a mock referral for the currentVetId to see in incoming
        if(_allVetProfiles.value.size > 1) {
            val sender = _allVetProfiles.value.first { it.vetId != currentVetId }
            val receiver = _allVetProfiles.value.first { it.vetId == currentVetId }
             _allReferrals.update { it + ReferralRequest(
                requestId = "ref_mock_${Date().time}", sendingVetId = sender.vetId, sendingVetName = sender.fullName,
                receivingVetId = receiver.vetId, receivingVetName = receiver.fullName,
                patientSummary = "Mock patient for ${receiver.fullName}", reasonForReferral = "Needs ${receiver.primarySpecialty} consult",
                dateSent = Date(), status = "pending_review"
            )}
        }
    }

    private fun loadMockVetProfiles(count: Int = 10) {
        val tempProfiles = mutableListOf<VetProfile>()
        val baseNames = listOf("Smith", "Jones", "Williams", "Brown", "Davis")
        val firstNames = listOf("Dr. Emily", "Dr. John", "Dr. Sarah", "Dr. Michael")
        val cities = listOf("New York, NY", "Ruralville, IA", "Metropolis, IL")

        // Ensure the currentVetId profile exists
        tempProfiles.add(VetProfile(
            vetId = currentVetId, fullName = "Dr. Current User (You)", licensedState = "CA", licenseNumber = "VET12345",
            primarySpecialty = specialtiesList.filterNot{it=="Any"}.random(), clinicName = "My Clinic", clinicAddress = "123 My Street, MyCity, CA",
            contactEmail = "me@example.com", contactPhone = "555-0000", yearsOfExperience = 10,
            profileBio = "This is the profile for the current logged-in user.", acceptingReferrals = true,
            lastActiveDate = Date()
        ))

        for (i in 0 until count) {
            val vetId = "vet_net_${(i + 2).toString().padStart(4, '0')}" // Start from 2 as 1 is current user
            if (vetId == currentVetId) continue // Skip if it's current user's mock ID

            tempProfiles.add(VetProfile(
                vetId = vetId,
                fullName = "${firstNames.random()} ${baseNames.random()}",
                licensedState = listOf("NY", "CA", "TX", "FL").random(),
                licenseNumber = "VET${Random.nextInt(10000, 99999)}",
                primarySpecialty = specialtiesList.filterNot{it=="Any"}.random(),
                secondarySpecialties = specialtiesList.filterNot{it=="Any"}.shuffled().take(Random.nextInt(0,2)),
                clinicName = "${listOf("Advanced", "Rural", "City", "University").random()} Vet Care",
                clinicAddress = "${Random.nextInt(100,999)} Main St, ${cities.random()}",
                contactEmail = "vet$i@example.com", contactPhone = "555-${Random.nextInt(100,999)}-${Random.nextInt(1000,9999)}",
                yearsOfExperience = Random.nextInt(1,30),
                profileBio = "Experienced vet in ${specialtiesList.filterNot{it=="Any"}.random()}.",
                acceptingReferrals = Random.nextBoolean(),
                lastActiveDate = Date(System.currentTimeMillis() - Random.nextLong(0, 60 * 86400000L))
            ))
        }
        _allVetProfiles.value = tempProfiles
    }

    fun updateFilters(newFilters: VetSearchFilters) { _filters.value = newFilters }
    fun selectVetProfile(vetId: String) { _selectedVetProfile.value = _allVetProfiles.value.find { it.vetId == vetId } }
    fun clearSelectedVetProfile() { _selectedVetProfile.value = null }

    fun sendReferral(receivingVetId: String, patientSummary: String, reason: String): Boolean {
        val sender = _allVetProfiles.value.find { it.vetId == currentVetId } ?: return false
        val receiver = _allVetProfiles.value.find { it.vetId == receivingVetId } ?: return false
        if (!receiver.acceptingReferrals) { println("Warning: Vet not actively accepting referrals."); /* Allow sending anyway for demo */ }

        val newReferral = ReferralRequest(
            requestId = "ref_${Date().time}", sendingVetId = sender.vetId, sendingVetName = sender.fullName,
            receivingVetId = receiver.vetId, receivingVetName = receiver.fullName,
            patientSummary = patientSummary, reasonForReferral = reason, dateSent = Date(), status = "pending_review"
        )
        _allReferrals.update { (it + newReferral).sortedByDescending { r -> r.dateSent } }
        return true
    }

    fun updateReferralStatus(requestId: String, newStatus: String, notes: String?) {
        _allReferrals.update { referrals ->
            referrals.map {
                if (it.requestId == requestId) {
                    it.copy(status = newStatus, notes = notes ?: it.notes, lastUpdatedAt = Date(), lastUpdatedBy = currentVetId)
                } else it
            }
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinaryNetworkScreen(viewModel: VeterinaryNetworkViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val filteredVets by viewModel.filteredVetProfiles.collectAsState()
    val selectedVet by viewModel.selectedVetProfile.collectAsState()
    val incomingReferrals by viewModel.incomingReferrals.collectAsState()
    val outgoingReferrals by viewModel.outgoingReferrals.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showReferralDialog by remember { mutableStateOf(false) }
    var vetToReferTo by remember { mutableStateOf<VetProfile?>(null) }
    var showUpdateReferralDialog by remember { mutableStateOf(false) }
    var referralToUpdate by remember { mutableStateOf<ReferralRequest?>(null) }


    if (showFilterDialog) {
        FilterVetsDialog(currentFilters, viewModel, onDismiss = { showFilterDialog = false })
    }
    if (showReferralDialog && vetToReferTo != null) {
        SendReferralDialog(vetToReferTo!!, viewModel, onDismiss = { showReferralDialog = false; vetToReferTo = null })
    }
     if (showUpdateReferralDialog && referralToUpdate != null) {
        UpdateReferralStatusDialog(referralToUpdate!!, viewModel, onDismiss = { showUpdateReferralDialog = false; referralToUpdate = null })
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedVet == null) "Veterinary Network" else selectedVet!!.fullName) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7CB342)), // Light Green
                navigationIcon = if (selectedVet != null) {
                    { IconButton(onClick = { viewModel.clearSelectedVetProfile() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
                } else null,
                actions = {
                    if (selectedVet == null) {
                        IconButton(onClick = { showFilterDialog = true }) { Icon(Icons.Filled.FilterList, "Filter Vets") }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (selectedVet == null) {
            NetworkDashboard(paddingValues, viewModel, filteredVets, incomingReferrals, outgoingReferrals,
                onVetClick = { viewModel.selectVetProfile(it.vetId) },
                onUpdateReferralClick = { referralToUpdate = it; showUpdateReferralDialog = true}
            )
        } else {
            VetProfileDetailScreen(paddingValues, selectedVet!!,
                onReferClick = { vetToReferTo = it; showReferralDialog = true }
            )
        }
    }
}

@Composable
fun NetworkDashboard(
    paddingValues: PaddingValues, viewModel: VeterinaryNetworkViewModel,
    filteredVets: List<VetProfile>, incomingReferrals: List<ReferralRequest>, outgoingReferrals: List<ReferralRequest>,
    onVetClick: (VetProfile) -> Unit, onUpdateReferralClick: (ReferralRequest) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Find Veterinarians (${filteredVets.size})") }
        if(filteredVets.isEmpty()) item { EmptyState("No vets match criteria.")}
        items(filteredVets.take(5), key = {it.vetId}) { vet -> VetListItem(vet, onClick = { onVetClick(vet) }) }
        if(filteredVets.size > 5) item { TextButton(onClick = { /* TODO: Navigate to full list */ }) { Text("View All ${filteredVets.size} Results...")}}


        item { SectionTitle("Incoming Referrals (${incomingReferrals.size})") }
        if(incomingReferrals.isEmpty()) item { EmptyState("No incoming referrals.")}
        items(incomingReferrals, key = {it.requestId}) { ref -> ReferralRequestItem(ref, true) { onUpdateReferralClick(ref) } }

        item { SectionTitle("Outgoing Referrals (${outgoingReferrals.size})") }
        if(outgoingReferrals.isEmpty()) item { EmptyState("No pending outgoing referrals.")}
        items(outgoingReferrals, key = {it.requestId}) { ref -> ReferralRequestItem(ref, false) { /* Maybe allow cancelling */ } }
    }
}

@Composable
fun VetListItem(vet: VetProfile, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(vet.fullName, style = MaterialTheme.typography.titleMedium)
            Text("${vet.primarySpecialty} at ${vet.clinicName}", style = MaterialTheme.typography.bodyMedium)
            Text(vet.clinicAddress, style = MaterialTheme.typography.bodySmall)
            Text("Accepting Referrals: ${if(vet.acceptingReferrals) "Yes" else "No"}", color = if(vet.acceptingReferrals) Color.Green else Color.Red)
        }
    }
}

@Composable
fun VetProfileDetailScreen(paddingValues: PaddingValues, vet: VetProfile, onReferClick: (VetProfile) -> Unit) {
    Column(Modifier.padding(paddingValues).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(vet.fullName, style = MaterialTheme.typography.headlineMedium)
        Text("Primary Specialty: ${vet.primarySpecialty}", style = MaterialTheme.typography.titleMedium)
        if (vet.secondarySpecialties.isNotEmpty()) {
            Text("Other Specialties: ${vet.secondarySpecialties.joinToString()}", style = MaterialTheme.typography.bodyMedium)
        }
        Text("Clinic: ${vet.clinicName}, ${vet.clinicAddress}")
        Text("Experience: ${vet.yearsOfExperience} years")
        Text("Contact: ${vet.contactEmail} / ${vet.contactPhone}")
        Text("License: ${vet.licensedState} - ${vet.licenseNumber}")
        Text("Bio: ${vet.profileBio}")
        if (vet.areasOfInterest.isNotEmpty()) Text("Areas of Interest: ${vet.areasOfInterest.joinToString()}")
        Text("Accepting Referrals: ${if(vet.acceptingReferrals) "Yes" else "No"}", fontWeight = FontWeight.Bold)
        Text("Last Active: ${vet.getFormattedLastActive()}")

        if(vet.acceptingReferrals) {
            Button(onClick = { onReferClick(vet) }, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)) {
                Icon(Icons.Filled.Send, "Refer Case"); Spacer(Modifier.width(4.dp)); Text("Refer a Case")
            }
        }
    }
}

@Composable
fun ReferralRequestItem(ref: ReferralRequest, isIncoming: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(if(isIncoming) "From: ${ref.sendingVetName}" else "To: ${ref.receivingVetName}", style = MaterialTheme.typography.titleMedium)
            Text("Patient: ${ref.patientSummary.take(50)}...", style = MaterialTheme.typography.bodyMedium)
            Text("Reason: ${ref.reasonForReferral.take(50)}...", style = MaterialTheme.typography.bodySmall)
            Text("Sent: ${ref.getFormattedDate(ref.dateSent)}, Status: ${ref.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun FilterVetsDialog(filters: VetSearchFilters, viewModel: VeterinaryNetworkViewModel, onDismiss: () -> Unit) {
    var specialty by remember { mutableStateOf(filters.specialty ?: viewModel.specialtiesList.first()) }
    var location by remember { mutableStateOf(filters.location ?: "") }
    var name by remember { mutableStateOf(filters.name ?: "") }
    var accepting by remember { mutableStateOf(filters.acceptingReferrals) } // null, true, false

    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Filter Veterinarians") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuForOptions("Specialty", viewModel.specialtiesList, specialty) { specialty = it }
                OutlinedTextField(value = location, onValueChange = {location=it}, label = {Text("Location Keyword")})
                OutlinedTextField(value = name, onValueChange = {name=it}, label = {Text("Name Keyword")})
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Accepting Referrals: ")
                    RadioButton(selected = accepting == true, onClick = { accepting = true }); Text("Yes")
                    RadioButton(selected = accepting == false, onClick = { accepting = false }); Text("No")
                    RadioButton(selected = accepting == null, onClick = { accepting = null }); Text("Any")
                }
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.updateFilters(VetSearchFilters(
                specialty = if(specialty == "Any") null else specialty,
                location = location.ifBlank { null }, name = name.ifBlank { null }, acceptingReferrals = accepting
            ))
            onDismiss()
        }) {Text("Apply Filters")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}

@Composable
fun SendReferralDialog(vetToReferTo: VetProfile, viewModel: VeterinaryNetworkViewModel, onDismiss: () -> Unit) {
    var patientSummary by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Refer Case to ${vetToReferTo.fullName}")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = patientSummary, onValueChange = {patientSummary=it}, label={Text("Patient Summary (e.g. Species, Age, Condition)")}, minLines = 3)
                OutlinedTextField(value = reason, onValueChange = {reason=it}, label={Text("Reason for Referral")}, minLines = 2)
            }
        },
        confirmButton = { Button(onClick = {
            if(patientSummary.isNotBlank() && reason.isNotBlank()) {
                viewModel.sendReferral(vetToReferTo.vetId, patientSummary, reason)
                onDismiss()
            }
        }, enabled = patientSummary.isNotBlank() && reason.isNotBlank()) {Text("Send Referral")} },
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")} }
    )
}

@Composable
fun UpdateReferralStatusDialog(referral: ReferralRequest, viewModel: VeterinaryNetworkViewModel, onDismiss: () -> Unit) {
    val statuses = listOf("accepted", "declined", "information_requested") // Possible actions by receiver
    var newStatus by remember { mutableStateOf(statuses.first())}
    var notes by remember { mutableStateOf(referral.notes ?: "")}
    AlertDialog(
        onDismissRequest = onDismiss, title = {Text("Update Referral from ${referral.sendingVetName}")},
        text = {
             Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Patient: ${referral.patientSummary.take(100)}...")
                ExposedDropdownMenuForOptions("New Status", statuses, newStatus) {newStatus = it}
                OutlinedTextField(value = notes, onValueChange = {notes=it}, label={Text("Notes (Optional)")})
            }
        },
        confirmButton = { Button(onClick = {
            viewModel.updateReferralStatus(referral.requestId, newStatus, notes)
            onDismiss()
        }) {Text("Update Status")}},
        dismissButton = { Button(onClick = onDismiss) {Text("Cancel")}}
    )
}


// Re-using ExposedDropdownMenuForOptions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selectedOption, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onOptionSelected(option); expanded = false }) }
        }
    }
}
@Composable
fun SectionTitle(title: String) = Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
@Composable
fun EmptyState(message: String) = Text(message, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)


@Preview(showBackground = true, heightDp = 1000)
@Composable
fun PreviewVeterinaryNetworkScreen_Dashboard() {
    MaterialTheme { VeterinaryNetworkScreen(viewModel = VeterinaryNetworkViewModel("vet_net_0001")) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewVeterinaryNetworkScreen_ProfileDetail() {
    val viewModel = VeterinaryNetworkViewModel("vet_net_0001")
    LaunchedEffect(Unit){
        if(viewModel.filteredVetProfiles.value.isNotEmpty()){
            viewModel.selectVetProfile(viewModel.filteredVetProfiles.value.first { it.vetId != viewModel.currentVetId }.vetId)
        }
    }
    MaterialTheme { VeterinaryNetworkScreen(viewModel = viewModel) }
}
