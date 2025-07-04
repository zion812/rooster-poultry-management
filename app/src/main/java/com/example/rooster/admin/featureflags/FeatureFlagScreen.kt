package com.example.rooster.admin.featureflags

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
data class FeatureFlag(
    val name: String, // Unique identifier
    var description: String,
    var isActive: Boolean,
    var rolloutPercentage: Int, // 0-100
    var targetUserSegment: String,
    val createdAt: Date,
    var updatedAt: Date,
    val createdBy: String
) {
    private fun formatDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)

    val formattedCreatedAt: String get() = formatDate(createdAt)
    val formattedUpdatedAt: String get() = formatDate(updatedAt)
}

// --- ViewModel ---
class FeatureFlagViewModel : ViewModel() {
    private val _featureFlags = MutableStateFlow<List<FeatureFlag>>(emptyList())
    val featureFlags: StateFlow<List<FeatureFlag>> = _featureFlags

    init {
        loadMockFlags()
    }

    private fun loadMockFlags() {
        val now = System.currentTimeMillis()
        _featureFlags.value = listOf(
            FeatureFlag("new_telemedicine_interface", "Enable the redesigned telemedicine interface for video consultations.", false, 0, "all", Date(now - 10 * 86400000L), Date(now - 1 * 86400000L), "admin_jane"),
            FeatureFlag("ai_diagnosis_assistant_beta", "Enable the AI-powered diagnosis assistant (Beta).", true, 10, "veterinarians_approved_beta", Date(now - 30 * 86400000L), Date(now - 5 * 3600000L), "admin_john"),
            FeatureFlag("marketplace_commission_increase_test", "A/B Test: Increase marketplace commission from 5% to 7% for a subset of new sellers.", true, 5, "new_sellers_last_7_days", Date(now - 3 * 86400000L), Date(now - 1 * 86400000L), "admin_jane"),
            FeatureFlag("disable_legacy_reporting", "Disable the old reporting system for users who have migrated to the new one.", false, 0, "migrated_users_group_A", Date(now - 5 * 86400000L), Date(now - 5 * 86400000L), "admin_john")
        )
    }

    fun createFlag(name: String, description: String, targetSegment: String, createdBy: String) {
        if (_featureFlags.value.any { it.name == name }) {
            // Handle error: flag already exists
            return
        }
        val newFlag = FeatureFlag(name, description, false, 0, targetSegment, Date(), Date(), createdBy)
        _featureFlags.update { it + newFlag }
    }

    fun updateFlagStatus(name: String, isActive: Boolean) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(isActive = isActive, updatedAt = Date()) else it }
        }
    }

    fun updateRolloutPercentage(name: String, percentage: Int) {
        if (percentage < 0 || percentage > 100) return // Basic validation
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(rolloutPercentage = percentage, updatedAt = Date()) else it }
        }
    }

    fun updateTargetSegment(name: String, segment: String) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(targetUserSegment = segment, updatedAt = Date()) else it }
        }
    }

    fun updateDescription(name: String, description: String) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(description = description, updatedAt = Date()) else it }
        }
    }

    fun deleteFlag(name: String) {
        _featureFlags.update { flags -> flags.filterNot { it.name == name } }
    }
}


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureFlagScreen(viewModel: FeatureFlagViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val flags by viewModel.featureFlags.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var currentEditingFlag by remember { mutableStateOf<FeatureFlag?>(null) }

    // State for Create Dialog
    var newFlagName by remember { mutableStateOf("") }
    var newFlagDesc by remember { mutableStateOf("") }
    var newFlagSegment by remember { mutableStateOf("") }


    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Feature Flag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newFlagName, onValueChange = { newFlagName = it }, label = { Text("Flag Name (ID)") })
                    OutlinedTextField(value = newFlagDesc, onValueChange = { newFlagDesc = it }, label = { Text("Description") })
                    OutlinedTextField(value = newFlagSegment, onValueChange = { newFlagSegment = it }, label = { Text("Target Segment") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newFlagName.isNotBlank() && newFlagDesc.isNotBlank() && newFlagSegment.isNotBlank()) {
                        viewModel.createFlag(newFlagName, newFlagDesc, newFlagSegment, "admin_user")
                        showCreateDialog = false
                        newFlagName = ""; newFlagDesc = ""; newFlagSegment = ""
                    }
                }) { Text("Create") }
            },
            dismissButton = { Button(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }

    currentEditingFlag?.let { flag ->
        if (showEditDialog) {
            EditFeatureFlagDialog(
                flag = flag,
                viewModel = viewModel,
                onDismiss = { showEditDialog = false }
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feature Flag Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Green) // Example
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, "Create new flag")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(flags) { flag ->
                FeatureFlagCard(
                    flag = flag,
                    onEditClick = {
                        currentEditingFlag = it
                        showEditDialog = true
                    },
                    onDeleteClick = { viewModel.deleteFlag(it.name) },
                    onToggleStatus = { viewModel.updateFlagStatus(flag.name, !flag.isActive) }
                )
            }
        }
    }
}

