package com.example.rooster.feature.marketplace.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface MarketplaceDestination {
    val route: String
}

object MarketplaceHome : MarketplaceDestination {
    override val route = "marketplace_home"
}

object ProductDetails : MarketplaceDestination {
    override val route = "product_details"
    const val productIdArg = "productId"
    val routeWithArgs = "$route/{$productIdArg}"
    val arguments = listOf(
        navArgument(productIdArg) { type = NavType.StringType }
    )
}

object Cart : MarketplaceDestination {
    override val route = "cart"
}

object Checkout : MarketplaceDestination {
    override val route = "checkout"
}

object OrderConfirmation : MarketplaceDestination {
    override val route = "order_confirmation"
    const val orderIdArg = "orderId"
    val routeWithArgs = "$route/{$orderIdArg}"
    val arguments = listOf(
        navArgument(orderIdArg) { type = NavType.StringType }
    )
}

// Helper for navigation actions
class MarketplaceNavigationActions(private val navController: androidx.navigation.NavController) {
    fun navigateToMarketplaceHome() {
        navController.navigate(MarketplaceHome.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToProductDetails(productId: String) {
        navController.navigate("${ProductDetails.route}/$productId")
    }

    fun navigateToCart() {
        navController.navigate(Cart.route)
    }

    fun navigateToCheckout() {
        navController.navigate(Checkout.route)
    }

    fun navigateToOrderConfirmation(orderId: String) {
        // Clear back stack up to marketplace home when order is confirmed
        navController.navigate("${OrderConfirmation.route}/$orderId") {
            popUpTo(MarketplaceHome.route) // Or to a specific point in the checkout flow if needed
            launchSingleTop = true
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}
