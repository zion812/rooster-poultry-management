package com.example.rooster.feature.marketplace.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rooster.feature.marketplace.domain.model.CartItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onProceedToCheckout: () -> Unit // Placeholder for navigation
) {
    val uiState by viewModel.cartUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessages.collectLatest { message ->
            val text = when (message) {
                is CartUserMessage.ItemAdded -> "${message.itemName} added to cart."
                is CartUserMessage.ItemUpdated -> "${message.itemName} quantity updated."
                is CartUserMessage.ItemRemoved -> "${message.itemName} removed from cart."
                is CartUserMessage.ErrorAddingItem -> "Error: ${message.errorMessage}"
                is CartUserMessage.CartCleared -> "Cart cleared."
            }
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is CartUiState.Success && (uiState as CartUiState.Success).items.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear All")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is CartUiState.Success && (uiState as CartUiState.Success).items.isNotEmpty()) {
                val successState = uiState as CartUiState.Success
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal (${successState.itemCount} items):", style = MaterialTheme.typography.titleMedium)
                        Text("₹${String.format("%.2f", successState.subTotal)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceed to Checkout")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is CartUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CartUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Text(
                            text = "Your cart is empty.",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        CartItemList(
                            items = state.items,
                            onQuantityChange = { listingId, newQuantity ->
                                viewModel.updateItemQuantity(listingId, newQuantity)
                            },
                            onRemoveItem = { listingId, itemName ->
                                viewModel.removeItemFromCart(listingId, itemName)
                            }
                        )
                    }
                }
                is CartUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemList(
    items: List<CartItem>,
    onQuantityChange: (listingId: String, newQuantity: Int) -> Unit,
    onRemoveItem: (listingId: String, itemName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.listingId }) { item ->
            CartListItem(
                item = item,
                onQuantityChange = { newQuantity -> onQuantityChange(item.listingId, newQuantity) },
                onRemoveItem = { onRemoveItem(item.listingId, item.title) }
            )
            Divider()
        }
    }
}

@Composable
fun CartListItem(
    item: CartItem,
    onQuantityChange: (newQuantity: Int) -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.primaryImageUrl)
                // .placeholder(R.drawable.placeholder_image)
                // .error(R.drawable.error_image)
                .crossfade(true)
                .build(),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Price: ₹${String.format("%.2f", item.unitPrice)}", style = MaterialTheme.typography.bodyMedium)
            Text("Seller ID: ${item.sellerId}", style = MaterialTheme.typography.bodySmall) // TODO: Show seller name
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if(item.quantity > 1) onQuantityChange(item.quantity - 1) else onRemoveItem() }, enabled = item.quantity > 0) {
                    Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease quantity")
                }
                Text(item.quantity.toString(), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = { if(item.quantity < item.stockAvailable) onQuantityChange(item.quantity + 1) }, enabled = item.quantity < item.stockAvailable) {
                    Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase quantity")
                }
            }
             if (item.quantity >= item.stockAvailable) {
                Text("Max stock", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        IconButton(onClick = onRemoveItem) {
            Icon(Icons.Filled.Delete, contentDescription = "Remove item")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartListItem() {
    val sampleItem = CartItem(
        listingId = "1", sellerId = "seller1", title = "Super Nattu Kodi Rooster - Champion Breed",
        unitPrice = 1250.0, quantity = 2, primaryImageUrl = "https://via.placeholder.com/150",
        stockAvailable = 5, addedToCartTimestamp = System.currentTimeMillis()
    )
    MaterialTheme {
        CartListItem(item = sampleItem, onQuantityChange = {}, onRemoveItem = {})
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun PreviewCartScreen_SuccessWithItems() {
     val sampleItems = listOf(
        CartItem("1", "seller1", "Rooster A", 1200.0, 1, null, 5, System.currentTimeMillis()),
        CartItem("2", "seller2", "Hen B - Special Laying Breed with excellent characteristics", 800.0, 2, null, 3, System.currentTimeMillis())
    )
    val mockUiState = CartUiState.Success(items = sampleItems, itemCount = 2, subTotal = 2800.0)

    // This is a simplified preview. A real preview of CartScreen would need a mock ViewModel.
    MaterialTheme {
        Scaffold(
             topBar = { TopAppBar(title = { Text("Your Cart") })},
             bottomBar = { Text("Bottom bar placeholder for subtotal and checkout", modifier = Modifier.padding(16.dp))}
        ) { padding ->
             Box(modifier = Modifier.padding(padding)) {
                CartItemList(
                    items = mockUiState.items,
                    onQuantityChange = {_,_ -> },
                    onRemoveItem = {_,_ -> }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
fun PreviewCartScreen_Empty() {
    val mockUiState = CartUiState.Success(items = emptyList(), itemCount = 0, subTotal = 0.0)
    MaterialTheme {
         Scaffold(
             topBar = { TopAppBar(title = { Text("Your Cart") })},
         ) {padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                 Text(
                    text = "Your cart is empty.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
         }
    }
}
