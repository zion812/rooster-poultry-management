package com.example.rooster.ui.marketplace

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
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
import com.google.firebase.crashlytics.FirebaseCrashlytics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    viewModel: MarketplaceViewModel = viewModel(),
) {
    val context = LocalContext.current

    // Form state
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fatherId by remember { mutableStateOf("") }
    var motherId by remember { mutableStateOf("") }
    var isBreeder by remember { mutableStateOf(false) }
    var isBloodlineVerified by remember { mutableStateOf(false) }
    // Auction state
    var isAuctionEnabled by remember { mutableStateOf(false) }
    var startingPrice by remember { mutableStateOf("") }
    var bidIncrement by remember { mutableStateOf("") }
    var auctionDurationHours by remember { mutableStateOf("") }

    // Photo state
    var selectedPhotos by remember { mutableStateOf<List<String>>(emptyList()) }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var breedDropdownExpanded by remember { mutableStateOf(false) }

    // Photo picker launchers
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                selectedPhotos = selectedPhotos + it.toString()
                FirebaseCrashlytics.getInstance()
                    .log("Photo selected for listing: ${selectedPhotos.size} total photos")
            }
        }

    val multiplePhotosPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val newPhotos = uris.map { it.toString() }
                selectedPhotos = selectedPhotos + newPhotos
                FirebaseCrashlytics.getInstance()
                    .log("Multiple photos selected for listing: ${uris.size} photos added, ${selectedPhotos.size} total")
            }
        }

    // Breed options
    val breedOptions =
        listOf(
            "Kadaknath",
            "Aseel",
            "Brahma",
            "Rhode Island Red",
            "Leghorn",
            "Cochin",
            "Desi",
            "Country Chicken",
        )

    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isTeluguMode) "కొత్త లిస్టింగ్" else "Create Listing")
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
            // Breed Selection
            ExposedDropdownMenuBox(
                expanded = breedDropdownExpanded,
                onExpandedChange = { breedDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text(if (isTeluguMode) "కోడి జాతి" else "Breed") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                )

                ExposedDropdownMenu(
                    expanded = breedDropdownExpanded,
                    onDismissRequest = { breedDropdownExpanded = false },
                ) {
                    breedOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                breed = option
                                breedDropdownExpanded = false
                            },
                        )
                    }
                }
            }

            // Age Input
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(if (isTeluguMode) "వయస్సు (వారాలలో)" else "Age (weeks)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            // Price Input
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(if (isTeluguMode) "ధర (₹)" else "Price (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (isTeluguMode) "వివరణ" else "Description") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            // Traceability Section
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = if (isTeluguMode) "వంశావళి సమాచారం" else "Lineage Information",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    OutlinedTextField(
                        value = fatherId,
                        onValueChange = { fatherId = it },
                        label = { Text(if (isTeluguMode) "తండ్రి ID" else "Father ID") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = motherId,
                        onValueChange = { motherId = it },
                        label = { Text(if (isTeluguMode) "తల్లి ID" else "Mother ID") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(if (isTeluguMode) "సంతానోత్పత్తికి అనుకూలమా?" else "Suitable for breeding?")
                        Checkbox(
                            checked = isBreeder,
                            onCheckedChange = { isBreeder = it },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(if (isTeluguMode) "వంశావళి ధృవీకరించబడిందా?" else "Bloodline verified?")
                        Checkbox(
                            checked = isBloodlineVerified,
                            onCheckedChange = { isBloodlineVerified = it },
                        )
                    }

                    // Auction enable toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(if (isTeluguMode) " వేలం ప్రారంభించండి" else "Enable Auction")
                        Checkbox(
                            checked = isAuctionEnabled,
                            onCheckedChange = { isAuctionEnabled = it },
                        )
                    }
                    if (isAuctionEnabled) {
                        // Starting price input
                        OutlinedTextField(
                            value = startingPrice,
                            onValueChange = { startingPrice = it },
                            label = { Text(if (isTeluguMode) "ప్రారంభ ధర (₹)" else "Starting Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        // Minimum increment input
                        OutlinedTextField(
                            value = bidIncrement,
                            onValueChange = { bidIncrement = it },
                            label = { Text(if (isTeluguMode) "నూన్యత పెంపు (₹)" else "Minimum Increment (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        // Auction duration input
                        OutlinedTextField(
                            value = auctionDurationHours,
                            onValueChange = { auctionDurationHours = it },
                            label = { Text(if (isTeluguMode) "వేలం వ్యవధి (గంటలు)" else "Auction Duration (hours)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
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
                        text = if (isTeluguMode) "ఫోటో జోడించండి" else "Add Photos",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = if (isTeluguMode) "మీ కోడి యొక్క చిత్రాలను జోడించండి" else "Add photos of your fowl",
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
                            Text(if (isTeluguMode) "గ్యాలరీ" else "Gallery")
                        }

                        OutlinedButton(
                            onClick = { multiplePhotosPickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Multiple")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTeluguMode) "అనేక ఫోటోలు" else "Multiple")
                        }
                    }

                    // Display selected photos
                    if (selectedPhotos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isTeluguMode) "ఎంచుకున్న ఫోటోలు (${selectedPhotos.size})" else "Selected Photos (${selectedPhotos.size})",
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
                                            FirebaseCrashlytics.getInstance()
                                                .log("Photo removed from listing: ${selectedPhotos.size} photos remaining")
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
                    val ageInt = age.toIntOrNull()
                    val priceDouble = price.toDoubleOrNull()

                    when {
                        breed.isBlank() -> {
                            errorMessage =
                                if (isTeluguMode) "కోడి జాతిని ఎంచుకోండి" else "Please select a breed"
                        }

                        ageInt == null || ageInt <= 0 -> {
                            errorMessage =
                                if (isTeluguMode) "సరైన వయస్సును నమోదు చేయండి" else "Please enter a valid age"
                        }

                        priceDouble == null || priceDouble <= 0 -> {
                            errorMessage =
                                if (isTeluguMode) "సరైన ధరను నమోదు చేయండి" else "Please enter a valid price"
                        }

                        description.isBlank() -> {
                            errorMessage =
                                if (isTeluguMode) "వివరణను నమోదు చేయండి" else "Please enter a description"
                        }

                        selectedPhotos.isEmpty() -> {
                            errorMessage =
                                if (isTeluguMode) "కనీసం ఒక ఫోటో జోడించండి" else "Please add at least one photo"
                        }

                        else -> {
                            errorMessage = null
                            isLoading = true

                            viewModel.createListing(
                                breed = breed,
                                age = ageInt,
                                price = priceDouble,
                                description = description,
                                fatherId = fatherId.ifBlank { "N/A" },
                                motherId = motherId.ifBlank { "N/A" },
                                isBreeder = isBreeder,
                                onSuccess = { listingId ->
                                    isLoading = false
                                    if (isAuctionEnabled) {
                                        // Navigate to auction creation for this listing
                                        navController.navigate("create_auction/$listingId")
                                    } else {
                                        showSuccess = true
                                        FirebaseCrashlytics.getInstance()
                                            .log("Listing created successfully: $breed with ${selectedPhotos.size} photos")
                                    }
                                },
                                onError = { error ->
                                    isLoading = false
                                    errorMessage = error
                                    FirebaseCrashlytics.getInstance()
                                        .recordException(Exception("Listing creation failed: $error"))
                                },
                            )
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
                Text(if (isTeluguMode) "లిస్టింగ్ సృష్టించండి" else "Create Listing")
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
                Text(if (isTeluguMode) "విజయవంతం!" else "Success!")
            },
            text = {
                Text(if (isTeluguMode) "మీ లిస్టింగ్ విజయవంతంగా సృష్టించబడింది" else "Your listing has been created successfully")
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
