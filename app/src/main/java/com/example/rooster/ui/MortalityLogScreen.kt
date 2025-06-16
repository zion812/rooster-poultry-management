package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.data.model.MortalityLog
import com.parse.ParseQuery
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortalityLogScreen() {
    val scope = rememberCoroutineScope()
    var mortalityLogs by remember { mutableStateOf<List<MortalityLog>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            loading = true
            error = null
            try {
                val query =
                    ParseQuery.getQuery(MortalityLog::class.java)
                        .orderByDescending("date")
                        .setLimit(100)
                mortalityLogs = query.find()
            } catch (e: Exception) {
                error = "Failed to load mortality logs: ${e.message}"
                mortalityLogs = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortality Logs") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.error,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Mortality Log")
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
            } else if (mortalityLogs.isEmpty()) {
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
                            Icons.Filled.Warning,
                            contentDescription = "No mortality logs",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No mortality logs recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = "Good news! No bird losses have been recorded.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(mortalityLogs) { log ->
                        MortalityLogCard(log = log)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMortalityLogDialog(
            onDismiss = { showAddDialog = false },
            onSave = { birdId, cause ->
                scope.launch {
                    try {
                        val newLog =
                            MortalityLog().apply {
                                this.birdId = birdId
                                this.cause = cause
                                this.date = Date()
                                this.attachments = emptyList()
                            }
                        newLog.saveInBackground()
                        showAddDialog = false
                        // Refresh list
                        val query =
                            ParseQuery.getQuery(MortalityLog::class.java)
                                .orderByDescending("date")
                                .setLimit(100)
                        mortalityLogs = query.find()
                    } catch (e: Exception) {
                        error = "Failed to save mortality log: ${e.message}"
                    }
                }
            },
        )
    }
}

@Composable
fun MortalityLogCard(log: MortalityLog) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
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
                        Icons.Filled.Warning,
                        contentDescription = "Mortality",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bird: ${log.birdId}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Text(
                    text = dateFormat.format(log.date ?: Date()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cause of Death",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = log.cause ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            val attachmentCount = log.attachments?.size ?: 0
            if (attachmentCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$attachmentCount attachment(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun AddMortalityLogDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var birdId by remember { mutableStateOf("") }
    var cause by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Mortality")
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
                    value = cause,
                    onValueChange = { cause = it },
                    label = { Text("Cause of Death") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    placeholder = { Text("Describe the cause of death...") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(birdId, cause) },
                enabled = birdId.isNotBlank() && cause.isNotBlank(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text("Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
