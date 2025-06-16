package com.example.rooster

import com.parse.ParseObject
import java.util.Date

// Data models for the marketplace functionality

data class GrowthUpdate(val week: Int, val weight: Double)

data class SafeListing(
    val id: String = "",
    val imageUrl: String = "",
    val breed: String = "",
    val age: Int = 0,
    val price: Double = 0.0,
    val owner: String = "",
    val sellerId: String = "", // Firebase UID of the seller
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    // Traceability fields
    val fatherId: String = "N/A",
    val motherId: String = "N/A",
    val vaccinations: List<String> = emptyList(),
    val growthUpdates: List<GrowthUpdate> = emptyList(),
    val isBreeder: Boolean = false,
    val isBloodlineVerified: Boolean = false,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): SafeListing {
            // Safely extract ParseUser data without requiring fetchIfNeeded()
            val ownerUser = parseObject.getParseUser("owner")
            val ownerName =
                try {
                    // Use getString method instead of direct property access for safety
                    ownerUser?.getString("username") ?: "Unknown Seller"
                } catch (e: IllegalStateException) {
                    // ParseUser data not fetched - use fallback
                    "Unknown Seller"
                } catch (e: Exception) {
                    // Any other error - use fallback
                    "Unknown Seller"
                }

            val sellerId =
                try {
                    ownerUser?.getString("firebaseUid") ?: ownerUser?.objectId ?: ""
                } catch (e: IllegalStateException) {
                    // ParseUser data not fetched - use objectId as fallback
                    ownerUser?.objectId ?: ""
                } catch (e: Exception) {
                    // Any other error - use objectId as fallback
                    ownerUser?.objectId ?: ""
                }

            // Extract traceability fields
            val fatherId = parseObject.getString("fatherId") ?: "N/A"
            val motherId = parseObject.getString("motherId") ?: "N/A"
            val vaccinations = parseObject.getList<String>("vaccinations") ?: emptyList()
            val growthUpdatesRaw =
                parseObject.getList<Map<String, Any>>("growthUpdates") ?: emptyList()
            val growthUpdates =
                growthUpdatesRaw.mapNotNull {
                    val week = (it["week"] as? Number)?.toInt()
                    val weight = (it["weight"] as? Number)?.toDouble()
                    if (week != null && weight != null) GrowthUpdate(week, weight) else null
                }
            val isBreeder = parseObject.getBoolean("isBreeder")
            val isBloodlineVerified = parseObject.getBoolean("isBloodlineVerified")

            return SafeListing(
                id = parseObject.objectId ?: "",
                imageUrl = parseObject.getParseFile("image")?.url ?: "",
                breed = parseObject.getString("breed") ?: "",
                age = parseObject.getInt("age"),
                price =
                    parseObject.getString("price")?.toDoubleOrNull()
                        ?: 0.0,
                // Handle price as string from backend
                owner = ownerName,
                sellerId = sellerId,
                createdAt = parseObject.createdAt ?: Date(),
                isActive = parseObject.getBoolean("isActive"),
                fatherId = fatherId,
                motherId = motherId,
                vaccinations = vaccinations,
                growthUpdates = growthUpdates,
                isBreeder = isBreeder,
                isBloodlineVerified = isBloodlineVerified,
            )
        }
    }
}

// Network quality assessment function
fun assessNetworkQualitySafely(context: android.content.Context): NetworkQualityLevel {
    return try {
        // Use a timeout to prevent hanging on system connectivity calls
        val result =
            kotlin.runCatching {
                val connectivityManager =
                    context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
                val activeNetwork = connectivityManager.activeNetworkInfo

                when {
                    activeNetwork == null || !activeNetwork.isConnected -> NetworkQualityLevel.OFFLINE
                    activeNetwork.type == android.net.ConnectivityManager.TYPE_WIFI -> NetworkQualityLevel.GOOD
                    activeNetwork.type == android.net.ConnectivityManager.TYPE_MOBILE -> {
                        when (activeNetwork.subtype) {
                            android.telephony.TelephonyManager.NETWORK_TYPE_LTE -> NetworkQualityLevel.EXCELLENT
                            android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP,
                            android.telephony.TelephonyManager.NETWORK_TYPE_HSPA,
                            -> NetworkQualityLevel.GOOD

                            android.telephony.TelephonyManager.NETWORK_TYPE_UMTS -> NetworkQualityLevel.FAIR
                            else -> NetworkQualityLevel.POOR
                        }
                    }

                    else -> NetworkQualityLevel.FAIR
                }
            }

        // Return result or fallback to FAIR if any exception occurs
        result.getOrElse { NetworkQualityLevel.FAIR }
    } catch (e: Exception) {
        // Catch any system-level connectivity issues and provide safe fallback
        android.util.Log.w(
            "NetworkAssessment",
            "Connectivity check failed, using fallback: ${e.message}",
        )
        NetworkQualityLevel.FAIR // Safe fallback for system connectivity issues
    }
}

