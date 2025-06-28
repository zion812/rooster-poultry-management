package com.example.rooster.feature.marketplace.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.CartItem
import com.example.rooster.feature.marketplace.domain.model.ShippingAddress
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    // Creates an order from cart items.
    // Payment processing would typically be a separate step after order creation or handled by use case.
    suspend fun createOrder(
        buyerId: String,
        items: List<CartItem>, // Items from the cart to form the order
        shippingAddress: ShippingAddress,
        billingAddress: ShippingAddress? = null,
        deliveryInstructions: String? = null
    ): Result<Order> // Returns the created Order object

    fun getOrderDetails(orderId: String): Flow<Result<Order?>>

    fun getOrdersForUser(
        userId: String,
        pageSize: Int = 10, // Default page size
        page: Int = 1       // Default to first page
    ): Flow<Result<List<Order>>>

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> // Simplified status update

    suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit>

    // TODO: Add methods for reorder, track shipment, etc.
}
