package com.example.rooster.ui.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.SafeListing
import com.example.rooster.ui.components.*

/**
 * Enhanced Marketplace List Screen - Demonstrates ProductListItem usage
 *
 * Features:
 * - Uses ProductListItem component with sample data
 * - Rural-optimized UI with large touch targets
 * - Telugu language support
 * - Loading, error, and empty states
 */
@Composable
fun EnhancedMarketplaceListScreen(
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // Sample data - in real app this would come from ViewModel
    val sampleListings =
        remember {
            listOf(
                SafeListing(
                    id = "1",
                    breed = "Kadaknath Rooster",
                    age = 12,
                    price = 2500.0,
                    owner = if (isTeluguMode) "రాము గారు" else "Ramu",
                    sellerId = "seller1",
                    fatherId = "KDF001",
                    motherId = "KDM002",
                    vaccinations = listOf("Mareks", "Newcastle", "IBD"),
                    isBreeder = true,
                    isBloodlineVerified = true,
                    imageUrl = "https://example.com/kadaknath.jpg",
                ),
                SafeListing(
                    id = "2",
                    breed = "Aseel Hen",
                    age = 8,
                    price = 1800.0,
                    owner = if (isTeluguMode) "లక్ష్మి అక్క" else "Lakshmi",
                    sellerId = "seller2",
                    fatherId = "ASF003",
                    motherId = "ASM004",
                    vaccinations = listOf("Newcastle", "IBD"),
                    isBreeder = false,
                    isBloodlineVerified = true,
                    imageUrl = "https://example.com/aseel.jpg",
                ),
                SafeListing(
                    id = "3",
                    breed = "Country Chicken",
                    age = 16,
                    price = 3200.0,
                    owner = if (isTeluguMode) "సురేష్ గారు" else "Suresh",
                    sellerId = "seller3",
                    fatherId = "CCF005",
                    motherId = "CCM006",
                    vaccinations = listOf("Mareks", "Newcastle", "IBD", "Fowl Pox"),
                    isBreeder = true,
                    isBloodlineVerified = false,
                    imageUrl = "https://example.com/country.jpg",
                ),
            )
        }

    var favoriteItems by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    StandardScreenLayout(
        topBar = {
            RoosterTopAppBar(
                title = if (isTeluguMode) "మార్కెట్‌ప్లేస్" else "Marketplace",
            )
        },
        scrollable = false,
        modifier = modifier,
    ) {
        when {
            isLoading -> {
                LoadingLayout(
                    message = if (isTeluguMode) "లోడ్ అవుతోంది..." else "Loading products...",
                )
            }

            hasError -> {
                ErrorLayout(
                    message = if (isTeluguMode) "దోషం సంభవించింది" else "Failed to load products",
                    actionText = if (isTeluguMode) "మళ్లీ ప్రయత్నించండి" else "Retry",
                    onAction = { hasError = false },
                )
            }

            sampleListings.isEmpty() -> {
                EmptyStateLayout(
                    message = if (isTeluguMode) "ఉత్పత్తులు అందుబాటులో లేవు" else "No products available",
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = sampleListings,
                        key = { it.id },
                    ) { listing ->
                        ProductListItem(
                            listing = listing,
                            onClick = { onProductClick(listing.id) },
                            onFavoriteClick = { isFavorited ->
                                favoriteItems =
                                    if (isFavorited) {
                                        favoriteItems + listing.id
                                    } else {
                                        favoriteItems - listing.id
                                    }
                                onFavoriteClick(listing.id, isFavorited)
                            },
                            onShareClick = { onShareClick(listing.id) },
                            isTeluguMode = isTeluguMode,
                            isFavorited = favoriteItems.contains(listing.id),
                            showFullDetails = true,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Top App Bar for Rooster Theme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoosterTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = modifier,
    )
}
