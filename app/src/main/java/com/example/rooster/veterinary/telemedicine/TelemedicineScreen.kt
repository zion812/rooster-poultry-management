package com.example.rooster.veterinary.telemedicine

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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class ChatMessage(
    val senderId: String,
    val message: String,
    val timestamp: Date
) {
    fun getFormattedTimestamp(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(timestamp)
}

data class SharedFile(
    val filename: String,
    val uploaderId: String,
    val timestamp: Date,
    val fileUrl: String
) {
     fun getFormattedTimestamp(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(timestamp)
}

data class TelemedicineSession(
    val sessionId: String,
    val vetId: String,
    val userId: String,
    val patientId: String,
    val patientName: String,
    val appointmentId: String? = null,
    val startTime: Date,
    var endTime: Date? = null,
    var status: String, // initiating, connecting, active, ended, disconnected
    var callQuality: String? = null,
    val chatLog: MutableList<ChatMessage> = mutableListOf(),
    val sharedFiles: MutableList<SharedFile> = mutableListOf(),
    var sessionNotesVet: String = "",
    val connectionUrl: String,
    var endedBy: String? = null,
    var durationMinutes: Double? = null
) {
    fun getFormattedTimestamp(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) } ?: "N/A"
}

// --- ViewModel ---
class TelemedicineViewModel : ViewModel() {
    private val _activeSession = MutableStateFlow<TelemedicineSession?>(null)
    val activeSession: StateFlow<TelemedicineSession?> = _activeSession

    private val _sessionHistory = MutableStateFlow<List<TelemedicineSession>>(emptyList())
    val sessionHistory: StateFlow<List<TelemedicineSession>> = _sessionHistory

    private var callSimulationJob: Job? = null

    // Mock data
    val knownVetIds = (1..2).map { "vet${it.toString().padStart(3,'0')}" }
    val knownUserIds = (1..3).map { "user${it.toString().padStart(3,'0')}" }
    val patientNames = listOf("Bessie", "Charlie", "Max")
    private val currentVetId = knownVetIds.first() // Assume this vet is using the app
    private val currentUserId = knownUserIds.first() // For simulating user actions

    fun initiateCall(vetId: String, userId: String, patientId: String, patientName: String) {
        if (_activeSession.value != null && _activeSession.value?.status != "ended") {
            // Prevent new call if one is active/pending
            return
        }
        val sessionId = "telemed_${Date().time}_${Random.nextInt(1000)}"
        val newSession = TelemedicineSession(
            sessionId = sessionId, vetId = vetId, userId = userId, patientId = patientId, patientName = patientName,
            startTime = Date(), status = "initiating", connectionUrl = "https://telemed.example.com/join/$sessionId"
        )
        _activeSession.value = newSession
        simulateCallProgress(newSession)
    }

    private fun simulateCallProgress(session: TelemedicineSession) {
        callSimulationJob?.cancel()
        callSimulationJob = viewModelScope.launch {
            delay(1000) // Initiating
            _activeSession.update { it?.copy(status = "connecting") }
            delay(1500) // Connecting
            _activeSession.update { it?.copy(status = "active", callQuality = listOf("good", "fair").random()) }
        }
    }

    fun sendChatMessage(senderId: String, message: String) {
        _activeSession.update { session ->
            session?.takeIf { it.status == "active" }?.apply {
                chatLog.add(ChatMessage(senderId, message, Date()))
            }?.let { return@update it.copy(chatLog = it.chatLog.toMutableList()) } // Force recomposition by creating new list
            session // return original if no update
        }
    }

    fun shareFile(uploaderId: String, filename: String) {
         _activeSession.update { session ->
            session?.takeIf { it.status == "active" }?.apply {
                sharedFiles.add(SharedFile(filename, uploaderId, Date(), "https://files.example.com/$sessionId/$filename"))
            }?.let { return@update it.copy(sharedFiles = it.sharedFiles.toMutableList()) }
            session
        }
    }
     fun updateVetNotes(notes: String) {
        _activeSession.update { session ->
            session?.takeIf { it.status == "active" && it.vetId == currentVetId }?.copy(sessionNotesVet = notes) ?: session
        }
    }


    fun endCall(endedById: String) {
        callSimulationJob?.cancel()
        _activeSession.value?.let { session ->
            val endedSession = session.copy(
                status = "ended",
                endTime = Date(),
                endedBy = endedById,
                durationMinutes = ((Date().time - session.startTime.time) / 60000.0)
            )
            _sessionHistory.update { listOf(endedSession) + it }
            _activeSession.value = null // Clear active session
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemedicineScreen(viewModel: TelemedicineViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val activeSession by viewModel.activeSession.collectAsState()
    val history by viewModel.sessionHistory.collectAsState()

    // For new call dialog
    var showInitiateCallDialog by remember { mutableStateOf(false) }

    if (showInitiateCallDialog) {
        InitiateCallDialog(viewModel = viewModel, onDismiss = { showInitiateCallDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telemedicine") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4)) // Light Blue
            )
        },
        floatingActionButton = {
            if (activeSession == null) { // Show FAB only if no active call
                FloatingActionButton(onClick = { showInitiateCallDialog = true }) {
                    Icon(Icons.Filled.VideoCall, "Initiate New Call")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            activeSession?.let { session ->
                ActiveCallScreen(session = session, viewModel = viewModel)
            } ?: SessionHistoryScreen(history = history)
        }
    }
}

@Composable
fun ActiveCallScreen(session: TelemedicineSession, viewModel: TelemedicineViewModel) {
    var chatInput by remember { mutableStateOf("") }
    var vetNotesInput by remember { mutableStateOf(session.sessionNotesVet) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Active Call with ${session.patientName} (User: ${session.userId}, Vet: ${session.vetId})", style = MaterialTheme.typography.headlineSmall)
        Text("Status: ${session.status}, Quality: ${session.callQuality ?: "N/A"}")
        Text("URL: ${session.connectionUrl}")
        Spacer(Modifier.height(8.dp))

        // Chat Area (Simplified)
        Text("Chat Log:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 4.dp).heightIn(max = 200.dp), reverseLayout = true) {
            items(session.chatLog.reversed()) { msg -> Text("${msg.senderId} (${msg.getFormattedTimestamp()}): ${msg.message}") }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = chatInput, onValueChange = { chatInput = it }, label = { Text("Send Message") }, modifier = Modifier.weight(1f))
            IconButton(onClick = { if(chatInput.isNotBlank()) viewModel.sendChatMessage(viewModel.currentVetId, chatInput); chatInput = "" }) {
                Icon(Icons.Filled.Send, "Send")
            }
        }
        Spacer(Modifier.height(8.dp))
        // Vet Notes
        OutlinedTextField(
            value = vetNotesInput,
            onValueChange = { vetNotesInput = it; viewModel.updateVetNotes(it) }, // Update ViewModel on change
            label = { Text("Vet Session Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
         Spacer(Modifier.height(8.dp))
        // Actions
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.shareFile(viewModel.currentVetId, "patient_image_${Random.nextInt(100)}.jpg") }) { Text("Share File") }
            Button(onClick = { viewModel.endCall(viewModel.currentVetId) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("End Call") }
        }
        if(session.sharedFiles.isNotEmpty()){
            Text("Shared Files:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=8.dp))
            session.sharedFiles.forEach{ file -> Text("- ${file.filename} by ${file.uploaderId} at ${file.getFormattedTimestamp()}")}
        }
    }
}

@Composable
fun SessionHistoryScreen(history: List<TelemedicineSession>) {
    Column {
        Text("Session History", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
        if (history.isEmpty()) {
            Text("No past sessions.")
        } else {
            LazyColumn {
                items(history, key = {it.sessionId}) { session ->
                    SessionHistoryCard(session)
                }
            }
        }
    }
}

@Composable
fun SessionHistoryCard(session: TelemedicineSession) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Session: ${session.sessionId.takeLast(6)} with ${session.patientName}", style = MaterialTheme.typography.titleMedium)
            Text("Vet: ${session.vetId}, User: ${session.userId}")
            Text("Started: ${session.getFormattedTimestamp(session.startTime)}, Ended: ${session.getFormattedTimestamp(session.endTime)}")
            Text("Duration: ${session.durationMinutes?.let{"%.2f".format(it)} ?: "N/A"} mins, Ended By: ${session.endedBy ?: "N/A"}")
            if(session.chatLog.isNotEmpty()) Text("Chat Messages: ${session.chatLog.size}")
            if(session.sharedFiles.isNotEmpty()) Text("Files Shared: ${session.sharedFiles.size}")
            if(session.sessionNotesVet.isNotBlank()) Text("Vet Notes: ${session.sessionNotesVet.take(50)}...")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitiateCallDialog(viewModel: TelemedicineViewModel, onDismiss: () -> Unit) {
    var selectedVet by remember { mutableStateOf(viewModel.knownVetIds.first()) }
    var selectedUser by remember { mutableStateOf(viewModel.knownUserIds.first()) }
    var patientName by remember { mutableStateOf(viewModel.patientNames.first()) }
    var patientId by remember { mutableStateOf("pet_${Random.nextInt(100, 199)}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Initiate New Telemedicine Call") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuForOptions("Select Vet", viewModel.knownVetIds, selectedVet) { selectedVet = it }
                ExposedDropdownMenuForOptions("Select User", viewModel.knownUserIds, selectedUser) { selectedUser = it }
                ExposedDropdownMenuForOptions("Select Patient Name", viewModel.patientNames, patientName) { patientName = it }
                OutlinedTextField(value = patientId, onValueChange = {patientId = it}, label = {Text("Patient ID")})
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.initiateCall(selectedVet, selectedUser, patientId, patientName)
                onDismiss()
            }) { Text("Start Call") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

// Re-using ExposedDropdownMenuForOptions, assuming it's accessible from other files in same package or shared UI
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


@Preview(showBackground = true, heightDp = 700)
@Composable
fun PreviewTelemedicineScreen_NoActiveCall() {
    MaterialTheme {
        TelemedicineScreen(viewModel = TelemedicineViewModel())
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun PreviewTelemedicineScreen_ActiveCall() {
    val viewModel = TelemedicineViewModel()
    // Simulate an active call for preview
    viewModel.initiateCall(viewModel.knownVetIds.first(), viewModel.knownUserIds.first(), "pet_preview", "PreviewPet")
    // Need a slight delay or mechanism for the simulation to reach 'active' state for preview if it's async.
    // For immediate preview, can manually set a mock active session:
    // (viewModel as any)._activeSession.value = TelemedicineSession(...)
    MaterialTheme {
        TelemedicineScreen(viewModel = viewModel)
    }
}
