package com.example.rooster.ui.marketplace

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Market categories with Telugu support
enum class MarketCategory(
    val displayName: String,
    val displayNameTelugu: String,
    val icon: ImageVector,
    val color: Color,
) {
    LIVE_BIRDS(
        "Live Birds",
        "సజీవ పక్షులు",
        Icons.Default.Pets,
        Color(0xFF059669),
    ),
    EGGS(
        "Eggs",
        "గుడ్లు",
        Icons.Default.EggAlt,
        Color(0xFFF59E0B),
    ),
    FEED_SUPPLEMENTS(
        "Feed & Supplements",
        "దాణా & పోషకాలు",
        Icons.Default.Grass,
        Color(0xFF8B5CF6),
    ),
    EQUIPMENT(
        "Equipment",
        "పరికరాలు",
        Icons.Default.Build,
        Color(0xFF3B82F6),
    ),
    MEDICINES(
        "Medicines",
        "మందులు",
        Icons.Default.MedicalServices,
        Color(0xFFDC2626),
    ),
    SERVICES(
        "Services",
        "సేవలు",
        Icons.Default.Handshake,
        Color(0xFF6366F1),
    ),
    BREEDING_STOCK(
        "Breeding Stock",
        "సంతానోత్పత్తి స్టాక్",
        Icons.Default.FamilyRestroom,
        Color(0xFFEC4899),
    ),
    ORGANIC(
        "Organic Products",
        "సేంద్రీయ ఉత్పత్తులు",
        Icons.Default.Eco,
        Color(0xFF10B981),
    ),
}

// Listing status with visual indicators
enum class ListingStatus(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: ImageVector,
    val isActive: Boolean = true,
) {
    ACTIVE(
        "Active",
        "క్రియాశీలకం",
        Color(0xFF059669),
        Icons.Default.CheckCircle,
        true,
    ),
    PENDING(
        "Pending Approval",
        "ఆమోదం పెండింగ్",
        Color(0xFFF59E0B),
        Icons.Default.Pending,
        false,
    ),
    SOLD(
        "Sold",
        "అమ్మబడిన",
        Color(0xFF6B7280),
        Icons.Default.ShoppingCart,
        false,
    ),
    EXPIRED(
        "Expired",
        "గడువు ముగిసిన",
        Color(0xFFEF4444),
        Icons.Default.AccessTime,
        false,
    ),
    SUSPENDED(
        "Suspended",
        "నిలిపివేయబడిన",
        Color(0xFFDC2626),
        Icons.Default.Block,
        false,
    ),
}

// Price range filters
data class PriceRange(
    val min: Double,
    val max: Double,
    val displayName: String,
    val displayNameTelugu: String,
) {
    companion object {
        fun getCommonRanges(): List<PriceRange> =
            listOf(
                PriceRange(0.0, 500.0, "Under ₹500", "₹500 కింద"),
                PriceRange(500.0, 1000.0, "₹500 - ₹1,000", "₹500 - ₹1,000"),
                PriceRange(1000.0, 2500.0, "₹1,000 - ₹2,500", "₹1,000 - ₹2,500"),
                PriceRange(2500.0, 5000.0, "₹2,500 - ₹5,000", "₹2,500 - ₹5,000"),
                PriceRange(5000.0, 10000.0, "₹5,000 - ₹10,000", "₹5,000 - ₹10,000"),
                PriceRange(10000.0, Double.MAX_VALUE, "Above ₹10,000", "₹10,000 మించిన"),
            )
    }
}

// Sorting options
enum class SortOption(
    val displayName: String,
    val displayNameTelugu: String,
    val field: String,
    val isDescending: Boolean = false,
) {
    NEWEST_FIRST(
        "Newest First",
        "కొత్తవి మొదట",
        "createdAt",
        true,
    ),
    OLDEST_FIRST(
        "Oldest First",
        "పాతవి మొదట",
        "createdAt",
        false,
    ),
    PRICE_LOW_HIGH(
        "Price: Low to High",
        "ధర: తక్కువ నుండి ఎక్కువ",
        "price",
        false,
    ),
    PRICE_HIGH_LOW(
        "Price: High to Low",
        "ధర: ఎక్కువ నుండి తక్కువ",
        "price",
        true,
    ),
    DISTANCE_NEAR(
        "Distance: Near to Far",
        "దూరం: దగ్గర నుండి దూరం",
        "distance",
        false,
    ),
    POPULARITY(
        "Most Popular",
        "అత్యధిక జనాదరణ",
        "views",
        true,
    ),
    RATING_HIGH(
        "Highest Rated",
        "అత్యధిక రేటింగ్",
        "rating",
        true,
    ),
}

// Advanced filters
data class MarketplaceFilters(
    val categories: Set<MarketCategory> = emptySet(),
    val priceRange: PriceRange? = null,
    val location: String? = null,
    val maxDistance: Int? = null, // in kilometers
    val isVerifiedSeller: Boolean = false,
    val hasImages: Boolean = false,
    val isNegotiable: Boolean = false,
    val availabilityStatus: Set<ListingStatus> = setOf(ListingStatus.ACTIVE),
    val breed: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val vaccinationStatus: Boolean = false,
    val organicCertified: Boolean = false,
    val sortBy: SortOption = SortOption.NEWEST_FIRST,
    val searchQuery: String? = null,
)

