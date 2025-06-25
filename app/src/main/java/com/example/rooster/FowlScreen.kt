package com.example.rooster

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.data.TransferRepository
import com.parse.ParseACL
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class FowlData(
    val objectId: String,
    val name: String,
    val type: String,
    val birthDate: String,
    val children: List<FowlData>? = null,
)

// Enum for FowlDetailTabs
enum class FowlDetailTab(val title: String, val icon: ImageVector) {
    MILESTONES("Milestones", Icons.Default.Timeline),
    VACCINATIONS("Vaccinations", Icons.Default.Biotech),
    MEDICATIONS("Medications", Icons.Default.Healing),
    HATCHING("Hatching", Icons.Default.Egg),
    BRUDING("Bruding", Icons.Default.Home), // Changed to Home as placeholder
}

// Enhanced FowlScreen with milestone tracking
@Composable
fun FowlScreen() {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Rooster") }
    var birthDate by remember { mutableStateOf("") }
    var fowls by remember { mutableStateOf(listOf<ParseObject>()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var showAddFowlDialog by remember { mutableStateOf(false) }
    var selectedMilestone by remember { mutableStateOf<Pair<MilestoneType, FowlData>?>(null) }
    var parentId by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showAddHatchingDialog by remember { mutableStateOf<FowlData?>(null) }
    var showAddBrudingDialog by remember { mutableStateOf<FowlData?>(null) }
    var showAddTransferDialog by remember { mutableStateOf<FowlData?>(null) }
    var showAddVaccinationDialog by remember { mutableStateOf<FowlData?>(null) }
    var showAddMedicationDialog by remember { mutableStateOf<FowlData?>(null) }
    var medicationPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var hatchingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Services
    val context = LocalContext.current.applicationContext as Application
    val milestoneService = remember { MilestoneTrackingService() }
    val healthManagementService = remember { HealthManagementService(context = context) }
    val hatchingAndBrudingService = remember { HatchingAndBrudingService(context = context) }
    val transferRepository = remember { TransferRepository() }
    val coroutineScope = rememberCoroutineScope()

    fun fetchFowls() {
        loading = true
        val query = ParseQuery.getQuery<ParseObject>("Fowl")
        query.whereEqualTo("owner", ParseUser.getCurrentUser())
        query.orderByDescending("createdAt")
        query.findInBackground { result, e ->
            loading = false
            if (e == null && result != null) {
                fowls = result
                ParseObject.pinAllInBackground("Fowl", result)
            } else {
                val localQuery = ParseQuery.getQuery<ParseObject>("Fowl")
                localQuery.fromLocalDatastore()
                localQuery.whereEqualTo("owner", ParseUser.getCurrentUser())
                localQuery.orderByDescending("createdAt")
                localQuery.findInBackground { localResult, _ ->
                    fowls = localResult ?: emptyList()
                    error = e?.localizedMessage ?: "Failed to load fowls. Showing offline data."
                }
            }
        }
    }

    fun addFowl() {
        coroutineScope.launch {
            val fowl = ParseObject("Fowl")
            fowl.put("name", name)
            fowl.put("type", type)
            fowl.put("birthDate", birthDate)
            fowl.put("owner", ParseUser.getCurrentUser())
            if (parentId != null) {
                val parentPointer = ParseObject.createWithoutData("Fowl", parentId)
                fowl.put("parentId", parentPointer)
            }
            if (photoUri != null) {
                val imageBytes =
                    try {
                        context.contentResolver.openInputStream(photoUri!!)?.use { it.readBytes() }
                    } catch (e: Exception) {
                        error = "Failed to read photo: ${e.localizedMessage}"
                        return@launch
                    }

                if (imageBytes != null) {
                    val parseFile = ParseFile("fowl_photo.jpg", imageBytes)
                    try {
                        withContext(Dispatchers.IO) {
                            parseFile.save()
                        }
                        fowl.put("photo", parseFile)
                    } catch (e: ParseException) {
                        error = e.localizedMessage ?: "Failed to upload photo."
                        return@launch
                    }
                }
            }
            val acl = ParseACL(ParseUser.getCurrentUser())
            acl.setPublicReadAccess(true)
            acl.setWriteAccess(ParseUser.getCurrentUser(), true)
            fowl.acl = acl
            try {
                withContext(Dispatchers.IO) {
                    fowl.save()
                    fowl.pin()
                }
                // Reset state on main thread
                name = ""
                birthDate = ""
                parentId = null
                photoUri = null
                showAddFowlDialog = false
                fetchFowls()
            } catch (e: ParseException) {
                error = e.localizedMessage ?: "Failed to add fowl."
            }
        }
    }

    LaunchedEffect(Unit) { fetchFowls() }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Fowl Management",
                style = MaterialTheme.typography.headlineSmall,
            )
            FloatingActionButton(
                onClick = { showAddFowlDialog = true },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Fowl")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (error.isNotEmpty()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(fowls) { fowl ->
                    val fowlData =
                        FowlData(
                            objectId = fowl.objectId,
                            name = fowl.getString("name") ?: "Unknown",
                            type = fowl.getString("type") ?: "Unknown",
                            birthDate = fowl.getString("birthDate") ?: "Unknown",
                        )

                    EnhancedFowlCard(
                        fowlData = fowlData,
                        milestoneService = milestoneService,
                        healthManagementService = healthManagementService,
                        hatchingAndBrudingService = hatchingAndBrudingService,
                        transferRepository = transferRepository,
                        onAddMilestone = { milestoneType ->
                            selectedMilestone = milestoneType to fowlData
                        },
                        onAddVaccination = { showAddVaccinationDialog = fowlData },
                        onAddMedication = { showAddMedicationDialog = fowlData },
                        onAddHatching = { showAddHatchingDialog = fowlData },
                        onAddBruding = { showAddBrudingDialog = fowlData },
                        onAddTransfer = { showAddTransferDialog = fowlData },
                    )
                }
            }
        }
    }

    if (showAddFowlDialog) {
        AddFowlDialog(
            name = name,
            onNameChange = { name = it },
            type = type,
            onTypeChange = { type = it },
            birthDate = birthDate,
            onBirthDateChange = { birthDate = it },
            parentId = parentId,
            onParentIdChange = { parentId = it },
            fowlOptions =
                fowls.map {
                    FowlData(
                        objectId = it.objectId,
                        name = it.getString("name") ?: "Unknown",
                        type = it.getString("type") ?: "Unknown",
                        birthDate = it.getString("birthDate") ?: "Unknown",
                    )
                },
            photoUri = photoUri,
            onPhotoUriChange = { photoUri = it },
            onDismiss = {
                showAddFowlDialog = false
                name = ""
                birthDate = ""
                parentId = null
                photoUri = null
            },
            onConfirm = { addFowl() },
        )
    }

    selectedMilestone?.let { (milestoneType, fowlData) ->
        val currentAge = milestoneService.calculateAgeInWeeks(fowlData.birthDate)
        MilestoneRecordingDialog(
            milestone = milestoneType,
            fowlData = fowlData,
            currentAgeWeeks = currentAge,
            onDismiss = { selectedMilestone = null },
            onSave = { milestoneData ->
                coroutineScope.launch {
                    milestoneService.saveMilestone(
                        milestoneData = milestoneData,
                        onSuccess = {
                            selectedMilestone = null
                            fetchFowls()
                        },
                        onError = { errorMsg ->
                            error = errorMsg
                            selectedMilestone = null
                        },
                    )
                }
            },
        )
    }

    showAddVaccinationDialog?.let { fowlData ->
        AddVaccinationDialog(
            fowlData = fowlData,
            healthManagementService = healthManagementService,
            onDismiss = { showAddVaccinationDialog = null },
            onVaccinationAdded = {
                showAddVaccinationDialog = null
                // Optionally refresh or update UI here
            },
        )
    }

    showAddMedicationDialog?.let { fowlData ->
        AddMedicationDialog(
            fowlData = fowlData,
            healthManagementService = healthManagementService,
            photoUri = medicationPhotoUri,
            onPhotoUriChange = { medicationPhotoUri = it },
            onDismiss = {
                showAddMedicationDialog = null
                medicationPhotoUri = null
            },
            onMedicationAdded = {
                showAddMedicationDialog = null
                medicationPhotoUri = null
                // Optionally refresh or update UI here
            },
        )
    }

    showAddHatchingDialog?.let { fowlData ->
        AddHatchingDialog(
            // Pass FowlData to prefill breed if needed
            fowlData = fowlData,
            hatchingAndBrudingService = hatchingAndBrudingService,
            photoUri = hatchingPhotoUri,
            onPhotoUriChange = { hatchingPhotoUri = it },
            onDismiss = {
                showAddHatchingDialog = null
                hatchingPhotoUri = null
            },
            onHatchingRecordAdded = {
                showAddHatchingDialog = null
                hatchingPhotoUri = null
                // Optionally refresh data or show success message
            },
        )
    }

    showAddBrudingDialog?.let { fowlData ->
        AddBrudingDialog(
            // Pass FowlData to prefill breed if needed
            fowlData = fowlData,
            hatchingAndBrudingService = hatchingAndBrudingService,
            onDismiss = { showAddBrudingDialog = null },
            onBrudingRecordAdded = {
                showAddBrudingDialog = null
                // Optionally refresh data or show success message
            },
        )
    }

    showAddTransferDialog?.let { fowlData ->
        AddTransferDialog(
            fowlData = fowlData,
            transferRepository = transferRepository,
            onDismiss = { showAddTransferDialog = null },
            onTransferAdded = {
                showAddTransferDialog = null
                // Optionally refresh data or show success message
            },
        )
    }
}

