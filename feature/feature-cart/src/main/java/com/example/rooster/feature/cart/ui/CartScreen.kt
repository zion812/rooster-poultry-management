package com.example.rooster.feature.cart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.core.common.model.Product // For Preview
import com.example.rooster.core.common.Result // For Preview
import com.example.rooster.feature.cart.domain.repository.CartRepository // For Preview
import com.example.rooster.feature.cart.ui.viewmodel.CartUiState
import com.example.rooster.feature.cart.ui.viewmodel.CartViewModel
import kotlinx.coroutines.flow.Flow // For Preview
import kotlinx.coroutines.flow.MutableStateFlow // For Preview
import kotlinx.coroutines.flow.StateFlow // For Preview
import kotlinx.coroutines.flow.flowOf // For Preview

// Mock R object for placeholder strings, similar to MarketplaceScreen
object MockCartR {
    object string {
        const val cart_title: String = "Your Cart"
        const val proceed_to_checkout: String = "Proceed to Checkout"
        const val empty_cart_message: String = "Your cart is empty."
        const val go_shopping: String = "Go Shopping"
        const val subtotal: String = "Subtotal"
        const val delivery_fee: String = "Delivery Fee"
        const val discount: String = "Discount"
        const val total: String = "Total"
        const val remove_item_desc: String = "Remove item"
        const val increase_quantity_desc: String = "Increase quantity"
        const val decrease_quantity_desc: String = "Decrease quantity"
        fun error_loading_cart(errorMsg: String): String = "Error: $errorMsg"
        const val retry: String = "Retry"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateToCheckout: () -> Unit,
    onNavigateToMarketplace: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(MockCartR.string.cart_title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() } ) { // Add confirmation dialog later
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Cart")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.cartItems.isNotEmpty() && !uiState.isLoading) {
                CheckoutSummaryBottomBar(
                    subtotal = uiState.subtotal,
                    deliveryFee = uiState.deliveryFee,
                    discount = uiState.discount,
                    total = uiState.total,
                    onProceedToCheckout = onNavigateToCheckout
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(MockCartR.string.error_loading_cart(uiState.error.orEmpty()))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshCart() }) {
                        Text(MockCartR.string.retry)
                    }
                }
            } else if (uiState.cartItems.isEmpty()) {
                EmptyCartView(onNavigateToMarketplace)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
                ) {
                    items(uiState.cartItems, key = { it.productId }) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(cartItem.productId, newQuantity)
                            },
                            onRemoveItem = { viewModel.removeItem(cartItem.productId) },
                            isUpdating = uiState.itemUpdating == cartItem.productId || uiState.itemRemoving == cartItem.productId
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    isUpdating: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cartItem.productImageUrl)
                .crossfade(true)
                // .placeholder()
                // .error()
                .build(),
            contentDescription = cartItem.productName,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(cartItem.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "₹${String.format("%.2f", cartItem.productPrice)}", // Placeholder currency
                style = MaterialTheme.typography.bodyMedium
            )
             Spacer(modifier = Modifier.height(8.dp))
            QuantitySelector(
                quantity = cartItem.quantity,
                onQuantityChange = onQuantityChange,
                enabled = !isUpdating,
                maxQuantity = 100 // TODO: Get actual max stock from product if needed
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if(isUpdating) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            IconButton(onClick = onRemoveItem) {
                Icon(Icons.Default.Close, contentDescription = MockCartR.string.remove_item_desc)
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    enabled: Boolean,
    maxQuantity: Int = 100 // Default max, can be product specific
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            enabled = enabled && quantity > 1,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = MockCartR.string.decrease_quantity_desc)
        }
        Text(
            text = "$quantity",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 12.dp) // Reduced padding
        )
        OutlinedIconButton(
            onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) },
            enabled = enabled && quantity < maxQuantity,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = MockCartR.string.increase_quantity_desc)
        }
    }
}


@Composable
fun CheckoutSummaryBottomBar(
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    total: Double,
    onProceedToCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PriceRow(label = MockCartR.string.subtotal, amount = subtotal)
            if (deliveryFee > 0) PriceRow(label = MockCartR.string.delivery_fee, amount = deliveryFee)
            if (discount > 0) PriceRow(label = MockCartR.string.discount, amount = -discount, isDiscount = true) // Show discount as negative

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(MockCartR.string.total, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "₹${String.format("%.2f", total)}", // Placeholder currency
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onProceedToCheckout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(MockCartR.string.proceed_to_checkout.uppercase())
            }
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = if (isDiscount) "- ₹${String.format("%.2f", kotlin.math.abs(amount))}" else "₹${String.format("%.2f", amount)}", // Placeholder
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDiscount) MaterialTheme.colorScheme.error else LocalContentColor.current
        )
    }
}


