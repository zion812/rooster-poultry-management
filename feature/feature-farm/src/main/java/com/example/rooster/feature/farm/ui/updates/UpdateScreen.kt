package com.example.rooster.feature.farm.ui.updates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rooster.feature.farm.domain.model.UpdateType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    fowlId: String,
    viewModel: UpdateViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(fowlId) { viewModel.loadUpdates(fowlId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Updates", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { showDialog = true }) { Text("Add Update") }
        Spacer(Modifier.height(8.dp))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(records) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${record.type.name} - ${sdf.format(record.date)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(record.details)
                        }
                        IconButton(onClick = { viewModel.removeUpdate(record.id, fowlId) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var selectedType by remember { mutableStateOf(UpdateType.CHICKS) }
        var date by remember { mutableStateOf(Date()) }
        var details by remember { mutableStateOf("") }
        var attachment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Update") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // TODO: Type dropdown
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = attachment,
                        onValueChange = { attachment = it },
                        label = { Text("Attachment URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addUpdate(fowlId, selectedType, date, details, attachment)
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}