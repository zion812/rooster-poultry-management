package com.example.rooster.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.GrowthUpdate
import com.example.rooster.NetworkQualityLevel
import com.example.rooster.SafeListing
import com.example.rooster.data.MarketplaceListing
import com.example.rooster.models.MarketplaceListingParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MarketplaceViewModel : ViewModel() {
    fun getListingById(listingId: String): StateFlow<SafeListing?> {
        val _listing = MutableStateFlow<SafeListing?>(null)
        viewModelScope.launch {
            try {
                val query = ParseQuery(MarketplaceListingParse::class.java)
                query.include("owner")
                val parseListing = query.get(listingId)
                _listing.value = (parseListing as? MarketplaceListingParse)?.toSafeListing()
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Error fetching listing by ID: $listingId", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                _listing.value = null
            }
        }
        return _listing
    }

    // UI State
    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    // Listings state
    private val _listings = MutableStateFlow<List<SafeListing>>(emptyList())
    val listings: StateFlow<List<SafeListing>> = _listings.asStateFlow()

    // Filter state
    private val _filters = MutableStateFlow(MarketplaceFilters())
    val filters: StateFlow<MarketplaceFilters> = _filters.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Log debugging information
                android.util.Log.d("MarketplaceVM", "Starting to load listings...")

                // Try Parse backend first, then fallback to mock data
                if (_uiState.value.context != null) {
                    loadFromParseWithFallback()
                } else {
                    // If no context available, use mock data immediately
                    android.util.Log.w("MarketplaceVM", "No context available, loading mock data (should not happen in production)")
                    loadMockData() // This will now just set an error state
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Error in loadListings", e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred",
                    )
                // Load mock data as final fallback
                loadMockData() // This will now just set an error state
            }
        }
    }

    private suspend fun loadFromParseWithFallback() {
        try {
            // Check if Parse is initialized
            val currentUser = ParseUser.getCurrentUser()
            android.util.Log.d("MarketplaceVM", "Parse current user: ${currentUser?.objectId}")
            android.util.Log.d(
                "MarketplaceVM",
                "Parse current user username: ${currentUser?.username}",
            )

            // First, try to fetch listings directly without network-aware strategy
            val directQuery = ParseQuery(MarketplaceListingParse::class.java)
            directQuery.include("owner")
            directQuery.whereEqualTo("isActive", true)
            directQuery.orderByDescending("createdAt")
            directQuery.limit = 50
            directQuery.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE

            android.util.Log.d("MarketplaceVM", "Executing direct Parse query for listings...")

            try {
                val directListings = directQuery.find().mapNotNull { it as? MarketplaceListingParse }
                android.util.Log.d(
                    "MarketplaceVM",
                    "Direct query returned ${directListings.size} raw listings",
                )

                // Log each listing for debugging
                directListings.forEachIndexed { index: Int, parseObject: MarketplaceListingParse ->
                    android.util.Log.d("MarketplaceVM", "Raw listing $index:")
                    android.util.Log.d("MarketplaceVM", "  - ObjectId: ${parseObject.objectId}")
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Breed: ${parseObject.getString("breed")}",
                    )
                    android.util.Log.d("MarketplaceVM", "  - Age: ${parseObject.getInt("age")}")
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Price: ${parseObject.getString("price")}",
                    )
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - IsActive: ${parseObject.getBoolean("isActive")}",
                    )
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Owner: ${parseObject.getParseUser("owner")?.objectId}",
                    )
                    android.util.Log.d("MarketplaceVM", "  - CreatedAt: ${parseObject.createdAt}")
                }

                if (directListings.isNotEmpty()) {
                    val convertedListings = directListings.map { it.toSafeListing() }
                    android.util.Log.d(
                        "MarketplaceVM",
                        "Successfully converted ${convertedListings.size} listings",
                    )

                    // Log converted listings
                    convertedListings.forEachIndexed { index: Int, safeListing: SafeListing ->
                        android.util.Log.d("MarketplaceVM", "Converted listing $index:")
                        android.util.Log.d("MarketplaceVM", "  - ID: ${safeListing.id}")
                        android.util.Log.d("MarketplaceVM", "  - Breed: ${safeListing.breed}")
                        android.util.Log.d("MarketplaceVM", "  - Owner: ${safeListing.owner}")
                        android.util.Log.d("MarketplaceVM", "  - Price: ${safeListing.price}")
                        android.util.Log.d("MarketplaceVM", "  - IsActive: ${safeListing.isActive}")
                    }

                    val filteredListings = applyFilters(convertedListings)
                    android.util.Log.d(
                        "MarketplaceVM",
                        "After filtering: ${filteredListings.size} listings",
                    )

                    _listings.value = filteredListings
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = null,
                            lastRefreshTime = System.currentTimeMillis(),
                        )

                    FirebaseCrashlytics.getInstance()
                        .log("Direct Parse query successful: ${directListings.size} listings loaded")
                    return
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Direct Parse query failed", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            // Removed fetchListingsSafely fallback logic
        } catch (e: Exception) {
            android.util.Log.e("MarketplaceVM", "Parse initialization failed", e)
            loadMockData() // This will now just set an error state
        }
    }

    private fun loadMockData() {
        android.util.Log.d("MarketplaceVM", "Attempted to load mock data. Setting error state.")
        _listings.value = emptyList()
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                error = "No listings found or network error. Please try again.",
                lastRefreshTime = System.currentTimeMillis(),
            )
        FirebaseCrashlytics.getInstance().log("Mock data fallback triggered - no Parse data.")
    }

    fun refreshListings() {
        // Clear cache before refreshing to ensure fresh data
        try {
            android.util.Log.d("MarketplaceVM", "Refreshing listings - clearing cache")
            ParseQuery.clearAllCachedResults()
        } catch (e: Exception) {
            android.util.Log.e("MarketplaceVM", "Failed to clear cache", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        loadListings()
    }

    fun createListing(
        breed: String,
        age: Int,
        price: Double,
        description: String,
        fatherId: String = "N/A",
        motherId: String = "N/A",
        vaccinations: List<String> = emptyList(),
        isBreeder: Boolean = false,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                FirebaseCrashlytics.getInstance().log("Creating new marketplace listing: $breed")

                val listing = MarketplaceListingParse()
                listing.breed = breed
                listing.age = age
                listing.price = price.toString()
                // description is not directly in MarketplaceListingParse, assuming it's part of SafeListing conversion or not stored directly
                // For now, let's add it as a custom field if needed
                // listing.put("description", description)

                listing.fatherId = fatherId
                listing.motherId = motherId
                listing.vaccinations = vaccinations
                listing.isBreeder = isBreeder
                listing.isActive = true
                listing.isBloodlineVerified = fatherId != "N/A" && motherId != "N/A"

                // Set current user as owner
                listing.owner = ParseUser.getCurrentUser()

                // Set default growth updates as empty list
                listing.growthUpdates = emptyList()

                suspendCoroutine<Unit> { continuation ->
                    listing.saveInBackground { e ->
                        if (e == null) {
                            FirebaseCrashlytics.getInstance()
                                .log("Listing created successfully: ${listing.objectId}")
                            onSuccess(listing.objectId)
                            continuation.resume(Unit)
                        } else {
                            val errorMessage = e.message ?: "Failed to create listing"
                            FirebaseCrashlytics.getInstance().recordException(e)
                            onError(errorMessage)
                            continuation.resumeWithException(e)
                        }
                    }
                }
                refreshListings() // Refresh listings after creation
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to create listing"
                onError(errorMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun placeBid(
        listingId: String,
        bidAmount: Double,
        onResult: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                FirebaseCrashlytics.getInstance()
                    .log("Placing bid on listing: $listingId, amount: $bidAmount")

                val bid = com.parse.ParseObject("Bid")
                bid.put("listingId", listingId)
                bid.put("bidAmount", bidAmount)
                bid.put("bidder", ParseUser.getCurrentUser())
                bid.put("status", "ACTIVE")
                bid.put("bidTime", java.util.Date())

                // Get listing to validate bid
                val listingQuery = ParseQuery(MarketplaceListingParse::class.java)
                suspendCoroutine<Unit> { continuation ->
                    listingQuery.getInBackground(listingId) { listing, error ->
                        if (error == null && listing != null) {
                            val currentPrice = listing.price?.toDoubleOrNull() ?: 0.0
                            if (bidAmount > currentPrice) {
                                // Valid bid - save it
                                bid.saveInBackground { saveError ->
                                    if (saveError == null) {
                                        FirebaseCrashlytics.getInstance()
                                            .log("Bid placed successfully: ${bid.objectId}")
                                        onResult(true, "Bid placed successfully")
                                        continuation.resume(Unit)
                                    } else {
                                        val errorMessage = saveError.message ?: "Failed to place bid"
                                        FirebaseCrashlytics.getInstance().recordException(saveError)
                                        onResult(false, errorMessage)
                                        continuation.resumeWithException(saveError)
                                    }
                                }
                            } else {
                                onResult(
                                    false,
                                    "Bid amount must be higher than current price (₹$currentPrice)",
                                )
                                continuation.resume(Unit) // Resume to avoid hanging
                            }
                        } else {
                            val errorMessage = error?.message ?: "Listing not found"
                            FirebaseCrashlytics.getInstance()
                                .recordException(error ?: Exception("Listing not found"))
                            onResult(false, errorMessage)
                            continuation.resumeWithException(error ?: Exception("Listing not found"))
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to place bid"
                onResult(false, errorMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun updateFilters(newFilters: MarketplaceFilters) {
        _filters.value = newFilters
        // Re-apply filters to current listings
        val currentListings = _listings.value
        _listings.value = applyFilters(currentListings)

        // Log filter usage for analytics
        FirebaseCrashlytics.getInstance().log("Marketplace filters applied: $newFilters")
    }

    fun searchListings(query: String) {
        val currentFilters = _filters.value
        updateFilters(currentFilters.copy(searchQuery = query))
    }

    fun filterByBreed(breed: String) {
        val currentFilters = _filters.value
        updateFilters(currentFilters.copy(selectedBreed = breed))
    }

    fun filterByPriceRange(
        minPrice: Double,
        maxPrice: Double,
    ) {
        val currentFilters = _filters.value
        updateFilters(currentFilters.copy(minPrice = minPrice, maxPrice = maxPrice))
    }

    fun toggleVerifiedOnly() {
        val currentFilters = _filters.value
        updateFilters(currentFilters.copy(verifiedOnly = !currentFilters.verifiedOnly))
    }

    fun toggleBloodlineOnly() {
        val currentFilters = _filters.value
        updateFilters(currentFilters.copy(bloodlineOnly = !currentFilters.bloodlineOnly))
    }

    private fun applyFilters(listings: List<SafeListing>): List<SafeListing> {
        val currentFilters = _filters.value
        return listings.filter {
            (currentFilters.searchQuery.isBlank() ||
                it.breed.contains(currentFilters.searchQuery, ignoreCase = true) ||
                it.owner.contains(currentFilters.searchQuery, ignoreCase = true)) &&
                (currentFilters.selectedBreed.isBlank() ||
                    it.breed.equals(currentFilters.selectedBreed, ignoreCase = true)) &&
                (it.price >= currentFilters.minPrice && it.price <= currentFilters.maxPrice) &&
                (!currentFilters.verifiedOnly || it.isBloodlineVerified) &&
                (!currentFilters.bloodlineOnly || it.isBloodlineVerified)
        }
    }

    fun setContext(context: Context) {
        _uiState.value = _uiState.value.copy(context = context)
    }
}

data class MarketplaceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastRefreshTime: Long = 0L,
    val context: Context? = null,
)

data class MarketplaceFilters(
    val searchQuery: String = "",
    val selectedBreed: String = "",
    val minPrice: Double = 0.0,
    val maxPrice: Double = 100000.0, // A reasonable upper limit
    val verifiedOnly: Boolean = false,
    val bloodlineOnly: Boolean = false,
)
