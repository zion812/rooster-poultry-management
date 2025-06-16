package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBatchScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    var batchName by remember { mutableStateOf("") }
    var batchSize by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var dateAcquired by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var batchType by remember { mutableStateOf("Broiler") }
    var notes by remember { mutableStateOf("") }

    val batchTypes = listOf("Broiler", "Layer", "Breeder", "Indigenous")
    var showTypeDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Batch") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Batch Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = batchName,
                            onValueChange = { batchName = it },
                            label = { Text("Batch Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = batchSize,
                            onValueChange = { batchSize = it },
                            label = { Text("Batch Size (Number of Birds)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(Icons.Default.Numbers, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Batch Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = showTypeDropdown,
                            onExpandedChange = { showTypeDropdown = it },
                        ) {
                            OutlinedTextField(
                                value = batchType,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Batch Type") },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Category, contentDescription = null)
                                },
                            )

                            ExposedDropdownMenu(
                                expanded = showTypeDropdown,
                                onDismissRequest = { showTypeDropdown = false },
                            ) {
                                batchTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            batchType = type
                                            showTypeDropdown = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = breed,
                            onValueChange = { breed = it },
                            label = { Text("Breed") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Pets, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = dateAcquired,
                            onValueChange = { dateAcquired = it },
                            label = { Text("Date Acquired (DD/MM/YYYY)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = supplier,
                            onValueChange = { supplier = it },
                            label = { Text("Supplier/Source") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Business, contentDescription = null)
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Additional Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            leadingIcon = {
                                Icon(Icons.Filled.Note, contentDescription = null)
                            },
                        )
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
                            // Save batch logic here
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = batchName.isNotBlank() && batchSize.isNotBlank(),
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Batch")
                    }
                }
            }
        }
    }
}
