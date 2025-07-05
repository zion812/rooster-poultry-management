package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateRecordsScreen(
    navController: NavController,
    updateType: String,
    isTeluguMode: Boolean = false,
) {
    val title =
        when (updateType) {
            "chicks" -> "Update Chicks"
            "adults" -> "Update Adults"
            "breeding" -> "Update Breeding Section"
            "incubation" -> "Update Incubation"
            "breeders" -> "Update Breeders"
            "eggs" -> "Update Eggs"
            else -> "Update Records"
        }

    var selectedBatch by remember { mutableStateOf("") }
    var updateDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var healthStatus by remember { mutableStateOf("") }
    var feedConsumption by remember { mutableStateOf("") }
    var eggProduction by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val batches = listOf("Batch A-23", "Batch B-15", "Layer Section", "Breeder Group")
    val healthStatuses = listOf("Excellent", "Good", "Fair", "Poor", "Critical")

    var showBatchDropdown by remember { mutableStateOf(false) }
    var showHealthDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Update,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Record updates for better tracking and analysis",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Basic Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Batch Selection
                        ExposedDropdownMenuBox(
                            expanded = showBatchDropdown,
                            onExpandedChange = { showBatchDropdown = it },
                        ) {
                            OutlinedTextField(
                                value = selectedBatch,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Batch") },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBatchDropdown)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Group, contentDescription = null)
                                },
                            )

                            ExposedDropdownMenu(
                                expanded = showBatchDropdown,
                                onDismissRequest = { showBatchDropdown = false },
                            ) {
                                batches.forEach { batch ->
                                    DropdownMenuItem(
                                        text = { Text(batch) },
                                        onClick = {
                                            selectedBatch = batch
                                            showBatchDropdown = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = updateDate,
                            onValueChange = { updateDate = it },
                            label = { Text("Update Date (DD/MM/YYYY)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Health Status
                        ExposedDropdownMenuBox(
                            expanded = showHealthDropdown,
                            onExpandedChange = { showHealthDropdown = it },
                        ) {
                            OutlinedTextField(
                                value = healthStatus,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Health Status") },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showHealthDropdown)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.HealthAndSafety, contentDescription = null)
                                },
                            )

                            ExposedDropdownMenu(
                                expanded = showHealthDropdown,
                                onDismissRequest = { showHealthDropdown = false },
                            ) {
                                healthStatuses.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status) },
                                        onClick = {
                                            healthStatus = status
                                            showHealthDropdown = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Type-specific fields
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text =
                                when (updateType) {
                                    "chicks" -> "Chick Metrics"
                                    "adults" -> "Adult Bird Metrics"
                                    "breeding" -> "Breeding Metrics"
                                    "incubation" -> "Incubation Metrics"
                                    "breeders" -> "Breeder Metrics"
                                    "eggs" -> "Egg Production Metrics"
                                    else -> "Metrics"
                                },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (updateType) {
                            "chicks", "adults", "breeders" -> {
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Average Weight (kg)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    leadingIcon = {
                                        Icon(Icons.Default.Scale, contentDescription = null)
                                    },
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = feedConsumption,
                                    onValueChange = { feedConsumption = it },
                                    label = { Text("Feed Consumption (kg/day)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    leadingIcon = {
                                        Icon(Icons.Default.Restaurant, contentDescription = null)
                                    },
                                )
                            }

                            "eggs" -> {
                                OutlinedTextField(
                                    value = eggProduction,
                                    onValueChange = { eggProduction = it },
                                    label = { Text("Eggs Produced Today") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = {
                                        Icon(Icons.Default.Egg, contentDescription = null)
                                    },
                                )
                            }

                            "incubation" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    OutlinedTextField(
                                        value = temperature,
                                        onValueChange = { temperature = it },
                                        label = { Text("Temperature (°C)") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Thermostat,
                                                contentDescription = null,
                                            )
                                        },
                                    )

                                    OutlinedTextField(
                                        value = humidity,
                                        onValueChange = { humidity = it },
                                        label = { Text("Humidity (%)") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        leadingIcon = {
                                            Icon(Icons.Default.Water, contentDescription = null)
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes/Observations") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            leadingIcon = {
                                Icon(Icons.Filled.Notes, contentDescription = null)
                            },
                        )
                    }
                }
            }

            // Recent Updates
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Recent Updates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val recentUpdates =
                            listOf(
                                RecentUpdate(
                                    "Today",
                                    "Batch A-23",
                                    "Weight: 2.1kg, Feed: 0.8kg",
                                    "Good",
                                ),
                                RecentUpdate(
                                    "Yesterday",
                                    "Batch B-15",
                                    "Weight: 1.8kg, Feed: 0.7kg",
                                    "Excellent",
                                ),
                                RecentUpdate(
                                    "2 days ago",
                                    "Layer Section",
                                    "Eggs: 145, Health: Good",
                                    "Good",
                                ),
                            )

                        recentUpdates.forEach { update ->
                            UpdateHistoryItem(update)
                            if (update != recentUpdates.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Save update
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedBatch.isNotBlank() && updateDate.isNotBlank(),
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Update")
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateHistoryItem(update: RecentUpdate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${update.date} - ${update.batch}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = update.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            Surface(
                color =
                    when (update.status) {
                        "Excellent" -> Color(0xFF4CAF50)
                        "Good" -> Color(0xFF2196F3)
                        "Fair" -> Color(0xFFFF9800)
                        else -> Color(0xFFf44336)
                    }.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = update.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        when (update.status) {
                            "Excellent" -> Color(0xFF4CAF50)
                            "Good" -> Color(0xFF2196F3)
                            "Fair" -> Color(0xFFFF9800)
                            else -> Color(0xFFf44336)
                        },
                )
            }
        }
    }
}

data class RecentUpdate(
    val date: String,
    val batch: String,
    val details: String,
    val status: String,
)
