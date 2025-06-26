package com.example.rooster.feature.marketplace.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.CartItem
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.repository.CartRepository
// Assuming a central User ID provider/manager exists, e.g., from an Auth module
// For now, hardcoding a placeholder userId for cart operations.
// import com.example.rooster.core.auth.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CartUiState {
    data object Loading : CartUiState
    data class Success(
        val items: List<CartItem>,
        val itemCount: Int = 0,
        val subTotal: Double = 0.0
    ) : CartUiState
    data class Error(val message: String) : CartUiState
}

// Simple event for one-time UI actions like showing a Snackbar
sealed interface CartUserMessage {
    data class ItemAdded(val itemName: String) : CartUserMessage
    data class ItemUpdated(val itemName: String) : CartUserMessage
    data class ItemRemoved(val itemName: String) : CartUserMessage
    data class ErrorAddingItem(val errorMessage: String) : CartUserMessage
    data object CartCleared : CartUserMessage
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
    // @Inject private val userManager: UserManager // Ideal scenario
) : ViewModel() {

    // TODO: Replace with actual User ID from an authentication manager/repository
    private val currentUserId: String = "placeholder_user_id"

    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    private val _userMessages = MutableSharedFlow<CartUserMessage>()
    val userMessages: SharedFlow<CartUserMessage> = _userMessages.asSharedFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            // Combine cart items and item count flows
            cartRepository.getCartItems(currentUserId)
                .combine(cartRepository.getCartItemCount(currentUserId)) { itemsResult, countResult ->
                    Pair(itemsResult, countResult)
                }
                .onEach { (itemsResult, countResult) ->
                    when {
                        itemsResult is Result.Success && countResult is Result.Success -> {
                            val items = itemsResult.data
                            val count = countResult.data
                            val subTotal = items.sumOf { it.unitPrice * it.quantity }
                            _cartUiState.value = CartUiState.Success(items, count, subTotal)
                        }
                        itemsResult is Result.Error -> {
                            _cartUiState.value = CartUiState.Error(itemsResult.exception.message ?: "Failed to load cart items")
                        }
                        countResult is Result.Error -> {
                             _cartUiState.value = CartUiState.Error(countResult.exception.message ?: "Failed to load cart count")
                        }
                        itemsResult is Result.Loading || countResult is Result.Loading -> {
                            _cartUiState.value = CartUiState.Loading
                        }
                    }
                }
                .catch { e -> _cartUiState.value = CartUiState.Error(e.message ?: "Error observing cart") }
                .launchIn(viewModelScope)
        }
    }

    fun addItemToCart(listing: ProductListing, quantity: Int = 1) {
        viewModelScope.launch {
            if (quantity <= 0) return@launch

            val cartItem = CartItem(
                listingId = listing.id,
                sellerId = listing.sellerId,
                title = listing.title,
                unitPrice = listing.price,
                quantity = quantity,
                primaryImageUrl = listing.imageUrls.firstOrNull(),
                stockAvailable = listing.quantityAvailable,
                addedToCartTimestamp = System.currentTimeMillis()
            )
            val result = cartRepository.addItemToCart(currentUserId, cartItem)
            if (result is Result.Success) {
                _userMessages.emit(CartUserMessage.ItemAdded(listing.title))
            } else if (result is Result.Error) {
                _userMessages.emit(CartUserMessage.ErrorAddingItem(result.exception.message ?: "Could not add item"))
            }
        }
    }

    fun updateItemQuantity(listingId: String, newQuantity: Int) {
        viewModelScope.launch {
            val result = cartRepository.updateCartItemQuantity(currentUserId, listingId, newQuantity)
            // Optionally, get item name for message
            if (result is Result.Success) {
                 _userMessages.emit(CartUserMessage.ItemUpdated("Item")) // Simplified message
            } else if (result is Result.Error) {
                // Handle error message
            }
        }
    }

    fun removeItemFromCart(listingId: String, itemName: String) {
        viewModelScope.launch {
            val result = cartRepository.removeCartItem(currentUserId, listingId)
             if (result is Result.Success) {
                 _userMessages.emit(CartUserMessage.ItemRemoved(itemName))
            } else if (result is Result.Error) {
                // Handle error message
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val result = cartRepository.clearCart(currentUserId)
            if (result is Result.Success) {
                 _userMessages.emit(CartUserMessage.CartCleared)
            } else if (result is Result.Error) {
                // Handle error message
            }
        }
    }
}
