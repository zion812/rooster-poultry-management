package com.example.rooster.feature.marketplace.ui.createlisting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    viewModel: CreateListingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onListingCreatedSuccessfully: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.formState.collectLatest { state ->
            if (state.submissionSuccess) {
                snackbarHostState.showSnackbar("Listing created successfully!")
                onListingCreatedSuccessfully() // Navigate away or reset form
            } else if (state.submissionError != null) {
                snackbarHostState.showSnackbar("Error: ${state.submissionError}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Listing") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = formState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                isError = formState.submissionError?.contains("Title", ignoreCase = true) == true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )
            Spacer(modifier = Modifier.height(8.dp))

            CategorySelector(
                selectedCategory = formState.category,
                onCategorySelected = viewModel::onCategoryChange
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fields specific to poultry, could be shown conditionally based on category
            OutlinedTextField(
                value = formState.breed,
                onValueChange = viewModel::onBreedChange,
                label = { Text("Breed (e.g., Nattu Kodi, Aseel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = formState.ageInWeeks,
                    onValueChange = viewModel::onAgeChange,
                    label = { Text("Age (weeks)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = formState.weightInKg,
                    onValueChange = viewModel::onWeightChange,
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = formState.price,
                    onValueChange = viewModel::onPriceChange,
                    label = { Text("Price (INR)*") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    isError = formState.submissionError?.contains("Price", ignoreCase = true) == true
                )
                OutlinedTextField(
                    value = formState.quantityAvailable,
                    onValueChange = viewModel::onQuantityChange,
                    label = { Text("Quantity*") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    isError = formState.submissionError?.contains("Quantity", ignoreCase = true) == true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.locationCity,
                onValueChange = viewModel::onLocationCityChange,
                label = { Text("City/Town") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.locationDistrict,
                onValueChange = viewModel::onLocationDistrictChange,
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(16.dp))

            ImageSelectionSection(
                selectedImageUris = formState.imageUris,
                onImagesSelected = viewModel::onImagesSelected,
                onRemoveImage = viewModel::removeImage
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submitListing() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !formState.isSubmitting
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit Listing")
                }
            }
            if (formState.submissionError != null && !formState.isSubmitting) {
                 Text(
                    text = formState.submissionError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selectedCategory: ProductCategory,
    onCategorySelected: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = ProductCategory.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory.name.replace("_", " ").capitalizeWords(),
            onValueChange = {}, // Not directly editable
            readOnly = true,
            label = { Text("Category*") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name.replace("_", " ").capitalizeWords()) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Helper to capitalize words in a string for display
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
    word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 1000)
@Composable
fun PreviewCreateListingScreen() {
    MaterialTheme {
        CreateListingScreen(onNavigateBack = {}, onListingCreatedSuccessfully = {})
    }
}

@Composable
fun ImageSelectionSection(
    selectedImageUris: List<android.net.Uri>,
    onImagesSelected: (List<android.net.Uri>) -> Unit,
    onRemoveImage: (android.net.Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<android.net.Uri> ->
        if (uris.isNotEmpty()) {
            onImagesSelected(uris)
        }
    }

    Column(modifier = modifier) {
        Text("Select Images (Max 5)", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Add Images")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Choose Images")
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUris.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedImageUris) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { onRemoveImage(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove image", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        } else {
            Text(
                "No images selected.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
