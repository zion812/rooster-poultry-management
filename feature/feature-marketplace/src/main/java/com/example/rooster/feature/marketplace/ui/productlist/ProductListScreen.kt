package com.example.rooster.feature.marketplace.ui.productlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
 jules/arch-assessment-1
import androidx.compose.foundation.lazy.grid.* // Import all from grid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // For detecting scroll to end
=======
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
 main
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel(),
    onProductClick: (listingId: String) -> Unit,
    onNavigateToCreateListing: () -> Unit // For a FAB or button
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
 jules/arch-assessment-1
            // TODO: Implement actual SearchBar and Category Filter UI that calls ViewModel methods
            TopAppBar(
                title = { Text( "Marketplace" ) },
                actions = {
                    // Placeholder for future refresh button
                    // IconButton(onClick = { viewModel.refreshListings() }) {
                    //     Icon(Icons.Filled.Refresh, "Refresh")
                    // }
                }
            )
=======
            TopAppBar(title = { Text("Marketplace") })
            // TODO: Add SearchBar and Category filter options here
 main
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateListing) {
                Icon(Icons.Filled.Add, contentDescription = "Create Listing")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
 jules/arch-assessment-1
                is ProductListUiState.InitialLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProductListUiState.Success -> {
                    if (state.listings.isEmpty() && !state.isLoadingMore) {
                        Text(
                            text = "No products found.",
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        ProductGrid(
                            listings = state.listings,
                            onProductClick = onProductClick,
                            isLoadingMore = state.isLoadingMore,
                            canLoadMore = state.canLoadMore,
                            onLoadMore = viewModel::loadMoreProductListings
                        )
                    }
                }
                is ProductListUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshListings() }) {
                            Text("Retry")
                        }
                    }
=======
                is ProductListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProductListUiState.Success -> {
                    if (state.listings.isEmpty()) {
                        Text(
                            text = "No products found.",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        ProductGrid(listings = state.listings, onProductClick = onProductClick)
                    }
                }
                is ProductListUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
 main
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    listings: List<ProductListing>,
    onProductClick: (listingId: String) -> Unit,
 jules/arch-assessment-1
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        state = gridState,
=======
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
 main
        columns = GridCells.Fixed(2), // Or Adaptive(minSize = 160.dp)
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(listings, key = { it.id }) { listing ->
            ProductListItem(listing = listing, onProductClick = onProductClick)
        }
 jules/arch-assessment-1

        if (isLoadingMore) {
            item(span = { GridItemSpan(2) }) { // Span across all columns
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Effect to trigger load more when near the end of the list
    LaunchedEffect(gridState, listings, isLoadingMore, canLoadMore) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: -1
                val totalItems = gridState.layoutInfo.totalItemsCount
                // Trigger load more if near the end, not already loading, and can load more
                if (lastVisibleItemIndex >= totalItems - 5 && totalItems > 0 && !isLoadingMore && canLoadMore) {
                    onLoadMore()
                }
            }
=======
 main
    }
}

