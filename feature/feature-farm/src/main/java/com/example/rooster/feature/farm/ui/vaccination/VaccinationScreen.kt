package com.example.rooster.feature.farm.ui.vaccination

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VaccinationScreen(
    fowlId: String,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(fowlId) {
        viewModel.loadRecords(fowlId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Vaccination Records",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(records) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = record.vaccineName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = sdf.format(record.recordedAt),
                            style = MaterialTheme.typography.bodySmall
                        )
                        record.notes?.let { notes ->
                            Text(
                                text = "Notes: $notes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        record.dosage?.let { dosage ->
                            Text(
                                text = "Dosage: $dosage",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        if (records.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No vaccination records found")
            }
        }
    }
}