// Navigation helper for marketplace
object MarketplaceNavigationHelper {
    // Get filter display text
    fun getFilterSummary(
        filters: MarketplaceFilters,
        isTeluguMode: Boolean,
    ): String {
        val parts = mutableListOf<String>()

        if (filters.categories.isNotEmpty()) {
            val categoryNames =
                filters.categories.map {
                    if (isTeluguMode) it.displayNameTelugu else it.displayName
                }
            parts.add(categoryNames.joinToString(", "))
        }

        filters.priceRange?.let { range ->
            parts.add(if (isTeluguMode) range.displayNameTelugu else range.displayName)
        }

        filters.location?.let { location ->
            parts.add("${if (isTeluguMode) "స్థానం" else "Location"}: $location")
        }

        if (filters.isVerifiedSeller) {
            parts.add(if (isTeluguMode) "ధృవీకరించబడిన విక్రేతలు" else "Verified Sellers")
        }

        return if (parts.isEmpty()) {
            if (isTeluguMode) "అన్ని లిస్టింగులు" else "All Listings"
        } else {
            parts.joinToString(" • ")
        }
    }

    // Get category icon and color
    fun getCategoryIcon(category: MarketCategory): Pair<ImageVector, Color> {
        return Pair(category.icon, category.color)
    }

    // Build search query parameters
    fun buildSearchQuery(filters: MarketplaceFilters): Map<String, Any> {
        val query = mutableMapOf<String, Any>()

        if (filters.categories.isNotEmpty()) {
            query["categories"] = filters.categories.map { it.name }
        }

        filters.priceRange?.let { range ->
            query["minPrice"] = range.min
            if (range.max != Double.MAX_VALUE) {
                query["maxPrice"] = range.max
            }
        }

        filters.location?.let { query["location"] = it }
        filters.maxDistance?.let { query["maxDistance"] = it }

        if (filters.isVerifiedSeller) {
            query["verifiedSeller"] = true
        }

        if (filters.hasImages) {
            query["hasImages"] = true
        }

        if (filters.isNegotiable) {
            query["negotiable"] = true
        }

        if (filters.availabilityStatus.isNotEmpty()) {
            query["status"] = filters.availabilityStatus.map { it.name }
        }

        filters.breed?.let { query["breed"] = it }
        filters.age?.let { query["age"] = it }
        filters.gender?.let { query["gender"] = it }

        if (filters.vaccinationStatus) {
            query["vaccinated"] = true
        }

        if (filters.organicCertified) {
            query["organic"] = true
        }

        query["sortBy"] = filters.sortBy.field
        query["sortDesc"] = filters.sortBy.isDescending

        filters.searchQuery?.let { query["search"] = it }

        return query
    }

    // Generate filter badge count
    fun getActiveFilterCount(filters: MarketplaceFilters): Int {
        var count = 0

        if (filters.categories.isNotEmpty()) count++
        if (filters.priceRange != null) count++
        if (filters.location != null) count++
        if (filters.maxDistance != null) count++
        if (filters.isVerifiedSeller) count++
        if (filters.hasImages) count++
        if (filters.isNegotiable) count++
        if (filters.breed != null) count++
        if (filters.age != null) count++
        if (filters.gender != null) count++
        if (filters.vaccinationStatus) count++
        if (filters.organicCertified) count++
        if (filters.searchQuery != null) count++

        return count
    }

    // Reset filters to default
    fun getDefaultFilters(): MarketplaceFilters {
        return MarketplaceFilters()
    }

    // Quick filter presets
    fun getQuickFilters(isTeluguMode: Boolean): List<Pair<String, MarketplaceFilters>> {
        return listOf(
            (if (isTeluguMode) "సజీవ పక్షులు" else "Live Birds") to
                MarketplaceFilters(
                    categories = setOf(MarketCategory.LIVE_BIRDS),
                ),
            (if (isTeluguMode) "గుడ్లు" else "Eggs") to
                MarketplaceFilters(
                    categories = setOf(MarketCategory.EGGS),
                ),
            (if (isTeluguMode) "ధృవీకరించబడిన విక్రేతలు" else "Verified Sellers") to
                MarketplaceFilters(
                    isVerifiedSeller = true,
                ),
            (if (isTeluguMode) "సేంద్రీయ" else "Organic") to
                MarketplaceFilters(
                    organicCertified = true,
                ),
            (if (isTeluguMode) "దగ్గరలో" else "Nearby") to
                MarketplaceFilters(
                    maxDistance = 10,
                    sortBy = SortOption.DISTANCE_NEAR,
                ),
            (if (isTeluguMode) "చౌకైన ధర" else "Budget Friendly") to
                MarketplaceFilters(
                    priceRange = PriceRange.getCommonRanges()[0],
                    sortBy = SortOption.PRICE_LOW_HIGH,
                ),
        )
    }
}

// Contact info for marketplace listings
data class ContactInfo(
    val phoneNumber: String,
    val whatsappNumber: String? = null,
    val allowDirectCall: Boolean = true,
    val allowWhatsApp: Boolean = true,
    val preferredContactTime: String? = null,
)
