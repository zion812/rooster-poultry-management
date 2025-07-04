package com.example.rooster.feature.cart.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.core.common.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(userId: String, productId: String): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToCart(cartItem: CartItem): Long

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeItemFromCart(userId: String, productId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    @Query("SELECT SUM(quantity * productPrice) FROM cart_items WHERE userId = :userId")
    fun getCartTotal(userId: String): Flow<Double?>

    // For sync purposes
    @Query("SELECT * FROM cart_items WHERE needsSync = 1")
    suspend fun getCartItemsForSync(): List<CartItem>
}
