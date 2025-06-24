package com.example.rooster.ui.marketplace

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.viewmodels.MarketplaceViewModel
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: MarketplaceViewModel = viewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Poultry") }
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Category options
    val categoryOptions =
        listOf(
            if (isTeluguMode) "కోడి" else "Poultry",
            if (isTeluguMode) "గుడ్లు" else "Eggs",
        )

    // Photo picker launchers
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                selectedPhotos = selectedPhotos + it
            }
        }

    val multiplePhotosPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                selectedPhotos = selectedPhotos + uris
            }
        }

    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "కొత్త లిస్టింగ్ సృష్టించండి" else "Create New Listing",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        text = if (isTeluguMode) "శీర్షిక *" else "Title *",
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank() && errorMessage != null,
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        text = if (isTeluguMode) "వివరణ *" else "Description *",
                    )
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                isError = description.isBlank() && errorMessage != null,
            )

            // Price Input
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = {
                    Text(
                        text = if (isTeluguMode) "ధర (₹) *" else "Price (₹) *",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = price.toDoubleOrNull() == null && errorMessage != null,
            )

            // Category Selection
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { },
                    label = {
                        Text(
                            text = if (isTeluguMode) "వర్గం" else "Category",
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryDropdownExpanded = false
                            },
                        )
                    }
                }
            }

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
                        text = if (isTeluguMode) "ఫోటోలను జోడించండి" else "Add Photos",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = if (isTeluguMode) "మీ ఉత్పత్తి యొక్క చిత్రాలను జోడించండి" else "Add photos of your product",
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
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTeluguMode) "గ్యాలరీ" else "Gallery")
                        }

                        OutlinedButton(
                            onClick = { multiplePhotosPickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTeluguMode) "అనేక ఫోటోలు" else "Multiple")
                        }
                    }

                    // Display selected photos
                    if (selectedPhotos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text =
                                if (isTeluguMode) {
                                    "ఎంచుకున్న ఫోటోలు (${selectedPhotos.size})"
                                } else {
                                    "Selected Photos (${selectedPhotos.size})"
                                },
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
                            itemsIndexed(selectedPhotos) { index, photoUri ->
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
                                            selectedPhotos =
                                                selectedPhotos.toMutableList().apply {
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
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            // Create Button
            Button(
                onClick = {
                    // Validate form
                    val priceValue = price.toDoubleOrNull()

                    when {
                        title.isBlank() -> {
                            errorMessage =
                                if (isTeluguMode) {
                                    "దయచేసి శీర్షికను నమోదు చేయండి"
                                } else {
                                    "Please enter a title"
                                }
                        }
                        description.isBlank() -> {
                            errorMessage =
                                if (isTeluguMode) {
                                    "దయచేసి వివరణను నమోదు చేయండి"
                                } else {
                                    "Please enter a description"
                                }
                        }
                        priceValue == null || priceValue <= 0 -> {
                            errorMessage =
                                if (isTeluguMode) {
                                    "దయచేసి సరైన ధరను నమోదు చేయండి"
                                } else {
                                    "Please enter a valid price"
                                }
                        }
                        else -> {
                            errorMessage = null
                            isLoading = true

                            coroutineScope.launch {
                                try {
                                    // Create Parse object
                                    val listing = ParseObject("Listings")
                                    listing.put("title", title)
                                    listing.put("description", description)
                                    listing.put("price", priceValue)
                                    listing.put("category", if (category == "కోడి" || category == "Poultry") "Poultry" else "Eggs")
                                    listing.put("userId", ParseUser.getCurrentUser())

                                    // Handle photo upload if available
                                    if (selectedPhotos.isNotEmpty()) {
                                        withContext(Dispatchers.IO) {
                                            val firstPhotoUri = selectedPhotos.first()
                                            val bitmap =
                                                MediaStore.Images.Media.getBitmap(
                                                    context.contentResolver,
                                                    firstPhotoUri,
                                                )
                                            val stream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                            val photoData = stream.toByteArray()
                                            val parseFile = ParseFile("photo.jpg", photoData)
                                            listing.put("photo", parseFile)
                                        }
                                    }

                                    // Save with offline support
                                    withContext(Dispatchers.IO) {
                                        listing.saveEventually()
                                    }

                                    isLoading = false
                                    showSuccess = true
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage =
                                        if (isTeluguMode) {
                                            "లిస్టింగ్ సృష్టించడంలో లోపం: ${e.message}"
                                        } else {
                                            "Error creating listing: ${e.message}"
                                        }
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isTeluguMode) "లిస్టింగ్ సృష్టించండి" else "Create Listing",
                )
            }
        }
    }

    // Success Dialog
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                navController.navigateUp()
            },
            title = {
                Text(if (isTeluguMode) "విజయం!" else "Success!")
            },
            text = {
                Text(
                    text =
                        if (isTeluguMode) {
                            "మీ లిస్టింగ్ విజయవంతంగా సృష్టించబడింది"
                        } else {
                            "Your listing has been created successfully"
                        },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccess = false
                        navController.navigateUp()
                    },
                ) {
                    Text(if (isTeluguMode) "సరే" else "OK")
                }
            },
        )
    }
}
