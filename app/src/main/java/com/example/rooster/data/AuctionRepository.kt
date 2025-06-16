package com.example.rooster.data

import com.example.rooster.models.AuctionBid
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Real auction repository with Parse backend integration
 */
object AuctionRepository {
    suspend fun listBids(productId: String): List<AuctionBid> {
        return suspendCancellableCoroutine { continuation ->
            val query = ParseQuery.getQuery<ParseObject>("AuctionBid")
            query.whereEqualTo("productId", productId)
            query.include("bidder")
            query.orderByDescending("bidAmount")
            query.limit = 50

            query.findInBackground { bids, error ->
                if (error == null && bids != null) {
                    val auctionBids =
                        bids.map { parseObject ->
                            AuctionBid(
                                id = parseObject.objectId ?: "",
                                productId = parseObject.getString("productId") ?: "",
                                bidderId = parseObject.getString("bidderId") ?: "",
                                amount = parseObject.getDouble("bidAmount") ?: 0.0,
                                timestamp = parseObject.createdAt?.time ?: System.currentTimeMillis(),
                                status =
                                    com.example.rooster.models.BidStatus.valueOf(
                                        parseObject.getString("status") ?: "PENDING",
                                    ),
                            )
                        }
                    continuation.resume(auctionBids)
                } else {
                    android.util.Log.e("AuctionRepo", "Failed to fetch bids", error)
                    continuation.resume(emptyList())
                }
            }
        }
    }

    suspend fun submitBid(bid: AuctionBid): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val parseObject = ParseObject("AuctionBid")
            parseObject.put("productId", bid.productId)
            parseObject.put("bidderId", bid.bidderId)
            parseObject.put("bidAmount", bid.amount)
            parseObject.put("status", bid.status.name)

            parseObject.saveInBackground(
                SaveCallback { error ->
                    if (error == null) {
                        android.util.Log.d("AuctionRepo", "Bid submitted successfully")
                        continuation.resume(true)
                    } else {
                        android.util.Log.e("AuctionRepo", "Failed to submit bid", error)
                        continuation.resume(false)
                    }
                },
            )
        }
    }

    suspend fun approveBid(
        productId: String,
        bidId: String,
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val query = ParseQuery.getQuery<ParseObject>("AuctionBid")
            query.whereEqualTo("objectId", bidId)
            query.whereEqualTo("productId", productId)

            query.findInBackground { bids, error ->
                if (error == null && bids?.isNotEmpty() == true) {
                    val bid = bids[0]
                    bid.put("status", "APPROVED")
                    bid.saveInBackground(
                        SaveCallback { saveError ->
                            if (saveError == null) {
                                android.util.Log.d("AuctionRepo", "Bid approved successfully")
                                continuation.resume(true)
                            } else {
                                android.util.Log.e("AuctionRepo", "Failed to approve bid", saveError)
                                continuation.resume(false)
                            }
                        },
                    )
                } else {
                    android.util.Log.e("AuctionRepo", "Bid not found", error)
                    continuation.resume(false)
                }
            }
        }
    }

    suspend fun getActiveAuctions(): List<String> {
        return suspendCancellableCoroutine { continuation ->
            val query = ParseQuery.getQuery<ParseObject>("Listing")
            query.whereEqualTo("isAuction", true)
            query.whereEqualTo("isActive", true)
            query.limit = 20

            query.findInBackground { listings, error ->
                if (error == null && listings != null) {
                    val productIds = listings.mapNotNull { it.objectId }
                    continuation.resume(productIds)
                } else {
                    android.util.Log.e("AuctionRepo", "Failed to fetch active auctions", error)
                    continuation.resume(emptyList())
                }
            }
        }
    }
}
