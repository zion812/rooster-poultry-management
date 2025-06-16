package com.example.rooster.data

import com.example.rooster.models.UserOrder
import com.example.rooster.models.UserOrderStatus
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.delay

/**
 * Stub repository for user orders. Replace with real backend integration.
 */
object OrderRepository {
    // Sample in-memory orders
    private val orders =
        mutableListOf(
            UserOrder("1", "Egg Pack (12)", System.currentTimeMillis(), UserOrderStatus.PENDING, 120.0),
            UserOrder(
                "2",
                "Fresh Vegetables Box",
                System.currentTimeMillis() - 86400000,
                UserOrderStatus.DELIVERED,
                200.0,
            ),
        )

    /** Fetches orders for a given user (stubbed). */
    suspend fun fetchUserOrders(userId: String): List<UserOrder> {
        // simulate network delay
        delay(500)
        return orders.toList()
    }

    /** Updates the status of an order. */
    suspend fun updateOrderStatus(
        orderId: String,
        status: UserOrderStatus,
    ) {
        delay(200)
        orders.find { it.id == orderId }?.let {
            val updated = it.copy(status = status)
            orders[orders.indexOf(it)] = updated
        }
    }

    /** Submits feedback for a delivered order. */
    suspend fun submitFeedback(
        orderId: String,
        rating: Int,
        comment: String,
    ) {
        delay(200)
        // In a real implementation, post feedback to backend
        println("Feedback submitted for order $orderId: rating=$rating, comment=$comment")
    }

    /**
     * Creates a new order (stub). In production, this would post to Parse backend.
     */
    suspend fun createOrder(
        productName: String,
        // For simplicity, we use productName as a stand-in for listingId
        method: String,
        advance: Boolean,
        status: UserOrderStatus,
        buyerId: String,
    ): UserOrder {
        delay(200)

        // Try lookup Listing in Parse to obtain price & title
        var price = 0.0
        var titleSafe = productName
        try {
            val listingObj: ParseObject? =
                ParseQuery.getQuery<ParseObject>("Listing").get(productName)
            titleSafe =
                listingObj?.getString("title") ?: listingObj?.getString("breed") ?: productName
            price = listingObj?.getNumber("price")?.toDouble() ?: 0.0
        } catch (_: Exception) {
            // ignore, fall back to stub values
        }

        val newOrder =
            UserOrder(
                id = System.currentTimeMillis().toString(),
                productName = titleSafe,
                orderDate = System.currentTimeMillis(),
                status = status,
                totalAmount = price,
                buyerId = buyerId,
            )
        orders.add(newOrder)
        return newOrder
    }
}
