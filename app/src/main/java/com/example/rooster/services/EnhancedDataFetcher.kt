// use context7
package com.example.rooster.services

import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class EnhancedDataFetcher
    @Inject
    constructor() {
        companion object {
            private const val TAG = "EnhancedDataFetcher"

            // Parse class names
            private const val MARKETPLACE_LISTING = "MarketplaceListing"
            private const val FOWL_RECORD = "FowlRecord"
            private const val TRANSACTION = "Transaction"
            private const val COMMUNITY_POST = "CommunityPost"
            private const val USER_PROFILE = "UserProfile"
            private const val FARM_ANALYTICS = "FarmAnalytics"
            private const val IOT_SENSOR_DATA = "IoTSensorData"
            private const val HEALTH_RECORD = "HealthRecord"
            private const val AUCTION_LISTING = "AuctionListing"
            private const val VERIFICATION_REQUEST = "VerificationRequest"
        }

        // Marketplace Operations
        suspend fun fetchMarketplaceListings(
            category: String? = null,
            searchQuery: String? = null,
            location: String? = null,
            limit: Int = 20,
            skip: Int = 0,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(MARKETPLACE_LISTING)
                                .apply {
                                    setLimit(limit)
                                    setSkip(skip)
                                    orderByDescending("createdAt")
                                    include("seller")

                                    // Apply filters
                                    category?.let { whereEqualTo("category", it) }

                                    searchQuery?.let { query ->
                                        val titleQuery =
                                            ParseQuery.getQuery<ParseObject>(MARKETPLACE_LISTING)
                                                .whereContains("title", query)
                                        val descriptionQuery =
                                            ParseQuery.getQuery<ParseObject>(MARKETPLACE_LISTING)
                                                .whereContains("description", query)
                                        whereMatchesQuery(
                                            "objectId",
                                            ParseQuery.or(listOf(titleQuery, descriptionQuery)),
                                        )
                                    }

                                    location?.let { whereContains("location", it) }
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} marketplace listings")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching marketplace listings", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchMarketplaceListings", e)
                    Result.failure(e)
                }
            }

        suspend fun createMarketplaceListing(
            title: String,
            description: String,
            price: Double,
            category: String,
            location: String,
            imageUrls: List<String> = emptyList(),
            availability: String,
        ): Result<ParseObject> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val listing =
                            ParseObject(MARKETPLACE_LISTING).apply {
                                put("title", title)
                                put("description", description)
                                put("price", price)
                                put("category", category)
                                put("location", location)
                                put("imageUrls", imageUrls)
                                put("availability", availability)
                                put("seller", ParseUser.getCurrentUser())
                                put("isActive", true)
                                put("viewCount", 0)
                            }

                        listing.saveInBackground { exception ->
                            if (exception == null) {
                                Log.d(TAG, "Created marketplace listing: ${listing.objectId}")
                                continuation.resume(Result.success(listing))
                            } else {
                                Log.e(TAG, "Error creating marketplace listing", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in createMarketplaceListing", e)
                    Result.failure(e)
                }
            }

        // Farm Management Operations
        suspend fun fetchFowlRecords(
            farmerId: String? = null,
            isActive: Boolean = true,
            breed: String? = null,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(FOWL_RECORD)
                                .apply {
                                    orderByDescending("createdAt")
                                    include("owner")

                                    farmerId?.let {
                                        val userQuery =
                                            ParseQuery.getQuery<ParseUser>("_User")
                                                .whereEqualTo("objectId", it)
                                        whereMatchesQuery("owner", userQuery)
                                    } ?: run {
                                        whereEqualTo("owner", ParseUser.getCurrentUser())
                                    }

                                    whereEqualTo("isActive", isActive)
                                    breed?.let { whereEqualTo("breed", it) }
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} fowl records")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching fowl records", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchFowlRecords", e)
                    Result.failure(e)
                }
            }

        suspend fun createFowlRecord(
            breed: String,
            gender: String,
            age: Int,
            weight: Double,
            healthStatus: String,
            notes: String = "",
        ): Result<ParseObject> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val fowlRecord =
                            ParseObject(FOWL_RECORD).apply {
                                put("breed", breed)
                                put("gender", gender)
                                put("age", age)
                                put("weight", weight)
                                put("healthStatus", healthStatus)
                                put("notes", notes)
                                put("owner", ParseUser.getCurrentUser())
                                put("isActive", true)
                                put("dateAdded", java.util.Date())
                            }

                        fowlRecord.saveInBackground { exception ->
                            if (exception == null) {
                                Log.d(TAG, "Created fowl record: ${fowlRecord.objectId}")
                                continuation.resume(Result.success(fowlRecord))
                            } else {
                                Log.e(TAG, "Error creating fowl record", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in createFowlRecord", e)
                    Result.failure(e)
                }
            }

        // Community Operations
        suspend fun fetchCommunityPosts(
            limit: Int = 20,
            skip: Int = 0,
            category: String? = null,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(COMMUNITY_POST)
                                .apply {
                                    setLimit(limit)
                                    setSkip(skip)
                                    orderByDescending("createdAt")
                                    include("author")

                                    category?.let { whereEqualTo("category", it) }
                                    whereEqualTo("isActive", true)
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} community posts")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching community posts", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchCommunityPosts", e)
                    Result.failure(e)
                }
            }

        suspend fun createCommunityPost(
            content: String,
            category: String,
            imageUrl: String? = null,
            tags: List<String> = emptyList(),
        ): Result<ParseObject> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val post =
                            ParseObject(COMMUNITY_POST).apply {
                                put("content", content)
                                put("category", category)
                                put("author", ParseUser.getCurrentUser())
                                put("imageUrl", imageUrl ?: "")
                                put("tags", tags.toList())
                                put("likeCount", 0)
                                put("commentCount", 0)
                                put("shareCount", 0)
                                put("isActive", true)
                            }

                        post.saveInBackground { exception ->
                            if (exception == null) {
                                Log.d(TAG, "Created community post: ${post.objectId}")
                                continuation.resume(Result.success(post))
                            } else {
                                Log.e(TAG, "Error creating community post", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in createCommunityPost", e)
                    Result.failure(e)
                }
            }

        // Analytics Operations
        suspend fun fetchFarmAnalytics(farmerId: String? = null): Result<ParseObject?> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(FARM_ANALYTICS)
                                .apply {
                                    farmerId?.let {
                                        val userQuery =
                                            ParseQuery.getQuery<ParseUser>("_User")
                                                .whereEqualTo("objectId", it)
                                        whereMatchesQuery("farmer", userQuery)
                                    } ?: run {
                                        whereEqualTo("farmer", ParseUser.getCurrentUser())
                                    }

                                    orderByDescending("updatedAt")
                                    setLimit(1)
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                val analytics = objects.firstOrNull()
                                Log.d(TAG, "Fetched farm analytics: ${analytics?.objectId}")
                                continuation.resume(Result.success(analytics))
                            } else {
                                Log.e(TAG, "Error fetching farm analytics", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchFarmAnalytics", e)
                    Result.failure(e)
                }
            }

        // Auction Operations
        suspend fun fetchActiveAuctions(
            category: String? = null,
            limit: Int = 20,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(AUCTION_LISTING)
                                .apply {
                                    setLimit(limit)
                                    orderByAscending("endTime")
                                    include("seller")

                                    whereEqualTo("status", "ACTIVE")
                                    whereGreaterThan("endTime", java.util.Date())

                                    category?.let { whereEqualTo("category", it) }
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} active auctions")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching active auctions", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchActiveAuctions", e)
                    Result.failure(e)
                }
            }

        // Health Management Operations
        suspend fun fetchHealthRecords(
            fowlId: String? = null,
            recordType: String? = null,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(HEALTH_RECORD)
                                .apply {
                                    orderByDescending("recordDate")
                                    include("fowl")

                                    fowlId?.let {
                                        val fowlQuery =
                                            ParseQuery.getQuery<ParseObject>(FOWL_RECORD)
                                                .whereEqualTo("objectId", it)
                                        whereMatchesQuery("fowl", fowlQuery)
                                    }

                                    recordType?.let { whereEqualTo("recordType", it) }
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} health records")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching health records", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchHealthRecords", e)
                    Result.failure(e)
                }
            }

        // Verification Operations for High-Level Users
        suspend fun fetchPendingVerifications(
            verificationType: String? = null,
            limit: Int = 50,
        ): Result<List<ParseObject>> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val query =
                            ParseQuery.getQuery<ParseObject>(VERIFICATION_REQUEST)
                                .apply {
                                    setLimit(limit)
                                    orderByAscending("createdAt")
                                    include("user")

                                    whereEqualTo("status", "PENDING")
                                    verificationType?.let { whereEqualTo("verificationType", it) }
                                }

                        query.findInBackground { objects, exception ->
                            if (exception == null) {
                                Log.d(TAG, "Fetched ${objects.size} pending verifications")
                                continuation.resume(Result.success(objects))
                            } else {
                                Log.e(TAG, "Error fetching pending verifications", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in fetchPendingVerifications", e)
                    Result.failure(e)
                }
            }

        // User Profile Operations
        suspend fun updateUserProfile(
            displayName: String? = null,
            location: String? = null,
            phoneNumber: String? = null,
            farmSize: String? = null,
            experience: String? = null,
        ): Result<ParseObject> =
            withContext(Dispatchers.IO) {
                try {
                    suspendCancellableCoroutine { continuation ->
                        val user = ParseUser.getCurrentUser()

                        displayName?.let { user.put("displayName", it) }
                        location?.let { user.put("location", it) }
                        phoneNumber?.let { user.put("phoneNumber", it) }
                        farmSize?.let { user.put("farmSize", it) }
                        experience?.let { user.put("experience", it) }

                        user.saveInBackground { exception ->
                            if (exception == null) {
                                Log.d(TAG, "Updated user profile: ${user.objectId}")
                                continuation.resume(Result.success(user))
                            } else {
                                Log.e(TAG, "Error updating user profile", exception)
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in updateUserProfile", e)
                    Result.failure(e)
                }
            }

        // Batch operations for performance
        suspend fun batchFetchUserData(): Result<Map<String, List<ParseObject>>> =
            withContext(Dispatchers.IO) {
                try {
                    val results = mutableMapOf<String, List<ParseObject>>()

                    // Fetch user's fowl records
                    fetchFowlRecords().getOrNull()?.let {
                        results["fowlRecords"] = it
                    }

                    // Fetch user's marketplace listings
                    val userListings =
                        fetchMarketplaceListings().getOrNull()?.filter {
                            it.getParseUser("seller")?.objectId == ParseUser.getCurrentUser()?.objectId
                        } ?: emptyList()
                    results["myListings"] = userListings

                    // Fetch farm analytics
                    fetchFarmAnalytics().getOrNull()?.let {
                        results["analytics"] = listOf(it)
                    } ?: run {
                        results["analytics"] = emptyList()
                    }

                    Log.d(TAG, "Batch fetched user data with ${results.size} categories")
                    Result.success(results)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in batchFetchUserData", e)
                    Result.failure(e)
                }
            }
    }
