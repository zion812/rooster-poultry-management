package com.example.rooster.feature.marketplace.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(userId: String): Flow<Result<List<CartItem>>>

    suspend fun addItemToCart(userId: String, cartItem: CartItem): Result<Unit>

    suspend fun updateCartItemQuantity(userId: String, listingId: String, newQuantity: Int): Result<Unit>

    suspend fun removeCartItem(userId: String, listingId: String): Result<Unit>

    suspend fun clearCart(userId: String): Result<Unit>

    fun getCartItemCount(userId: String): Flow<Result<Int>>
}
