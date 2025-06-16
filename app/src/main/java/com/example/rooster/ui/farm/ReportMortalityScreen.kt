package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
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
fun ReportMortalityScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    var selectedBatch by remember { mutableStateOf("") }
    var selectedBird by remember { mutableStateOf("") }
    var mortalityCount by remember { mutableStateOf("") }
    var causeOfDeath by remember { mutableStateOf("") }
    var dateOfDeath by remember { mutableStateOf("") }
    var timeOfDeath by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var hasAttachment by remember { mutableStateOf(false) }

    val batches = listOf("Batch A-23", "Batch B-15", "Layer Section", "Breeder Group")
    val birds = listOf("All Birds", "Bird #001", "Bird #002", "Bird #003", "Multiple Birds")
    val causes =
        listOf("Disease", "Predator Attack", "Natural Death", "Accident", "Unknown", "Other")

    var showBatchDropdown by remember { mutableStateOf(false) }
    var showBirdDropdown by remember { mutableStateOf(false) }
    var showCauseDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Mortality") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFEBEE),
                        titleContentColor = Color(0xFFD32F2F),
                    ),
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
                            containerColor = Color(0xFFFFEBEE),
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Please provide accurate information for mortality tracking and analysis.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD32F2F),
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
                            text = "Mortality Details",
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

                        // Bird Selection
                        ExposedDropdownMenuBox(
                            expanded = showBirdDropdown,
                            onExpandedChange = { showBirdDropdown = it },
                        ) {
                            OutlinedTextField(
                                value = selectedBird,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Bird(s)") },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBirdDropdown)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Pets, contentDescription = null)
                                },
                            )

                            ExposedDropdownMenu(
                                expanded = showBirdDropdown,
                                onDismissRequest = { showBirdDropdown = false },
                            ) {
                                birds.forEach { bird ->
                                    DropdownMenuItem(
                                        text = { Text(bird) },
                                        onClick = {
                                            selectedBird = bird
                                            showBirdDropdown = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = mortalityCount,
                            onValueChange = { mortalityCount = it },
                            label = { Text("Number of Deaths") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(Icons.Default.Numbers, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cause of Death
                        ExposedDropdownMenuBox(
                            expanded = showCauseDropdown,
                            onExpandedChange = { showCauseDropdown = it },
                        ) {
                            OutlinedTextField(
                                value = causeOfDeath,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Cause of Death") },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCauseDropdown)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null)
                                },
                            )

                            ExposedDropdownMenu(
                                expanded = showCauseDropdown,
                                onDismissRequest = { showCauseDropdown = false },
                            ) {
                                causes.forEach { cause ->
                                    DropdownMenuItem(
                                        text = { Text(cause) },
                                        onClick = {
                                            causeOfDeath = cause
                                            showCauseDropdown = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedTextField(
                                value = dateOfDeath,
                                onValueChange = { dateOfDeath = it },
                                label = { Text("Date (DD/MM/YYYY)") },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                                },
                            )

                            OutlinedTextField(
                                value = timeOfDeath,
                                onValueChange = { timeOfDeath = it },
                                label = { Text("Time (HH:MM)") },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null)
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = comments,
                            onValueChange = { comments = it },
                            label = { Text("Comments/Additional Details") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null)
                            },
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
                            text = "Attachments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = hasAttachment,
                                onCheckedChange = { hasAttachment = it },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("I have photos or documents to attach")
                        }

                        if (hasAttachment) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { /* Handle photo/document upload */ },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Photos/Documents")
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
                            // Save mortality report
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedBatch.isNotBlank() && selectedBird.isNotBlank() && mortalityCount.isNotBlank(),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F),
                            ),
                    ) {
                        Icon(Icons.Default.Report, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Report")
                    }
                }
            }
        }
    }
}
