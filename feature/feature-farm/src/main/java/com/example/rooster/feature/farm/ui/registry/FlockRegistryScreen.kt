package com.example.rooster.feature.farm.ui.registry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rooster.feature.farm.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockRegistryScreen(
    farmId: String,
    onBack: () -> Unit,
    onError: (String) -> Unit,
    viewModel: FlockRegistryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(uiState.error) {
        uiState.error?.let { onError(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register New Flock") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.submitRegistration(farmId) },
                        enabled = uiState.canSubmit
                    ) {
                        Text("Submit")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Registry Type Selection
            item {
                RegistryTypeSelector(
                    selectedType = uiState.registryType,
                    onTypeSelected = viewModel::updateRegistryType
                )
            }

            // Age Group Selection
            item {
                AgeGroupSelector(
                    selectedAgeGroup = uiState.ageGroup,
                    onAgeGroupSelected = viewModel::updateAgeGroup
                )
            }

            // Basic Form Fields
            item {
                BasicRegistrationForm(
                    uiState = uiState,
                    onFieldUpdate = viewModel::updateField,
                    dateFormatter = dateFormatter
                )
            }

            // Verification Notice
            item {
                if (uiState.requiresVerification) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Verification Required",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This flock registration will require verification before being listed. Please ensure all required information and proofs are provided.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun RegistryTypeSelector(
    selectedType: RegistryType?,
    onTypeSelected: (RegistryType) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Registry Type",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(Modifier.selectableGroup()) {
                RegistryType.values().forEach { type ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (selectedType == type),
                                onClick = { onTypeSelected(type) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedType == type),
                            onClick = null
                        )
                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = when (type) {
                                    RegistryType.TRACEABLE -> "Traceable"
                                    RegistryType.NON_TRACEABLE -> "Non-Traceable"
                                }
                            )
                            Text(
                                text = when (type) {
                                    RegistryType.TRACEABLE -> "Full lineage tracking with complete documentation"
                                    RegistryType.NON_TRACEABLE -> "Basic registration with optional family history"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeGroupSelector(
    selectedAgeGroup: AgeGroup?,
    onAgeGroupSelected: (AgeGroup) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Age Group",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(Modifier.selectableGroup()) {
                AgeGroup.values().forEach { ageGroup ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (selectedAgeGroup == ageGroup),
                                onClick = { onAgeGroupSelected(ageGroup) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedAgeGroup == ageGroup),
                            onClick = null
                        )
                        Text(
                            text = when (ageGroup) {
                                AgeGroup.CHICKS -> "Chicks"
                                AgeGroup.WEEKS_0_5 -> "0 - 5 Weeks"
                                AgeGroup.WEEKS_5_5MONTHS -> "5 Weeks - 5 Months"
                                AgeGroup.MONTHS_5_12PLUS -> "5 - 12+ Months"
                                AgeGroup.UNKNOWN -> "Unknown"
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicRegistrationForm(
    uiState: FlockRegistryUiState,
    onFieldUpdate: (String, String) -> Unit,
    dateFormatter: SimpleDateFormat
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Registration Details",
                style = MaterialTheme.typography.titleMedium
            )

            // Basic Fields
            OutlinedTextField(
                value = uiState.breed ?: "",
                onValueChange = { onFieldUpdate("breed", it) },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.colorsText ?: "",
                onValueChange = { onFieldUpdate("colors", it) },
                label = { Text("Colors (comma separated)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Black, Red, White") }
            )

            // Weight & Height
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.weightText ?: "",
                    onValueChange = { onFieldUpdate("weight", it) },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.heightText ?: "",
                    onValueChange = { onFieldUpdate("height", it) },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Family Tree (for traceable)
            if (uiState.registryType == RegistryType.TRACEABLE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.fatherId ?: "",
                        onValueChange = { onFieldUpdate("fatherId", it) },
                        label = { Text("Father ID") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search or enter ID") }
                    )

                    OutlinedTextField(
                        value = uiState.motherId ?: "",
                        onValueChange = { onFieldUpdate("motherId", it) },
                        label = { Text("Mother ID") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search or enter ID") }
                    )
                }

                OutlinedTextField(
                    value = uiState.placeOfBirth ?: "",
                    onValueChange = { onFieldUpdate("placeOfBirth", it) },
                    label = { Text("Place of Birth") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Additional Fields
            OutlinedTextField(
                value = uiState.identification ?: "",
                onValueChange = { onFieldUpdate("identification", it) },
                label = { Text("Identification") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tag, Band, or ID Number") }
            )

            OutlinedTextField(
                value = uiState.specialty ?: "",
                onValueChange = { onFieldUpdate("specialty", it) },
                label = { Text("Specialty") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Fighting, Show, Breeding, etc.") }
            )

            // Proof Photos Section
            Text(
                text = "Proof Photos",
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle camera */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }

                Button(
                    onClick = { /* Handle gallery */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
            }

            if (uiState.proofPhotos.isNotEmpty()) {
                Text("${uiState.proofPhotos.size} photo(s) added")
            }
        }
    }
}
