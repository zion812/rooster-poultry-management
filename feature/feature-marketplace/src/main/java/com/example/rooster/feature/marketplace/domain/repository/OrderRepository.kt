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

    fun getOrdersForUser(userId: String): Flow<Result<List<Order>>>

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> // Simplified status update

    suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit>

    // Methods for SyncWorker
 feature/phase1-foundations-community-likes
    suspend fun getUnsyncedOrderEntities(): List<com.example.rooster.feature.marketplace.data.local.model.OrderEntity>
    suspend fun syncOrderRemote(order: Order): Result<Unit>
    suspend fun updateLocalOrder(orderEntity: com.example.rooster.feature.marketplace.data.local.model.OrderEntity) // For worker
    suspend fun mapOrderEntityToDomain(orderEntity: com.example.rooster.feature.marketplace.data.local.model.OrderEntity): Order // For worker, suspend due to items
=======
    suspend fun getUnsyncedOrders(): List<Order>
    suspend fun syncOrder(order: Order): Result<Unit>
 main

    // TODO: Add methods for reorder, track shipment, etc.
}
