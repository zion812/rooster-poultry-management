package com.example.rooster.feature.marketplace.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MarketplaceScreen(navController: NavController) {
    // TODO: Implement Marketplace landing screen with product lists/categories
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Marketplace Feature Screen (Placeholder)")
    }
}

@Composable
fun ProductListScreen(navController: NavController, categoryId: String?) {
    // TODO: Fetch and display products for a given category or all products
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Product List for Category: ${categoryId ?: "All"} (Placeholder)")
    }
}

@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    // TODO: Fetch and display details for a specific product
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Product Detail for ID: $productId (Placeholder)")
    }
}
