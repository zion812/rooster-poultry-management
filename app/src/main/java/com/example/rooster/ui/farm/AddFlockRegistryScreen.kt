package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlockRegistryScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    var flockType by remember { mutableStateOf("") }
    var selectedAgeGroup by remember { mutableStateOf("") }
    var showAgeGroupDialog by remember { mutableStateOf(false) }

    val ageGroups =
        listOf(
            "Chick/Chicks",
            "0 - 5 weeks",
            "5 weeks - 5 months",
            "5 months - 12 months+",
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Flock Registry") },
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
                Text(
                    text = "New Listing",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Text(
                    text = "Select Flock Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlockTypeCard(
                        title = "Traceable",
                        description = "Complete documentation with family tree, verification, and full traceability",
                        icon = Icons.Default.Verified,
                        isSelected = flockType == "Traceable",
                        onClick = { flockType = "Traceable" },
                    )

                    FlockTypeCard(
                        title = "Non-Traceable",
                        description = "Basic documentation with optional traceability features",
                        icon = Icons.AutoMirrored.Filled.Note,
                        isSelected = flockType == "Non-Traceable",
                        onClick = { flockType = "Non-Traceable" },
                    )
                }
            }

            if (flockType.isNotEmpty()) {
                item {
                    OutlinedTextField(
                        value = selectedAgeGroup,
                        onValueChange = { },
                        label = { Text("Age Group") },
                        placeholder = { Text("Select age group") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showAgeGroupDialog = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
                            }
                        },
                    )
                }

                if (selectedAgeGroup.isNotEmpty()) {
                    item {
                        Text(
                            text = "Required Fields for $flockType - $selectedAgeGroup",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    item {
                        RequiredFieldsSection(
                            flockType = flockType,
                            ageGroup = selectedAgeGroup,
                        )
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
                                    // Submit for verification
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Submit for Verification")
                            }
                        }
                    }
                }
            }
        }

        // Age Group Selection Dialog
        if (showAgeGroupDialog) {
            AlertDialog(
                onDismissRequest = { showAgeGroupDialog = false },
                title = { Text("Select Age Group") },
                text = {
                    LazyColumn {
                        items(ageGroups.size) { index ->
                            val ageGroup = ageGroups[index]
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = selectedAgeGroup == ageGroup,
                                            onClick = {
                                                selectedAgeGroup = ageGroup
                                                showAgeGroupDialog = false
                                            },
                                        )
                                        .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selectedAgeGroup == ageGroup,
                                    onClick = {
                                        selectedAgeGroup = ageGroup
                                        showAgeGroupDialog = false
                                    },
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(ageGroup)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAgeGroupDialog = false }) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
fun FlockTypeCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        border =
            if (isSelected) {
                androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                )
            } else {
                null
            },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun RequiredFieldsSection(
    flockType: String,
    ageGroup: String,
) {
    val requiredFields = getRequiredFields(flockType, ageGroup)
    val optionalFields = getOptionalFields(flockType, ageGroup)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (requiredFields.isNotEmpty()) {
                Text(
                    text = "Required Fields:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )

                requiredFields.forEach { field ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = field,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            if (optionalFields.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Optional Fields:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                optionalFields.forEach { field ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = field,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

fun getRequiredFields(
    flockType: String,
    ageGroup: String,
): List<String> {
    return when (flockType) {
        "Traceable" -> {
            when (ageGroup) {
                "Chick/Chicks" ->
                    listOf(
                        "Family tree",
                        "Place of birth",
                        "Date of birth",
                        "Proofs",
                        "Colors",
                        "Vaccination",
                        "Verification",
                    )

                "0 - 5 weeks" ->
                    listOf(
                        "Family tree",
                        "Place of birth",
                        "Date of birth",
                        "Proofs",
                        "Colors",
                        "Vaccination",
                        "Weight",
                        "Height",
                        "Gender",
                        "Identification",
                        "Verification",
                    )

                "5 weeks - 5 months", "5 months - 12 months+" ->
                    listOf(
                        "Family tree",
                        "Place of birth",
                        "Date of birth",
                        "Proofs",
                        "Colors",
                        "Vaccination",
                        "Weight",
                        "Height",
                        "Gender",
                        "Identification",
                        "Size",
                        "Specialty",
                        "Verification",
                    )

                else -> emptyList()
            }
        }

        "Non-Traceable" ->
            listOf(
                "Colors",
                "Weight",
                "Height",
                "Gender",
                "Identification",
                "Size",
                "Specialty",
                "Verification",
            )

        else -> emptyList()
    }
}

fun getOptionalFields(
    flockType: String,
    ageGroup: String,
): List<String> {
    return when (flockType) {
        "Non-Traceable" ->
            listOf(
                "Family tree",
                "Place of birth",
                "Date of birth",
                "Vaccination",
                "Proofs",
            )

        else -> emptyList()
    }
}
