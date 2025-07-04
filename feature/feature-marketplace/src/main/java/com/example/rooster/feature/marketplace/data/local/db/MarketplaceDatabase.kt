package com.example.rooster.feature.marketplace.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.Review
import com.example.rooster.core.common.model.Supplier
import com.example.rooster.core.common.persistence.common.CommonTypeConverters

@Database(
    entities = [Product::class, Supplier::class, Review::class],
    version = 1, // AGENTS.md: MarketplaceDatabase is currently at version 1
    exportSchema = false // Set to true for production apps for schema history
)
@TypeConverters(CommonTypeConverters::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun supplierDao(): SupplierDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        const val DATABASE_NAME = "rooster_marketplace_db"
    }
}
