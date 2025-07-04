package com.example.rooster.feature.cart.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rooster.core.common.model.WishlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId")
    fun getWishlistItems(userId: String): Flow<List<WishlistItem>>

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getWishlistItemByProductId(userId: String, productId: String): WishlistItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore if already in wishlist
    suspend fun addToWishlist(wishlistItem: WishlistItem): Long

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeFromWishlist(userId: String, productId: String)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId")
    suspend fun clearWishlist(userId: String)

    // For sync purposes
    @Query("SELECT * FROM wishlist_items WHERE needsSync = 1")
    suspend fun getWishlistItemsForSync(): List<WishlistItem>

    @Query("UPDATE wishlist_items SET needsSync = :needsSync, syncAttempts = :syncAttempts, lastSyncAttemptTimestamp = :lastSyncAttemptTimestamp WHERE id = :itemId")
    suspend fun updateWishlistItemSyncStatus(itemId: Long, needsSync: Boolean, syncAttempts: Int, lastSyncAttemptTimestamp: Long)
}
