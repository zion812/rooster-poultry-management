// Enhanced Demo Screen with better rural simulation
@Composable
fun PhotoUploadComponentsDemoScreen(
    photoUploadService: PhotoUploadService,
    networkQualityManager: com.example.rooster.util.NetworkQualityManager,
) {
    val context = LocalContext.current
{{ ... }}
            // Simulate a more realistic rural network scenario
            networkQualityManager.simulateFluctuatingNetwork(context)

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

        Spacer(modifier = Modifier.height(16.dp))
{{ ... }}
