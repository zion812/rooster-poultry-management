package com.example.rooster.professionaltools.casestudies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
data class PatientSignalment(
    val species: String, val breed: String, val age: String, val sex: String, val weightKg: Double?
)

data class DiagnosticWorkupItem(val test: String, val findings: String)
data class CaseStudyOutcome(val status: String, val dateResolvedOrClosed: Date, val longTermFollowUp: String?)
data class CaseStudyAttachment(val type: String, val filename: String, val caption: String)
data class PeerComment(
    val commentId: String, val vetId: String, val vetName: String, val commentText: String, val timestamp: Date
) {
    fun getFormattedTimestamp(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timestamp)
}

data class CaseStudy(
    val studyId: String,
    var title: String,
    val authorVetId: String,
    val authorVetName: String,
    val dateCreated: Date,
    var status: String, // draft, published, peer_review, archived
    var confidentiality: String,
    var patientSignalment: PatientSignalment,
    var dateOfPresentation: Date,
    var presentingComplaint: String,
    var history: String,
    var diagnosticWorkup: List<DiagnosticWorkupItem> = emptyList(),
    var diagnosis: String,
    var differentialDiagnoses: List<String> = emptyList(),
    var treatmentProtocol: String,
    var treatmentChallenges: String?,
    var outcome: CaseStudyOutcome,
    var discussionAndLearningPoints: String,
    var attachments: List<CaseStudyAttachment> = emptyList(),
    var keywords: List<String> = emptyList(),
    var citations: List<String> = emptyList(),
    var viewCount: Int = 0,
    var peerComments: MutableList<PeerComment> = mutableListOf()
) {
    fun getFormattedDate(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "N/A"
}

// --- ViewModel ---
class CaseStudyViewModel : ViewModel() {
    private val _caseStudies = MutableStateFlow<List<CaseStudy>>(emptyList())
    val caseStudies: StateFlow<List<CaseStudy>> = _caseStudies

    private val _selectedCaseStudy = MutableStateFlow<CaseStudy?>(null)
    val selectedCaseStudy: StateFlow<CaseStudy?> = _selectedCaseStudy

    // Mock data for creation dialogs
    val speciesList = listOf("Cow", "Dog", "Horse", "Cat", "Sheep", "Pig")
    val mockDiagnoses = listOf("Atypical Pneumonia", "Laminitis", "Mastitis", "GDV", "Colic")
    val statuses = listOf("draft", "published", "peer_review", "archived")
    val knownVetNames = mapOf("vet001" to "Dr. Alice", "vet002" to "Dr. Bob", "vet003" to "Dr. Carol")


    init {
        loadMockCaseStudies()
    }

    private fun loadMockCaseStudies(count: Int = 3) {
        val tempStudies = mutableListOf<CaseStudy>()
        for (i in 0 until count) {
            val species = speciesList.random()
            val diagnosis = mockDiagnoses.random()
            val presentationDate = Date(System.currentTimeMillis() - Random.nextLong(30, 365) * 86400000L)
            val resolvedDate = Date(presentationDate.time + Random.nextLong(7, 90) * 86400000L)
            val authorId = knownVetNames.keys.random()

            tempStudies.add(
                CaseStudy(
                    studyId = "cs_${presentationDate.time}_$i",
                    title = "Case: $diagnosis in a $species",
                    authorVetId = authorId,
                    authorVetName = knownVetNames[authorId] ?: "Unknown Vet",
                    dateCreated = Date(resolvedDate.time + Random.nextLong(1,10) * 86400000L),
                    status = statuses.filterNot { it == "archived"}.random(),
                    confidentiality = listOf("anonymous_patient", "owner_consent_given").random(),
                    patientSignalment = PatientSignalment(species, "Breed ${Random.nextInt(1,5)}", "${Random.nextInt(1,10)} years", listOf("Male", "Female", "Castrated Male").random(), Random.nextDouble(3.0, 700.0)),
                    dateOfPresentation = presentationDate,
                    presentingComplaint = "Presented with severe symptoms.",
                    history = "Relevant history noted.",
                    diagnosticWorkup = listOf(DiagnosticWorkupItem("Physical Exam", "Key findings noted.")),
                    diagnosis = diagnosis,
                    treatmentProtocol = "Standard protocol followed.",
                    outcome = CaseStudyOutcome(listOf("Full Recovery", "Partial Recovery", "Managed").random(), resolvedDate, "Follow-up scheduled."),
                    discussionAndLearningPoints = "Important learning points from this case.",
                    keywords = listOf(species, diagnosis.split(" ").first()),
                    viewCount = Random.nextInt(0, 200)
                )
            )
        }
        _caseStudies.value = tempStudies.sortedByDescending { it.dateCreated }
    }

    fun selectCaseStudy(studyId: String) {
        val study = _caseStudies.value.find { it.studyId == studyId }
        study?.let {
            it.viewCount++ // Increment view count
            _selectedCaseStudy.value = it.copy() // Ensure UI update
            _caseStudies.update { list -> list.map { c -> if(c.studyId == studyId) c.copy(viewCount = c.viewCount) else c } }
        }
    }

    fun clearSelectedCaseStudy() {
        _selectedCaseStudy.value = null
    }

    fun addComment(studyId: String, vetId: String, commentText: String) {
        _caseStudies.update { studies ->
            studies.map { study ->
                if (study.studyId == studyId && study.status == "published") {
                    val newComment = PeerComment("cmt_${Date().time}", vetId, knownVetNames[vetId] ?: "Vet User", commentText, Date())
                    study.copy(peerComments = (study.peerComments + newComment).toMutableList())
                } else study
            }
        }
        // If this comment was for the selected study, update it too
        if (_selectedCaseStudy.value?.studyId == studyId && _selectedCaseStudy.value?.status == "published") {
             val newComment = PeerComment("cmt_${Date().time}", vetId, knownVetNames[vetId] ?: "Vet User", commentText, Date())
            _selectedCaseStudy.update { it?.copy(peerComments = (it.peerComments + newComment).toMutableList()) }
        }
    }

    fun createCaseStudy(title: String, authorVetId: String, signalment: PatientSignalment, diagnosis: String, discussion: String) {
        val now = Date()
        val newStudy = CaseStudy(
            studyId = "cs_new_${now.time}", title = title, authorVetId = authorVetId, authorVetName = knownVetNames[authorVetId] ?: "Vet User",
            dateCreated = now, status = "draft", confidentiality = "anonymous_patient", patientSignalment = signalment,
            dateOfPresentation = now, presentingComplaint = "N/A (Simplified)", history = "N/A (Simplified)", diagnosis = diagnosis,
            treatmentProtocol = "N/A (Simplified)", outcome = CaseStudyOutcome("Pending", now, null), discussionAndLearningPoints = discussion,
            keywords = listOf(signalment.species, diagnosis.split(" ").firstOrNull() ?: "")
        )
        _caseStudies.update { (listOf(newStudy) + it).sortedByDescending { cs -> cs.dateCreated } }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseStudyScreen(viewModel: CaseStudyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val studies by viewModel.caseStudies.collectAsState()
    val selectedStudy by viewModel.selectedCaseStudy.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateCaseStudyDialog(viewModel = viewModel, onDismiss = { showCreateDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedStudy == null) "Case Studies" else "Case Study Details") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B)), // Dark Teal
                navigationIcon = if (selectedStudy != null) {
                    { IconButton(onClick = { viewModel.clearSelectedCaseStudy() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
                } else null,
                actions = {
                    if (selectedStudy == null) { // Search on list view
                        IconButton(onClick = { /* TODO: Implement Search/Filter Dialog */ }) {
                            Icon(Icons.Filled.Search, "Search Case Studies")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedStudy == null) { // FAB on list view
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Filled.Add, "Create New Case Study")
                }
            }
        }
    ) { paddingValues ->
        if (selectedStudy == null) {
            CaseStudyListScreen(paddingValues, studies, onStudyClick = { viewModel.selectCaseStudy(it.studyId) })
        } else {
            CaseStudyDetailScreen(paddingValues, selectedStudy!!, viewModel)
        }
    }
}

@Composable
fun CaseStudyListScreen(paddingValues: PaddingValues, studies: List<CaseStudy>, onStudyClick: (CaseStudy) -> Unit) {
    LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
        if (studies.isEmpty()) {
            item { Text("No case studies found.") }
        } else {
            items(studies, key = { it.studyId }) { study ->
                CaseStudyListItem(study, onClick = { onStudyClick(study) })
            }
        }
    }
}

@Composable
fun CaseStudyListItem(study: CaseStudy, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(study.title, style = MaterialTheme.typography.titleMedium)
            Text("Author: ${study.authorVetName}, Species: ${study.patientSignalment.species}", style = MaterialTheme.typography.bodyMedium)
            Text("Diagnosis: ${study.diagnosis}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Status: ${study.status}", style = MaterialTheme.typography.bodySmall)
                Text("Views: ${study.viewCount}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CaseStudyDetailScreen(paddingValues: PaddingValues, study: CaseStudy, viewModel: CaseStudyViewModel) {
    var commentText by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(study.title, style = MaterialTheme.typography.headlineMedium) }
        item { DetailSection("Author", "${study.authorVetName} (${study.authorVetId})") }
        item { DetailSection("Published", study.getFormattedDate(study.dateCreated) + " (Status: ${study.status})") }
        item { DetailSection("Confidentiality", study.confidentiality) }

        item { SectionTitle("Patient Signalment") }
        item { Text("Species: ${study.patientSignalment.species}, Breed: ${study.patientSignalment.breed}") }
        item { Text("Age: ${study.patientSignalment.age}, Sex: ${study.patientSignalment.sex}, Weight: ${study.patientSignalment.weightKg?.toString() ?: "N/A"} kg") }

        item { SectionTitle("Case Details") }
        item { DetailSection("Presentation Date", study.getFormattedDate(study.dateOfPresentation)) }
        item { DetailSection("Complaint", study.presentingComplaint) }
        item { DetailSection("History", study.history) }

        if (study.diagnosticWorkup.isNotEmpty()) {
            item { SectionTitle("Diagnostic Workup") }
            items(study.diagnosticWorkup) { workup -> Text("- ${workup.test}: ${workup.findings}") }
        }

        item { DetailSection("Diagnosis", study.diagnosis, isBoldValue = true) }
        if (study.differentialDiagnoses.isNotEmpty()) {
            item { DetailSection("Differential Diagnoses", study.differentialDiagnoses.joinToString()) }
        }
        item { DetailSection("Treatment Protocol", study.treatmentProtocol) }
        study.treatmentChallenges?.let { item { DetailSection("Treatment Challenges", it) } }

        item { SectionTitle("Outcome") }
        item { Text("Status: ${study.outcome.status}, Resolved/Closed: ${study.getFormattedDate(study.outcome.dateResolvedOrClosed)}") }
        study.outcome.longTermFollowUp?.let { item { Text("Follow-up: $it") } }

        item { DetailSection("Discussion & Learning Points", study.discussionAndLearningPoints) }

        if (study.keywords.isNotEmpty()) item { DetailSection("Keywords", study.keywords.joinToString()) }
        // Attachments, Citations could be listed similarly

        item { SectionTitle("Comments (${study.peerComments.size})") }
        if (study.peerComments.isEmpty()) {
            item { Text("No comments yet.") }
        } else {
            items(study.peerComments, key = {it.commentId}) { comment -> CommentItem(comment) }
        }

        if (study.status == "published") { // Allow comments only on published
            item {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Add a comment") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.addComment(study.studyId, "vet_currentUser", commentText) // Use actual current vet ID
                            commentText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                    enabled = commentText.isNotBlank()
                ) { Icon(Icons.Filled.Comment, "Post"); Spacer(Modifier.width(4.dp)); Text("Post Comment") }
            }
        }
    }
}

