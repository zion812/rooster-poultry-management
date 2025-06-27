package com.example.rooster.feature.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.rooster.feature.marketplace.data.local.model.OrderEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderItemEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderWithItems // To be created
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithItemsById(orderId: String): Flow<OrderWithItems?> // OrderWithItems is a @Relation class

    @Transaction
    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY orderTimestamp DESC")
    fun getOrdersWithItemsForUser(buyerId: String): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE needsSync = 1")
    suspend fun getUnsyncedOrdersSuspend(): List<OrderEntity>

    @Query("SELECT * FROM order_items WHERE parentOrderId = :orderId")
    suspend fun getOrderItemsForOrderSuspend(orderId: String): List<OrderItemEntity>

<< jules/arch-assessment-1
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderByIdSuspend(orderId: String): OrderEntity?

=======
> main
    // Add other queries as needed, e.g., to update order status, delete orders etc.
    @Query("UPDATE orders SET status = :newStatus, lastUpdatedTimestamp = :timestamp WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String, timestamp: Long)
}
