package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.OrderItem
import kotlinx.coroutines.flow.Flow

interface BuyerOrderRemoteDataSource {
    fun getRecentOrders(buyerId: String, count: Int): Flow<List<OrderItem>>
}
