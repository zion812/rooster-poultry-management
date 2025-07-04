package com.example.rooster.feature.marketplace.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.Order // Updated to common model
import com.example.rooster.core.common.model.CartItem // Updated to common model
import com.example.rooster.core.common.model.OrderStatus // For status updates
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(
        userId: String, // Renamed from buyerId for consistency
        items: List<CartItem>,
        totalAmount: Double, // Added from my previous plan for this interface
        shippingAddress: String, // Simplified to String
        paymentMethod: String, // Added
        paymentId: String?, // Added
        deliveryOption: String?, // Added
        orderNotes: String? // Added from my previous plan for this interface
    ): Result<Order> // Returns the created Order object

    fun getOrderDetails(orderId: String): Flow<Result<Order?>>

    fun getOrdersForUser(userId: String): Flow<Result<List<Order>>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> // Use common OrderStatus enum

    suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit>

    // Methods for SyncWorker (can be kept or adjusted based on common Order model)
    suspend fun getUnsyncedOrders(): List<Order> // Assumes Order model has needsSync
    suspend fun syncOrder(order: Order): Result<Unit>

    // TODO: Add methods for reorder, track shipment, etc.
}