@Composable
fun FeatureFlagCard(
    flag: FeatureFlag,
    onEditClick: (FeatureFlag) -> Unit,
    onDeleteClick: (FeatureFlag) -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(flag.name, style = MaterialTheme.typography.titleLarge)
            Text("Description: ${flag.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Target Segment: ${flag.targetUserSegment}", style = MaterialTheme.typography.bodySmall)
            Text("Rollout: ${flag.rolloutPercentage}%", style = MaterialTheme.typography.bodySmall)
            Text("Created By: ${flag.createdBy} at ${flag.formattedCreatedAt}", style = MaterialTheme.typography.bodySmall)
            Text("Last Updated: ${flag.formattedUpdatedAt}", style = MaterialTheme.typography.bodySmall)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Active: ")
                Switch(checked = flag.isActive, onCheckedChange = { onToggleStatus() })
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onEditClick(flag) }) {
                    Icon(Icons.Filled.Edit, "Edit Flag")
                }
                IconButton(onClick = { onDeleteClick(flag) }) {
                    Icon(Icons.Filled.Delete, "Delete Flag")
                }
            }
        }
    }
}

@Composable
fun EditFeatureFlagDialog(
    flag: FeatureFlag,
    viewModel: FeatureFlagViewModel,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf(flag.description) }
    var rolloutPercentage by remember { mutableStateOf(flag.rolloutPercentage.toString()) }
    var targetSegment by remember { mutableStateOf(flag.targetUserSegment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit: ${flag.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(
                    value = rolloutPercentage,
                    onValueChange = { rolloutPercentage = it.filter { char -> char.isDigit() } },
                    label = { Text("Rollout % (0-100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = targetSegment, onValueChange = { targetSegment = it }, label = { Text("Target Segment") })
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.updateDescription(flag.name, description)
                rolloutPercentage.toIntOrNull()?.let { percent ->
                     if(percent in 0..100) viewModel.updateRolloutPercentage(flag.name, percent)
                }
                viewModel.updateTargetSegment(flag.name, targetSegment)
                onDismiss()
            }) { Text("Save Changes") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewFeatureFlagScreen() {
    MaterialTheme {
        FeatureFlagScreen(viewModel = FeatureFlagViewModel())
    }
}
package com.example.rooster.admin.featureflags

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
data class FeatureFlag(
    val name: String, // Unique identifier
    var description: String,
    var isActive: Boolean,
    var rolloutPercentage: Int, // 0-100
    var targetUserSegment: String,
    val createdAt: Date,
    var updatedAt: Date,
    val createdBy: String
) {
    private fun formatDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)

    val formattedCreatedAt: String get() = formatDate(createdAt)
    val formattedUpdatedAt: String get() = formatDate(updatedAt)
}

// --- ViewModel ---
class FeatureFlagViewModel : ViewModel() {
    private val _featureFlags = MutableStateFlow<List<FeatureFlag>>(emptyList())
    val featureFlags: StateFlow<List<FeatureFlag>> = _featureFlags

    init {
        loadMockFlags()
    }

    private fun loadMockFlags() {
        val now = System.currentTimeMillis()
        _featureFlags.value = listOf(
            FeatureFlag("new_telemedicine_interface", "Enable the redesigned telemedicine interface for video consultations.", false, 0, "all", Date(now - 10 * 86400000L), Date(now - 1 * 86400000L), "admin_jane"),
            FeatureFlag("ai_diagnosis_assistant_beta", "Enable the AI-powered diagnosis assistant (Beta).", true, 10, "veterinarians_approved_beta", Date(now - 30 * 86400000L), Date(now - 5 * 3600000L), "admin_john"),
            FeatureFlag("marketplace_commission_increase_test", "A/B Test: Increase marketplace commission from 5% to 7% for a subset of new sellers.", true, 5, "new_sellers_last_7_days", Date(now - 3 * 86400000L), Date(now - 1 * 86400000L), "admin_jane"),
            FeatureFlag("disable_legacy_reporting", "Disable the old reporting system for users who have migrated to the new one.", false, 0, "migrated_users_group_A", Date(now - 5 * 86400000L), Date(now - 5 * 86400000L), "admin_john")
        ).sortedBy { it.name }
    }

    fun createFlag(name: String, description: String, targetSegment: String, createdBy: String) {
        if (_featureFlags.value.any { it.name == name }) {
            // Ideally, show an error message to the user
            println("Error: Feature flag '$name' already exists.")
            return
        }
        val newFlag = FeatureFlag(name, description, false, 0, targetSegment, Date(), Date(), createdBy)
        _featureFlags.update { (it + newFlag).sortedBy { f -> f.name } }
    }

    fun updateFlagStatus(name: String, isActive: Boolean) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(isActive = isActive, updatedAt = Date()) else it }
        }
    }

    fun updateRolloutPercentage(name: String, percentage: Int) {
        if (percentage < 0 || percentage > 100) {
             println("Error: Percentage must be between 0 and 100.")
            return
        }
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(rolloutPercentage = percentage, updatedAt = Date()) else it }
        }
    }

    fun updateTargetSegment(name: String, segment: String) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(targetUserSegment = segment, updatedAt = Date()) else it }
        }
    }

    fun updateDescription(name: String, description: String) {
        _featureFlags.update { flags ->
            flags.map { if (it.name == name) it.copy(description = description, updatedAt = Date()) else it }
        }
    }

    fun deleteFlag(name: String) {
        _featureFlags.update { flags -> flags.filterNot { it.name == name } }
    }
}


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureFlagScreen(viewModel: FeatureFlagViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val flags by viewModel.featureFlags.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var currentEditingFlag by remember { mutableStateOf<FeatureFlag?>(null) }

    // State for Create Dialog
    var newFlagName by remember { mutableStateOf("") }
    var newFlagDesc by remember { mutableStateOf("") }
    var newFlagSegment by remember { mutableStateOf("") }


    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Feature Flag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newFlagName, onValueChange = { newFlagName = it }, label = { Text("Flag Name (ID)") }, singleLine = true)
                    OutlinedTextField(value = newFlagDesc, onValueChange = { newFlagDesc = it }, label = { Text("Description") })
                    OutlinedTextField(value = newFlagSegment, onValueChange = { newFlagSegment = it }, label = { Text("Target Segment") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newFlagName.isNotBlank() && newFlagDesc.isNotBlank() && newFlagSegment.isNotBlank()) {
                        viewModel.createFlag(newFlagName.trim(), newFlagDesc.trim(), newFlagSegment.trim(), "admin_compose_ui")
                        showCreateDialog = false
                        newFlagName = ""; newFlagDesc = ""; newFlagSegment = ""
                    }
                }) { Text("Create") }
            },
            dismissButton = { Button(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }

    currentEditingFlag?.let { flag ->
        if (showEditDialog) {
            EditFeatureFlagDialog(
                flag = flag,
                viewModel = viewModel,
                onDismiss = { showEditDialog = false; currentEditingFlag = null }
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feature Flag Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)) // A green color
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                newFlagName = ""; newFlagDesc = ""; newFlagSegment = "" // Clear fields before showing
                showCreateDialog = true
            }) {
                Icon(Icons.Filled.Add, "Create new flag")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(flags, key = { it.name }) { flag -> // Added key for better performance
                FeatureFlagCard(
                    flag = flag,
                    onEditClick = {
                        currentEditingFlag = it
                        showEditDialog = true
                    },
                    onDeleteClick = { viewModel.deleteFlag(it.name) },
                    onToggleStatus = { viewModel.updateFlagStatus(flag.name, !flag.isActive) }
                )
            }
        }
    }
}

