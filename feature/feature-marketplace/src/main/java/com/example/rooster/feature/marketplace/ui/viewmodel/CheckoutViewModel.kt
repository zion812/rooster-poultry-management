package com.example.rooster.feature.marketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.Order
import com.example.rooster.feature.cart.domain.repository.CartRepository
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.rooster.core.common.Result

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 50.0, // Example fixed fee
    val discount: Double = 0.0, // Example discount
    val totalAmount: Double = 0.0,
    val shippingAddress: String = "",
    val paymentMethod: String = "Razorpay", // Default or selected
    val deliveryOption: String? = null,
    val orderNotes: String = "",
    val isLoadingCart: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val orderError: String? = null,
    val orderResult: Order? = null // Holds the successful order
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
    // TODO: Inject UserIdProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // TODO: Replace with actual user ID from a provider
    private val currentUserId = "temp_user_id"

    init {
        loadCheckoutSummary()
    }

    private fun loadCheckoutSummary() {
        _uiState.update { it.copy(isLoadingCart = true) }
        viewModelScope.launch {
            cartRepository.getCartItems(currentUserId)
                .combine(cartRepository.getCartTotal(currentUserId)) { items, cartTotalOrNull ->
                    val subtotalCalc = cartTotalOrNull ?: items.sumOf { it.productPrice * it.quantity }
                    val delivery = if (items.isNotEmpty()) _uiState.value.deliveryFee else 0.0
                    val discountCalc = if (subtotalCalc > 1000) subtotalCalc * 0.05 else 0.0 // Mock 5% discount over 1000
                    val finalTotal = subtotalCalc + delivery - discountCalc
                    Pair(items, finalTotal)
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingCart = false,
                            orderError = "Error loading cart summary: ${e.message}"
                        )
                    }
                }
                .collect { (items, finalTotal) ->
                    _uiState.update {
                        it.copy(
                            cartItems = items,
                            subtotal = items.sumOf { item -> item.productPrice * item.quantity },
                            totalAmount = finalTotal,
                            isLoadingCart = false
                        )
                    }
                }
        }
    }

    fun onShippingAddressChanged(address: String) {
        _uiState.update { it.copy(shippingAddress = address) }
    }

    fun onPaymentMethodChanged(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun onDeliveryOptionChanged(option: String) {
        _uiState.update { it.copy(deliveryOption = option) }
    }

    fun onOrderNotesChanged(notes: String) {
        _uiState.update { it.copy(orderNotes = notes) }
    }

    fun placeOrder() {
        if (_uiState.value.isPlacingOrder || _uiState.value.cartItems.isEmpty()) return
        if (_uiState.value.shippingAddress.isBlank()) {
            _uiState.update { it.copy(orderError = "Shipping address cannot be empty.") }
            return
        }

        _uiState.update { it.copy(isPlacingOrder = true, orderError = null, orderResult = null) }

        viewModelScope.launch {
            val currentState = _uiState.value
            val result = orderRepository.createOrder(
                userId = currentUserId,
                items = currentState.cartItems,
                totalAmount = currentState.totalAmount,
                shippingAddress = currentState.shippingAddress,
                paymentMethod = currentState.paymentMethod,
                paymentId = "mock_razorpay_${System.currentTimeMillis()}", // Mock payment ID
                deliveryOption = currentState.deliveryOption,
                orderNotes = currentState.orderNotes
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isPlacingOrder = false, orderResult = result.data) }
                    // Clear cart after successful order placement
                    cartRepository.clearCart(currentUserId)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isPlacingOrder = false,
                            orderError = result.exception?.message ?: "Failed to place order."
                        )
                    }
                }
                Result.Loading -> { /* Should not happen with suspend fun returning Result */ }
            }
        }
    }

    fun resetOrderResult() {
        _uiState.update { it.copy(orderResult = null, orderError = null) }
    }
}
