package com.rooster.buyerhome.data.source

import com.rooster.buyerhome.domain.model.OrderItem
import com.rooster.buyerhome.domain.model.OrderStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

class MockBuyerOrderRemoteDataSource @Inject constructor() : BuyerOrderRemoteDataSource {
    override fun getRecentOrders(buyerId: String, count: Int): Flow<List<OrderItem>> = flow {
        delay(900) // Simulate delay
        val orders = mutableListOf<OrderItem>()
        val calendar = Calendar.getInstance()

        for (i in 1..(count + Random.nextInt(0,3))) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -Random.nextInt(1, 30))
            val orderDate = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(2, 7))
            val deliveryDate = if (Random.nextBoolean()) calendar.time else null

            orders.add(
                OrderItem(
                    id = "order${1000 + i}",
                    productName = listOf("Broilers", "Eggs", "Feed", "Chicks").random(),
                    quantity = "${Random.nextInt(5, 50)} " + listOf("kg", "dozen", "quintals", "units").random(),
                    totalPrice = "₹${Random.nextInt(500, 25000)}",
                    orderDate = orderDate,
                    expectedDeliveryDate = deliveryDate,
                    status = OrderStatus.values().random(),
                    sellerName = listOf("Farm Fresh Ltd.", "Agro Traders", "Poultry Hub", "Village Farms").random()
                )
            )
        }
        emit(orders.sortedByDescending { it.orderDate }.take(count))
    }
}
