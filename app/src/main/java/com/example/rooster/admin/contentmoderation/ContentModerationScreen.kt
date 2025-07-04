package com.example.rooster.admin.contentmoderation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data Classes ---
data class ReportedContent(
    val reportId: String,
    val contentId: String,
    val contentType: String,
    val reporterUserId: String,
    val reportedUserId: String,
    val reason: String,
    val timestamp: Date,
    var status: String, // pending_review, approved, rejected, action_taken
    val contentPreview: String,
    var moderatorNotes: String? = null,
    var actionTaken: String? = null,
    var moderatorId: String? = null,
    var reviewTimestamp: Date? = null
) {
    fun getFormattedTimestamp(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timestamp)

    fun getFormattedReviewTimestamp(): String? =
        reviewTimestamp?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) }
}

// --- ViewModel ---
class ContentModerationViewModel : ViewModel() {
    private val _reports = MutableStateFlow<List<ReportedContent>>(emptyList())
    val reports: StateFlow<List<ReportedContent>> = _reports

    val pendingReports: StateFlow<List<ReportedContent>> = MutableStateFlow(emptyList()) // Derived flow

    init {
        loadMockContent()
        // This is a simple way to create a derived flow. For more complex scenarios,
        // you might use .map or .combine on the original _reports flow.
        _reports.value.filter { it.status == "pending_review" }.also {
            (pendingReports as MutableStateFlow).value = it
        }
    }

    private fun loadMockContent() {
        val now = System.currentTimeMillis()
        _reports.value = listOf(
            ReportedContent(
                "report001", "post123", "forum_post", "user002", "user003",
                "Spam and unsolicited advertising.", Date(now - 3600000), "pending_review",
                "Check out my amazing new product at spam.com!"
            ),
            ReportedContent(
                "report002", "comment456", "article_comment", "user001", "user004",
                "Offensive language.", Date(now - 3 * 3600000), "pending_review",
                "This article is terrible and the author is an idiot."
            ),
            ReportedContent(
                "report003", "listing789", "marketplace_listing", "user003", "user002",
                "Misleading product description.", Date(now - 24 * 3600000), "action_taken",
                "Miracle cure for all animal diseases! Guaranteed!",
                moderatorNotes = "Removed listing due to false claims. Warned user.",
                actionTaken = "listing_removed", moderatorId = "admin_mod", reviewTimestamp = Date(now - 12 * 3600000)
            )
        )
        updatePendingReports()
    }

    private fun updatePendingReports() {
        (pendingReports as MutableStateFlow).value = _reports.value.filter { it.status == "pending_review" }
    }

    fun reviewContent(reportId: String, action: String, moderatorId: String, notes: String) {
        _reports.update { currentReports ->
            currentReports.map { report ->
                if (report.reportId == reportId) {
                    report.copy(
                        status = "action_taken",
                        actionTaken = action,
                        moderatorId = moderatorId,
                        moderatorNotes = notes,
                        reviewTimestamp = Date()
                    )
                } else report
            }
        }
        updatePendingReports()
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentModerationScreen(viewModel: ContentModerationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val allReports by viewModel.reports.collectAsState()
    // val pendingReports by viewModel.pendingReports.collectAsState() // Use this if you prefer separate state

    // For this example, we'll filter directly from allReports for simplicity in tabs or sections
    val pendingReportsList = allReports.filter { it.status == "pending_review" }
    val reviewedReportsList = allReports.filter { it.status != "pending_review" }


    var showDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<ReportedContent?>(null) }
    var actionNotes by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf("") } // e.g. "remove_content", "warn_user"

    val possibleActions = listOf("approve_content", "remove_content", "warn_user", "ban_user", "reject_report")