@Composable
fun FeatureFlagCard(
    flag: FeatureFlag,
    onEditClick: (FeatureFlag) -> Unit,
    onDeleteClick: (FeatureFlag) -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(flag.name, style = MaterialTheme.typography.titleLarge)
            Text("Description: ${flag.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Target Segment: ${flag.targetUserSegment}", style = MaterialTheme.typography.bodySmall)
            Text("Rollout: ${flag.rolloutPercentage}%", style = MaterialTheme.typography.bodySmall)
            Text("Created By: ${flag.createdBy} at ${flag.formattedCreatedAt}", style = MaterialTheme.typography.bodySmall)
            Text("Last Updated: ${flag.formattedUpdatedAt}", style = MaterialTheme.typography.bodySmall)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Text("Active: ", style = MaterialTheme.typography.labelMedium)
                     Switch(checked = flag.isActive, onCheckedChange = { onToggleStatus() })
                }
                Row {
                    IconButton(onClick = { onEditClick(flag) }) {
                        Icon(Icons.Filled.Edit, "Edit Flag")
                    }
                    IconButton(onClick = { onDeleteClick(flag) }) {
                        Icon(Icons.Filled.Delete, "Delete Flag", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun EditFeatureFlagDialog(
    flag: FeatureFlag,
    viewModel: FeatureFlagViewModel,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf(flag.description) }
    var rolloutPercentage by remember { mutableStateOf(flag.rolloutPercentage.toString()) }
    var targetSegment by remember { mutableStateOf(flag.targetUserSegment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit: ${flag.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(
                    value = rolloutPercentage,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { char -> char.isDigit() }
                        if (filtered.isEmpty() || (filtered.toIntOrNull() ?: 0) <= 100) {
                             rolloutPercentage = filtered
                        } else if (filtered.toInt() > 100) {
                            rolloutPercentage = "100"
                        }
                    },
                    label = { Text("Rollout % (0-100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(value = targetSegment, onValueChange = { targetSegment = it }, label = { Text("Target Segment") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.updateDescription(flag.name, description.trim())
                val percentValue = rolloutPercentage.toIntOrNull() ?: flag.rolloutPercentage
                viewModel.updateRolloutPercentage(flag.name, if(percentValue in 0..100) percentValue else flag.rolloutPercentage)
                viewModel.updateTargetSegment(flag.name, targetSegment.trim())
                onDismiss()
            }) { Text("Save Changes") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewFeatureFlagScreen() {
    MaterialTheme {
        FeatureFlagScreen(viewModel = FeatureFlagViewModel())
    }
}
