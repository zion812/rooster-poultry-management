package com.example.rooster.feature.cart.domain.repository

import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.Product // Assuming Product is in core.common.model
import com.example.rooster.core.common.model.WishlistItem
import kotlinx.coroutines.flow.Flow
import com.example.rooster.core.common.Result

interface CartRepository {

    // Cart Operations
    fun getCartItems(userId: String): Flow<List<CartItem>>
    suspend fun addItemToCart(userId: String, product: Product, quantity: Int): Result<Unit>
    suspend fun updateItemQuantity(userId: String, productId: String, newQuantity: Int): Result<Unit>
    suspend fun removeItemFromCart(userId: String, productId: String): Result<Unit>
    suspend fun clearCart(userId: String): Result<Unit>
    fun getCartTotal(userId: String): Flow<Double?>

    // Wishlist Operations
    fun getWishlistItems(userId: String): Flow<List<WishlistItem>>
    suspend fun addToWishlist(userId: String, product: Product): Result<Unit>
    suspend fun removeFromWishlist(userId: String, productId: String): Result<Unit>
    suspend fun isProductInWishlist(userId: String, productId: String): Flow<Boolean>
    suspend fun clearWishlist(userId: String): Result<Unit>

    // Sync Operations (Conceptual, can be expanded)
    suspend fun syncCart(userId: String): Result<Unit>
    suspend fun syncWishlist(userId: String): Result<Unit>
}