@Composable
fun ProductListItem(
    listing: ProductListing,
    onProductClick: (listingId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(listing.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.imageUrls.firstOrNull())
 jules/arch-assessment-1
                        .placeholder(com.example.rooster.R.drawable.placeholder_image) // Assuming R is app level
                        .error(com.example.rooster.R.drawable.error_image) // Assuming R is app level
                    .crossfade(true)
                    .build(),
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Square image
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${listing.price}", // Assuming INR
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listing.category.name.replace("_", " ").lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // TODO: Add seller info, location if needed for list item
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductListItem() {
    val sampleListing = ProductListing(
        id = "1",
        sellerId = "seller1",
        title = "Healthy Nattu Kodi Rooster - Prime Breed",
        description = "A very healthy and active Nattu Kodi rooster, perfect for breeding or special occasions. Raised organically.",
        category = ProductCategory.LIVE_BIRD_ROOSTER,
        breed = "Nattu Kodi Special",
        ageInWeeks = 20,
        weightInKg = 2.5,
        price = 1200.00,
        quantityAvailable = 5,
        imageUrls = listOf("https://via.placeholder.com/300.png?text=Rooster+Image"),
        locationCity = "Vijayawada",
        locationDistrict = "Krishna",
        postedDateTimestamp = System.currentTimeMillis(),
        updatedDateTimestamp = System.currentTimeMillis(),
        status = ListingStatus.ACTIVE
    )
    MaterialTheme { // Wrap with MaterialTheme for preview
        ProductListItem(listing = sampleListing, onProductClick = {})
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun PreviewProductListScreen_Success() {
    val sampleListings = List(6) { index -> ProductListing(
        id = "$index",
        sellerId = "seller$index",
        title = "Sample Rooster $index - Special Offer Today Only Live",
        description = "Description for rooster $index",
        category = if (index % 2 == 0) ProductCategory.LIVE_BIRD_ROOSTER else ProductCategory.LIVE_BIRD_CHICKEN,
        price = 500.0 + (index * 100),
        quantityAvailable = 10,
        imageUrls = listOf("https://via.placeholder.com/300.png?text=Rooster+$index"),
        postedDateTimestamp = System.currentTimeMillis(),
        updatedDateTimestamp = System.currentTimeMillis(),
        status = ListingStatus.ACTIVE,
        locationCity = "Guntur",
        locationDistrict = "Guntur"
    )}
    MaterialTheme {
         ProductGrid(
             listings = sampleListings,
             onProductClick = {},
             isLoadingMore = false,
             canLoadMore = true,
             onLoadMore = {}
        )
    }
}
// TODO: Add Previews for Loading and Error states if needed, and for the full ProductListScreen
// For full screen preview, you might need a mock ViewModel or pass state directly.
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    listings: List<ProductListing>,
    onProductClick: (listingId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Or Adaptive(minSize = 160.dp)
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(listings, key = { it.id }) { listing ->
            ProductListItem(listing = listing, onProductClick = onProductClick)
        }
    }
}

@Composable
fun ProductListItem(
    listing: ProductListing,
    onProductClick: (listingId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(listing.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.imageUrls.firstOrNull())
                        .placeholder(com.example.rooster.R.drawable.placeholder_image) // Assuming R is app level
                        .error(com.example.rooster.R.drawable.error_image) // Assuming R is app level
=======
 jules/arch-assessment-1
                        .placeholder(com.example.rooster.R.drawable.placeholder_image) // Assuming R is app level
                        .error(com.example.rooster.R.drawable.error_image) // Assuming R is app level
=======
 jules/arch-assessment-1
                        .placeholder(com.example.rooster.R.drawable.placeholder_image) // Assuming R is app level
                        .error(com.example.rooster.R.drawable.error_image) // Assuming R is app level
=======
                    // TODO: Add placeholder and error drawables from core module
                    // .placeholder(R.drawable.placeholder_image)
                    // .error(R.drawable.error_image)
 main
 main
 main
                    .crossfade(true)
                    .build(),
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Square image
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${listing.price}", // Assuming INR
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listing.category.name.replace("_", " ").lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // TODO: Add seller info, location if needed for list item
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductListItem() {
    val sampleListing = ProductListing(
        id = "1",
        sellerId = "seller1",
        title = "Healthy Nattu Kodi Rooster - Prime Breed",
        description = "A very healthy and active Nattu Kodi rooster, perfect for breeding or special occasions. Raised organically.",
        category = ProductCategory.LIVE_BIRD_ROOSTER,
        breed = "Nattu Kodi",
        ageInWeeks = 20,
        weightInKg = 2.5,
        price = 1200.00,
        quantityAvailable = 5,
        imageUrls = listOf("https://via.placeholder.com/300.png?text=Rooster+Image"),
        locationCity = "Vijayawada",
        locationDistrict = "Krishna",
        postedDateTimestamp = System.currentTimeMillis(),
        updatedDateTimestamp = System.currentTimeMillis(),
        status = ListingStatus.ACTIVE
    )
    MaterialTheme { // Wrap with MaterialTheme for preview
        ProductListItem(listing = sampleListing, onProductClick = {})
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun PreviewProductListScreen_Success() {
    val sampleListings = List(6) { index -> ProductListing(
        id = "$index",
        sellerId = "seller$index",
        title = "Sample Rooster $index - Special Offer Today Only Live",
        description = "Description for rooster $index",
        category = if (index % 2 == 0) ProductCategory.LIVE_BIRD_ROOSTER else ProductCategory.LIVE_BIRD_CHICKEN,
        price = 500.0 + (index * 100),
        quantityAvailable = 10,
        imageUrls = listOf("https://via.placeholder.com/300.png?text=Rooster+$index"),
        postedDateTimestamp = System.currentTimeMillis(),
        updatedDateTimestamp = System.currentTimeMillis(),
        status = ListingStatus.ACTIVE,
        locationCity = "Guntur",
        locationDistrict = "Guntur"
    )}
    MaterialTheme {
         ProductGrid(listings = sampleListings, onProductClick = {})
    }
}
// TODO: Add Previews for Loading and Error states if needed, and for the full ProductListScreen
// For full screen preview, you might need a mock ViewModel or pass state directly.
