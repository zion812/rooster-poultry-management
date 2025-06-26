package com.example.rooster.feature.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.marketplace.data.local.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToCart(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND listingId = :listingId")
    fun getCartItemByListingId(userId: String, listingId: String): Flow<CartItemEntity?>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND listingId = :listingId")
    suspend fun getCartItemByListingIdSuspend(userId: String, listingId: String): CartItemEntity?

    @Query("DELETE FROM cart_items WHERE userId = :userId AND listingId = :listingId")
    suspend fun removeCartItem(userId: String, listingId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    @Query("SELECT COUNT(listingId) FROM cart_items WHERE userId = :userId")
    fun getCartItemCount(userId: String): Flow<Int>
}
