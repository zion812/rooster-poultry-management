package com.example.rooster.feature.marketplace.data.repository

import com.example.rooster.core.common.model.Order
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.OrderItem as CommonOrderItem // Alias to avoid clash if Order has items
import com.example.rooster.core.common.model.OrderStatus
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class MockOrderRepositoryImpl @Inject constructor() : OrderRepository {

    private val mockOrders = mutableListOf<Order>()

    override suspend fun createOrder(
        userId: String,
        items: List<CartItem>,
        totalAmount: Double,
        shippingAddress: String,
        paymentMethod: String,
        paymentId: String?,
        deliveryOption: String?,
        orderNotes: String?
    ): Result<Order> {
        val orderItems = items.map { cartItem ->
            CommonOrderItem( // Use aliased CommonOrderItem
                productId = cartItem.productId,
                productName = cartItem.productName,
                quantity = cartItem.quantity,
                pricePerUnit = cartItem.productPrice,
                imageUrl = cartItem.productImageUrl
            )
        }

        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            orderItems = orderItems,
            totalAmount = totalAmount,
            orderDate = System.currentTimeMillis(),
            status = OrderStatus.CONFIRMED, // Default to confirmed for mock
            shippingAddress = shippingAddress,
            paymentMethod = paymentMethod,
            paymentId = paymentId,
            transactionId = "mock_trans_${UUID.randomUUID()}",
            deliveryOption = deliveryOption,
            estimatedDeliveryDate = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000), // 3 days later
            needsSync = false // Mock doesn't sync
        )
        mockOrders.add(newOrder)
        return Result.Success(newOrder)
    }

    override fun getOrderDetails(orderId: String): Flow<Result<Order?>> {
        val order = mockOrders.find { it.id == orderId }
        return flowOf(Result.Success(order))
    }

    override fun getOrdersForUser(userId: String): Flow<Result<List<Order>>> {
        val userOrders = mockOrders.filter { it.userId == userId }
        return flowOf(Result.Success(userOrders))
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        val orderIndex = mockOrders.indexOfFirst { it.id == orderId }
        if (orderIndex != -1) {
            mockOrders[orderIndex] = mockOrders[orderIndex].copy(status = newStatus)
            return Result.Success(Unit)
        }
        return Result.Error("Order not found")
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit> {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    override suspend fun getUnsyncedOrders(): List<Order> {
        return emptyList() // Mock doesn't handle sync
    }

    override suspend fun syncOrder(order: Order): Result<Unit> {
        return Result.Success(Unit) // Mock doesn't handle sync
    }
}