@Composable
fun EnhancedFowlCard(
    fowlData: FowlData,
    milestoneService: MilestoneTrackingService,
    healthManagementService: HealthManagementService,
    hatchingAndBrudingService: HatchingAndBrudingService,
    transferRepository: TransferRepository,
    onAddMilestone: (MilestoneType) -> Unit,
    onAddVaccination: (FowlData) -> Unit,
    onAddMedication: (FowlData) -> Unit,
    onAddHatching: (FowlData) -> Unit,
    onAddBruding: (FowlData) -> Unit,
    onAddTransfer: (FowlData) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(FowlDetailTab.MILESTONES) }
    var lineage by remember { mutableStateOf<List<FowlData>>(emptyList()) }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fowlData) {
        lineage = fetchLineage(fowlData)
        // Fetch photo URL if exists
        val query = ParseQuery.getQuery<ParseObject>("Fowl")
        val obj = query.get(fowlData.objectId)
        photoUrl = obj.getParseFile("photo")?.url
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${fowlData.name}")
            Text("Type: ${fowlData.type}")
            Text("Birth Date: ${fowlData.birthDate}")
            photoUrl?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Fowl Photo",
                    modifier = Modifier.size(80.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            ScrollableTabRow(selectedTabIndex = selectedTab.ordinal, edgePadding = 0.dp) {
                FowlDetailTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                FowlDetailTab.MILESTONES -> {
                    MilestoneContent(fowlData, milestoneService, onAddMilestone)
                }

                FowlDetailTab.VACCINATIONS -> {
                    VaccinationContent(fowlData, healthManagementService, onAddVaccination)
                }

                FowlDetailTab.MEDICATIONS -> {
                    MedicationContent(fowlData, healthManagementService, onAddMedication)
                }
                FowlDetailTab.HATCHING -> {
                    HatchingContent(
                        // Used for context, e.g., prefilling breed for a new batch
                        fowlData = fowlData,
                        hatchingAndBrudingService = hatchingAndBrudingService,
                        onAddHatching = onAddHatching
                    )
                }

                FowlDetailTab.BRUDING -> {
                    BrudingContent(
                        // Used for context
                        fowlData = fowlData,
                        hatchingAndBrudingService = hatchingAndBrudingService,
                        onAddBruding = onAddBruding
                    )
                }
            }

            if (lineage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Lineage:",
                    style = MaterialTheme.typography.titleSmall,
                )
                LineageTree(lineage)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { onAddMilestone(MilestoneTypes.BIRTH) }) { Icon(Icons.Default.Add, "Milestone") }
                IconButton(onClick = { onAddTransfer(fowlData) }) { Icon(Icons.Default.Share, "Transfer") }
            }
        }
    }
}

