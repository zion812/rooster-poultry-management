package com.example.rooster.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.FarmerDashboardViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun CertificationRequestScreen(
    farmerId: String,
    vm: FarmerDashboardViewModel = viewModel(),
) {
    var docs by remember { mutableStateOf(listOf<String>()) }
    var isSubmitting by remember { mutableStateOf(false) }
    val requests by vm.kycRequests.collectAsState()

    // File picker launcher for documents
    val documentPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val newDocs = uris.map { it.toString() }
                docs = docs + newDocs
                FirebaseCrashlytics.getInstance()
                    .log("Documents selected for certification: ${uris.size} documents added, ${docs.size} total")
            }
        }

    // Single document picker for specific files
    val singleDocumentPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                docs = docs + it.toString()
                FirebaseCrashlytics.getInstance()
                    .log("Document selected for certification: ${docs.size} total documents")
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Certification Request", style = MaterialTheme.typography.titleLarge)

        Text(
            text = "Upload your KYC documents to get certified. Accepted formats: PDF, JPG, PNG",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Document Upload Section
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.Upload,
                    contentDescription = "Upload Documents",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Upload KYC Documents",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Select documents like ID proof, address proof, farm certificates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Document picker buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = { singleDocumentPickerLauncher.launch("*/*") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Single File")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Single File")
                    }

                    OutlinedButton(
                        onClick = { documentPickerLauncher.launch("*/*") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Multiple Files")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Multiple Files")
                    }
                }

                // Display selected documents
                if (docs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selected Documents (${docs.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.align(Alignment.Start),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    docs.forEachIndexed { index, docUri ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = "Document",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Document ${index + 1}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        docs =
                                            docs.toMutableList().apply {
                                                removeAt(index)
                                            }
                                        FirebaseCrashlytics.getInstance()
                                            .log("Document removed from certification: ${docs.size} documents remaining")
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove document",
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }

                        if (index < docs.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                isSubmitting = true
                vm.submitKYC(farmerId, docs)
                FirebaseCrashlytics.getInstance()
                    .log("KYC certification request submitted for farmer: $farmerId with ${docs.size} documents")
                isSubmitting = false
            },
            enabled = docs.isNotEmpty() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSubmitting) "Submitting..." else "Submit Request")
        }

        if (requests.isNotEmpty()) {
            Text(
                "Past Requests:",
                style = MaterialTheme.typography.titleMedium,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    requests.forEach { req ->
                        ListItem(
                            headlineContent = { Text("Request ID: ${req.requestId}") },
                            supportingContent = { Text("Status: ${req.status.name}") },
                        )
                        if (req != requests.last()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
