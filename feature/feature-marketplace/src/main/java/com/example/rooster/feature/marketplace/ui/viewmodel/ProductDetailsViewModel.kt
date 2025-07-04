package com.example.rooster.feature.marketplace.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.Review
import com.example.rooster.core.common.model.Supplier
import com.example.rooster.feature.cart.domain.repository.CartRepository
import com.example.rooster.feature.marketplace.domain.repository.MarketplaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailsUiState(
    val product: Product? = null,
    val supplier: Supplier? = null,
    val reviews: List<Review> = emptyList(),
    val relatedProducts: List<Product> = emptyList(), // Placeholder for now
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedQuantity: Int = 1,
    val isAddingToCart: Boolean = false,
    val addToCartError: String? = null,
    val addToCartSuccess: Boolean = false,
    val currentImageIndex: Int = 0 // For image carousel
)

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    private val cartRepository: CartRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") ?: ""

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        if (productId.isNotBlank()) {
            loadProductDetails()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Product ID not found.") }
        }
    }

    fun loadProductDetails() {
        _uiState.update { it.copy(isLoading = true, error = null, addToCartSuccess = false, addToCartError = null) }

        viewModelScope.launch {
            marketplaceRepository.getProductDetails(productId)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val product = result.data
                            _uiState.update { it.copy(product = product) }
                            if (product != null) {
                                loadSupplierDetails(product.supplierId)
                                loadProductReviews(product.id)
                                // TODO: Load related products
                            } else {
                                _uiState.update { it.copy(isLoading = false, error = "Product not found.") }
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.exception?.message ?: "Error loading product details."
                                )
                            }
                        }
                        Result.Loading -> { /* Handled by initial isLoading = true */ }
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unexpected error."
                        )
                    }
                }
                .launchIn(viewModelScope) // Use launchIn for collecting a single flow result that then triggers others
        }
    }

    private fun loadSupplierDetails(supplierId: String) {
        viewModelScope.launch {
            marketplaceRepository.getSupplierProfile(supplierId)
                .onEach { result ->
                    if (result is Result.Success) {
                        _uiState.update { it.copy(supplier = result.data) }
                    }
                    // Optionally handle supplier loading error separately
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadProductReviews(productId: String) {
        viewModelScope.launch {
            marketplaceRepository.getProductReviews(productId)
                .onEach { result ->
                    if (result is Result.Success) {
                        _uiState.update { it.copy(reviews = result.data ?: emptyList()) }
                    }
                     // Combine isLoading states or handle individually
                    _uiState.update { it.copy(isLoading = false) } // Consider all details loaded now
                }
                .catch { _ -> _uiState.update { it.copy(isLoading = false) } } // Ensure loading stops on error
                .launchIn(viewModelScope)
        }
    }

    fun onQuantityChanged(quantity: Int) {
        if (quantity > 0) {
            _uiState.update { it.copy(selectedQuantity = quantity) }
        }
    }

    fun addToCart() {
        val currentProduct = _uiState.value.product
        val currentUserId = "temp_user_id" // TODO: Replace with actual logged-in user ID from a provider

        if (currentProduct == null) {
            _uiState.update { it.copy(addToCartError = "Product not available.") }
            return
        }
        if (_uiState.value.isAddingToCart) return

        _uiState.update { it.copy(isAddingToCart = true, addToCartError = null, addToCartSuccess = false) }

        viewModelScope.launch {
            val result = cartRepository.addItemToCart(
                userId = currentUserId,
                product = currentProduct,
                quantity = _uiState.value.selectedQuantity
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isAddingToCart = false, addToCartSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingToCart = false,
                            addToCartError = result.exception?.message ?: "Failed to add to cart."
                        )
                    }
                }
                Result.Loading -> { /* Should not happen with suspend fun returning Result */ }
            }
        }
    }

    fun onImageSwiped(index: Int) {
        _uiState.update { it.copy(currentImageIndex = index) }
    }

    fun dismissAddToCartResult() {
        _uiState.update { it.copy(addToCartError = null, addToCartSuccess = false) }
    }
}
