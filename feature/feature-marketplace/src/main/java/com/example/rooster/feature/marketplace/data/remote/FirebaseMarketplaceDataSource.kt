package com.example.rooster.feature.marketplace.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldPath
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

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    override fun getProductListingsStream(
        category: String?,
        sellerId: String?,
        searchTerm: String?,
        pageSize: Int,
        lastVisibleTimestamp: Long?,
        lastVisibleDocId: String?
    ): Flow<Result<List<ProductListing>>> = callbackFlow {
        var query: Query = listingsCollection
            .orderBy("postedDateTimestamp", Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)

        if (category != null) {
            query = query.whereEqualTo("category", category)
        }
        if (sellerId != null) {
            query = query.whereEqualTo("sellerId", sellerId)
        }

        query = query.limit(pageSize.toLong())

        var listener: com.google.firebase.firestore.ListenerRegistration? = null

        if (lastVisibleTimestamp != null && lastVisibleDocId != null) {
            // Get the last document snapshot first
            firestore.collection("marketplace_listings")
                .document(lastVisibleDocId).get()
                .addOnSuccessListener { lastDocSnapshot ->
                    val finalQuery = if (lastDocSnapshot.exists()) {
                        query.startAfter(lastDocSnapshot)
                    } else {
                        query
                    }

                    listener = finalQuery.addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            trySend(Result.Error(e))
                            return@addSnapshotListener
                        }
                        if (snapshots != null) {
                            val listings = snapshots.toObjects<ProductListing>()
                            trySend(Result.Success(listings))
                        } else {
                            trySend(Result.Success(emptyList()))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    trySend(Result.Error(e))
                }
        } else {
            listener = query.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    trySend(Result.Error(e))
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val listings = snapshots.toObjects<ProductListing>()
                    trySend(Result.Success(listings))
                } else {
                    trySend(Result.Success(emptyList()))
                }
            }
        }

        awaitClose { listener?.remove() }
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
            val docRef = listingsCollection.document(listingData.id)
            docRef.set(listingData).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(listingData: ProductListing): Result<Unit> {
        return try {
            listingsCollection.document(listingData.id).set(listingData).await()
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
            val docRef = ordersCollection.document(orderData.orderId)
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
                trySend(Result.Success(null)).isSuccess
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
            ordersCollection.document(orderId).update(
                "status", newStatus,
                "lastUpdatedTimestamp", System.currentTimeMillis()
            ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId).update(
                "status", "CANCELLED_BY_USER",
                "lastUpdatedTimestamp", System.currentTimeMillis()
            ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
