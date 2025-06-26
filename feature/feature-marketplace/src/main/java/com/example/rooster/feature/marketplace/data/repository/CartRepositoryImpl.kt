package com.example.rooster.feature.marketplace.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.data.local.dao.CartDao
import com.example.rooster.feature.marketplace.data.local.model.CartItemEntity
import com.example.rooster.feature.marketplace.domain.model.CartItem
import com.example.rooster.feature.marketplace.domain.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
    // No remote data source for cart in this phase
) : CartRepository {

    override fun getCartItems(userId: String): Flow<Result<List<CartItem>>> {
        return cartDao.getCartItems(userId).map { entities ->
            Result.Success(entities.map { mapEntityToDomain(it) })
        }
        // TODO: Add .catch { emit(Result.Error(it)) } and .flowOn(Dispatchers.IO) if needed
    }

    override suspend fun addItemToCart(userId: String, cartItem: CartItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Check if item already exists, if so, update quantity
            val existingEntity = cartDao.getCartItemByListingIdSuspend(userId, cartItem.listingId)
            if (existingEntity != null) {
                val newQuantity = existingEntity.quantity + cartItem.quantity
                if (newQuantity <= cartItem.stockAvailable) {
                    cartDao.updateCartItem(existingEntity.copy(quantity = newQuantity))
                } else {
                    // Handle case where requested quantity exceeds stock (e.g., set to max available or throw error)
                    cartDao.updateCartItem(existingEntity.copy(quantity = cartItem.stockAvailable))
                    // Optionally return a specific result indicating quantity adjusted
                }
            } else {
                if (cartItem.quantity <= cartItem.stockAvailable) {
                    cartDao.addItemToCart(mapDomainToEntity(cartItem, userId))
                } else {
                     // Handle case where requested quantity exceeds stock
                    cartDao.addItemToCart(mapDomainToEntity(cartItem.copy(quantity = cartItem.stockAvailable), userId))
                     // Optionally return a specific result
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateCartItemQuantity(userId: String, listingId: String, newQuantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = cartDao.getCartItemByListingIdSuspend(userId, listingId)
            if (entity != null) {
                if (newQuantity > 0 && newQuantity <= entity.stockAvailable) {
                    cartDao.updateCartItem(entity.copy(quantity = newQuantity))
                } else if (newQuantity <= 0) {
                    cartDao.removeCartItem(userId, listingId) // Remove if quantity is zero or less
                } else {
                    // Requested quantity exceeds stock, update to max available
                    cartDao.updateCartItem(entity.copy(quantity = entity.stockAvailable))
                    // Optionally return a specific result indicating quantity adjusted
                }
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Cart item not found to update"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeCartItem(userId: String, listingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cartDao.removeCartItem(userId, listingId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cartDao.clearCart(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCartItemCount(userId: String): Flow<Result<Int>> {
        return cartDao.getCartItemCount(userId).map { count ->
            Result.Success(count)
        }
        // TODO: Add .catch { emit(Result.Error(it)) } and .flowOn(Dispatchers.IO)
    }

    // --- Mappers ---
    private fun mapEntityToDomain(entity: CartItemEntity): CartItem {
        return CartItem(
            listingId = entity.listingId,
            sellerId = entity.sellerId,
            title = entity.title,
            unitPrice = entity.unitPrice,
            quantity = entity.quantity,
            primaryImageUrl = entity.primaryImageUrl,
            stockAvailable = entity.stockAvailable,
            addedToCartTimestamp = entity.addedToCartTimestamp
        )
    }

    private fun mapDomainToEntity(domain: CartItem, userId: String): CartItemEntity {
        return CartItemEntity(
            listingId = domain.listingId,
            sellerId = domain.sellerId,
            title = domain.title,
            unitPrice = domain.unitPrice,
            quantity = domain.quantity,
            primaryImageUrl = domain.primaryImageUrl,
            stockAvailable = domain.stockAvailable,
            addedToCartTimestamp = domain.addedToCartTimestamp,
            userId = userId
        )
    }
}
