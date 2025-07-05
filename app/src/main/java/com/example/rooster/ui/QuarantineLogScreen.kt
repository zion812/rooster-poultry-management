package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rooster.data.model.QuarantineLog
import com.parse.ParseQuery
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuarantineLogScreen() {
    val scope = rememberCoroutineScope()
    var quarantineLogs by remember { mutableStateOf<List<QuarantineLog>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            loading = true
            error = null
            try {
                val query =
                    ParseQuery.getQuery(QuarantineLog::class.java)
                        .orderByDescending("startDate")
                        .setLimit(100)
                quarantineLogs = query.find()
            } catch (e: Exception) {
                error = "Failed to load quarantine logs: ${e.message}"
                quarantineLogs = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quarantine Logs") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFE082), // Light amber
                        titleContentColor = Color(0xFF8D6E63), // Brown
                    ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFF8A65), // Light orange
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Quarantine Log")
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            } else if (quarantineLogs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Filled.HealthAndSafety,
                            contentDescription = "No quarantine logs",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No quarantine logs recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = "No birds are currently in quarantine.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(quarantineLogs) { log ->
                        QuarantineLogCard(log = log)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddQuarantineLogDialog(
            onDismiss = { showAddDialog = false },
            onSave = { birdId, reason, endDate ->
                scope.launch {
                    try {
                        val newLog =
                            QuarantineLog().apply {
                                this.birdId = birdId
                                this.reason = reason
                                this.startDate = Date()
                                this.endDate = endDate
                                this.medicalLogs = emptyList()
                            }
                        newLog.saveInBackground()
                        showAddDialog = false
                        // Refresh list
                        val query =
                            ParseQuery.getQuery(QuarantineLog::class.java)
                                .orderByDescending("startDate")
                                .setLimit(100)
                        quarantineLogs = query.find()
                    } catch (e: Exception) {
                        error = "Failed to save quarantine log: ${e.message}"
                    }
                }
            },
        )
    }
}

@Composable
fun QuarantineLogCard(log: QuarantineLog) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val now = Date()
    val isActive = log.endDate?.after(now) ?: true

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isActive) {
                        Color(0xFFFFF3E0) // Light orange background for active quarantine
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.HealthAndSafety,
                        contentDescription = "Quarantine",
                        tint = if (isActive) Color(0xFFFF6F00) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bird: ${log.birdId}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isActive) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface,
                    )
                }

                QuarantineStatusChip(isActive = isActive)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Reason for Quarantine",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = log.reason ?: "No reason specified",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = dateFormat.format(log.startDate ?: Date()),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text =
                            if (log.endDate != null) {
                                dateFormat.format(log.endDate!!)
                            } else {
                                "Ongoing"
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (log.endDate == null) Color(0xFFFF6F00) else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            val medicalLogCount = log.medicalLogs?.size ?: 0
            if (medicalLogCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$medicalLogCount medical log(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun QuarantineStatusChip(isActive: Boolean) {
    val backgroundColor = if (isActive) Color(0xFFFF6F00) else MaterialTheme.colorScheme.outline
    val textColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = Modifier.padding(4.dp),
    ) {
        Text(
            text = if (isActive) "ACTIVE" else "COMPLETED",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
        )
    }
}

@Composable
fun AddQuarantineLogDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Date?) -> Unit,
) {
    var birdId by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var endDateStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.HealthAndSafety,
                    contentDescription = "Quarantine",
                    tint = Color(0xFFFF6F00),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Quarantine")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = birdId,
                    onValueChange = { birdId = it },
                    label = { Text("Bird ID") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter bird identifier") },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Quarantine") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    placeholder = { Text("Illness, injury, preventive measure...") },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = endDateStr,
                    onValueChange = { endDateStr = it },
                    label = { Text("Expected End Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("YYYY-MM-DD") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val endDate =
                        if (endDateStr.isNotBlank()) {
                            try {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDateStr)
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    onSave(birdId, reason, endDate)
                },
                enabled = birdId.isNotBlank() && reason.isNotBlank(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6F00),
                    ),
            ) {
                Text("Add to Quarantine")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
