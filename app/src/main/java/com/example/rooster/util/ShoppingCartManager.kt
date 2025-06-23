package com.example.rooster.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shopping cart manager for handling cart operations
 */
@Singleton
class ShoppingCartManager @Inject constructor() {

    private val _cart = MutableStateFlow<List<com.example.rooster.models.CartItem>>(emptyList()) // Use imported CartItem
    val cart: StateFlow<List<com.example.rooster.models.CartItem>> = _cart.asStateFlow() // Use imported CartItem

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    fun addItem(item: com.example.rooster.models.CartItem) {
        val currentCart = _cart.value.toMutableList()
        val exitingItemIndex = currentCart.indexOfFirst { it.listingId == item.listingId } // Use listingId

        if (exitingItemIndex >= 0) {
            // Update quantity of existing item
            val existingItem = currentCart[exitingItemIndex]
            currentCart[exitingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
        } else {
            // Add new item
            currentCart.add(item)
        }

        _cart.value = currentCart
        updateTotalPrice()
    }

    fun removeItem(listingId: String) { // Changed parameter name from productId to listingId
        val currentCart = _cart.value.toMutableList()
        currentCart.removeAll { it.listingId == listingId } // Use listingId
        _cart.value = currentCart
        updateTotalPrice()
    }

    fun updateQuantity(listingId: String, quantity: Int) { // Changed parameter name
        val currentCart = _cart.value.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.listingId == listingId } // Use listingId

        if (itemIndex >= 0) {
            if (quantity > 0) {
                currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = quantity)
            } else {
                currentCart.removeAt(itemIndex)
            }
        }

        _cart.value = currentCart
        updateTotalPrice()
    }

    fun clearCart() {
        _cart.value = emptyList()
        _totalPrice.value = 0.0
    }

    private fun updateTotalPrice() {
        val total = _cart.value.sumOf { it.price * it.quantity }
        _totalPrice.value = total
    }
}

// Local CartItem definition removed, using com.example.rooster.models.CartItem instead