@Composable
fun DetailSection(label: String, value: String, isBoldValue: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = if(isBoldValue) FontWeight.Bold else FontWeight.Normal)
    }
}
@Composable
fun SectionTitle(title: String) = Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))

@Composable
fun CommentItem(comment: PeerComment) {
    Column(Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Text("${comment.vetName} (${comment.getFormattedTimestamp()})", style = MaterialTheme.typography.labelMedium)
        Text(comment.commentText, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaseStudyDialog(viewModel: CaseStudyViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(viewModel.speciesList.first()) }
    var diagnosis by remember { mutableStateOf(viewModel.mockDiagnoses.first()) }
    var discussion by remember { mutableStateOf("") }
    // Simplified signalment for dialog
    var patientAge by remember { mutableStateOf("") }
    var patientSex by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Case Study") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }) }
                item { ExposedDropdownMenuForOptions("Species", viewModel.speciesList, species) { species = it } }
                item { OutlinedTextField(value = patientAge, onValueChange = { patientAge = it }, label = { Text("Patient Age (e.g., 5 years)") }) }
                item { OutlinedTextField(value = patientSex, onValueChange = { patientSex = it }, label = { Text("Patient Sex") }) }
                item { ExposedDropdownMenuForOptions("Primary Diagnosis", viewModel.mockDiagnoses, diagnosis) { diagnosis = it } }
                item { OutlinedTextField(value = discussion, onValueChange = { discussion = it }, label = { Text("Discussion & Learning Points") }, minLines = 3) }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && diagnosis.isNotBlank() && discussion.isNotBlank()) {
                    val signalment = PatientSignalment(species, "Mixed (Dialog)", patientAge, patientSex, null)
                    viewModel.createCaseStudy(title, "vet_currentUser", signalment, diagnosis, discussion)
                    onDismiss()
                }
            }, enabled = title.isNotBlank() && diagnosis.isNotBlank() && discussion.isNotBlank()) { Text("Create Draft") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
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
fun PreviewCaseStudyScreen_List() {
    MaterialTheme {
        CaseStudyScreen(viewModel = CaseStudyViewModel())
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun PreviewCaseStudyScreen_Detail() {
    val viewModel = CaseStudyViewModel()
    // Simulate selecting a study for detail view preview
    LaunchedEffect(Unit) {
        if(viewModel.caseStudies.value.isNotEmpty()){
            viewModel.selectCaseStudy(viewModel.caseStudies.value.first().studyId)
        }
    }
    MaterialTheme {
        CaseStudyScreen(viewModel = viewModel)
    }
}
