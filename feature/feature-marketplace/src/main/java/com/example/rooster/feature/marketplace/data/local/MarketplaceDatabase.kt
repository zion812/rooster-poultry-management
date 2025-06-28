package com.example.rooster.feature.marketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rooster.feature.marketplace.data.local.dao.CartDao
import com.example.rooster.feature.marketplace.data.local.dao.OrderDao
import com.example.rooster.feature.marketplace.data.local.dao.ProductListingDao
import com.example.rooster.feature.marketplace.data.local.model.CartItemEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderItemEntity
import com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity

@Database(
    entities = [
        ProductListingEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1, // Initial version for this database
    exportSchema = true // Recommended to export schema for migrations
)
@TypeConverters(MarketplaceTypeConverters::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productListingDao(): ProductListingDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}
