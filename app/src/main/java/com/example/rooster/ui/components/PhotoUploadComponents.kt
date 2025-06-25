package com.example.rooster.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/*
// Enhanced Demo Screen with better rural simulation
@Composable
fun PhotoUploadComponentsDemoScreen(
    photoUploadService: PhotoUploadService,
    networkQualityManager: com.example.rooster.util.NetworkQualityManager,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Side effects in Compose should be handled in a controlled environment like LaunchedEffect.
        LaunchedEffect(key1 = networkQualityManager) {
            // Simulate a more realistic rural network scenario
            networkQualityManager.simulateFluctuatingNetwork(context)
        }

        // Simulate a new photo upload request
        Button(onClick = {
            val newUri = PhotoUriHelper.newUri(context)
            photoUploadService.uploadPhoto(
                uri = newUri,
                metadata = mapOf("type" to "vaccination", "birdId" to "B001"),
            )
        }) {
            Text("Simulate Upload")
        }

        // Simulate a failed upload
        Button(onClick = {
            val newUri = PhotoUriHelper.newUri(context)
            photoUploadService.uploadPhoto(
                uri = newUri,
                metadata = mapOf("type" to "vaccination", "birdId" to "FAIL"),
            )
        }) {
            Text("Simulate Fail")
        }

        // Simulate a slow upload
        Button(onClick = {
            val newUri = PhotoUriHelper.newUri(context)
            photoUploadService.uploadPhoto(
                uri = newUri,
                metadata = mapOf("type" to "vaccination", "birdId" to "SLOW"),
            )
        }) {
            Text("Simulate Slow")
        }
    }
}
*/

@Composable
fun PhotoPicker(onPhotoSelected: (Uri) -> Unit) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                uri?.let(onPhotoSelected)
            },
        )

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Pick a photo")
    }
}
