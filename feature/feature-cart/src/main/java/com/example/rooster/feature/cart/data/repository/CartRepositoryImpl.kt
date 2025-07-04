package com.example.rooster.feature.cart.data.repository

import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.WishlistItem
import com.example.rooster.feature.cart.data.local.db.CartDao
import com.example.rooster.feature.cart.data.local.db.WishlistDao
import com.example.rooster.feature.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.rooster.core.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val wishlistDao: WishlistDao
    // private val remoteDataSource: CartRemoteDataSource // For future backend integration
) : CartRepository {

    override fun getCartItems(userId: String): Flow<List<CartItem>> {
        return cartDao.getCartItems(userId)
    }

    override suspend fun addItemToCart(userId: String, product: Product, quantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (quantity <= 0) return@withContext Result.Error("Quantity must be positive.")

            val existingItem = cartDao.getCartItemByProductId(userId, product.id)
            if (existingItem != null) {
                val newQuantity = existingItem.quantity + quantity
                cartDao.updateCartItem(existingItem.copy(quantity = newQuantity, needsSync = true))
            } else {
                val cartItem = CartItem(
                    userId = userId,
                    productId = product.id,
                    quantity = quantity,
                    productName = product.name,
                    productPrice = product.price,
                    productImageUrl = product.imageUrls.firstOrNull(),
                    needsSync = true
                )
                cartDao.addItemToCart(cartItem)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to add item to cart", e)
        }
    }

    override suspend fun updateItemQuantity(userId: String, productId: String, newQuantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (newQuantity <= 0) {
                // If quantity is zero or less, remove the item
                cartDao.removeItemFromCart(userId, productId)
            } else {
                val existingItem = cartDao.getCartItemByProductId(userId, productId)
                if (existingItem != null) {
                    cartDao.updateCartItem(existingItem.copy(quantity = newQuantity, needsSync = true))
                } else {
                    return@withContext Result.Error("Item not found in cart.")
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to update item quantity.", e)
        }
    }

    override suspend fun removeItemFromCart(userId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cartDao.removeItemFromCart(userId, productId)
            // Consider if the cart item itself needs a 'deleted' flag for sync, or if removal is purely local first.
            // For now, direct deletion. If syncing deletions is required, an update with needsSync might be needed before delete.
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to remove item from cart.", e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cartDao.clearCart(userId)
            // Similar to removeItem, consider sync implications for "clear all".
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to clear cart.", e)
        }
    }

    override fun getCartTotal(userId: String): Flow<Double?> {
        return cartDao.getCartTotal(userId)
    }

    // Wishlist Operations
    override fun getWishlistItems(userId: String): Flow<List<WishlistItem>> {
        return wishlistDao.getWishlistItems(userId)
    }

    override suspend fun addToWishlist(userId: String, product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val wishlistItem = WishlistItem(
                userId = userId,
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                productImageUrl = product.imageUrls.firstOrNull(),
                needsSync = true
            )
            wishlistDao.addToWishlist(wishlistItem)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to add item to wishlist.", e)
        }
    }

    override suspend fun removeFromWishlist(userId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            wishlistDao.removeFromWishlist(userId, productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to remove item from wishlist.", e)
        }
    }

     override suspend fun isProductInWishlist(userId: String, productId: String): Flow<Boolean> = withContext(Dispatchers.IO) {
        return@withContext wishlistDao.getWishlistItems(userId).map { wishlist ->
            wishlist.any { it.productId == productId }
        }
    }


    override suspend fun clearWishlist(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            wishlistDao.clearWishlist(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to clear wishlist.", e)
        }
    }

    // Sync Operations (Placeholders)
    override suspend fun syncCart(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // TODO: Implement actual sync logic with a remote data source
        // 1. Get items with needsSync = true from cartDao
        // 2. Send them to remoteDataSource
        // 3. On success, update local items needsSync = false
        // For now, simulate success
        try {
            val itemsToSync = cartDao.getCartItemsForSync().filter { it.userId == userId }
            if (itemsToSync.isNotEmpty()) {
                // remoteDataSource.syncCartItems(itemsToSync) // Example call
                itemsToSync.forEach {
                    // cartDao.updateCartItem(it.copy(needsSync = false, syncAttempts = 0)) // On successful sync
                }
            }
            Result.Success(Unit)
        } catch (e: IOException) { // Network error
            Result.Error("Network error during cart sync.", e)
        }
         catch (e: Exception) {
            Result.Error("Failed to sync cart: ${e.message}", e)
        }
    }

    override suspend fun syncWishlist(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // TODO: Implement actual sync logic
        try {
            val itemsToSync = wishlistDao.getWishlistItemsForSync().filter { it.userId == userId }
             if (itemsToSync.isNotEmpty()) {
                // remoteDataSource.syncWishlistItems(itemsToSync) // Example call
                itemsToSync.forEach {
                    // wishlistDao.updateWishlistItemSyncStatus(it.id, false, 0, System.currentTimeMillis()) // On successful sync
                }
            }
            Result.Success(Unit)
        } catch (e: IOException) { // Network error
            Result.Error("Network error during wishlist sync.", e)
        }
        catch (e: Exception) {
            Result.Error("Failed to sync wishlist: ${e.message}", e)
        }
    }
}