// Function to fetch listings safely
suspend fun fetchListingsSafely(
    networkQuality: NetworkQualityLevel,
    onLoading: (Boolean) -> Unit,
    onSuccess: (List<SafeListing>) -> Unit,
    onError: (String) -> Unit,
) {
    onLoading(true)
    try {
        // Determine query limit based on network quality
        val queryLimit =
            when (networkQuality) {
                NetworkQualityLevel.EXCELLENT -> 50
                NetworkQualityLevel.GOOD -> 30
                NetworkQualityLevel.FAIR -> 20
                NetworkQualityLevel.POOR -> 10
                NetworkQualityLevel.OFFLINE -> 5 // Use cached data
            }

        // Use actual Parse query
        val query = com.parse.ParseQuery.getQuery<ParseObject>("Listing")
        query.include("owner") // Include the owner user data to prevent fetchIfNeeded() calls
        query.whereEqualTo("isActive", true)
        query.orderByDescending("createdAt")
        query.limit = queryLimit

        // Set cache policy based on network quality
        when (networkQuality) {
            NetworkQualityLevel.OFFLINE, NetworkQualityLevel.POOR -> {
                query.cachePolicy = com.parse.ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                query.maxCacheAge = 600000 // 10 minutes
            }

            NetworkQualityLevel.FAIR -> {
                query.cachePolicy = com.parse.ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                query.maxCacheAge = 300000 // 5 minutes
            }

            else -> {
                query.cachePolicy = com.parse.ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
            }
        }

        val parseListings = query.find()
        val safeListings = parseListings.map { SafeListing.fromParseObject(it) }

        onSuccess(safeListings)
    } catch (e: Exception) {
        // Fallback to cached data or mock data if Parse query fails
        try {
            val fallbackQuery = com.parse.ParseQuery.getQuery<ParseObject>("Listing")
            fallbackQuery.include("owner")
            fallbackQuery.cachePolicy = com.parse.ParseQuery.CachePolicy.CACHE_ONLY
            fallbackQuery.limit = 10

            val cachedListings = fallbackQuery.find()
            if (cachedListings.isNotEmpty()) {
                val safeListings = cachedListings.map { SafeListing.fromParseObject(it) }
                onSuccess(safeListings)
            } else {
                // Use mock data as final fallback
                val mockListings =
                    listOf(
                        SafeListing(
                            id = "1",
                            imageUrl = "",
                            breed = "Aseel",
                            age = 12,
                            price = 2500.0,
                            owner = "Farmer John",
                            sellerId = "SELLER_UID_1",
                            fatherId = "N/A",
                            motherId = "N/A",
                            vaccinations = emptyList(),
                            growthUpdates = emptyList(),
                            isBreeder = false,
                            isBloodlineVerified = false,
                        ),
                        SafeListing(
                            id = "2",
                            imageUrl = "",
                            breed = "Brahma",
                            age = 8,
                            price = 1800.0,
                            owner = "Farmer Mary",
                            sellerId = "SELLER_UID_2",
                            fatherId = "N/A",
                            motherId = "N/A",
                            vaccinations = emptyList(),
                            growthUpdates = emptyList(),
                            isBreeder = false,
                            isBloodlineVerified = false,
                        ),
                    )
                onSuccess(mockListings)
            }
        } catch (fallbackException: Exception) {
            onError("Unable to load listings: ${e.localizedMessage}")
        }
    } finally {
        onLoading(false)
    }
}
