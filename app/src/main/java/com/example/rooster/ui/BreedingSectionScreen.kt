package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.data.model.BreedingCycle
import com.parse.ParseQuery
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingSectionScreen() {
    val scope = rememberCoroutineScope()
    var breedingCycles by remember { mutableStateOf<List<BreedingCycle>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            loading = true
            error = null
            try {
                val query =
                    ParseQuery.getQuery(BreedingCycle::class.java)
                        .orderByDescending("startDate")
                        .setLimit(50)
                breedingCycles = query.find()
            } catch (e: Exception) {
                error = "Failed to load breeding cycles: ${e.message}"
                breedingCycles = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Cycles") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Breeding Cycle")
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
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(breedingCycles) { cycle ->
                        BreedingCycleCard(cycle = cycle)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBreedingCycleDialog(
            onDismiss = { showAddDialog = false },
            onSave = { roosterId, henId ->
                scope.launch {
                    try {
                        val newCycle =
                            BreedingCycle().apply {
                                this.roosterId = roosterId
                                this.henId = henId
                                this.startDate = Date()
                                this.expectedHatchDate =
                                    Date(System.currentTimeMillis() + 21L * 24 * 3600 * 1000)
                                this.status = "INCUBATING"
                                this.traceable = true
                            }
                        newCycle.saveInBackground()
                        showAddDialog = false
                        // Refresh list
                        val query =
                            ParseQuery.getQuery(BreedingCycle::class.java)
                                .orderByDescending("startDate")
                                .setLimit(50)
                        breedingCycles = query.find()
                    } catch (e: Exception) {
                        error = "Failed to save breeding cycle: ${e.message}"
                    }
                }
            },
        )
    }
}

@Composable
fun BreedingCycleCard(cycle: BreedingCycle) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Cycle ${cycle.objectId?.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                StatusChip(status = cycle.status ?: "UNKNOWN")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rooster ID",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = cycle.roosterId ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hen ID",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = cycle.henId ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Started",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = dateFormat.format(cycle.startDate ?: Date()),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Expected Hatch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = dateFormat.format(cycle.expectedHatchDate ?: Date()),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (cycle.traceable) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "✓ Traceable",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color =
        when (status) {
            "INCUBATING" -> MaterialTheme.colorScheme.secondary
            "HATCHED" -> MaterialTheme.colorScheme.primary
            "BROODED" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.outline
        }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
        modifier = Modifier.padding(4.dp),
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun AddBreedingCycleDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var roosterId by remember { mutableStateOf("") }
    var henId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Breeding Cycle") },
        text = {
            Column {
                OutlinedTextField(
                    value = roosterId,
                    onValueChange = { roosterId = it },
                    label = { Text("Rooster ID") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = henId,
                    onValueChange = { henId = it },
                    label = { Text("Hen ID") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(roosterId, henId) },
                enabled = roosterId.isNotBlank() && henId.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
