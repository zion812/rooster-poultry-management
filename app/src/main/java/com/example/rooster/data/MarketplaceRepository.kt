package com.example.rooster.data

import com.example.rooster.GrowthUpdate
import com.example.rooster.SafeListing
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MarketplaceRepository {
    companion object {
        private const val LISTING_CLASS = "Listing"
        private const val BID_CLASS = "Bid"
    }

    suspend fun createListing(
        breed: String,
        age: Int,
        price: Double,
        description: String,
        imageFile: ParseFile? = null,
        fatherId: String = "N/A",
        motherId: String = "N/A",
        vaccinations: List<String> = emptyList(),
        growthUpdates: List<GrowthUpdate> = emptyList(),
        isBreeder: Boolean = false,
        isBloodlineVerified: Boolean = false,
    ): String =
        suspendCancellableCoroutine { continuation ->

            val listing =
                ParseObject(LISTING_CLASS).apply {
                    put("breed", breed)
                    put("age", age)
                    put("price", price)
                    put("description", description)
                    put("owner", ParseUser.getCurrentUser())
                    put("sellerId", ParseUser.getCurrentUser()?.objectId ?: "")
                    put("isActive", true)
                    put("fatherId", fatherId)
                    put("motherId", motherId)
                    put("vaccinations", vaccinations)
                    put("isBreeder", isBreeder)
                    put("isBloodlineVerified", isBloodlineVerified)

                    // Convert growth updates to Parse-compatible format
                    val growthUpdateMaps =
                        growthUpdates.map { update ->
                            mapOf(
                                "week" to update.week,
                                "weight" to update.weight,
                            )
                        }
                    put("growthUpdates", growthUpdateMaps)

                    // Add image if provided
                    imageFile?.let { put("image", it) }
                }

            listing.saveInBackground { e ->
                if (e == null) {
                    continuation.resume(listing.objectId)
                } else {
                    continuation.resumeWithException(e)
                }
            }
        }

    suspend fun fetchListings(
        limit: Int = 50,
        offset: Int = 0,
        breed: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        verifiedOnly: Boolean = false,
        isActive: Boolean = true,
    ): List<SafeListing> =
        suspendCancellableCoroutine { continuation ->

            val query =
                ParseQuery.getQuery<ParseObject>(LISTING_CLASS).apply {
                    include("owner")
                    whereEqualTo("isActive", isActive)
                    orderByDescending("createdAt")
                    this.limit = limit
                    skip = offset

                    // Apply filters
                    breed?.let { whereEqualTo("breed", it) }
                    minPrice?.let { whereGreaterThanOrEqualTo("price", it) }
                    maxPrice?.let { whereLessThanOrEqualTo("price", it) }
                    if (verifiedOnly) {
                        whereEqualTo("isBloodlineVerified", true)
                    }
                }

            query.findInBackground { objects, e ->
                if (e == null && objects != null) {
                    val listings =
                        objects.mapNotNull { parseObject ->
                            try {
                                SafeListing.fromParseObject(parseObject)
                            } catch (ex: Exception) {
                                // Log error but don't fail entire operation
                                null
                            }
                        }
                    continuation.resume(listings)
                } else {
                    continuation.resumeWithException(e ?: Exception("Unknown error"))
                }
            }
        }

    suspend fun fetchListingById(listingId: String): SafeListing? =
        suspendCancellableCoroutine { continuation ->
            val query =
                ParseQuery.getQuery<ParseObject>(LISTING_CLASS).apply {
                    include("owner")
                }

            query.getInBackground(listingId) { parseObject, e ->
                if (e == null && parseObject != null) {
                    try {
                        val listing = SafeListing.fromParseObject(parseObject)
                        continuation.resume(listing)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                } else {
                    continuation.resume(null)
                }
            }
        }

    suspend fun updateListing(
        listingId: String,
        updates: Map<String, Any>,
    ): Boolean =
        suspendCancellableCoroutine { continuation ->

            val query = ParseQuery.getQuery<ParseObject>(LISTING_CLASS)
            query.getInBackground(listingId) { parseObject, e ->
                if (e == null && parseObject != null) {
                    // Apply updates
                    updates.forEach { (key, value) ->
                        parseObject.put(key, value)
                    }

                    parseObject.saveInBackground { saveError ->
                        if (saveError == null) {
                            continuation.resume(true)
                        } else {
                            continuation.resumeWithException(saveError)
                        }
                    }
                } else {
                    continuation.resumeWithException(e ?: Exception("Listing not found"))
                }
            }
        }

    suspend fun deleteListing(listingId: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            // Soft delete by setting isActive to false
            val query = ParseQuery.getQuery<ParseObject>(LISTING_CLASS)
            query.getInBackground(listingId) { parseObject, e ->
                if (e == null && parseObject != null) {
                    parseObject.put("isActive", false)
                    parseObject.saveInBackground { saveError ->
                        if (saveError == null) {
                            continuation.resume(true)
                        } else {
                            continuation.resumeWithException(saveError)
                        }
                    }
                } else {
                    continuation.resumeWithException(e ?: Exception("Listing not found"))
                }
            }
        }

    suspend fun placeBid(
        listingId: String,
        bidAmount: Double,
        bidderMessage: String = "",
    ): String =
        suspendCancellableCoroutine { continuation ->

            val bid =
                ParseObject(BID_CLASS).apply {
                    put("listingId", listingId)
                    put("bidAmount", bidAmount)
                    put("bidder", ParseUser.getCurrentUser())
                    put("bidderId", ParseUser.getCurrentUser()?.objectId ?: "")
                    put("message", bidderMessage)
                    put("status", "ACTIVE")
                    put("bidDate", Date())
                }

            bid.saveInBackground { e ->
                if (e == null) {
                    continuation.resume(bid.objectId)
                } else {
                    continuation.resumeWithException(e)
                }
            }
        }

    suspend fun fetchBidsForListing(listingId: String): List<Bid> =
        suspendCancellableCoroutine { continuation ->
            val query =
                ParseQuery.getQuery<ParseObject>(BID_CLASS).apply {
                    include("bidder")
                    whereEqualTo("listingId", listingId)
                    whereEqualTo("status", "ACTIVE")
                    orderByDescending("bidAmount")
                }

            query.findInBackground { objects, e ->
                if (e == null && objects != null) {
                    val bids =
                        objects.mapNotNull { parseObject ->
                            try {
                                Bid.fromParseObject(parseObject)
                            } catch (ex: Exception) {
                                null
                            }
                        }
                    continuation.resume(bids)
                } else {
                    continuation.resumeWithException(e ?: Exception("Unknown error"))
                }
            }
        }

    suspend fun acceptBid(
        bidId: String,
        listingId: String,
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
            // Update bid status to ACCEPTED
            val bidQuery = ParseQuery.getQuery<ParseObject>(BID_CLASS)
            bidQuery.getInBackground(bidId) { bidObject, e ->
                if (e == null && bidObject != null) {
                    bidObject.put("status", "ACCEPTED")
                    bidObject.saveInBackground { bidSaveError ->
                        if (bidSaveError == null) {
                            // Mark listing as sold
                            val listingQuery = ParseQuery.getQuery<ParseObject>(LISTING_CLASS)
                            listingQuery.getInBackground(listingId) { listingObject, listingError ->
                                if (listingError == null && listingObject != null) {
                                    listingObject.put("isActive", false)
                                    listingObject.put("status", "SOLD")
                                    listingObject.saveInBackground { finalSaveError ->
                                        if (finalSaveError == null) {
                                            continuation.resume(true)
                                        } else {
                                            continuation.resumeWithException(finalSaveError)
                                        }
                                    }
                                } else {
                                    continuation.resumeWithException(
                                        listingError ?: Exception("Listing not found"),
                                    )
                                }
                            }
                        } else {
                            continuation.resumeWithException(bidSaveError)
                        }
                    }
                } else {
                    continuation.resumeWithException(e ?: Exception("Bid not found"))
                }
            }
        }

    suspend fun searchListings(
        query: String,
        limit: Int = 20,
    ): List<SafeListing> =
        suspendCancellableCoroutine { continuation ->

            // Create breed query
            val breedQuery =
                ParseQuery.getQuery<ParseObject>(LISTING_CLASS).apply {
                    whereContains("breed", query)
                    whereEqualTo("isActive", true)
                }

            // Create description query
            val descQuery =
                ParseQuery.getQuery<ParseObject>(LISTING_CLASS).apply {
                    whereContains("description", query)
                    whereEqualTo("isActive", true)
                }

            // Combine queries with OR
            val combinedQuery =
                ParseQuery.or(listOf(breedQuery, descQuery)).apply {
                    include("owner")
                    orderByDescending("createdAt")
                    this.limit = limit
                }

            combinedQuery.findInBackground { objects, e ->
                if (e == null && objects != null) {
                    val listings =
                        objects.mapNotNull { parseObject ->
                            try {
                                SafeListing.fromParseObject(parseObject)
                            } catch (ex: Exception) {
                                null
                            }
                        }
                    continuation.resume(listings)
                } else {
                    continuation.resumeWithException(e ?: Exception("Search failed"))
                }
            }
        }

    suspend fun fetchMyListings(): List<SafeListing> =
        suspendCancellableCoroutine { continuation ->
            val query =
                ParseQuery.getQuery<ParseObject>(LISTING_CLASS).apply {
                    include("owner")
                    whereEqualTo("owner", ParseUser.getCurrentUser())
                    orderByDescending("createdAt")
                }

            query.findInBackground { objects, e ->
                if (e == null && objects != null) {
                    val listings =
                        objects.mapNotNull { parseObject ->
                            try {
                                SafeListing.fromParseObject(parseObject)
                            } catch (ex: Exception) {
                                null
                            }
                        }
                    continuation.resume(listings)
                } else {
                    continuation.resumeWithException(e ?: Exception("Failed to fetch my listings"))
                }
            }
        }
}

// Bid data class for bidding functionality
data class Bid(
    val id: String,
    val listingId: String,
    val bidAmount: Double,
    val bidder: String,
    val bidderId: String,
    val message: String,
    val status: String,
    val bidDate: Date,
) {
    companion object {
        fun fromParseObject(parseObject: ParseObject): Bid {
            val bidderUser = parseObject.getParseUser("bidder")
            val bidderName =
                try {
                    bidderUser?.getString("username") ?: "Unknown Bidder"
                } catch (e: IllegalStateException) {
                    "Unknown Bidder"
                }

            val bidderId =
                try {
                    bidderUser?.getString("firebaseUid") ?: bidderUser?.objectId ?: ""
                } catch (e: IllegalStateException) {
                    bidderUser?.objectId ?: ""
                }

            return Bid(
                id = parseObject.objectId ?: "",
                listingId = parseObject.getString("listingId") ?: "",
                bidAmount = parseObject.getDouble("bidAmount"),
                bidder = bidderName,
                bidderId = bidderId,
                message = parseObject.getString("message") ?: "",
                status = parseObject.getString("status") ?: "ACTIVE",
                bidDate = parseObject.getDate("bidDate") ?: Date(),
            )
        }
    }
}
