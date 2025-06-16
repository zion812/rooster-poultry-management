package com.example.rooster

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.*
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.util.NetworkQualityManager
import com.example.rooster.worker.FeedbackUploadWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.parse.ParseUser
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// --- Data Models ---
data class FeedbackCategory(
    val id: String,
    val nameResId: Int,
    val icon: ImageVector,
    val descriptionResId: Int,
)

enum class FeedbackPriority(val id: String, val displayNameResId: Int, val color: Color) {
    ESSENTIAL("essential", R.string.priority_essential, Color(0xFFD32F2F)),
    IMPORTANT("important", R.string.priority_important, Color(0xFFFF5722)),
    OPTIONAL("optional", R.string.priority_optional, Color(0xFFFF9800)),
    LUXURY("luxury", R.string.priority_luxury, Color(0xFF4CAF50)),
}

// --- Feedback Categories ---
fun getFeedbackCategories(): List<FeedbackCategory> =
    listOf(
        FeedbackCategory(
            "ui_ux",
            R.string.category_ui_ux,
            Icons.Default.Palette,
            R.string.category_ui_ux_desc,
        ),
        FeedbackCategory(
            "fowl_management",
            R.string.category_fowl_management,
            Icons.Default.Pets,
            R.string.category_fowl_management_desc,
        ),
        FeedbackCategory(
            "marketplace",
            R.string.category_marketplace,
            Icons.Default.ShoppingCart,
            R.string.category_marketplace_desc,
        ),
        FeedbackCategory(
            "social_features",
            R.string.category_social_features,
            Icons.Default.Group,
            R.string.category_social_features_desc,
        ),
        FeedbackCategory(
            "cultural_events",
            R.string.category_cultural_events,
            Icons.Default.Festival,
            R.string.category_cultural_events_desc,
        ),
        FeedbackCategory(
            "performance",
            R.string.category_performance,
            Icons.Default.Speed,
            R.string.category_performance_desc,
        ),
        FeedbackCategory(
            "network_issues",
            R.string.category_network_issues,
            Icons.Default.NetworkCheck,
            R.string.category_network_issues_desc,
        ),
        FeedbackCategory(
            "translation",
            R.string.category_translation,
            Icons.Default.Language,
            R.string.category_translation_desc,
        ),
        FeedbackCategory(
            "general",
            R.string.category_general,
            Icons.Default.Feedback,
            R.string.category_general_desc,
        ),
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FeedbackScreen() {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<FeedbackCategory?>(null) }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackMessage by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(FeedbackPriority.OPTIONAL) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submissionResult by remember { mutableStateOf<String?>(null) }
    var networkQuality by remember { mutableStateOf(context.getString(R.string.network_quality_unknown)) }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val networkQualityManager = remember { NetworkQualityManager(context) }
    val photoUploadService = remember { PhotoUploadService(context, networkQualityManager) }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { selectedImageUris = selectedImageUris + it } },
        )
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var tempCameraUriHolder by remember { mutableStateOf<Uri?>(null) } // Use a different name to avoid confusion

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempCameraUriHolder?.let { uri -> // Use the holder here
                    selectedImageUris = selectedImageUris + uri
                }
            }
        }

    val coroutineScope = rememberCoroutineScope()
    val categories = getFeedbackCategories()
    val workManager = WorkManager.getInstance(context)

    LaunchedEffect(Unit) {
        networkQuality = context.getString(R.string.network_quality_fair)

        // This is a placeholder for observing actual upload results from PhotoUploadService
        // In a real app, PhotoUploadService would emit events for each upload's progress/completion.
        // For now, we'll assume uploads are enqueued and worker will handle them.
        // coroutineScope.launch {
        //     photoUploadService.uploadResults.collectLatest { result ->
        //         // Update UI based on upload status
        //     }
        // }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.uat_feedback_header)) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
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
            // Category Selection
            item {
                FeedbackSectionCard(title = stringResource(R.string.select_feedback_category)) {
                    Column(modifier = Modifier.selectableGroup()) {
                        categories.forEach { category ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = selectedCategory == category,
                                            onClick = { selectedCategory = category },
                                        )
                                        .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                )
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Column {
                                    Text(
                                        text = stringResource(category.nameResId),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        text = stringResource(category.descriptionResId),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Priority Selection
            item {
                FeedbackSectionCard(title = stringResource(R.string.select_priority)) {
                    Column(modifier = Modifier.selectableGroup()) {
                        FeedbackPriority.values().forEach { priorityOption ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = priority == priorityOption,
                                            onClick = { priority = priorityOption },
                                        )
                                        .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = priority == priorityOption,
                                    onClick = { priority = priorityOption },
                                )
                                Box(
                                    modifier =
                                        Modifier
                                            .size(12.dp)
                                            .background(
                                                priorityOption.color,
                                                androidx.compose.foundation.shape.CircleShape,
                                            ),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(priorityOption.displayNameResId),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }

            // Feedback Message
            item {
                FeedbackSectionCard(title = stringResource(R.string.feedback_message_title)) {
                    OutlinedTextField(
                        value = feedbackMessage,
                        onValueChange = { feedbackMessage = it },
                        label = { Text(stringResource(R.string.feedback_message_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8,
                        placeholder = { Text(stringResource(R.string.feedback_message_placeholder)) },
                    )
                }
            }

            // Star Rating
            item {
                FeedbackSectionCard(title = stringResource(R.string.rate_your_experience)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        (1..5).forEach { starNumber ->
                            IconButton(onClick = { rating = starNumber }) {
                                Icon(
                                    imageVector = if (starNumber <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Rate $starNumber stars",
                                    tint = if (starNumber <= rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text =
                            when (rating) {
                                1 -> stringResource(R.string.rating_very_poor)
                                2 -> stringResource(R.string.rating_poor)
                                3 -> stringResource(R.string.rating_average)
                                4 -> stringResource(R.string.rating_good)
                                5 -> stringResource(R.string.rating_excellent)
                                else -> ""
                            },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Photo Upload Section
            item {
                FeedbackSectionCard(title = stringResource(R.string.attach_screenshots_optional)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build(),
                            )
                        }) {
                            Icon(
                                Icons.Filled.PhotoLibrary,
                                contentDescription = stringResource(R.string.pick_from_gallery),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.gallery))
                        }
                        Button(onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                val newUri = context.createImageFileUri()
                                tempCameraUriHolder = newUri // Store it in the holder
                                cameraLauncher.launch(newUri) // Launch with the new URI
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = stringResource(R.string.take_photo),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.camera))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (selectedImageUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedImageUris) { uri ->
                                Box(modifier = Modifier.size(100.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = "Selected image",
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .clip(
                                                    androidx.compose.foundation.shape.RoundedCornerShape(
                                                        8.dp,
                                                    ),
                                                )
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline,
                                                    androidx.compose.foundation.shape.RoundedCornerShape(
                                                        8.dp,
                                                    ),
                                                ),
                                        contentScale = ContentScale.Crop,
                                    )
                                    IconButton(
                                        onClick = { selectedImageUris = selectedImageUris - uri },
                                        modifier =
                                            Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .background(
                                                    Color.Black.copy(alpha = 0.5f),
                                                    androidx.compose.foundation.shape.CircleShape,
                                                )
                                                .size(24.dp),
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = stringResource(R.string.remove_image),
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // System Information (Read-only)
            item {
                FeedbackSectionCard(title = stringResource(R.string.system_information)) {
                    val deviceInfo =
                        remember {
                            "${context.getString(R.string.device_model)}: ${Build.MANUFACTURER} ${Build.MODEL}\n" +
                                "${context.getString(R.string.android_version)}: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n" +
                                "${context.getString(R.string.app_version)}: ${getAppVersion(context)}"
                        }
                    Text(deviceInfo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${stringResource(R.string.network_quality)}: $networkQuality",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (selectedCategory != null && feedbackMessage.isNotBlank()) {
                            isSubmitting = true
                            coroutineScope.launch {
                                val photoUploadJobIds = mutableListOf<String>()
                                val successfullyEnqueuedPhotoIds = mutableListOf<String>()

                                if (selectedImageUris.isNotEmpty()) {
                                    selectedImageUris.forEach { uri ->
                                        val uploadJobId = UUID.randomUUID().toString()
                                        // Create SerializablePhotoUploadRequest for the service
                                        val request =
                                            SerializablePhotoUploadRequest(
                                                id = uploadJobId,
                                                uri = uri, // Pass Uri object directly
                                                fileName = "feedback_image_${System.currentTimeMillis()}.jpg", // Generate a filename
                                                // For feedback, we might not link to a specific ParseObject initially,
                                                // The worker will handle creating the UATFeedback object and then linking photos.
                                                // So, these can be placeholders or a special value if your service handles it.
                                                targetParseObjectId = "UATFeedback_Placeholder", // Or a specific ID if known
                                                targetClassName = "UATFeedback",
                                                targetField = "photoAttachments", // Example field name
                                                status = UploadStatus.PENDING, // Use imported enum
                                            )
                                        photoUploadService.enqueueUpload(request)
                                        successfullyEnqueuedPhotoIds.add(uploadJobId) // Store ID of enqueued item
                                    }
                                }
                                // Pass the job IDs of photos that were *actually* enqueued to the worker
                                val feedbackDataMap =
                                    mapOf(
                                        "category" to selectedCategory!!.id,
                                        "priority" to priority.id,
                                        "message" to feedbackMessage,
                                        "starRating" to rating,
                                        "deviceInfo" to "${Build.MANUFACTURER} ${Build.MODEL} (API ${Build.VERSION.SDK_INT})",
                                        "networkQuality" to networkQuality,
                                        "photoUploadJobIds" to successfullyEnqueuedPhotoIds, // Use this list
                                        "userId" to (
                                            ParseUser.getCurrentUser()?.objectId
                                                ?: "anonymous"
                                        ),
                                        "appVersion" to getAppVersion(context),
                                    )

                                val feedbackJson = com.google.gson.Gson().toJson(feedbackDataMap)

                                val uploadWorkRequest =
                                    OneTimeWorkRequestBuilder<FeedbackUploadWorker>()
                                        .setInputData(workDataOf(FeedbackUploadWorker.KEY_FEEDBACK_DATA_JSON to feedbackJson))
                                        .setConstraints(
                                            Constraints.Builder()
                                                .setRequiredNetworkType(NetworkType.CONNECTED).build(),
                                        )
                                        .setBackoffCriteria(
                                            BackoffPolicy.EXPONENTIAL,
                                            WorkRequest.MIN_BACKOFF_MILLIS, // Corrected constant
                                            TimeUnit.MILLISECONDS,
                                        )
                                        .build()
                                workManager.enqueueUniqueWork(
                                    "uploadFeedback_${UUID.randomUUID()}",
                                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                                    uploadWorkRequest,
                                )

                                submissionResult = context.getString(R.string.feedback_queued_success)
                                isSubmitting = false
                                // Clear form
                                selectedCategory = null
                                feedbackMessage = ""
                                rating = 5
                                priority = FeedbackPriority.OPTIONAL
                                selectedImageUris = emptyList()
                            }
                        } else {
                            submissionResult = context.getString(R.string.feedback_fill_required_fields)
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isSubmitting) stringResource(R.string.submitting) else stringResource(R.string.submit_feedback),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // Submission Result Message
            submissionResult?.let { result ->
                item {
                    val isSuccess = result == context.getString(R.string.feedback_queued_success)
                    ResultCard(message = result, isSuccess = isSuccess)
                    LaunchedEffect(result) {
                        kotlinx.coroutines.delay(5000)
                        submissionResult = null
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ResultCard(
    message: String,
    isSuccess: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFDECEA),
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                contentDescription = null,
                tint = if (isSuccess) Color(0xFF388E3C) else Color(0xFFD32F2F),
            )
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun Context.createImageFileUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"

    var determinedStorageDir: File? = null
    val externalPicturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    if (externalPicturesDir != null && externalPicturesDir.exists() && externalPicturesDir.canWrite()) {
        determinedStorageDir = externalPicturesDir
    } else {
        val internalFilesPicturesDir = File(filesDir, "Pictures")
        if (!internalFilesPicturesDir.exists()) {
            internalFilesPicturesDir.mkdirs()
        }
        if (internalFilesPicturesDir.exists() && internalFilesPicturesDir.canWrite()) {
            determinedStorageDir = internalFilesPicturesDir
        } else {
            val internalCachePicturesDir = File(cacheDir, "Pictures")
            if (!internalCachePicturesDir.exists()) {
                internalCachePicturesDir.mkdirs()
            }
            determinedStorageDir = internalCachePicturesDir
        }
    }

    val finalStorageDir = determinedStorageDir
    if (finalStorageDir == null || !finalStorageDir.exists() || !finalStorageDir.isDirectory || !finalStorageDir.canWrite()) {
        val externalPath = externalPicturesDir?.absolutePath ?: "null"
        val internalFilesPath = File(filesDir, "Pictures").absolutePath
        val internalCachePath = File(cacheDir, "Pictures").absolutePath
        com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().log(
            "Critical error: No writable storage directory found for image creation. External: $externalPath, InternalFiles: $internalFilesPath, InternalCache: $internalCachePath",
        )
        // As a very last resort, try to use filesDir directly if it exists and is writable
        // This path should ideally not be reached.
        if (filesDir.exists() && filesDir.isDirectory && filesDir.canWrite()) {
            val imageFile = File.createTempFile(imageFileName, ".jpg", filesDir)
            val authority = "${this.packageName}.provider"
            return androidx.core.content.FileProvider.getUriForFile(this, authority, imageFile)
        } else if (cacheDir.exists() && cacheDir.isDirectory && cacheDir.canWrite()) {
            val imageFile = File.createTempFile(imageFileName, ".jpg", cacheDir)
            val authority = "${this.packageName}.provider"
            return androidx.core.content.FileProvider.getUriForFile(this, authority, imageFile)
        } else {
            // If even filesDir and cacheDir are unusable, this is a severe issue.
            throw IOException("Unable to obtain any writable storage directory for image creation.")
        }
    }

    val imageFile =
        File.createTempFile(
            imageFileName,
            ".jpg",
            finalStorageDir!!, // Non-null assertion added as requested
        )

    val authority = "${this.packageName}.provider"
    return androidx.core.content.FileProvider.getUriForFile(this, authority, imageFile)
}
