package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.launch

@Composable
fun CartScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf(listOf<com.example.rooster.CartItem>()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var orderSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        com.example.rooster.fetchCartItems(
            onResult = { items -> cartItems = items },
            onError = { e -> error = e },
            setLoading = { loading -> isLoading = loading },
        )
    }

    StandardScreenLayout(scrollable = true) {
        Text(text = "Cart", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (cartItems.isEmpty()) {
            Text(text = "Your cart is empty")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems, key = { item -> item.objectId }) { item ->
                    CartItemCard(item, onOrder = { itemId ->
                        coroutineScope.launch {
                            placeOrder(
                                itemId,
                                onSuccess = {
                                    orderSuccess = true
                                    com.example.rooster.fetchCartItems(
                                        onResult = { items -> cartItems = items },
                                        onError = { e -> error = e },
                                        setLoading = { loading -> isLoading = loading },
                                    )
                                },
                                onError = { e -> error = e },
                            )
                        }
                    })
                }
            }
        }
        if (orderSuccess) {
            Text("Order placed successfully!", color = MaterialTheme.colorScheme.primary)
        }
        error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CartItemCard(
    item: com.example.rooster.CartItem,
    onOrder: (String) -> Unit,
) {
    Card(modifier = Modifier.padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Title: ${item.title}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Price: ${item.price}")
            Text(text = "Seller: ${item.sellerName}")
            Button(
                onClick = { onOrder(item.objectId) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
            ) {
                Text("Place Order")
            }
        }
    }
}

fun placeOrder(
    listingId: String,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit,
) {
    try {
        val order = ParseObject("Order")
        order.put("listing", ParseObject.createWithoutData("Listing", listingId))
        order.put("buyer", ParseUser.getCurrentUser())
        order.put("status", "pending")
        order.saveInBackground { e ->
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                onSuccess()
            }
        }
    } catch (e: Exception) {
        onError(e.localizedMessage)
    }
}
