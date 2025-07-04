package com.example.rooster.feature.cart.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.WishlistItem
import com.example.rooster.core.common.persistence.common.ListConverter // For potential future use with list fields

@Database(
    entities = [CartItem::class, WishlistItem::class],
    version = 1,
    exportSchema = false // Set to true for production apps for schema history
)
@TypeConverters(ListConverter::class) // Add any global type converters if needed
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        const val DATABASE_NAME = "rooster_cart_db"
    }
}
