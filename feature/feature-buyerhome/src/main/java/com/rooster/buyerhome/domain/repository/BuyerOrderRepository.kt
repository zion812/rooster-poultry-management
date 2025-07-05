package com.rooster.buyerhome.domain.repository

import com.rooster.buyerhome.domain.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface BuyerOrderRepository {
    fun getRecentOrders(buyerId: String, count: Int = 5): Flow<List<OrderItem>>
    // fun getOrderDetails(orderId: String): Flow<OrderItem?>
}
