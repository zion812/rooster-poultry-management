package com.example.rooster.feature.auctions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.rooster.feature.auctions.ui.AuctionDetailScreen
import com.example.rooster.feature.auctions.ui.AuctionListScreen
import com.example.rooster.feature.auctions.ui.EnhancedBiddingScreen
import com.example.rooster.feature.auctions.ui.TokenPurchaseScreen

// Define routes specific to the auctions feature
object AuctionScreens {
    const val AUCTIONS_FEATURE_ROUTE = "auctions_feature_graph" // Route for the entire auctions feature graph

    const val AUCTION_LIST_ROUTE = "auction_list"
    const val AUCTION_DETAIL_ROUTE_BASE = "auction_detail"
    const val ENHANCED_BIDDING_ROUTE_BASE = "enhanced_bidding"
    const val TOKEN_PURCHASE_ROUTE_BASE = "auction_token_purchase" // Make it distinct from any global token purchase

    // Routes with arguments
    fun auctionDetailRoute(auctionId: String) = "$AUCTION_DETAIL_ROUTE_BASE/$auctionId"
    fun enhancedBiddingRoute(auctionId: String) = "$ENHANCED_BIDDING_ROUTE_BASE/$auctionId"
    fun tokenPurchaseRoute(productCost: Double?) = "$TOKEN_PURCHASE_ROUTE_BASE?cost=${productCost ?: 0.0}"


    // Argument names
    const val ARG_AUCTION_ID = "auctionId"
    const val ARG_PRODUCT_COST = "cost"
}

fun NavGraphBuilder.auctionsFeatureGraph(navController: NavController, isTeluguMode: Boolean) {
    navigation(
        route = AuctionScreens.AUCTIONS_FEATURE_ROUTE,
        startDestination = AuctionScreens.AUCTION_LIST_ROUTE
    ) {
        composable(AuctionScreens.AUCTION_LIST_ROUTE) {
            AuctionListScreen(navController = navController) // Assumes AuctionViewModel is Hilt-injected
        }
        composable(
            route = "${AuctionScreens.AUCTION_DETAIL_ROUTE_BASE}/{${AuctionScreens.ARG_AUCTION_ID}}",
            arguments = listOf(navArgument(AuctionScreens.ARG_AUCTION_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val auctionId = backStackEntry.arguments?.getString(AuctionScreens.ARG_AUCTION_ID)
            requireNotNull(auctionId) { "auctionId parameter was not found. Please make sure it is set!" }
            AuctionDetailScreen(auctionId = auctionId, navController = navController)
        }
        composable(
            route = "${AuctionScreens.ENHANCED_BIDDING_ROUTE_BASE}/{${AuctionScreens.ARG_AUCTION_ID}}",
            arguments = listOf(navArgument(AuctionScreens.ARG_AUCTION_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val auctionId = backStackEntry.arguments?.getString(AuctionScreens.ARG_AUCTION_ID)
            requireNotNull(auctionId) { "auctionId parameter was not found. Please make sure it is set!" }
            // TODO: Pass isTeluguMode from a shared source or ViewModel if needed globally, or remove if handled internally
            EnhancedBiddingScreen(auctionId = auctionId, navController = navController, isTeluguMode = isTeluguMode)
        }
        composable(
            route = "${AuctionScreens.TOKEN_PURCHASE_ROUTE_BASE}?${AuctionScreens.ARG_PRODUCT_COST}={${AuctionScreens.ARG_PRODUCT_COST}}",
            arguments = listOf(
                navArgument(AuctionScreens.ARG_PRODUCT_COST) {
                    type = NavType.FloatType
                    defaultValue = 0.0f
                }
            )
        ) { backStackEntry ->
            val productCost = backStackEntry.arguments?.getFloat(AuctionScreens.ARG_PRODUCT_COST)?.toDouble()
            // TODO: Pass isTeluguMode from a shared source or ViewModel
            TokenPurchaseScreen(navController = navController, isTeluguMode = isTeluguMode /*, productCost = productCost */)
            // Note: TokenPurchaseScreen was refactored to not take productCost directly but use packages.
            // This route argument might need to be removed or re-purposed if productCost is no longer used.
            // For now, keeping it to show argument passing, but the screen itself needs alignment.
            // Corrected call assuming TokenPurchaseScreen no longer takes productCost:
            // TokenPurchaseScreen(navController = navController, isTeluguMode = isTeluguMode)
        }
    }
}
