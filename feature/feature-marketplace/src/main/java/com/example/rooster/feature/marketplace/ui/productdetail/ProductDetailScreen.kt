package com.example.rooster.feature.marketplace.ui.productdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
import com.google.accompanist.pager.HorizontalPager // Assuming Accompanist Pager for image gallery
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalMaterial3Api::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAddToCart: (listing: ProductListing) -> Unit // Callback when "Add to Cart" is clicked
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is ProductDetailUiState.Success) {
                val listing = (uiState as ProductDetailUiState.Success).listing
                Button(
                    onClick = { onAddToCart(listing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add to Cart (₹${listing.price})")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProductDetailUiState.Success -> {
                    ProductDetailsContent(listing = state.listing)
                }
                is ProductDetailUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ProductDetailsContent(listing: ProductListing, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 72.dp) // Space for the bottom bar
    ) {
        // Image Gallery / Pager
        if (listing.imageUrls.isNotEmpty()) {
            val pagerState = rememberPagerState()
            HorizontalPager(
                count = listing.imageUrls.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Adjust aspect ratio as needed
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(listing.imageUrls[page])
                        // .placeholder(R.drawable.placeholder_image)
                        // .error(R.drawable.error_image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${listing.title} image ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // TODO: Add PagerIndicator if multiple images
        } else {
            // Placeholder if no images
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.5f).align(Alignment.CenterHorizontally)) {
                 Text("No Image Available", modifier = Modifier.align(Alignment.Center))
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${listing.price}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (listing.quantityAvailable <= 0) {
                Text("Out of Stock", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            } else if (listing.quantityAvailable < 5) { // Example low stock warning
                Text("Only ${listing.quantityAvailable} left in stock!", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(listing.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Details", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            DetailRow("Category:", listing.category.name.replace("_", " ").capitalize())
            listing.breed?.let { DetailRow("Breed:", it) }
            listing.ageInWeeks?.let { DetailRow("Age:", "$it weeks") }
            listing.weightInKg?.let { DetailRow("Weight:", "$it kg") }
            listing.locationCity?.let { city ->
                val location = listOfNotNull(city, listing.locationDistrict, listing.locationState).joinToString(", ")
                DetailRow("Location:", location)
            }
            listing.isOrganic?.let { DetailRow("Organic:", if (it) "Yes" else "No") }
            listing.isVaccinated?.let { DetailRow("Vaccinated:", if (it) "Yes" else "No") }
            DetailRow("Status:", listing.status.name.capitalize())

            // TODO: Seller Info section
            // TODO: Additional Properties section if map is not empty
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(120.dp))
        Text(value)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductDetailScreen_Success() {
    val sampleListing = ProductListing(
        id = "1",
        sellerId = "seller1",
        title = "Healthy Nattu Kodi Rooster - Prime Breed",
        description = "A very healthy and active Nattu Kodi rooster, perfect for breeding or special occasions. Raised organically over many months with great care and attention to detail. This bird has excellent lineage and will be a great addition to your farm or for any traditional event requiring a top-quality bird.",
        category = ProductCategory.LIVE_BIRD_ROOSTER,
        breed = "Nattu Kodi Special",
        ageInWeeks = 24,
        weightInKg = 3.1,
        price = 1500.00,
        quantityAvailable = 3,
        imageUrls = listOf("https://via.placeholder.com/600x400.png?text=Rooster+1", "https://via.placeholder.com/600x400.png?text=Rooster+2"),
        locationCity = "Vijayawada",
        locationDistrict = "Krishna",
        locationState = "Andhra Pradesh",
        isOrganic = true,
        isVaccinated = true,
        postedDateTimestamp = System.currentTimeMillis(),
        updatedDateTimestamp = System.currentTimeMillis(),
        status = ListingStatus.ACTIVE
    )
    MaterialTheme {
        // For previewing the full screen, you might need to mock the ViewModel interactions
        // or directly call ProductDetailsContent if the Scaffold is too complex for preview.
        // Here, just previewing the content part.
        ProductDetailsContent(listing = sampleListing)
    }
}