@Composable
fun EmptyCartView(onNavigateToMarketplace: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.ShoppingCartCheckout, // Or other suitable icon
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            MockCartR.string.empty_cart_message,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToMarketplace) {
            Icon(Icons.Filled.Storefront, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(MockCartR.string.go_shopping)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview_NotEmpty() {
    val mockItems = listOf(
        CartItem(1, "user1", "P001", 2, System.currentTimeMillis(), "Fresh Broiler Chicken", 250.0, "https://via.placeholder.com/150/FFC107/000000?Text=Chicken"),
        CartItem(2, "user1", "P002", 1, System.currentTimeMillis(), "Organic Country Eggs", 120.0, "https://via.placeholder.com/150/4CAF50/FFFFFF?Text=Eggs")
    )
    val mockUiState = CartUiState(
        cartItems = mockItems,
        subtotal = 620.0,
        deliveryFee = 50.0,
        discount = 62.0,
        total = 620.0 + 50.0 - 62.0,
        isLoading = false
    )

    class PreviewCartViewModel : CartViewModel(FakeCartRepositoryPreview()) { // Use specific preview fake
        override val uiState: StateFlow<CartUiState> = MutableStateFlow(mockUiState)
    }
    MaterialTheme {
        CartScreen(
            viewModel = PreviewCartViewModel(),
            onNavigateToCheckout = {},
            onNavigateToMarketplace = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview_Empty() {
    val mockUiState = CartUiState(cartItems = emptyList(), isLoading = false)

    class PreviewCartViewModel : CartViewModel(FakeCartRepositoryPreview()) {
        override val uiState: StateFlow<CartUiState> = MutableStateFlow(mockUiState)
    }
    MaterialTheme {
        CartScreen(
            viewModel = PreviewCartViewModel(),
            onNavigateToCheckout = {},
            onNavigateToMarketplace = {},
            onNavigateBack = {}
        )
    }
}

// Fake repository for CartScreen preview
class FakeCartRepositoryPreview : CartRepository {
    override fun getCartItems(userId: String): Flow<List<CartItem>> = flowOf(emptyList())
    override suspend fun addItemToCart(userId: String, product: Product, quantity: Int): Result<Unit> = Result.Success(Unit)
    override suspend fun updateItemQuantity(userId: String, productId: String, newQuantity: Int): Result<Unit> = Result.Success(Unit)
    override suspend fun removeItemFromCart(userId: String, productId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun clearCart(userId: String): Result<Unit> = Result.Success(Unit)
    override fun getCartTotal(userId: String): Flow<Double?> = flowOf(0.0)
    override fun getWishlistItems(userId: String): Flow<List<com.example.rooster.core.common.model.WishlistItem>> = flowOf(emptyList())
    override suspend fun addToWishlist(userId: String, product: Product): Result<Unit> = Result.Success(Unit)
    override suspend fun removeFromWishlist(userId: String, productId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun isProductInWishlist(userId: String, productId: String): Flow<Boolean> = flowOf(false)
    override suspend fun clearWishlist(userId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun syncCart(userId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun syncWishlist(userId: String): Result<Unit> = Result.Success(Unit)
}

// TODOs:
// - Replace MockCartR with stringResource calls.
// - Implement navigation.
// - Implement actual User ID fetching.
// - Implement actual delivery fee and discount logic in ViewModel/Repository.
// - Add confirmation dialog for "Clear Cart".
// - Refine loading/error states, especially for item updates.
// - Add placeholder/error images for Coil.
// - Full Material 3 theming, rural-friendly UI, accessibility, localization.
```

**Summary of `CartScreen.kt` and `CartViewModel.kt`:**
*   **`CartViewModel.kt`**:
    *   Manages `CartUiState` (items, totals, loading/error).
    *   Fetches cart items and total from `CartRepository`.
    *   Handles item quantity updates and removals.
    *   Includes logic for cart refresh and clearing the cart.
    *   Uses a placeholder `currentUserId` (TODO: replace with actual).
    *   Calculates subtotal, total, and has placeholders for delivery fee/discount.
*   **`CartScreen.kt`**:
    *   Displays cart items in a `LazyColumn`.
    *   Each `CartItemRow` shows product image, name, price, and a `QuantitySelector`.
    *   `QuantitySelector` allows incrementing/decrementing item quantity.
    *   `CheckoutSummaryBottomBar` displays subtotal, delivery, discount, total, and a "Proceed to Checkout" button.
    *   Shows an `EmptyCartView` if the cart is empty.
    *   Includes basic loading and error states.
    *   Uses a `MockCartR` object for strings (TODO: replace with `stringResource`).

This completes the implementation of the `CartScreen` and its ViewModel. The next step will be the Checkout flow.
