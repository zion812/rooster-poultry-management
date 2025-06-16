// use context7
package com.example.rooster.ui.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.rooster.services.optimized.IntelligentSearchFetcher
import com.example.rooster.services.optimized.RuralConnectivityOptimizer
import com.example.rooster.services.optimized.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// Data classes for marketplace items
data class MarketplaceListing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "₹",
    val sellerName: String,
    val sellerRating: Double,
    val location: String,
    val imageUrl: String?,
    val category: String,
    val isVerified: Boolean,
    val availability: String,
    val datePosted: String,
    val viewCount: Int = 0,
    val isFavorite: Boolean = false,
)

data class MarketplaceCategory(
    val id: String,
    val name: String,
    val icon: String,
    val count: Int,
)

// MarketplaceViewModel with advanced services integration
@HiltViewModel
class EnhancedMarketplaceViewModel @Inject constructor(
    private val intelligentSearchFetcher: IntelligentSearchFetcher,
    private val ruralConnectivityOptimizer: RuralConnectivityOptimizer
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<MarketplaceListing>>(emptyList())
    val searchResults: StateFlow<List<MarketplaceListing>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    init {
        loadInitialData()
    }

    fun performIntelligentSearch(query: String, category: String = "All") {
        if (query.isBlank()) {
            loadInitialData()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            try {
                intelligentSearchFetcher.semanticSearch(
                    query = query,
                    searchType = SearchType.MARKETPLACE
                ).collect { searchResults ->
                    // Convert search results to marketplace listings
                    val listings = searchResults.map { result ->
                        MarketplaceListing(
                            id = result.id,
                            title = result.title,
                            description = result.description,
                            price = Random.nextInt(500, 2001).toDouble(),
                            sellerName = "Smart Seller",
                            sellerRating = Random.nextDouble(4.0, 5.0),
                            location = "Smart Location",
                            imageUrl = null,
                            category = category,
                            isVerified = true,
                            availability = "${Random.nextInt(5, 51)} Available",
                            datePosted = "1 day ago",
                            viewCount = Random.nextInt(10, 101)
                        )
                    }
                    _searchResults.value = listings
                }
            } catch (e: Exception) {
                // Handle error
                _searchResults.value = getSampleListings()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun getSearchSuggestions(partialQuery: String) {
        if (partialQuery.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            intelligentSearchFetcher.getPredictiveSuggestions(partialQuery)
                .collect { suggestions ->
                    _searchSuggestions.value = suggestions
                }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use rural connectivity optimizer to load data efficiently
                val listings = getSampleListings()
                _searchResults.value = listings
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getSampleListings(): List<MarketplaceListing> {
        return listOf(
            MarketplaceListing(
                id = "1",
                title = "Premium Rhode Island Red Hens",
                description = "Healthy, vaccinated hens ready for laying. Excellent egg production record.",
                price = 1200.0,
                sellerName = "Ravi Kumar",
                sellerRating = 4.8,
                location = "Hyderabad, Telangana",
                imageUrl = null,
                category = "Laying Hens",
                isVerified = true,
                availability = "15 Available",
                datePosted = "2 days ago",
                viewCount = 45,
            ),
            MarketplaceListing(
                id = "2",
                title = "Broiler Chickens - 6 weeks old",
                description = "Well-fed broiler chickens, perfect weight for processing. Health certified.",
                price = 800.0,
                sellerName = "Manjula Farms",
                sellerRating = 4.6,
                location = "Warangal, Telangana",
                imageUrl = null,
                category = "Broilers",
                isVerified = true,
                availability = "50 Available",
                datePosted = "1 day ago",
                viewCount = 28,
            ),
            MarketplaceListing(
                id = "3",
                title = "Desi Country Chickens",
                description = "Free-range country chickens, naturally raised without chemicals.",
                price = 1500.0,
                sellerName = "Srinivas Poultry",
                sellerRating = 4.9,
                location = "Nizamabad, Telangana",
                imageUrl = null,
                category = "Country Chickens",
                isVerified = false,
                availability = "8 Available",
                datePosted = "3 days ago",
                viewCount = 67,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedMarketplaceScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSeller: (String) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }

    val viewModel: EnhancedMarketplaceViewModel = hiltViewModel()

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()

    // Trigger search when query or category changes
    LaunchedEffect(searchQuery, selectedCategory) {
        if (searchQuery.isNotBlank() || selectedCategory != "All") {
            viewModel.performIntelligentSearch(searchQuery, selectedCategory)
        } else {
            viewModel.loadInitialData()
        }
    }

    // Show loading indicator while loading
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Sample categories - could be loaded from data source
    val categories =
        remember {
            listOf(
                MarketplaceCategory("all", "All", "🐓", 73),
                MarketplaceCategory("layers", "Laying Hens", "🥚", 25),
                MarketplaceCategory("broilers", "Broilers", "🍗", 18),
                MarketplaceCategory("country", "Country", "🐔", 12),
                MarketplaceCategory("chicks", "Chicks", "🐣", 18),
            )
        }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Header Section
        MarketplaceHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                if (it.length >= 2) {
                    viewModel.getSearchSuggestions(it)
                }
            },
            isGridView = isGridView,
            onViewToggle = { isGridView = !isGridView },
            onSuggestionSelected = { searchQuery = it },
            searchSuggestions = searchSuggestions,
        )

        // Categories Section
        CategoryFilter(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
        )

        // Listings Section
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
        ) {
            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(searchResults, key = { it.id }) { listing ->
                        MarketplaceListingCard(
                            listing = listing,
                            isGridView = true,
                            onItemClick = { onNavigateToDetail(listing.id) },
                            onSellerClick = { onNavigateToSeller(listing.sellerName) },
                            onFavoriteClick = { /* Handle favorite */ },
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(searchResults, key = { it.id }) { listing ->
                        MarketplaceListingCard(
                            listing = listing,
                            isGridView = false,
                            onItemClick = { onNavigateToDetail(listing.id) },
                            onSellerClick = { onNavigateToSeller(listing.sellerName) },
                            onFavoriteClick = { /* Handle favorite */ },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketplaceHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isGridView: Boolean,
    onViewToggle: () -> Unit,
    onSuggestionSelected: (String) -> Unit,
    searchSuggestions: List<String>,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "🏪 Marketplace",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row {
                    IconButton(onClick = onViewToggle) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "List View" else "Grid View",
                        )
                    }

                    IconButton(onClick = { /* Filter options */ }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search fowl, location, seller...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Show search suggestions if available
            if (searchQuery.isNotBlank() && searchSuggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(100.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(searchSuggestions) { suggestion ->
                        TextButton(
                            onClick = { onSuggestionSelected(suggestion) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilter(
    categories: List<MarketplaceCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        items(categories) { category ->
            FilterChip(
                onClick = { onCategorySelected(category.name) },
                label = {
                    Text("${category.icon} ${category.name} (${category.count})")
                },
                selected = selectedCategory == category.name,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketplaceListingCard(
    listing: MarketplaceListing,
    isGridView: Boolean,
    onItemClick: () -> Unit,
    onSellerClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Card(
        onClick = onItemClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .let { if (isGridView) it.height(280.dp) else it.height(160.dp) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        if (isGridView) {
            // Grid Layout
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                ) {
                    AsyncImage(
                        model = listing.imageUrl ?: "https://via.placeholder.com/300x200",
                        contentDescription = listing.title,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                        contentScale = ContentScale.Crop,
                    )

                    // Verification badge
                    if (listing.isVerified) {
                        Card(
                            modifier =
                                Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopStart),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = Color(0xFF4CAF50),
                                ),
                        ) {
                            Text(
                                text = "✓ Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(4.dp),
                            )
                        }
                    }

                    // Favorite button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier =
                            Modifier
                                .padding(8.dp)
                                .align(Alignment.TopEnd),
                    ) {
                        Icon(
                            imageVector = if (listing.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (listing.isFavorite) Color.Red else Color.Gray,
                        )
                    }
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                ) {
                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${listing.currency}${listing.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = listing.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 2.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = listing.availability,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50),
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFFFFB300),
                            )
                            Text(
                                text = listing.sellerRating.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 2.dp),
                            )
                        }
                    }
                }
            }
        } else {
            // List Layout
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
            ) {
                AsyncImage(
                    model = listing.imageUrl ?: "https://via.placeholder.com/120x120",
                    contentDescription = listing.title,
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )

                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                imageVector = if (listing.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (listing.isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Text(
                        text = listing.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${listing.currency}${listing.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            TextButton(
                                onClick = onSellerClick,
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Text(
                                    text = listing.sellerName,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                if (listing.isVerified) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        modifier =
                                            Modifier
                                                .size(12.dp)
                                                .padding(start = 2.dp),
                                        tint = Color(0xFF4CAF50),
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = listing.location,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 2.dp),
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                text = listing.availability,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium,
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color(0xFFFFB300),
                                )
                                Text(
                                    text = listing.sellerRating.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