@Composable
fun MilestoneContent(
    fowlData: FowlData,
    milestoneService: MilestoneTrackingService,
    onAddMilestone: (MilestoneType) -> Unit,
) {
    var progress by remember { mutableStateOf<FowlMilestoneProgress?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(fowlData) {
        isLoading = true
        milestoneService.getFowlMilestoneProgress(
            fowlId = fowlData.objectId,
            fowlType = fowlData.type,
            birthDate = fowlData.birthDate,
            onResult = { milestoneProgress ->
                progress = milestoneProgress
                isLoading = false
            },
            onError = { isLoading = false },
        )
    }

    if (isLoading) {
        CircularProgressIndicator() // Show loading indicator for milestones
    } else {
        progress?.let {
            MilestoneProgressCard(
                fowlData = fowlData,
                progress = it,
                onAddMilestone = onAddMilestone,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun VaccinationContent(
    fowlData: FowlData,
    healthManagementService: HealthManagementService,
    onAddVaccination: (FowlData) -> Unit,
) {
    var vaccinations by remember { mutableStateOf<List<VaccinationRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fowlData.objectId) {
        isLoading = true
        val result = healthManagementService.getVaccinationHistory(fowlData.objectId)
        if (result.isSuccess) {
            vaccinations = result.getOrNull() ?: emptyList()
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load vaccinations"
        }
        isLoading = false
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Vaccination History", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onAddVaccination(fowlData) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Vaccination")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (vaccinations.isEmpty()) {
            Text("No vaccination records found.")
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) { // Limit height
                items(vaccinations) { record ->
                    VaccinationCard(record)
                }
            }
        }
    }
}

@Composable
fun MedicationContent(
    fowlData: FowlData,
    healthManagementService: HealthManagementService,
    onAddMedication: (FowlData) -> Unit,
) {
    var medications by remember { mutableStateOf<List<MedicationRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fowlData.objectId) {
        isLoading = true
        val result = healthManagementService.getMedicationHistory(fowlData.objectId)
        if (result.isSuccess) {
            medications = result.getOrNull() ?: emptyList()
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load medications"
        }
        isLoading = false
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Medication History", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onAddMedication(fowlData) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Medication")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (medications.isEmpty()) {
            Text("No medication records found.")
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) { // Limit height
                items(medications) { record ->
                    MedicationCard(record)
                }
            }
        }
    }
}

@Composable
fun HatchingContent(
    // Used for context, e.g., prefilling breed for a new batch
    fowlData: FowlData,
    hatchingAndBrudingService: HatchingAndBrudingService,
    onAddHatching: (FowlData) -> Unit,
) {
    var hatchingRecords by remember { mutableStateOf<List<HatchingRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch hatching records associated with the current user (not specific to fowlData.objectId)
    LaunchedEffect(Unit) { // Keyed to Unit to fetch once for the user
        isLoading = true
        val result = hatchingAndBrudingService.getHatchingHistoryForUser()
        if (result.isSuccess) {
            hatchingRecords = result.getOrNull() ?: emptyList()
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load hatching records"
        }
        isLoading = false
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Hatching Records", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onAddHatching(fowlData) }) { // Pass fowlData for context
                Icon(Icons.Default.Add, contentDescription = "Add Hatching Record")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (hatchingRecords.isEmpty()) {
            Text("No hatching records found.")
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(hatchingRecords) { record ->
                    HatchingCard(record) { /* TODO: Handle update hatching record */ }
                }
            }
        }
    }
}

@Composable
fun BrudingContent(
    // Used for context
    fowlData: FowlData,
    hatchingAndBrudingService: HatchingAndBrudingService,
    onAddBruding: (FowlData) -> Unit,
) {
    var brudingRecords by remember { mutableStateOf<List<BrudingRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch bruding records associated with the current user
    LaunchedEffect(Unit) {
        isLoading = true
        val result = hatchingAndBrudingService.getBrudingHistoryForUser()
        if (result.isSuccess) {
            brudingRecords = result.getOrNull() ?: emptyList()
        } else {
            error = result.exceptionOrNull()?.message ?: "Failed to load bruding records"
        }
        isLoading = false
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Bruding Records", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onAddBruding(fowlData) }) { // Pass fowlData for context
                Icon(Icons.Default.Add, contentDescription = "Add Bruding Record")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (brudingRecords.isEmpty()) {
            Text("No bruding records found.")
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(brudingRecords) { record ->
                    BrudingCard(record) { /* TODO: Handle update bruding record */ }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransferDialog(
    fowlData: FowlData,
    transferRepository: TransferRepository,
    onDismiss: () -> Unit,
    onTransferAdded: () -> Unit,
) {
    var toOwnerId by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoUri = it }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transfer for ${'$'}{fowlData.name}") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(toOwnerId, { toOwnerId = it }, label = { Text("To Owner ID") })
                OutlinedTextField(location, { location = it }, label = { Text("Location (optional)") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") })
                PhotoCaptureSection(photoUri) { photoLauncher.launch("image/*") }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val proof = mutableListOf<ParseFile>()
                        if (photoUri != null) {
                            val bytes = context.contentResolver.openInputStream(photoUri!!)?.readBytes()
                            bytes?.let {
                                val pf = ParseFile("transfer_proof.jpg", it)
                                withContext(Dispatchers.IO) { pf.save() }
                                proof.add(pf)
                            }
                        }
                        val res =
                            transferRepository.createTransferRequest(
                                fowlId = fowlData.objectId,
                                fromOwnerId = ParseUser.getCurrentUser()?.objectId ?: "",
                                toOwnerId = toOwnerId,
                                proofPhotos = proof,
                                location = location,
                                notes = notes,
                            )
                        if (res.success) onTransferAdded()
                    }
                },
                enabled = toOwnerId.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

// Simple card for displaying a vaccination record
@Composable
fun VaccinationCard(record: VaccinationRecord) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(record.vaccineName, style = MaterialTheme.typography.titleSmall)
            Text(
                "Date: ${formatDate(record.administeredDate)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("Dosage: ${record.dosage}", style = MaterialTheme.typography.bodyMedium)
            if (record.notes.isNotBlank()) {
                Text("Notes: ${record.notes}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// Simple card for displaying a medication record
@Composable
fun MedicationCard(record: MedicationRecord) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(record.medicineName, style = MaterialTheme.typography.titleSmall)
            Text(
                "Start Date: ${formatDate(record.startDate)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Dosage: ${record.dosage}, ${record.frequency}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("Duration: ${record.duration} days", style = MaterialTheme.typography.bodyMedium)
            if (record.purpose.isNotBlank()) {
                Text("Purpose: ${record.purpose}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun HatchingCard(
    record: HatchingRecord,
    onUpdateClick: (HatchingRecord) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Batch: ${record.batchName} (${record.breed})",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                "Eggs: ${record.numberOfEggs}, Hatched: ${record.hatchedCount}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Start Date: ${formatDate(record.startDate)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Expected Hatch: ${formatDate(record.expectedHatchDate)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Status: ${record.status.displayName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            if (record.notes.isNotBlank()) {
                Text("Notes: ${record.notes}", style = MaterialTheme.typography.bodySmall)
            }
            // TODO: Add button or clickable area to trigger onUpdateClick(record)
        }
    }
}

@Composable
fun BrudingCard(
    record: BrudingRecord,
    onUpdateClick: (BrudingRecord) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Batch: ${record.batchName} (${record.breed})",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                "Chicks: ${record.numberOfChicks}, Mortality: ${record.mortalityCount}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Start Date: ${formatDate(record.startDate)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Health: ${record.healthStatus.displayName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            if (record.notes.isNotBlank()) {
                Text("Notes: ${record.notes}", style = MaterialTheme.typography.bodySmall)
            }
            // TODO: Add button or clickable area to trigger onUpdateClick(record)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccinationDialog(
    fowlData: FowlData,
    healthManagementService: HealthManagementService,
    onDismiss: () -> Unit,
    onVaccinationAdded: () -> Unit,
) {
    var vaccineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var administeredDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault(),
            ).format(Date()),
        )
    }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Vaccination for ${fowlData.name}") },
        text = {
            Column {
                OutlinedTextField(
                    vaccineName,
                    { vaccineName = it },
                    label = { Text("Vaccine Name") },
                )
                OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") })
                OutlinedTextField(
                    administeredDate,
                    { administeredDate = it },
                    label = { Text("Administered Date (YYYY-MM-DD)") },
                )
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (Optional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val record =
                            VaccinationRecord(
                                birdId = fowlData.objectId,
                                birdName = fowlData.name,
                                vaccineType = VaccineType.ROUTINE, // Default, make selectable later
                                vaccineName = vaccineName,
                                dosage = dosage,
                                administeredDate =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.getDefault(),
                                    ).parse(administeredDate) ?: Date(),
                                notes = notes,
                            )
                        val result =
                            healthManagementService.addVaccinationRecord(fowlData.objectId, record)
                        if (result.isSuccess) onVaccinationAdded()
                        // TODO: Handle error
                    }
                },
                enabled = vaccineName.isNotBlank() && dosage.isNotBlank() && administeredDate.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    fowlData: FowlData,
    healthManagementService: HealthManagementService,
    photoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onMedicationAdded: () -> Unit,
) {
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var dateAdministered by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var reason by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onPhotoUriChange(it) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication for ${fowlData.name}") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(medicationName, { medicationName = it }, label = { Text("Medication Name") })
                OutlinedTextField(dosage, { dosage = it }, label = { Text("Dosage") })
                OutlinedTextField(
                    dateAdministered,
                    { dateAdministered = it },
                    label = { Text("Date Administered (YYYY-MM-DD)") },
                )
                OutlinedTextField(reason, { reason = it }, label = { Text("Reason for Medication") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") })
                PhotoCaptureSection(photoUri) { photoLauncher.launch("image/*") }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val record =
                            MedicationRecord(
                                id = UUID.randomUUID().toString(),
                                birdId = fowlData.objectId,
                                medicationType = MedicationType.OTHER,
                                medicineName = medicationName,
                                dosage = dosage,
                                frequency = "As needed",
                                duration = 1,
                                startDate =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
                                        dateAdministered,
                                    ) ?: Date(),
                                purpose = reason,
                                instructions = notes,
                            )

                        if (photoUri != null) {
                            val bytes = context.contentResolver.openInputStream(photoUri!!)?.readBytes()
                            bytes?.let {
                                val pf = ParseFile("medication_photo.jpg", it)
                                withContext(Dispatchers.IO) { pf.save() }
                                record.photo = pf
                            }
                        }

                        val result = healthManagementService.addMedicationRecord(fowlData.objectId, record, photoUri, context)
                        if (result.isSuccess) onMedicationAdded()
                        // TODO: Handle error
                    }
                },
                enabled = medicationName.isNotBlank() && dosage.isNotBlank() && dateAdministered.isNotBlank() && reason.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHatchingDialog(
    // Context for default breed, etc.
    fowlData: FowlData,
    hatchingAndBrudingService: HatchingAndBrudingService,
    photoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onHatchingRecordAdded: () -> Unit,
) {
    var batchName by remember { mutableStateOf("Batch-${Date().time % 10000}") }
    var breed by remember { mutableStateOf(fowlData.type) } // Prefill breed from fowl context
    var numberOfEggs by remember { mutableStateOf("") }
    var startDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault(),
            ).format(Date()),
        )
    }
    var expectedHatchDays by remember { mutableStateOf("21") } // Default for chickens
    var incubatorSettings by remember { mutableStateOf("Temp: 37.5°C, Humidity: 55%") }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onPhotoUriChange(it) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Hatching Record") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(batchName, { batchName = it }, label = { Text("Batch Name") })
                OutlinedTextField(breed, { breed = it }, label = { Text("Breed") })
                OutlinedTextField(
                    numberOfEggs,
                    { numberOfEggs = it },
                    label = { Text("Number of Eggs") },
                )
                OutlinedTextField(
                    startDate,
                    { startDate = it },
                    label = { Text("Incubation Start Date (YYYY-MM-DD)") },
                )
                OutlinedTextField(
                    expectedHatchDays,
                    { expectedHatchDays = it },
                    label = { Text("Expected Hatch Duration (days)") },
                )
                OutlinedTextField(
                    incubatorSettings,
                    { incubatorSettings = it },
                    label = { Text("Incubator Settings") },
                )
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (Optional)") })
                PhotoCaptureSection(photoUri) { photoLauncher.launch("image/*") }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val sDate =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(startDate)
                                ?: Date()
                        val calendar = Calendar.getInstance().apply { time = sDate }
                        calendar.add(Calendar.DAY_OF_YEAR, expectedHatchDays.toIntOrNull() ?: 21)
                        val expectedHatchDate = calendar.time

                        val record =
                            HatchingRecord(
                                id = UUID.randomUUID().toString(),
                                eggId = UUID.randomUUID().toString(), // Or a more specific ID system
                                batchName = batchName,
                                breed = breed,
                                numberOfEggs = numberOfEggs.toIntOrNull() ?: 0,
                                startDate = sDate,
                                expectedHatchDate = expectedHatchDate,
                                incubatorSettings = incubatorSettings,
                                notes = notes,
                                region = ParseUser.getCurrentUser()?.getString("region") ?: "",
                                // createdBy will be set in service
                            )

                        if (photoUri != null) {
                            val bytes = context.contentResolver.openInputStream(photoUri!!)?.readBytes()
                            bytes?.let {
                                val pf = ParseFile("hatching_photo.jpg", it)
                                withContext(Dispatchers.IO) { pf.save() }
                                record.photo = pf
                            }
                        }

                        val result = hatchingAndBrudingService.addHatchingRecord(record, photoUri, context)
                        if (result.isSuccess) onHatchingRecordAdded()
                        // TODO: Handle error
                    }
                },
                enabled = batchName.isNotBlank() && breed.isNotBlank() && numberOfEggs.isNotBlank() && startDate.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBrudingDialog(
    // Context for default breed
    fowlData: FowlData,
    hatchingAndBrudingService: HatchingAndBrudingService,
    onDismiss: () -> Unit,
    onBrudingRecordAdded: () -> Unit,
) {
    var batchName by remember { mutableStateOf("BrudeBatch-${Date().time % 10000}") }
    var breed by remember { mutableStateOf(fowlData.type) } // Prefill breed
    var numberOfChicks by remember { mutableStateOf("") }
    var startDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault(),
            ).format(Date()),
        )
    }
    var temperatureSchedule by remember { mutableStateOf("Week1: 32-35°C, reduce 3°C weekly") }
    var feedType by remember { mutableStateOf("Chick Starter Mash") }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bruding Record") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(batchName, { batchName = it }, label = { Text("Batch Name") })
                OutlinedTextField(breed, { breed = it }, label = { Text("Breed") })
                OutlinedTextField(
                    numberOfChicks,
                    { numberOfChicks = it },
                    label = { Text("Number of Chicks") },
                )
                OutlinedTextField(
                    startDate,
                    { startDate = it },
                    label = { Text("Bruding Start Date (YYYY-MM-DD)") },
                )
                OutlinedTextField(
                    temperatureSchedule,
                    { temperatureSchedule = it },
                    label = { Text("Temperature Schedule") },
                )
                OutlinedTextField(feedType, { feedType = it }, label = { Text("Feed Type") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (Optional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val record =
                            BrudingRecord(
                                chickId =
                                    UUID.randomUUID()
                                        .toString(),
                                // Or a more specific ID system for batch
                                batchName = batchName,
                                breed = breed,
                                numberOfChicks = numberOfChicks.toIntOrNull() ?: 0,
                                startDate =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
                                        startDate,
                                    ) ?: Date(),
                                temperatureSchedule = temperatureSchedule,
                                feedType = feedType,
                                notes = notes,
                                region = ParseUser.getCurrentUser()?.getString("region") ?: "",
                                // createdBy will be set in service
                            )
                        val result = hatchingAndBrudingService.addBrudingRecord(record)
                        if (result.isSuccess) onBrudingRecordAdded()
                        // TODO: Handle error
                    }
                },
                enabled = batchName.isNotBlank() && breed.isNotBlank() && numberOfChicks.isNotBlank() && startDate.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun AddFowlDialog(
    name: String,
    onNameChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    parentId: String?,
    onParentIdChange: (String?) -> Unit,
    fowlOptions: List<FowlData>,
    photoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onPhotoUriChange(uri)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Fowl") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Column {
                    Text("Type:", style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = type == "Rooster",
                            onClick = { onTypeChange("Rooster") },
                        )
                        Text("Rooster")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = type == "Hen", onClick = { onTypeChange("Hen") })
                        Text("Hen")
                    }
                }

                OutlinedTextField(
                    value = birthDate,
                    onValueChange = onBirthDateChange,
                    label = { Text("Birth Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Parent selection dropdown
                Text("Parent (optional):", style = MaterialTheme.typography.bodyMedium)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(fowlOptions.find { it.objectId == parentId }?.name ?: "Select Parent")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                onParentIdChange(null)
                                expanded = false
                            },
                        )
                        fowlOptions.forEach { fowl ->
                            DropdownMenuItem(
                                text = { Text(fowl.name) },
                                onClick = {
                                    onParentIdChange(fowl.objectId)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Photo (optional):", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (photoUri != null) "Change Photo" else "Pick Photo")
                }
                photoUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Fowl Photo",
                        modifier = Modifier.size(80.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = name.isNotBlank() && birthDate.isNotBlank(),
            ) {
                Text("Add Fowl")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

fun formatDate(date: Date?): String {
    return date?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "N/A"
}

@Composable
fun LineageTree(
    fowls: List<FowlData>,
    depth: Int = 0,
) {
    Column(modifier = Modifier.padding(start = (depth * 16).dp)) {
        fowls.forEach { fowl ->
            Text("└── ${fowl.name} (${fowl.type})")
            fowl.children?.let { LineageTree(it, depth + 1) }
        }
    }
}

suspend fun fetchLineage(fowl: FowlData): List<FowlData> {
    val lineage = mutableListOf<FowlData>()
    var currentFowl: ParseObject? = ParseQuery.getQuery<ParseObject>("Fowl").get(fowl.objectId)

    while (currentFowl != null) {
        val parent = currentFowl.getParseObject("parentId")
        if (parent != null) {
            lineage.add(
                0,
                FowlData(
                    objectId = parent.objectId ?: "",
                    name = parent.getString("name") ?: "Unknown",
                    type = parent.getString("type") ?: "Unknown",
                    birthDate = parent.getString("birthDate") ?: "Unknown",
                ),
            )
            currentFowl = parent
        } else {
            break
        }
    }

    return lineage
}

@Preview(showBackground = true)
@Composable
fun FowlScreenPreview() {
    FowlScreen()
}
