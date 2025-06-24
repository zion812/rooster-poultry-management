package com.example.rooster.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.GrowthUpdate
import com.example.rooster.NetworkQualityLevel
import com.example.rooster.SafeListing
import com.example.rooster.assessNetworkQualitySafely
import com.example.rooster.data.MarketplaceListing
import com.example.rooster.fetchListingsSafely
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {
    fun getListingById(listingId: String): StateFlow<MarketplaceListing?> {
        val _listing = MutableStateFlow<MarketplaceListing?>(null)
        viewModelScope.launch {
            // Simulate fetching data
            val dummyListing =
                MarketplaceListing(
                    id = listingId,
                    title = "Sample Listing $listingId",
                    description = "This is a detailed description for sample listing $listingId. It provides information about the product, its features, and benefits.",
                    price = 123.45,
                    imageUrl = "https://via.placeholder.com/150",
                    sellerId = "seller123",
                    contactInfo = "seller@example.com",
                )
            _listing.value = dummyListing
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

    // Network quality state
    private val _networkQuality = MutableStateFlow(NetworkQualityLevel.FAIR)
    val networkQuality: StateFlow<NetworkQualityLevel> = _networkQuality.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Log debugging information
                android.util.Log.d("MarketplaceVM", "Starting to load listings...")

                // Assess network quality
                val context = _uiState.value.context
                context?.let {
                    val quality = assessNetworkQualitySafely(it)
                    _networkQuality.value = quality
                    android.util.Log.d("MarketplaceVM", "Network quality: $quality")
                }

                // Try Parse backend first, then fallback to mock data
                if (context != null) {
                    loadFromParseWithFallback()
                } else {
                    // If no context available, use mock data immediately
                    android.util.Log.w("MarketplaceVM", "No context available, loading mock data")
                    loadMockData()
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Error in loadListings", e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred",
                    )
                // Load mock data as final fallback
                loadMockData()
            }
        }
    }

    private suspend fun loadFromParseWithFallback() {
        try {
            // Check if Parse is initialized
            val currentUser = com.parse.ParseUser.getCurrentUser()
            android.util.Log.d("MarketplaceVM", "Parse current user: ${currentUser?.objectId}")
            android.util.Log.d(
                "MarketplaceVM",
                "Parse current user username: ${currentUser?.username}",
            )

            // First, try to fetch listings directly without network-aware strategy
            val directQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
            directQuery.include("owner")
            directQuery.whereEqualTo("isActive", true)
            directQuery.orderByDescending("createdAt")
            directQuery.limit = 50
            directQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ELSE_CACHE

            android.util.Log.d("MarketplaceVM", "Executing direct Parse query for listings...")

            try {
                val directListings = directQuery.find()
                android.util.Log.d(
                    "MarketplaceVM",
                    "Direct query returned ${directListings.size} raw listings",
                )

                // Log each listing for debugging
                directListings.forEachIndexed { index, parseObject ->
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
                    val convertedListings = directListings.map { SafeListing.fromParseObject(it) }
                    android.util.Log.d(
                        "MarketplaceVM",
                        "Successfully converted ${convertedListings.size} listings",
                    )

                    // Log converted listings
                    convertedListings.forEachIndexed { index, safeListing ->
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
            }

            // If direct query failed or returned empty, try the network-aware strategy
            android.util.Log.d("MarketplaceVM", "Falling back to network-aware strategy...")

            // Fetch listings with network-aware strategy
            fetchListingsSafely(
                networkQuality = _networkQuality.value,
                onLoading = { isLoading ->
                    _uiState.value = _uiState.value.copy(isLoading = isLoading)
                },
                onSuccess = { listings ->
                    android.util.Log.d(
                        "MarketplaceVM",
                        "Network-aware strategy loaded ${listings.size} listings",
                    )
                    if (listings.isNotEmpty()) {
                        _listings.value = applyFilters(listings)
                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                error = null,
                                lastRefreshTime = System.currentTimeMillis(),
                            )
                    } else {
                        android.util.Log.w(
                            "MarketplaceVM",
                            "Network-aware strategy returned empty listings, loading mock data",
                        )
                        loadMockData()
                    }
                    FirebaseCrashlytics.getInstance()
                        .log("Marketplace listings loaded: ${listings.size}")
                },
                onError = { error ->
                    android.util.Log.e("MarketplaceVM", "Network-aware strategy failed: $error")
                    // Load mock data when Parse fails
                    loadMockData()
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Marketplace load error: $error"))
                },
            )
        } catch (e: Exception) {
            android.util.Log.e("MarketplaceVM", "Parse initialization failed", e)
            loadMockData()
        }
    }

    private fun loadMockData() {
        try {
            android.util.Log.d("MarketplaceVM", "Loading mock data as fallback")
            val mockListings = getMockListings()
            _listings.value = applyFilters(mockListings)
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    lastRefreshTime = System.currentTimeMillis(),
                )
            android.util.Log.d(
                "MarketplaceVM",
                "Successfully loaded ${mockListings.size} mock listings",
            )
        } catch (e: Exception) {
            android.util.Log.e("MarketplaceVM", "Failed to load mock data", e)
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load listings. Please try again.",
                )
        }
    }

    private fun getMockListings(): List<SafeListing> {
        return listOf(
            SafeListing(
                id = "mock1",
                imageUrl = "",
                breed = "Kadaknath Rooster",
                age = 12,
                price = 2500.0,
                owner = "రాము గారు",
                sellerId = "seller1",
                fatherId = "KDF001",
                motherId = "KDM002",
                vaccinations = listOf("Mareks (Day 1)", "Ranikhet (Week 1)", "IBD (Week 2)"),
                growthUpdates =
                    listOf(
                        GrowthUpdate(week = 4, weight = 0.5),
                        GrowthUpdate(week = 8, weight = 1.2),
                        GrowthUpdate(week = 12, weight = 1.8),
                    ),
                isBreeder = true,
                isBloodlineVerified = true,
            ),
            SafeListing(
                id = "mock2",
                imageUrl = "",
                breed = "Aseel Hen",
                age = 8,
                price = 1800.0,
                owner = "సీత గారు",
                sellerId = "seller2",
                fatherId = "ASF003",
                motherId = "ASM004",
                vaccinations = listOf("Mareks (Day 1)", "Ranikhet (Week 1)"),
                growthUpdates =
                    listOf(
                        GrowthUpdate(week = 4, weight = 0.4),
                        GrowthUpdate(week = 8, weight = 1.0),
                    ),
                isBreeder = false,
                isBloodlineVerified = true,
            ),
            SafeListing(
                id = "mock3",
                imageUrl = "",
                breed = "Brahma Chicks",
                age = 4,
                price = 300.0,
                owner = "కృష్ణ గారు",
                sellerId = "seller3",
                fatherId = "BRF005",
                motherId = "BRM006",
                vaccinations = listOf("Mareks (Day 1)"),
                growthUpdates = listOf(GrowthUpdate(week = 4, weight = 0.3)),
                isBreeder = false,
                isBloodlineVerified = false,
            ),
            SafeListing(
                id = "mock4",
                imageUrl = "",
                breed = "Country Chicken",
                age = 16,
                price = 1200.0,
                owner = "వెంకట్ రెడ్డి",
                sellerId = "seller4",
                fatherId = "CCF007",
                motherId = "CCM008",
                vaccinations = listOf("Newcastle", "IBD", "Fowl Pox"),
                growthUpdates =
                    listOf(
                        GrowthUpdate(week = 8, weight = 1.0),
                        GrowthUpdate(week = 12, weight = 1.5),
                        GrowthUpdate(week = 16, weight = 2.0),
                    ),
                isBreeder = true,
                isBloodlineVerified = false,
            ),
            SafeListing(
                id = "mock5",
                imageUrl = "",
                breed = "Rhode Island Red",
                age = 20,
                price = 2800.0,
                owner = "లక్ష్మి దేవి",
                sellerId = "seller5",
                fatherId = "RIR009",
                motherId = "RIR010",
                vaccinations =
                    listOf(
                        "Mareks",
                        "Newcastle",
                        "IBD",
                        "Fowl Pox",
                        "Infectious Bronchitis",
                    ),
                growthUpdates =
                    listOf(
                        GrowthUpdate(week = 8, weight = 1.1),
                        GrowthUpdate(week = 12, weight = 1.6),
                        GrowthUpdate(week = 16, weight = 2.1),
                        GrowthUpdate(week = 20, weight = 2.5),
                    ),
                isBreeder = true,
                isBloodlineVerified = true,
            ),
        )
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

    fun refreshListings() {
        // Clear cache before refreshing to ensure fresh data
        try {
            android.util.Log.d("MarketplaceVM", "Refreshing listings - clearing cache")
            com.parse.ParseQuery.clearAllCachedResults()
        } catch (e: Exception) {
            android.util.Log.e("MarketplaceVM", "Failed to clear cache", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        loadListings()
    }

    fun testParseConnection() {
        viewModelScope.launch {
            try {
                android.util.Log.d("MarketplaceVM", "Testing Parse connection...")

                // Check Parse initialization
                val parseInitialized =
                    try {
                        // Check if Parse is initialized by getting the application ID
                        com.parse.Parse.getApplicationContext()
                        true
                    } catch (e: Exception) {
                        android.util.Log.e("MarketplaceVM", "Parse not initialized", e)
                        false
                    }

                if (!parseInitialized) {
                    android.util.Log.e("MarketplaceVM", "Parse is not properly initialized")
                    loadMockData()
                    return@launch
                }

                // Check current user
                val currentUser = com.parse.ParseUser.getCurrentUser()
                android.util.Log.d("MarketplaceVM", "Current user: ${currentUser?.objectId}")
                android.util.Log.d(
                    "MarketplaceVM",
                    "Current user username: ${currentUser?.username}",
                )
                android.util.Log.d("MarketplaceVM", "User is authenticated: ${currentUser != null}")

                // Test basic Parse connectivity
                val testQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("_User")
                testQuery.limit = 1
                testQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ONLY

                val testResult = testQuery.find()
                android.util.Log.d(
                    "MarketplaceVM",
                    "Parse connection test successful: ${testResult.size} users found",
                )

                // Test Listing table specifically with different queries
                android.util.Log.d("MarketplaceVM", "Testing Listing table access...")

                // Query 1: Count all listings
                val countQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                val totalCount = countQuery.count()
                android.util.Log.d("MarketplaceVM", "Total listings in database: $totalCount")

                // Query 2: Get all listings (no filters)
                val allListingsQuery =
                    com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                allListingsQuery.limit = 10
                allListingsQuery.include("owner")
                allListingsQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ONLY

                val allListings = allListingsQuery.find()
                android.util.Log.d(
                    "MarketplaceVM",
                    "All listings query returned: ${allListings.size} listings",
                )

                // Query 3: Get only active listings
                val activeQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                activeQuery.whereEqualTo("isActive", true)
                activeQuery.limit = 10
                activeQuery.include("owner")
                activeQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ONLY

                val activeListings = activeQuery.find()
                android.util.Log.d(
                    "MarketplaceVM",
                    "Active listings query returned: ${activeListings.size} listings",
                )

                // Query 4: Get listings created by current user
                if (currentUser != null) {
                    val userListingsQuery =
                        com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                    userListingsQuery.whereEqualTo("owner", currentUser)
                    userListingsQuery.limit = 10
                    userListingsQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ONLY

                    val userListings = userListingsQuery.find()
                    android.util.Log.d(
                        "MarketplaceVM",
                        "Current user's listings: ${userListings.size} listings",
                    )
                }

                // Log sample data from first listing if available
                if (allListings.isNotEmpty()) {
                    val sampleListing = allListings[0]
                    android.util.Log.d("MarketplaceVM", "Sample listing data:")
                    android.util.Log.d("MarketplaceVM", "  - ID: ${sampleListing.objectId}")
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Breed: ${sampleListing.getString("breed")}",
                    )
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Price: ${sampleListing.getString("price")}",
                    )
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Active: ${sampleListing.getBoolean("isActive")}",
                    )
                    android.util.Log.d(
                        "MarketplaceVM",
                        "  - Owner: ${sampleListing.getParseUser("owner")?.objectId}",
                    )
                    android.util.Log.d("MarketplaceVM", "  - Created: ${sampleListing.createdAt}")
                }

                if (totalCount == 0) {
                    android.util.Log.w(
                        "MarketplaceVM",
                        "Listing table is completely empty - no data exists",
                    )
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "No listings found in database. Create some listings first.",
                        )
                } else if (activeListings.isEmpty()) {
                    android.util.Log.w(
                        "MarketplaceVM",
                        "No active listings found - all listings may be inactive",
                    )
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "No active listings found. All listings appear to be inactive.",
                        )
                } else {
                    android.util.Log.d(
                        "MarketplaceVM",
                        "Parse connection test successful - loading real data",
                    )
                    // Force reload with the successful connection
                    loadFromParseWithFallback()
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Parse connection test failed", e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Parse connection failed: ${e.message}",
                    )
                // Force load mock data if Parse is completely unavailable
                loadMockData()
            }
        }
    }

    fun forceLoadMockData() {
        android.util.Log.d("MarketplaceVM", "Forcing mock data load by user request")
        loadMockData()
    }

    fun createTestListing() {
        viewModelScope.launch {
            try {
                android.util.Log.d("MarketplaceVM", "Creating test listing...")

                val currentUser = com.parse.ParseUser.getCurrentUser()
                if (currentUser == null) {
                    android.util.Log.e(
                        "MarketplaceVM",
                        "Cannot create test listing - no user logged in",
                    )
                    _uiState.value =
                        _uiState.value.copy(
                            error = "Please log in to create test listings",
                        )
                    return@launch
                }

                val testListing = com.parse.ParseObject("Listing")
                testListing.put("breed", "Test Kadaknath")
                testListing.put("age", 15)
                testListing.put("price", "3000")
                testListing.put(
                    "description",
                    "Test listing created for debugging - Premium Kadaknath rooster",
                )
                testListing.put("fatherId", "TEST_F001")
                testListing.put("motherId", "TEST_M001")
                testListing.put("vaccinations", listOf("Mareks", "Newcastle", "IBD"))
                testListing.put("isBreeder", true)
                testListing.put("isActive", true)
                testListing.put("isBloodlineVerified", true)
                testListing.put("owner", currentUser)
                testListing.put("growthUpdates", emptyList<Map<String, Any>>())

                testListing.saveInBackground { e ->
                    if (e == null) {
                        android.util.Log.d(
                            "MarketplaceVM",
                            "Test listing created successfully: ${testListing.objectId}",
                        )
                        // Refresh listings to show the new test listing
                        refreshListings()
                    } else {
                        android.util.Log.e("MarketplaceVM", "Failed to create test listing", e)
                        _uiState.value =
                            _uiState.value.copy(
                                error = "Failed to create test listing: ${e.message}",
                            )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketplaceVM", "Error creating test listing", e)
                _uiState.value =
                    _uiState.value.copy(
                        error = "Error creating test listing: ${e.message}",
                    )
            }
        }
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
                // Implementation for creating new listing with Parse backend
                FirebaseCrashlytics.getInstance().log("Creating new marketplace listing: $breed")

                val listing = com.parse.ParseObject("Listing")
                listing.put("breed", breed)
                listing.put("age", age)
                listing.put("price", price.toString()) // Convert to string to match backend schema
                listing.put("description", description)
                listing.put("fatherId", fatherId)
                listing.put("motherId", motherId)
                listing.put("vaccinations", vaccinations)
                listing.put("isBreeder", isBreeder)
                listing.put("isActive", true)
                listing.put("isBloodlineVerified", fatherId != "N/A" && motherId != "N/A")

                // Set current user as owner
                listing.put("owner", com.parse.ParseUser.getCurrentUser())

                // Set default growth updates as empty list
                listing.put("growthUpdates", emptyList<Map<String, Any>>())

                listing.saveInBackground { e ->
                    if (e == null) {
                        FirebaseCrashlytics.getInstance()
                            .log("Listing created successfully: ${listing.objectId}")

                        // Clear Parse cache to ensure fresh data loads
                        try {
                            com.parse.ParseQuery.clearAllCachedResults()
                        } catch (cacheException: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(cacheException)
                        }

                        onSuccess(listing.objectId)
                        // Refresh listings after creation with cache bypass
                        refreshListingsWithCacheBypass()
                    } else {
                        val errorMessage = e.message ?: "Failed to create listing"
                        FirebaseCrashlytics.getInstance().recordException(e)
                        onError(errorMessage)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to create listing"
                onError(errorMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun refreshListingsWithCacheBypass() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Force network fetch to bypass cache
                val query = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                query.include("owner")
                query.whereEqualTo("isActive", true)
                query.orderByDescending("createdAt")
                query.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ONLY
                query.limit = 30

                val parseListings = query.find()
                val freshListings = parseListings.map { SafeListing.fromParseObject(it) }

                _listings.value = applyFilters(freshListings)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        lastRefreshTime = System.currentTimeMillis(),
                    )

                FirebaseCrashlytics.getInstance()
                    .log("Fresh listings loaded after creation: ${freshListings.size}")
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to refresh listings",
                    )
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
                // Implementation for placing bid with Parse backend
                FirebaseCrashlytics.getInstance()
                    .log("Placing bid on listing: $listingId, amount: $bidAmount")

                val bid = com.parse.ParseObject("Bid")
                bid.put("listingId", listingId)
                bid.put("bidAmount", bidAmount)
                bid.put("bidder", com.parse.ParseUser.getCurrentUser())
                bid.put("status", "ACTIVE")
                bid.put("bidTime", java.util.Date())

                // Get listing to validate bid
                val listingQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Listing")
                listingQuery.getInBackground(listingId) { listing, error ->
                    if (error == null && listing != null) {
                        val currentPrice = listing.getString("price")?.toDoubleOrNull() ?: 0.0
                        if (bidAmount > currentPrice) {
                            // Valid bid - save it
                            bid.saveInBackground { saveError ->
                                if (saveError == null) {
                                    FirebaseCrashlytics.getInstance()
                                        .log("Bid placed successfully: ${bid.objectId}")
                                    onResult(true, "Bid placed successfully")
                                } else {
                                    val errorMessage = saveError.message ?: "Failed to place bid"
                                    FirebaseCrashlytics.getInstance().recordException(saveError)
                                    onResult(false, errorMessage)
                                }
                            }
                        } else {
                            onResult(
                                false,
                                "Bid amount must be higher than current price (₹$currentPrice)",
                            )
                        }
                    } else {
                        val errorMessage = error?.message ?: "Listing not found"
                        FirebaseCrashlytics.getInstance()
                            .recordException(error ?: Exception("Listing not found"))
                        onResult(false, errorMessage)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to place bid"
                onResult(false, errorMessage)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun applyFilters(listings: List<SafeListing>): List<SafeListing> {
        val currentFilters = _filters.value

        return listings.filter { listing ->
            // Search query filter
            if (currentFilters.searchQuery.isNotEmpty()) {
                val query = currentFilters.searchQuery.lowercase()
                if (!listing.breed.lowercase().contains(query) &&
                    !listing.owner.lowercase().contains(query)
                ) {
                    return@filter false
                }
            }

            // Breed filter
            if (currentFilters.selectedBreed != "All" &&
                listing.breed != currentFilters.selectedBreed
            ) {
                return@filter false
            }

            // Price range filter
            if (listing.price < currentFilters.minPrice ||
                listing.price > currentFilters.maxPrice
            ) {
                return@filter false
            }

            // Verified only filter
            if (currentFilters.verifiedOnly && !listing.isBloodlineVerified) {
                return@filter false
            }

            // Bloodline only filter
            if (currentFilters.bloodlineOnly &&
                (listing.fatherId == "N/A" || listing.motherId == "N/A")
            ) {
                return@filter false
            }

            // Age group filter
            if (currentFilters.selectedAgeGroup != "All") {
                val ageInWeeks = listing.age
                val matchesAgeGroup =
                    when (currentFilters.selectedAgeGroup) {
                        "Chicks" -> ageInWeeks <= 8
                        "Young" -> ageInWeeks in 9..20
                        "Adult" -> ageInWeeks > 20
                        else -> true
                    }
                if (!matchesAgeGroup) return@filter false
            }

            true
        }.sortedWith(
            compareBy<SafeListing> { item -> // Explicitly name the lambda parameter 'item'
                when (currentFilters.sortBy) {
                    "Price Low to High" -> item.price // Use 'item' instead of 'listing'
                    "Price High to Low" -> -item.price // Use 'item' instead of 'listing'
                    "Newest" -> -item.createdAt.time // Use 'item' instead of 'listing'
                    "Oldest" -> item.createdAt.time // Use 'item' instead of 'listing'
                    else -> -item.createdAt.time // Default to newest // Use 'item' instead of 'listing'
                }
            },
        )
    }

    fun getInstance(context: Context) {
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
    val selectedBreed: String = "All",
    val selectedAgeGroup: String = "All",
    val minPrice: Double = 0.0,
    val maxPrice: Double = 50000.0,
    val verifiedOnly: Boolean = false,
    val bloodlineOnly: Boolean = false,
    val sortBy: String = "Newest",
)
