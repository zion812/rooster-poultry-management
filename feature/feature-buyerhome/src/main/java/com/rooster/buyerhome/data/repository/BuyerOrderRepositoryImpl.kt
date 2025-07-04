package com.rooster.buyerhome.data.repository

import com.rooster.buyerhome.data.source.BuyerOrderRemoteDataSource
import com.rooster.buyerhome.domain.model.OrderItem
import com.rooster.buyerhome.domain.repository.BuyerOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyerOrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: BuyerOrderRemoteDataSource
    // TODO: Add localDataSource for caching
) : BuyerOrderRepository {
    override fun getRecentOrders(buyerId: String, count: Int): Flow<List<OrderItem>> {
        return remoteDataSource.getRecentOrders(buyerId, count)
    }
}
