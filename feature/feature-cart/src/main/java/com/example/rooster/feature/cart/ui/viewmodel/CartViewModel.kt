package com.example.rooster.feature.cart.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.feature.cart.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0, // Placeholder, can be calculated later
    val discount: Double = 0.0,    // Placeholder for bulk discounts
    val total: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val itemUpdating: String? = null, // productId of item being updated
    val itemRemoving: String? = null  // productId of item being removed
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
    // TODO: Inject UserIdProvider to get current user ID
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // TODO: Replace with actual user ID from a provider
    private val currentUserId = "temp_user_id"

    init {
        loadCartContent()
    }

    private fun loadCartContent() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            cartRepository.getCartItems(currentUserId)
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .combine(cartRepository.getCartTotal(currentUserId)) { items, totalOrNull ->
                    val subtotalCalc = totalOrNull ?: items.sumOf { it.productPrice * it.quantity }
                    // TODO: Implement actual delivery fee and discount logic
                    val delivery = if (items.isNotEmpty()) 50.0 else 0.0 // Mock delivery
                    val discountCalc = if (subtotalCalc > 500) subtotalCalc * 0.1 else 0.0 // Mock discount
                    val finalTotal = subtotalCalc + delivery - discountCalc

                    _uiState.update {
                        it.copy(
                            cartItems = items,
                            subtotal = subtotalCalc,
                            deliveryFee = delivery,
                            discount = discountCalc,
                            total = finalTotal,
                            isLoading = false
                        )
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error loading cart."
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(productId)
            return
        }
        _uiState.update { it.copy(itemUpdating = productId) }
        viewModelScope.launch {
            when (val result = cartRepository.updateItemQuantity(currentUserId, productId, newQuantity)) {
                is Result.Success -> {
                    // State will be updated by the flow from getCartItems
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception?.message ?: "Failed to update quantity") }
                }
                Result.Loading -> {}
            }
            _uiState.update { it.copy(itemUpdating = null) }
        }
    }

    fun removeItem(productId: String) {
        _uiState.update { it.copy(itemRemoving = productId) }
        viewModelScope.launch {
            when (val result = cartRepository.removeItemFromCart(currentUserId, productId)) {
                is Result.Success -> {
                     // State will be updated by the flow from getCartItems
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception?.message ?: "Failed to remove item") }
                }
                Result.Loading -> {}
            }
             _uiState.update { it.copy(itemRemoving = null) }
        }
    }

    fun refreshCart() {
        loadCartContent()
    }

    fun clearCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Show general loading for clear
            when(val result = cartRepository.clearCart(currentUserId)) {
                 is Result.Success -> {
                     // State will be updated by the flow from getCartItems
                 }
                 is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception?.message ?: "Failed to clear cart", isLoading = false) }
                }
                Result.Loading -> {}
            }
            // isLoading will be set to false by the flow update
        }
    }
}
