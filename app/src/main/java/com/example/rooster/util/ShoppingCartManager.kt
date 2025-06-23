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

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    fun addItem(item: CartItem) {
        val currentCart = _cart.value.toMutableList()
        val exitingItemIndex = currentCart.indexOfFirst { it.productId == item.productId }

        if (exitingItemIndex >= 0) {
            currentCart[exitingItemIndex] = currentCart[exitingItemIndex].copy(
                quantity = currentCart[exitingItemIndex].quantity + item.quantity
            )
        } else {
            currentCart.add(item)
        }

        _cart.value = currentCart
        updateTotalPrice()
    }

    fun removeItem(productId: String) {
        val currentCart = _cart.value.toMutableList()
        currentCart.removeAll { it.productId == productId }
        _cart.value = currentCart
        updateTotalPrice()
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val currentCart = _cart.value.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.productId == productId }

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

/**
 * Cart item data class
 */
data class CartItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int = 1,
    val imageUrl: String? = null
)