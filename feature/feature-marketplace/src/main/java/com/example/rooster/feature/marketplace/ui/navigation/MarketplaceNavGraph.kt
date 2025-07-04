package com.example.rooster.feature.marketplace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.feature.cart.ui.CartScreen // Assuming CartScreen is in feature-cart
import com.example.rooster.feature.marketplace.ui.marketplace.CheckoutScreen
import com.example.rooster.feature.marketplace.ui.marketplace.MarketplaceScreen
import com.example.rooster.feature.marketplace.ui.marketplace.OrderConfirmationScreen
import com.example.rooster.feature.marketplace.ui.marketplace.ProductDetailsScreen

@Composable
fun MarketplaceNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MarketplaceHome.route,
    // Actions for navigating outside this graph, if needed
    onNavigateToAuth: () -> Unit = {}, // Example
    // Potentially, a callback to show a global snackbar or dialog
    showSnackbar: (String) -> Unit = {}
) {
    val marketplaceNavActions = remember(navController) {
        MarketplaceNavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MarketplaceHome.route) {
            MarketplaceScreen(
                onProductClick = { productId ->
                    marketplaceNavActions.navigateToProductDetails(productId)
                }
                // TODO: Pass other necessary actions, e.g., for navigating to cart from a FAB
            )
        }

        composable(
            route = ProductDetails.routeWithArgs,
            arguments = ProductDetails.arguments
        ) { navBackStackEntry ->
            // val productId = navBackStackEntry.arguments?.getString(ProductDetails.productIdArg)
            // ViewModel will get it from SavedStateHandle
            ProductDetailsScreen(
                onNavigateBack = { marketplaceNavActions.navigateBack() },
                onViewSupplierProfile = { supplierId -> /* TODO: Navigate to supplier profile */ },
                onViewCart = { marketplaceNavActions.navigateToCart() }
            )
        }

        composable(Cart.route) {
            // Assuming CartScreen is in feature-cart and can be called directly.
            // If it has its own NavGraph or specific setup, adjust accordingly.
            CartScreen(
                onNavigateToCheckout = { marketplaceNavActions.navigateToCheckout() },
                onNavigateToMarketplace = { marketplaceNavActions.navigateToMarketplaceHome() },
                onNavigateBack = { marketplaceNavActions.navigateBack() }
            )
        }

        composable(Checkout.route) {
            CheckoutScreen(
                onNavigateBack = { marketplaceNavActions.navigateBack() },
                onOrderConfirmed = { orderId ->
                    marketplaceNavActions.navigateToOrderConfirmation(orderId)
                }
            )
        }

        composable(
            route = OrderConfirmation.routeWithArgs,
            arguments = OrderConfirmation.arguments
        ) { navBackStackEntry ->
            val orderId = navBackStackEntry.arguments?.getString(OrderConfirmation.orderIdArg)
            OrderConfirmationScreen(
                orderId = orderId ?: "N/A",
                onNavigateToMarketplace = { marketplaceNavActions.navigateToMarketplaceHome() },
                onViewOrderDetails = { confirmedOrderId -> /* TODO: Navigate to full order details screen */ }
            )
        }

        // TODO: Add other destinations like SupplierProfileScreen if part of this graph
    }
}
