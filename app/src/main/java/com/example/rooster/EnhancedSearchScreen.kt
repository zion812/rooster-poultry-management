package com.example.rooster

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import java.util.*
// Core search imports
import com.example.rooster.core.search.SearchRepository
import com.example.rooster.core.search.SearchResultItem
import com.example.rooster.core.search.PlaceholderSearchRepository // For placeholder injection
import kotlinx.coroutines.flow.collectLatest

// TODO: Define these enums in :core:search or a common module accessible by both :core:search and :app
enum class AppSearchResultType(val displayName: String) {
    // Renamed to avoid conflict if SearchResultType is also in core:search for other reasons
    FOWL("Fowl"),
    MARKETPLACE("Marketplace"),
    FARMER("Farmer"),
    COMMUNITY("Community"),
    EVENT("Event"),
    UNKNOWN("Unknown"),
    ;

    companion object {
        fun fromTypeString(typeString: String): AppSearchResultType {
            return values().find { it.name.equals(typeString, ignoreCase = true) } ?: UNKNOWN
        }
    }
}

enum class AppSearchCategory(val displayName: String) {
    ALL("All"),
    FOWL("Fowl"),
    MARKETPLACE("Marketplace"),
    FARMERS("Farmers"),
    COMMUNITY("Community"),
    EVENTS("Events"),
}

