package com.example.rooster.feature.marketplace.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.CartItem
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.repository.CartRepository
import com.example.rooster.core.common.user.UserIdProvider
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

sealed interface CartUserMessage {
    data class ItemAdded(val itemName: String) : CartUserMessage
    data class ItemUpdated(val itemName: String) : CartUserMessage
    data class ItemRemoved(val itemName: String) : CartUserMessage
    data class ErrorAddingItem(val errorMessage: String) : CartUserMessage
    data object CartCleared : CartUserMessage
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userIdProvider: UserIdProvider
) : ViewModel() {

    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    private val _userMessages = MutableSharedFlow<CartUserMessage>()
    val userMessages: SharedFlow<CartUserMessage> = _userMessages.asSharedFlow()

    init {
        viewModelScope.launch {
            userIdProvider.currentUserIdFlow.collectLatest { userId ->
                if (userId != null) {
                    loadCartForUser(userId)
                } else {
                    _cartUiState.value = CartUiState.Success(emptyList(), 0, 0.0)
                }
            }
        }
    }

    private fun loadCartForUser(userId: String) {
        cartRepository.getCartItems(userId)
            .combine(cartRepository.getCartItemCount(userId)) { itemsResult, countResult ->
                Pair(itemsResult, countResult)
            }
            .onEach { (itemsResult, countResult) ->
                _cartUiState.value = when {
                    itemsResult is Result.Success && countResult is Result.Success -> {
                        val items = itemsResult.data
                        val count = countResult.data
                        val subTotal = items.sumOf { it.unitPrice * it.quantity }
                        CartUiState.Success(items, count, subTotal)
                    }
                    itemsResult is Result.Error -> CartUiState.Error(itemsResult.exception.message ?: "Failed to load cart items")
                    countResult is Result.Error -> CartUiState.Error(countResult.exception.message ?: "Failed to load cart count")
                    itemsResult is Result.Loading || countResult is Result.Loading -> CartUiState.Loading
                    else -> CartUiState.Error("Unknown cart state")
                }
            }
            .catch { e -> _cartUiState.value = CartUiState.Error(e.message ?: "Error observing cart") }
            .launchIn(viewModelScope)
    }

    fun addItemToCart(listing: ProductListing, quantity: Int = 1) {
        val userId = userIdProvider.getCurrentUserId()
        if (userId == null) {
            viewModelScope.launch {
                _userMessages.emit(CartUserMessage.ErrorAddingItem("You must be logged in to add items to cart."))
            }
            return
        }

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
            val result = cartRepository.addItemToCart(userId, cartItem)
            if (result is Result.Success) {
                _userMessages.emit(CartUserMessage.ItemAdded(listing.title))
            } else if (result is Result.Error) {
                _userMessages.emit(CartUserMessage.ErrorAddingItem(result.exception.message ?: "Could not add item"))
            }
        }
    }

    fun updateItemQuantity(listingId: String, newQuantity: Int) {
        val userId = userIdProvider.getCurrentUserId()
        if (userId == null) return

        viewModelScope.launch {
            val result = cartRepository.updateCartItemQuantity(userId, listingId, newQuantity)
            if (result is Result.Success) {
                _userMessages.emit(CartUserMessage.ItemUpdated("Item"))
            } else if (result is Result.Error) {
                // Handle error
            }
        }
    }

    fun removeItemFromCart(listingId: String, itemName: String) {
        val userId = userIdProvider.getCurrentUserId()
        if (userId == null) return

        viewModelScope.launch {
            val result = cartRepository.removeCartItem(userId, listingId)
            if (result is Result.Success) {
                _userMessages.emit(CartUserMessage.ItemRemoved(itemName))
            } else if (result is Result.Error) {
                // Handle error
            }
        }
    }

    fun clearCart() {
        val userId = userIdProvider.getCurrentUserId()
        if (userId == null) return

        viewModelScope.launch {
            val result = cartRepository.clearCart(userId)
            if (result is Result.Success) {
                _userMessages.emit(CartUserMessage.CartCleared)
            } else if (result is Result.Error) {
                // Handle error
            }
        }
    }
}