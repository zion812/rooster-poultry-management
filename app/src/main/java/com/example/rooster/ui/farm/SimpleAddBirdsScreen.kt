package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAddBirdsScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    var birdCount by remember { mutableStateOf("") }
    var birdType by remember { mutableStateOf("") }
    var birdAge by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val birdTypes =
        if (isTeluguMode) {
            listOf("కోళ్లు", "రూస్టర్లు", "చిక్స్", "ఇతరులు")
        } else {
            listOf("Hens", "Roosters", "Chicks", "Others")
        }

    val ageOptions =
        if (isTeluguMode) {
            listOf("చిన్నవి (0-2 నెలలు)", "మధ్యమ (3-8 నెలలు)", "పెద్దవి (8+ నెలలు)")
        } else {
            listOf("Young (0-2 months)", "Medium (3-8 months)", "Adult (8+ months)")
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "కొత్త కోళ్లు చేర్చండి" else "Add New Birds",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF4CAF50),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Instructions Card
            item {
                InstructionCard(isTeluguMode)
            }

            // Bird Count Input
            item {
                SimpleInputCard(
                    title = if (isTeluguMode) "ఎన్ని కోళ్లు?" else "How many birds?",
                    hint = if (isTeluguMode) "సంఖ్య రాయండి" else "Enter number",
                    value = birdCount,
                    onValueChange = { birdCount = it },
                    keyboardType = KeyboardType.Number,
                    icon = Icons.Default.Numbers,
                )
            }

            // Bird Type Selection
            item {
                SimpleSelectionCard(
                    title = if (isTeluguMode) "ఏ రకం కోళ్లు?" else "What type of birds?",
                    options = birdTypes,
                    selectedOption = birdType,
                    onSelectionChange = { birdType = it },
                    icon = Icons.Default.Pets,
                )
            }

            // Age Selection
            item {
                SimpleSelectionCard(
                    title = if (isTeluguMode) "వయస్సు ఎంత?" else "What age?",
                    options = ageOptions,
                    selectedOption = birdAge,
                    onSelectionChange = { birdAge = it },
                    icon = Icons.Default.Schedule,
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (birdCount.isNotEmpty() && birdType.isNotEmpty() && birdAge.isNotEmpty()) {
                            showSuccess = true
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                        ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTeluguMode) "కోళ్లను చేర్చండి" else "Add Birds",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Help Section
            item {
                SimpleHelpSection(isTeluguMode)
            }
        }

        // Success Dialog
        if (showSuccess) {
            AlertDialog(
                onDismissRequest = {
                    showSuccess = false
                    navController.popBackStack()
                },
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF4CAF50),
                    )
                },
                title = {
                    Text(
                        text = if (isTeluguMode) "విజయవంతం!" else "Success!",
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Text(
                        text =
                            if (isTeluguMode) {
                                "$birdCount $birdType విజయవంతంగా చేర్చబడ్డాయి"
                            } else {
                                "$birdCount $birdType added successfully"
                            },
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccess = false
                            navController.popBackStack()
                        },
                    ) {
                        Text(if (isTeluguMode) "సరే" else "OK")
                    }
                },
            )
        }
    }
}

@Composable
fun InstructionCard(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E8),
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF4CAF50),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text =
                    if (isTeluguMode) {
                        "మీ కొత్త కోళ్ల గురించి సమాచారం ఇవ్వండి"
                    } else {
                        "Tell us about your new birds"
                    },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
            )
        }
    }
}

@Composable
fun SimpleInputCard(
    title: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(hint) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )
        }
    }
}

@Composable
fun SimpleSelectionCard(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelectionChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            options.forEach { option ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { onSelectionChange(option) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleHelpSection(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0),
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFFF9800),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "సలహా" else "Tip",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text =
                    if (isTeluguMode) {
                        "మీ కోళ్లను సరిగ్గా లెక్కించండి. తర్వాత వాటిని అమ్మడానికి లేదా చూడడానికి వీలవుతుంది."
                    } else {
                        "Count your birds carefully. This will help you sell them or track them later."
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE65100),
            )
        }
    }
}
