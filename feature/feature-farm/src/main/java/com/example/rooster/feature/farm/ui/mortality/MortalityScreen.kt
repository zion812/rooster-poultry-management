package com.example.rooster.feature.farm.ui.mortality

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rooster.feature.farm.domain.model.MortalityRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortalityScreen(
    fowlId: String,
    onBack: () -> Unit,
    onError: (String) -> Unit,
    viewModel: MortalityViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(fowlId) { viewModel.loadMortality(fowlId) }
    LaunchedEffect(error) {
        error?.let {
            onError(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("మరణ రికార్డులు", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "వెనుకకు")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {

            // Action button
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("కొత్త రికార్డు జోడించండి")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Records list or empty state
            if (records.isEmpty() && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "రికార్డులు లేవు",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ఇంకా ఎలాంటి మరణ రికార్డులు జోడించలేదు",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(records) { record ->
                        MortalityRecordCard(
                            record = record,
                            onDelete = { viewModel.removeRecord(record.id, fowlId) },
                            isLoading = isLoading,
                            sdf = sdf
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        MortalityRecordDialog(
            onDismiss = { showDialog = false },
            onSave = { cause, description, attachment ->
                viewModel.addRecord(fowlId, cause, description, attachment)
                showDialog = false
            },
            isLoading = isLoading
        )
    }
}

@Composable
private fun MortalityRecordCard(
    record: MortalityRecord,
    onDelete: () -> Unit,
    isLoading: Boolean,
    sdf: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                // Date
                Text(
                    text = sdf.format(record.recordedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Cause
                Text(
                    text = "కారణం: ${record.cause}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )

                // Description if available
                record.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Weight if available
                record.weight?.let { weight ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "బరువు: ${weight}kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "తొలగించు",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun MortalityRecordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    isLoading: Boolean
) {
    var cause by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var attachment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                "కొత్త మరణ రికార్డు",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = cause,
                    onValueChange = { cause = it },
                    label = { Text("మరణ కారణం *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = cause.isBlank(),
                    supportingText = if (cause.isBlank()) {
                        { Text("కారణం తప్పనిసరి", color = MaterialTheme.colorScheme.error) }
                    } else null
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("వివరణ") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = attachment,
                    onValueChange = { attachment = it },
                    label = { Text("ఫోటో URL (ఐచ్ఛిక)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(cause, description, attachment) },
                enabled = !isLoading && cause.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("సేవ్ చేయండి")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("రద్దు చేయండి")
            }
        }
    )
}
