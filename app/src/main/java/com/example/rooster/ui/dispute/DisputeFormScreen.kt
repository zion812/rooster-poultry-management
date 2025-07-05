package com.example.rooster.ui.dispute

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.data.model.DisputeRecord
import com.example.rooster.data.model.DisputeType
import com.example.rooster.data.repo.DisputeRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

@Composable
fun DisputeFormScreen(
    userId: String,
    onSubmitted: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var disputeType by remember { mutableStateOf("") }
    var relatedOrderId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var attachedPhotos by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val disputeTypes =
        listOf(
            "PAYMENT_PROBLEM" to "Payment not received or incorrect amount",
            "ORDER_ISSUE" to "Purchased fowl not delivered",
            "PRODUCT_DAMAGED" to "Fowl condition different from listing",
            "OTHER" to "Other dispute requiring resolution",
        )

    // Photo picker launcher for single photo
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                attachedPhotos = attachedPhotos + it.toString()
            }
        }

    // Multiple photos picker launcher
    val multiplePhotosPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val newPhotos = uris.map { it.toString() }
                attachedPhotos = attachedPhotos + newPhotos
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Submit Dispute",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "Please provide details about your dispute. Our team will review and respond within 24 hours.",
            style = MaterialTheme.typography.bodyMedium,
        )

        // Dispute Type Selection
        Text("Dispute Type", style = MaterialTheme.typography.titleMedium)
        Column {
            disputeTypes.forEach { (type, description) ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (disputeType == type),
                                onClick = { disputeType = type },
                            )
                            .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (disputeType == type),
                        onClick = { disputeType = type },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(type, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // Order ID Input
        OutlinedTextField(
            value = relatedOrderId,
            onValueChange = { relatedOrderId = it },
            label = { Text("Related Order/Transfer ID (if applicable)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Detailed Description") },
            placeholder = { Text("Please describe the issue in detail...") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            maxLines = 6,
        )

        // Contact Info
        OutlinedTextField(
            value = contactInfo,
            onValueChange = { contactInfo = it },
            label = { Text("Your Contact Information") },
            placeholder = { Text("Phone number or email for follow-up") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        // Photo Attachment Section
        Text("Attach Photos (Optional)", style = MaterialTheme.typography.titleMedium)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Gallery")
            }

            OutlinedButton(
                onClick = { multiplePhotosPickerLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Multiple")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Multiple")
            }
        }

        // Display attached photos
        if (attachedPhotos.isNotEmpty()) {
            Text("Attached Photos", style = MaterialTheme.typography.titleSmall)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(attachedPhotos) { index, photoUri ->
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier.padding(2.dp),
                    ) {
                        Card(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(photoUri),
                                contentDescription = "Attached photo ${index + 1}",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        IconButton(
                            onClick = {
                                attachedPhotos =
                                    attachedPhotos.toMutableList().apply {
                                        removeAt(index)
                                    }
                            },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }

        // Error Message
        errorMessage?.let { error ->
            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (disputeType.isBlank() || description.isBlank() || contactInfo.isBlank()) {
                    errorMessage = "Please complete all required fields"
                    return@Button
                }

                isSubmitting = true
                errorMessage = null

                scope.launch {
                    try {
                        val disputeRecord =
                            DisputeRecord(
                                userId = userId,
                                type = DisputeType.valueOf(disputeType),
                                message = description,
                                relatedOrderId = relatedOrderId.takeIf { it.isNotBlank() },
                                mediaUrls = attachedPhotos,
                            )

                        DisputeRepository.submitDispute(
                            record = disputeRecord,
                            onSuccess = { showSuccess = true },
                            onError = { errorMessage = "Failed to submit dispute" },
                        )
                    } catch (e: Exception) {
                        errorMessage = "Failed to submit dispute: ${e.message}"
                        FirebaseCrashlytics.getInstance().recordException(e)
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            enabled = disputeType.isNotBlank() && description.isNotBlank() && contactInfo.isNotBlank() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isSubmitting) "Submitting..." else "Submit Dispute")
        }
    }

    // Success Dialog
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                onSubmitted()
            },
            title = { Text("Dispute Submitted") },
            text = { Text("Your dispute has been submitted successfully. Our team will review it and respond within 24 hours.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onSubmitted()
                }) {
                    Text("OK")
                }
            },
        )
    }
}
