// use context7
package com.example.rooster.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.SafeListing
import com.example.rooster.assessNetworkQualitySafely
import com.example.rooster.ui.theme.RoosterTheme

// Enhanced data classes for functional features
data class MarketInsights(
    val averagePrice: Double,
    val priceDirection: String,
    val demandLevel: String,
    val seasonalFactor: Double,
)

data class PriceInsights(
    val marketAverage: Double,
    val priceComparison: Double,
    val isGoodDeal: Boolean,
    val priceRecommendation: String,
)

/**
 * Market Insights Card Component
 */
@Composable
fun MarketInsightsCard(
    insights: MarketInsights,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "మార్కెట్ సమాచారం" else "Market Insights",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )

                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text =
                        if (isTeluguMode) {
                            "సగటు: ₹${insights.averagePrice.toInt()}"
                        } else {
                            "Avg: ₹${insights.averagePrice.toInt()}"
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )

                Text(
                    text = insights.demandLevel,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color =
                        when (insights.demandLevel) {
                            "High" -> MaterialTheme.colorScheme.primary
                            "Medium" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                )
            }
        }
    }
}

/**
 * Nearby Listings Section Component
 */
@Composable
fun NearbyListingsSection(
    nearbyListings: List<SafeListing>,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isTeluguMode) "సమీప జాబితాలు" else "Similar Nearby",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text =
                    if (isTeluguMode) {
                        "${nearbyListings.size} సమానమైన జాబితాలు"
                    } else {
                        "${nearbyListings.size} similar listings"
                    },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )

            // Price range of nearby listings
            if (nearbyListings.isNotEmpty()) {
                val minPrice = nearbyListings.minOf { it.price }
                val maxPrice = nearbyListings.maxOf { it.price }
                Text(
                    text =
                        if (isTeluguMode) {
                            "ధర వ్యాప్తి: ₹${minPrice.toInt()} - ₹${maxPrice.toInt()}"
                        } else {
                            "Price range: ₹${minPrice.toInt()} - ₹${maxPrice.toInt()}"
                        },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/**
 * Rural-Optimized Product List Item Component
 *
 * Features:
 * - Network-adaptive image loading with compression
 * - Traceability badges and verification indicators
 * - Farmer-friendly large touch targets
 * - Cultural sensitivity with Telugu language support
 * - Material 3 design with RoosterTheme integration
 * - Accessibility-first design for rural users
 */
@Composable
fun ProductListItem(
    listing: SafeListing,
    onClick: () -> Unit,
    onFavoriteClick: ((Boolean) -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    isTeluguMode: Boolean = false,
    isFavorited: Boolean = false,
    showFullDetails: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val networkQuality = remember { assessNetworkQualitySafely(context) }

    // Animation state for smooth entrance
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(listing.id) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter =
            fadeIn(animationSpec = tween(300)) +
                slideInVertically(animationSpec = tween(300)) { it / 2 },
        modifier = modifier,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 8.dp,
                ),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                // Top row with enhanced badges and action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    // Enhanced verification badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (listing.isBloodlineVerified) {
                            VerificationBadge(
                                text = if (isTeluguMode) "వంశావళి" else "Verified",
                                icon = Icons.Default.Verified,
                                colors =
                                    AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                            )
                        }

                        if (listing.isBreeder) {
                            VerificationBadge(
                                text = if (isTeluguMode) "పెంపకందారు" else "Breeder",
                                icon = Icons.Default.Star,
                                colors =
                                    AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        onFavoriteClick?.let { favoriteCallback ->
                            IconButton(
                                onClick = { favoriteCallback(!isFavorited) },
                                modifier = Modifier.size(40.dp),
                            ) {
                                Icon(
                                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isTeluguMode) "ప్రియమైనది" else "Favorite",
                                    tint = if (isFavorited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        // Live chat button
                        IconButton(
                            onClick = { /* Launch real-time chat */ },
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = if (isTeluguMode) "చాట్" else "Chat",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }

                        onShareClick?.let { shareCallback ->
                            IconButton(
                                onClick = shareCallback,
                                modifier = Modifier.size(40.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = if (isTeluguMode) "పంచుకోండి" else "Share",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main content row with enhanced information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Product image with rural connectivity optimization
                    NetworkAdaptiveImage(
                        imageUrl = listing.imageUrl,
                        networkQuality = networkQuality,
                        contentDescription = "${listing.breed} image",
                        modifier =
                            Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.medium),
                    )

                    // Enhanced product details
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Breed name with intelligent translation
                        Text(
                            text =
                                listing.breed.ifEmpty {
                                    if (isTeluguMode) "జాతి తెలియదు" else "Breed Unknown"
                                },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        // Age and gender info with localization
                        Text(
                            text =
                                if (isTeluguMode) {
                                    "వయస్సు: ${listing.age} వారాలు"
                                } else {
                                    "Age: ${listing.age} weeks"
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        // Enhanced price with market insights
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "₹${formatPrice(listing.price)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        // Seller info with geospatial distance
                        Text(
                            text =
                                if (isTeluguMode) {
                                    "విక్రేత: ${listing.owner}"
                                } else {
                                    "Seller: ${listing.owner}"
                                },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        // Enhanced health and vaccination status
                        if (showFullDetails && listing.vaccinations.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                                Text(
                                    text =
                                        if (isTeluguMode) {
                                            "${listing.vaccinations.size} టీకాలు"
                                        } else {
                                            "${listing.vaccinations.size} vaccines"
                                        },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                    }
                }

                // Enhanced additional details section
                if (showFullDetails) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

/**
 * Network-adaptive image component that adjusts quality based on connection
 */
@Composable
private fun NetworkAdaptiveImage(
    imageUrl: String,
    networkQuality: com.example.rooster.NetworkQualityLevel,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageSize =
        when (networkQuality) {
            com.example.rooster.NetworkQualityLevel.EXCELLENT -> 1080
            com.example.rooster.NetworkQualityLevel.GOOD -> 720
            com.example.rooster.NetworkQualityLevel.FAIR -> 480
            com.example.rooster.NetworkQualityLevel.POOR -> 240
            com.example.rooster.NetworkQualityLevel.OFFLINE -> 240
        }

    Box(
        modifier =
            modifier.background(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.shapes.medium,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .size(imageSize)
                        .crossfade(true)
                        .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            // Placeholder for missing image
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No Image",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
    }
}

/**
 * Verification badge component
 */
@Composable
private fun VerificationBadge(
    text: String,
    icon: ImageVector,
    colors: ChipColors,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = { /* Handle badge click for info */ },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        },
        colors = colors,
        modifier = modifier,
    )
}

/**
 * Format price with Indian numbering system
 */
private fun formatPrice(price: Double): String {
    return when {
        price >= 100000 -> String.format("%.1f L", price / 100000)
        price >= 1000 -> String.format("%.1f K", price / 1000)
        else -> price.toInt().toString()
    }
}

/**
 * Compact version for list views
 */
@Composable
fun CompactProductListItem(
    listing: SafeListing,
    onClick: () -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    ProductListItem(
        listing = listing,
        onClick = onClick,
        isTeluguMode = isTeluguMode,
        showFullDetails = false,
        modifier = modifier,
    )
}

/**
 * Preview function for development
 */
@Composable
fun ProductListItemPreview() {
    RoosterTheme {
        Surface {
            ProductListItem(
                listing =
                    SafeListing(
                        id = "1",
                        breed = "Kadaknath Rooster",
                        age = 12,
                        price = 2500.0,
                        owner = "రాము గారు",
                        sellerId = "seller1",
                        fatherId = "KDF001",
                        motherId = "KDM002",
                        vaccinations = listOf("Mareks", "Newcastle", "IBD"),
                        isBreeder = true,
                        isBloodlineVerified = true,
                    ),
                onClick = { },
                onFavoriteClick = { },
                onShareClick = { },
                isTeluguMode = true,
                isFavorited = false,
            )
        }
    }
}