    if (showDialog && selectedReport != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Review Report: ${selectedReport?.reportId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Content: \"${selectedReport?.contentPreview}\"", style = MaterialTheme.typography.bodyMedium)
                    Text("Reason: ${selectedReport?.reason}", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = actionType,
                        onValueChange = { actionType = it },
                        label = { Text("Action Type (e.g., remove_content)") }
                        // Consider a DropdownMenu for predefined actions
                    )
                    OutlinedTextField(
                        value = actionNotes,
                        onValueChange = { actionNotes = it },
                        label = { Text("Moderator Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedReport?.let {
                        viewModel.reviewContent(it.reportId, actionType, "current_admin_user", actionNotes)
                    }
                    showDialog = false
                    actionNotes = ""
                    actionType = ""
                }) { Text("Submit Review") }
            },
            dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Moderation") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red) // Example
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item {
                Text("Pending Reports", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (pendingReportsList.isEmpty()) {
                item { Text("No pending reports.") }
            } else {
                items(pendingReportsList) { report ->
                    ReportCard(report) {
                        selectedReport = it
                        showDialog = true
                    }
                }
            }

            item {
                Text("Reviewed Reports", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            if (reviewedReportsList.isEmpty()) {
                item { Text("No reviewed reports.") }
            } else {
                items(reviewedReportsList) { report ->
                    ReportCard(report, isReviewed = true) {} // No action on click for reviewed for now
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: ReportedContent, isReviewed: Boolean = false, onReviewClick: (ReportedContent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Report ID: ${report.reportId}", style = MaterialTheme.typography.titleMedium)
            Text("Content ID: ${report.contentId} (${report.contentType})")
            Text("Preview: \"${report.contentPreview}\"")
            Text("Reason: ${report.reason}")
            Text("Reported by: ${report.reporterUserId} against ${report.reportedUserId}")
            Text("Reported at: ${report.getFormattedTimestamp()}")
            Text("Status: ${report.status}", color = if (report.status == "pending_review") Color.Blue else Color.DarkGray)

            if (report.status != "pending_review") {
                Text("Action Taken: ${report.actionTaken ?: "N/A"}")
                Text("Moderator: ${report.moderatorId ?: "N/A"}")
                Text("Moderator Notes: ${report.moderatorNotes ?: "N/A"}")
                report.getFormattedReviewTimestamp()?.let { Text("Reviewed At: $it") }
            }

            if (!isReviewed && report.status == "pending_review") {
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onReviewClick(report) }, modifier = Modifier.align(Alignment.End)) {
                    Text("Review Report")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContentModerationScreen() {
    MaterialTheme {
        ContentModerationScreen(viewModel = ContentModerationViewModel())
    }
}
package com.example.rooster.admin.contentmoderation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment

// --- Data Classes ---
data class ReportedContent(
    val reportId: String,
    val contentId: String,
    val contentType: String,
    val reporterUserId: String,
    val reportedUserId: String,
    val reason: String,
    val timestamp: Date,
    var status: String, // pending_review, approved, rejected, action_taken
    val contentPreview: String,
    var moderatorNotes: String? = null,
    var actionTaken: String? = null,
    var moderatorId: String? = null,
    var reviewTimestamp: Date? = null
) {
    fun getFormattedTimestamp(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timestamp)

    fun getFormattedReviewTimestamp(): String? =
        reviewTimestamp?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) }
}

// --- ViewModel ---
class ContentModerationViewModel : ViewModel() {
    private val _reports = MutableStateFlow<List<ReportedContent>>(emptyList())
    val reports: StateFlow<List<ReportedContent>> = _reports

    // No need for a separate pendingReports StateFlow if we filter in the Composable or derive it there.
    // However, if complex logic depended on it, it could be useful.
    // val pendingReports: StateFlow<List<ReportedContent>> = _reports.map { it.filter { report -> report.status == "pending_review" } }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        loadMockContent()
    }

    private fun loadMockContent() {
        val now = System.currentTimeMillis()
        _reports.value = listOf(
            ReportedContent(
                "report001", "post123", "forum_post", "user002", "user003",
                "Spam and unsolicited advertising.", Date(now - 3600000), "pending_review",
                "Check out my amazing new product at spam.com!"
            ),
            ReportedContent(
                "report002", "comment456", "article_comment", "user001", "user004",
                "Offensive language.", Date(now - 3 * 3600000), "pending_review",
                "This article is terrible and the author is an idiot."
            ),
            ReportedContent(
                "report003", "listing789", "marketplace_listing", "user003", "user002",
                "Misleading product description.", Date(now - 24 * 3600000), "action_taken",
                "Miracle cure for all animal diseases! Guaranteed!",
                moderatorNotes = "Removed listing due to false claims. Warned user.",
                actionTaken = "listing_removed", moderatorId = "admin_mod", reviewTimestamp = Date(now - 12 * 3600000)
            )
        )
    }

    fun reviewContent(reportId: String, action: String, moderatorId: String, notes: String) {
        _reports.update { currentReports ->
            currentReports.map { report ->
                if (report.reportId == reportId) {
                    report.copy(
                        status = "action_taken",
                        actionTaken = action,
                        moderatorId = moderatorId,
                        moderatorNotes = notes,
                        reviewTimestamp = Date()
                    )
                } else report
            }
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentModerationScreen(viewModel: ContentModerationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val allReports by viewModel.reports.collectAsState()

    val pendingReportsList = allReports.filter { it.status == "pending_review" }
    val reviewedReportsList = allReports.filter { it.status != "pending_review" }

    var showDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<ReportedContent?>(null) }
    var actionNotes by remember { mutableStateOf("") }
    var actionTypeInput by remember { mutableStateOf("") } // For TextField
    var expanded by remember { mutableStateOf(false) } // For Dropdown


    val possibleActions = listOf("approve_content", "remove_content", "warn_user", "ban_user", "reject_report")


    if (showDialog && selectedReport != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Review Report: ${selectedReport?.reportId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Content: \"${selectedReport?.contentPreview}\"", style = MaterialTheme.typography.bodyMedium)
                    Text("Reason: ${selectedReport?.reason}", style = MaterialTheme.typography.bodySmall)

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = actionTypeInput,
                            onValueChange = {}, // Not directly changed, selected from dropdown
                            readOnly = true,
                            label = { Text("Select Action") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            possibleActions.forEach { action ->
                                DropdownMenuItem(
                                    text = { Text(action) },
                                    onClick = {
                                        actionTypeInput = action
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = actionNotes,
                        onValueChange = { actionNotes = it },
                        label = { Text("Moderator Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedReport?.let {
                        viewModel.reviewContent(it.reportId, actionTypeInput, "current_admin_user", actionNotes)
                    }
                    showDialog = false
                    actionNotes = ""
                    actionTypeInput = ""
                }, enabled = actionTypeInput.isNotBlank()) { Text("Submit Review") }
            },
            dismissButton = { Button(onClick = { showDialog = false; actionNotes = ""; actionTypeInput = "" }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Moderation") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Magenta) // Example color
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item {
                Text("Pending Reports", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (pendingReportsList.isEmpty()) {
                item { Text("No pending reports.") }
            } else {
                items(pendingReportsList) { report ->
                    ReportCard(report) {
                        selectedReport = it
                        actionTypeInput = "" // Reset for new dialog
                        actionNotes = ""
                        showDialog = true
                    }
                }
            }

            item {
                Text("Reviewed Reports", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            if (reviewedReportsList.isEmpty()) {
                item { Text("No reviewed reports.") }
            } else {
                items(reviewedReportsList) { report ->
                    ReportCard(report, isReviewed = true) {} // No action on click for reviewed for now
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: ReportedContent, isReviewed: Boolean = false, onReviewClick: (ReportedContent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Report ID: ${report.reportId}", style = MaterialTheme.typography.titleSmall)
            Text("Content ID: ${report.contentId} (${report.contentType})", style = MaterialTheme.typography.bodyMedium)
            Text("Preview: \"${report.contentPreview}\"", style = MaterialTheme.typography.bodyMedium)
            Text("Reason: ${report.reason}", style = MaterialTheme.typography.bodyMedium)
            Text("Reported by: ${report.reporterUserId} against ${report.reportedUserId}", style = MaterialTheme.typography.bodySmall)
            Text("Reported at: ${report.getFormattedTimestamp()}", style = MaterialTheme.typography.bodySmall)
            Text("Status: ${report.status}", color = if (report.status == "pending_review") MaterialTheme.colorScheme.primary else Color.DarkGray, style = MaterialTheme.typography.labelMedium)

            if (report.status != "pending_review") {
                Text("Action Taken: ${report.actionTaken ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                Text("Moderator: ${report.moderatorId ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                Text("Moderator Notes: ${report.moderatorNotes ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                report.getFormattedReviewTimestamp()?.let { Text("Reviewed At: $it", style = MaterialTheme.typography.bodySmall) }
            }

            if (!isReviewed && report.status == "pending_review") {
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onReviewClick(report) }, modifier = Modifier.align(Alignment.End)) {
                    Text("Review Report")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContentModerationScreen() {
    MaterialTheme {
        ContentModerationScreen(viewModel = ContentModerationViewModel())
    }
}