enum class AppSortOption(val displayName: String) {
    RELEVANCE("Relevance"),
    NEWEST("Newest"),
    OLDEST("Oldest"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    DISTANCE("Distance"),
    RATING("Rating"),
}

/**
 * Enhanced Search & Discovery System - Moderate Level Feature
 * Advanced search with smart filtering, regional recommendations, and network optimization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSearchScreen(
    // TODO: Inject via Hilt ViewModel
    searchRepository: SearchRepository = remember { PlaceholderSearchRepository() },
    onNavigateToDetails: (String, AppSearchResultType) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current.applicationContext as Application
    // val searchService = remember { EnhancedSearchService(context) } // Replaced by searchRepository
    val coroutineScope = rememberCoroutineScope()

    // Search state
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchResultItem>>(emptyList()) } // Use SearchResultItem
    // Recent, popular searches and recommendations would also come from SearchRepository
    var recentSearches by remember { mutableStateOf<List<String>>(emptyList()) }
    var popularSearches by remember { mutableStateOf<List<String>>(emptyList()) }
    var recommendations by remember { mutableStateOf<List<SearchResultItem>>(emptyList()) } // Use SearchResultItem
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Filter and sort state
    var showFilters by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf(AppSearchCategory.ALL) } // Use AppSearchCategory
    var selectedRegion by rememberSaveable { mutableStateOf("All Regions") }
    var priceRangeStart by rememberSaveable { mutableStateOf(0f) }
    var priceRangeEnd by rememberSaveable { mutableStateOf(50000f) }
    val priceRange = priceRangeStart..priceRangeEnd
    var sortBy by rememberSaveable { mutableStateOf(AppSortOption.RELEVANCE) } // Use AppSortOption
    var selectedBreeds by remember { mutableStateOf<Set<String>>(emptySet()) }

    val availableRegions =
        listOf("All Regions", "Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu")
    val availableBreeds =
        listOf("Kadaknath", "Asil", "Brahma", "Leghorn", "Rhode Island Red", "Country Chicken")

    // TODO: Replace local EnhancedSearchService calls with searchRepository calls for recent, popular, recommendations
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Example:
            // searchRepository.getRecentSearches().collectLatest { result -> ... }
            // For now, keeping existing placeholder logic for these, will need repository methods
            recentSearches = listOf("Kadaknath rooster", "Asil hens", "Traditional market")
            popularSearches = listOf("Country chicken", "Broiler chicks", "Egg layers")
            recommendations = emptyList() // Placeholder
        }
    }

    // Perform search when query or filters change
    LaunchedEffect(
        searchQuery,
        selectedCategory,
        selectedRegion,
        priceRange,
        sortBy,
        selectedBreeds,
    ) {
        if (searchQuery.length > 2) { // Typically search after a few chars
            isLoading = true
            error = null

            // Create filter map for SearchRepository
            val filters = mutableMapOf<String, String>()
            if (selectedCategory != AppSearchCategory.ALL) {
                filters["category"] = selectedCategory.name
            }
            if (selectedRegion != "All Regions") {
                filters["region"] = selectedRegion
            }
            filters["price_min"] = priceRange.start.toInt().toString()
            filters["price_max"] = priceRange.endInclusive.toInt().toString()
            if (selectedBreeds.isNotEmpty()) {
                filters["breeds"] = selectedBreeds.joinToString(",")
            }
            filters["sort_by"] = sortBy.name

            searchRepository.performSearch(searchQuery, filters).collectLatest { result ->
                isLoading = false
                when (result) {
                    is com.example.rooster.core.common.Result.Success -> {
                        searchResults = result.data
                        error = null
                        // TODO: Save to recent searches via repository
                        // Log search analytics
                        FirebaseAnalytics.getInstance(context).logEvent(
                            "search_performed",
                            bundleOf(
                                "term" to searchQuery,
                                "result_count" to result.data.size,
                                "category" to selectedCategory.name,
                                "region" to selectedRegion,
                            ),
                        )
                    }
                    is com.example.rooster.core.common.Result.Error -> {
                        searchResults = emptyList()
                        error = "Search failed: ${result.exception.message}"
                    }
                    is com.example.rooster.core.common.Result.Loading -> {
                        isLoading = true
                    }
                }
            }
        } else {
            searchResults = emptyList()
            isLoading = false // Stop loading if query is too short
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        // Search Header
        SearchHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onFilterClick = { showFilters = !showFilters },
            isLoading = isLoading,
        )

        // Filter Section
        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            FilterSection(
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                selectedRegion = selectedRegion,
                onRegionChange = { selectedRegion = it },
                availableRegions = availableRegions,
                priceRange = priceRange,
                onPriceRangeChange = {
                    priceRangeStart = it.start
                    priceRangeEnd = it.endInclusive
                },
                sortBy = sortBy,
                onSortByChange = { sortBy = it },
                selectedBreeds = selectedBreeds,
                onBreedsChange = { selectedBreeds = it },
                availableBreeds = availableBreeds,
                onClearFilters = {
                    selectedCategory = SearchCategory.ALL
                    selectedRegion = "All Regions"
                    priceRangeStart = 0f
                    priceRangeEnd = 50000f
                    sortBy = SortOption.RELEVANCE
                    selectedBreeds = emptySet()
                },
            )
        }

        // Content
        if (searchQuery.isEmpty()) {
            // Show search suggestions and recommendations
            SearchSuggestionsContent(
                recentSearches = recentSearches,
                popularSearches = popularSearches,
                recommendations = recommendations, // Now List<SearchResultItem>
                onSearchSuggestionClick = { searchQuery = it },
                onRecommendationClick = { searchResultItem -> // Changed parameter type
                    onNavigateToDetails(searchResultItem.id, AppSearchResultType.fromTypeString(searchResultItem.type))
                },
            )
        } else {
            // Show search results
            SearchResultsContent(
                searchResults = searchResults, // Now List<SearchResultItem>
                isLoading = isLoading,
                error = error,
                onResultClick = { searchResultItem -> // Changed parameter type
                    onNavigateToDetails(searchResultItem.id, AppSearchResultType.fromTypeString(searchResultItem.type))
                },
            )
        }
    }
}

@Composable
private fun SearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    isLoading: Boolean,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search fowl, marketplace, farmers...") },
                leadingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onSearch = { /* Search is triggered by state change */ },
                    ),
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                singleLine = true,
            )

            IconButton(onClick = onFilterClick) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedCategory: SearchCategory,
    onCategoryChange: (SearchCategory) -> Unit,
    selectedRegion: String,
    onRegionChange: (String) -> Unit,
    availableRegions: List<String>,
    priceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    sortBy: SortOption,
    onSortByChange: (SortOption) -> Unit,
    selectedBreeds: Set<String>,
    onBreedsChange: (Set<String>) -> Unit,
    availableBreeds: List<String>,
    onClearFilters: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
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
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = onClearFilters) {
                    Text("Clear All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(SearchCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.displayName) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Region Filter
            Text(
                text = "Region",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(availableRegions) { region ->
                    FilterChip(
                        selected = selectedRegion == region,
                        onClick = { onRegionChange(region) },
                        label = { Text(region) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Range (for marketplace items)
            if (selectedCategory == SearchCategory.MARKETPLACE || selectedCategory == SearchCategory.ALL) {
                Text(
                    text = "Price Range (₹${priceRange.start.toInt()} - ₹${priceRange.endInclusive.toInt()})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = priceRange,
                    onValueChange = onPriceRangeChange,
                    valueRange = 0f..50000f,
                    steps = 49,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Breed Filter
            Text(
                text = "Breeds",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(availableBreeds) { breed ->
                    FilterChip(
                        selected = selectedBreeds.contains(breed),
                        onClick = {
                            if (selectedBreeds.contains(breed)) {
                                onBreedsChange(selectedBreeds - breed)
                            } else {
                                onBreedsChange(selectedBreeds + breed)
                            }
                        },
                        label = { Text(breed) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Options
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(SortOption.values()) { option ->
                    FilterChip(
                        selected = sortBy == option,
                        onClick = { onSortByChange(option) },
                        label = { Text(option.displayName) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestionsContent(
    recentSearches: List<String>,
    popularSearches: List<String>,
    recommendations: List<SearchResult>,
    onSearchSuggestionClick: (String) -> Unit,
    onRecommendationClick: (SearchResult) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Recent Searches
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSuggestionSection(
                    title = "Recent Searches",
                    icon = Icons.Default.History,
                    suggestions = recentSearches,
                    onSuggestionClick = onSearchSuggestionClick,
                )
            }
        }

        // Popular Searches
        if (popularSearches.isNotEmpty()) {
            item {
                SearchSuggestionSection(
                    title = "Popular Searches",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    suggestions = popularSearches,
                    onSuggestionClick = onSearchSuggestionClick,
                )
            }
        }

        // Personalized Recommendations
        if (recommendations.isNotEmpty()) {
            item {
                Text(
                    text = "Recommended for You",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(recommendations.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { result ->
                        Box(modifier = Modifier.weight(1f)) {
                            RecommendationCard(
                                result = result,
                                onClick = { onRecommendationClick(result) },
                            )
                        }
                    }

                    // Fill remaining space if odd number of items
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestionSection(
    title: String,
    icon: ImageVector,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchResults: List<SearchResult>,
    isLoading: Boolean,
    error: String?,
    onResultClick: (SearchResult) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            error != null -> {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            searchResults.isEmpty() -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try adjusting your search terms or filters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                ) {
                    items(searchResults) { result ->
                        SearchResultCard(
                            result = result,
                            onClick = { onResultClick(result) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    result: SearchResult,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector =
                        when (result.type) {
                            SearchResultType.FOWL -> Icons.Default.Pets
                            SearchResultType.MARKETPLACE -> Icons.Default.Store
                            SearchResultType.FARMER -> Icons.Default.Person
                            SearchResultType.COMMUNITY -> Icons.Default.Group
                            SearchResultType.EVENT -> Icons.Default.Event
                        },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = result.type.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = result.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (result.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (result.price > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${result.price}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector =
                            when (result.type) {
                                SearchResultType.FOWL -> Icons.Default.Pets
                                SearchResultType.MARKETPLACE -> Icons.Default.Store
                                SearchResultType.FARMER -> Icons.Default.Person
                                SearchResultType.COMMUNITY -> Icons.Default.Group
                                SearchResultType.EVENT -> Icons.Default.Event
                            },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = result.type.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (result.relevanceScore > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ) {
                        Text(
                            text = "${(result.relevanceScore * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (result.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (result.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (result.price > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "₹${result.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (result.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = result.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// Data classes and enums for the search system
data class SearchResult(
    val id: String,
    val type: SearchResultType,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val price: Int = 0,
    val location: String = "",
    val imageUrl: String = "",
    val relevanceScore: Float = 0f,
    val timestamp: Date = Date(),
    val tags: List<String> = emptyList(),
)

enum class SearchResultType(val displayName: String) {
    FOWL("Fowl"),
    MARKETPLACE("Marketplace"),
    FARMER("Farmer"),
    COMMUNITY("Community"),
    EVENT("Event"),
}

enum class SearchCategory(val displayName: String) {
    ALL("All"),
    FOWL("Fowl"),
    MARKETPLACE("Marketplace"),
    FARMERS("Farmers"),
    COMMUNITY("Community"),
    EVENTS("Events"),
}

enum class SortOption(val displayName: String) {
    RELEVANCE("Relevance"),
    NEWEST("Newest"),
    OLDEST("Oldest"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    DISTANCE("Distance"),
    RATING("Rating"),
}

data class SearchFilters(
    val category: SearchCategory = SearchCategory.ALL,
    val region: String? = null,
    val priceRange: ClosedFloatingPointRange<Float> = 0f..50000f,
    val breeds: Set<String> = emptySet(),
    val sortBy: SortOption = SortOption.RELEVANCE,
    val maxDistance: Float? = null,
    val minRating: Float? = null,
)

// Search Service (interface definition - implementation would be in separate file)
class EnhancedSearchService(private val context: Application) {
    suspend fun performSearch(
        query: String,
        filters: SearchFilters,
    ): Result<List<SearchResult>> {
        // Implementation would include:
        // - Parse backend queries with complex filtering
        // - Full-text search capabilities
        // - Smart ranking algorithms
        // - Network-adaptive result limits
        // - Offline search in cached data
        return Result.success(emptyList()) // Placeholder
    }

    suspend fun getRecentSearches(): Result<List<String>> {
        // Implementation would load from local storage
        return Result.success(listOf("Kadaknath rooster", "Asil hens", "Traditional market"))
    }

    suspend fun getPopularSearches(): Result<List<String>> {
        // Implementation would load from backend analytics
        return Result.success(listOf("Country chicken", "Broiler chicks", "Egg layers"))
    }

    suspend fun getPersonalizedRecommendations(): Result<List<SearchResult>> {
        // Implementation would use ML recommendations
        return Result.success(emptyList())
    }

    suspend fun saveToRecentSearches(query: String): Result<Unit> {
        // Implementation would save to local storage
        return Result.success(Unit)
    }
}
