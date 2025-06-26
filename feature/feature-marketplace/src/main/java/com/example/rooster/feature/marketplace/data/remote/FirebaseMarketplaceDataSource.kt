package com.example.rooster.feature.marketplace.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMarketplaceDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : MarketplaceRemoteDataSource {

    private val listingsCollection = firestore.collection("marketplace_listings")
    private val ordersCollection = firestore.collection("marketplace_orders")

    override fun getProductListingsStream(
        category: String?,
        sellerId: String?,
        searchTerm: String? // Basic search on title, more advanced search would need dedicated solution (e.g. Algolia)
    ): Flow<Result<List<ProductListing>>> = callbackFlow {
        var query: Query = listingsCollection
            .orderBy("postedDateTimestamp", Query.Direction.DESCENDING)

        if (category != null) {
            query = query.whereEqualTo("category", category)
        }
        if (sellerId != null) {
            query = query.whereEqualTo("sellerId", sellerId)
        }
        // Firestore basic search is limited. For robust search, use a dedicated search service.
        // This basic version might filter client-side or require specific indexing for searchTerm on title.
        // For now, not implementing searchTerm directly in query to avoid complexity.

        val listener = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                trySend(Result.Error(e)).isFailure
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val listings = snapshots.toObjects<ProductListing>() // Assumes ProductListing is directly mappable
                trySend(Result.Success(listings)).isSuccess
            } else {
                trySend(Result.Success(emptyList())).isSuccess // Or an error if null snapshots are unexpected
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getProductListingDetails(listingId: String): Result<ProductListing?> {
        return try {
            val document = listingsCollection.document(listingId).get().await()
            Result.Success(document.toObject<ProductListing>())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createProductListing(listingData: ProductListing): Result<String> {
        return try {
            val docRef = listingsCollection.document(listingData.id) // Use provided ID or generate if not
            docRef.set(listingData).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listingData: ProductListing): Result<Unit> {
        return try {
            listingsCollection.document(listingData.id).set(listingData).await() // Or .update for specific fields
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProductListing(listingId: String): Result<Unit> {
        return try {
            listingsCollection.document(listingId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createOrder(orderData: Order): Result<String> {
        return try {
            val docRef = ordersCollection.document(orderData.orderId) // Use provided ID
            docRef.set(orderData).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getOrderDetailsStream(orderId: String): Flow<Result<Order?>> = callbackFlow {
        val listener = ordersCollection.document(orderId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.Error(e)).isFailure
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(Result.Success(snapshot.toObject<Order>())).isSuccess
            } else {
                trySend(Result.Success(null)).isSuccess // Or error if order must exist
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getOrdersForUserStream(userId: String): Flow<Result<List<Order>>> = callbackFlow {
        val listener = ordersCollection.whereEqualTo("buyerId", userId)
            .orderBy("orderTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    trySend(Result.Error(e)).isFailure
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val orders = snapshots.toObjects<Order>()
                    trySend(Result.Success(orders)).isSuccess
                } else {
                    trySend(Result.Success(emptyList())).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            // This is a simplified update. A real scenario might involve more checks.
            ordersCollection.document(orderId).update("status", newStatus, "lastUpdatedTimestamp", System.currentTimeMillis()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        // In a real app, this would involve business logic: checking if order can be cancelled,
        // refund processes, updating stock, etc. Here, it's a simple status update.
        return try {
            // For simplicity, just updating status. Could use a specific "CANCELLED_BY_USER" status from enum.
            ordersCollection.document(orderId).update(
                "status", "CANCELLED_BY_USER", // Assuming OrderStatus.CANCELLED_BY_USER.name
                "lastUpdatedTimestamp", System.currentTimeMillis()
            ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
