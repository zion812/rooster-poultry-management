package com.example.rooster.ui.vet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.models.VetConsultationRequest
import com.example.rooster.viewmodel.VetConsultationViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun VetConsultationScreen(
    vm: VetConsultationViewModel = viewModel(),
    onRequestSubmitted: (String?) -> Unit, // Changed to String?
) {
    var animalId by remember { mutableStateOf(TextFieldValue("")) }
    var issueDesc by remember { mutableStateOf(TextFieldValue("")) }
    var preferredDate by remember { mutableStateOf(TextFieldValue("")) }
    var photos by remember { mutableStateOf(listOf<String>()) }
    val isLoading by vm.isSubmitting.collectAsState()
    val error by vm.error.collectAsState() // Observe error state
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message in Snackbar
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Long,
                )
                vm.clearError() // Clear error after showing
            }
        }
    }

    // Photo picker launchers
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                photos = photos + it.toString()
                FirebaseCrashlytics.getInstance()
                    .log("Photo selected for vet consultation: ${photos.size} total photos")
            }
        }

    val multiplePhotosPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val newPhotos = uris.map { it.toString() }
                photos = photos + newPhotos
                FirebaseCrashlytics.getInstance()
                    .log("Multiple photos selected for vet consultation: ${uris.size} photos added, ${photos.size} total")
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
        Text("Request Vet Consultation", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = animalId,
            onValueChange = { animalId = it },
            label = { Text("Animal ID") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = issueDesc,
            onValueChange = { issueDesc = it },
            label = { Text("Issue Description") },
            placeholder = { Text("Describe the health issue or symptoms in detail...") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            maxLines = 6,
        )

        OutlinedTextField(
            value = preferredDate,
            onValueChange = { preferredDate = it },
            label = { Text("Preferred Date") },
            placeholder = { Text("e.g., 2025-02-01 or ASAP") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Photo Upload Section
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
                    Icons.Default.PhotoCamera,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add Photos",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Photos help the vet better understand the issue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Photo picker buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = { singlePhotoPickerLauncher.launch("image/*") },
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

                // Display selected photos
                if (photos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selected Photos (${photos.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.align(Alignment.Start),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        itemsIndexed(photos) { index, photoUri ->
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
                                        contentDescription = "Selected photo ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        photos =
                                            photos.toMutableList().apply {
                                                removeAt(index)
                                            }
                                        FirebaseCrashlytics.getInstance()
                                            .log("Photo removed from vet consultation: ${photos.size} photos remaining")
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
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val req =
                    VetConsultationRequest(
                        id = System.currentTimeMillis().toString(), // Consider generating ID in ViewModel or Repository
                        animalId = animalId.text,
                        issueDescription = issueDesc.text,
                        photoUrls = photos,
                        // Pass the actual text input for now. ViewModel/Repository should parse/validate.
                        // TODO: Replace preferredDate text field with a DatePickerDialog for better UX.
                        preferredDateString = preferredDate.text, // New field in model, or handle parsing here/VM
                        preferredDate = 0L, // Or handle parsing preferredDate.text to Long/Date
                    )
                vm.submitRequest(req) { reqId ->
                    if (reqId != null) {
                        FirebaseCrashlytics.getInstance()
                            .log("Vet consultation request submitted: $reqId with ${photos.size} photos")
                        // Potentially show a success snackbar here too
                        scope.launch {
                            snackbarHostState.showSnackbar("Request submitted successfully!", SnackbarDuration.Short)
                        }
                    }
                    // onRequestSubmitted will be called by the screen that hosts this,
                    // after this composable potentially navigates away or shows success.
                    // Or, if it's just for logging/side-effect:
                    onRequestSubmitted(reqId)
                }
            },
            enabled = !isLoading && animalId.text.isNotBlank() && issueDesc.text.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "Submitting..." else "Submit Request")
        }
    }
}
