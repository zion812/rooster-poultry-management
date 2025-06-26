package com.example.rooster

// Import existing screens
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
// import androidx.navigation.compose.NavHost // Will be replaced by AppNavHost
import androidx.navigation.compose.composable // Still needed for AppNavHost's builder
import androidx.navigation.compose.rememberNavController
import com.example.rooster.models.UserRole
import com.example.rooster.ui.theme.RoosterTheme
import com.example.rooster.viewmodel.AuthViewModel
import com.example.rooster.core.navigation.AppNavHost // Import AppNavHost
import com.example.rooster.core.navigation.AppScreens // Import AppScreens for startDestination
import com.example.rooster.feature.farm.navigation.FarmScreens // Import Farm specific routes
import com.example.rooster.feature.farm.navigation.farmFeatureGraph // Import farm graph builder
import com.example.rooster.feature.auctions.navigation.auctionsFeatureGraph // Import auctions graph builder
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint

// Import the screens we are about to use

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {
    // Payment result handling for Razorpay
    private var onPaymentResult: ((Boolean, String?, String?) -> Unit)? = null

    fun setPaymentResultCallback(callback: (Boolean, String?, String?) -> Unit) {
        onPaymentResult = callback
    }

    override fun onPaymentSuccess(paymentId: String?) {
        Log.d("MainActivity", "Payment successful: $paymentId")
        onPaymentResult?.invoke(true, paymentId, null)
    }

    override fun onPaymentError(
        code: Int,
        response: String?,
    ) {
        Log.e("MainActivity", "Payment failed: Code $code, Response $response")

        val errorMessage =
            when (code) {
                1 -> "Network error - Please check your connection"
                2 -> "Invalid payment credentials"
                0 -> "Payment cancelled by user"
                else -> "Payment failed: $response"
            }

        onPaymentResult?.invoke(false, null, errorMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoosterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RoosterApp()
                }
            }
        }
    }
}

@Composable
fun RoosterApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    var isTeluguMode by remember { mutableStateOf(true) }

    // Check authentication state on app start
    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    val startDestination =
        if (authState.isAuthenticated) {
            when (authViewModel.normalizedUserRole) {
                UserRole.FARMER -> NavigationRoute.FarmerHome.route
                UserRole.HIGH_LEVEL -> NavigationRoute.HighLevelHome.route
                else -> NavigationRoute.Marketplace.route
            }
        } else {
            AppScreens.Login.route // Use AppScreens for start destination consistency
        }

    // Use AppNavHost from :core:navigation
    AppNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // This 'this' is a NavGraphBuilder instance provided by AppNavHost

        // Define app-level composable routes that are not part of a specific feature graph yet
        // or are entry points.
        composable(NavigationRoute.Auth.route) { // Assuming NavigationRoute.Auth.route is "login" or similar
            com.example.rooster.ui.screens.AuthScreen(authViewModel)
        }

        composable(NavigationRoute.Marketplace.route) { // Assuming this is AppScreens.Home.route or similar
            MarketplaceScreen( // This might become the main "Home" screen or part of a main tabbed layout
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode },
            )
        }
        composable(NavigationRoute.FarmerHome.route) { // This could be an entry to FarmFeature or a distinct home
            FarmerHomeScreen(navController = navController)
        }
        composable(NavigationRoute.HighLevelHome.route) {
             HighLevelHomeScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode },
            )
        }

        // Integrate the farm feature graph
        farmFeatureGraph(navController)

        // Integrate the auctions feature graph
        auctionsFeatureGraph(navController, isTeluguMode)


        // TODO: Integrate other feature graphs here as they are developed
        // e.g., marketplaceFeatureGraph(navController)



        // Keep other existing placeholder routes for now, they might be refactored into features
        // or specific graphs later.
        composable(NavigationRoute.SimpleViewBirds.route) {
            Text("Simple View Birds Screen - Coming Soon")
        }
        composable(NavigationRoute.SimpleSellBirds.route) {
            Text("Simple Sell Birds Screen - Coming Soon")
        }
        composable("payment/{listingId}/{amount}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            Text("Dummy Payment Screen - Coming Soon")
        }
        composable("payment") {
            Text("Dummy Payment Screen - Coming Soon")
        }
        composable(NavigationRoute.Auctions.route) {
            Text("Auctions Screen - Coming Soon")
        }
        composable(NavigationRoute.Cart.route) {
            Text("Cart Screen - Coming Soon")
        }
        composable(NavigationRoute.OrderHistory.route) {
            Text("Order History Screen - Coming Soon")
        }
        composable(NavigationRoute.Profile.route) {
            Text("Profile Screen - Coming Soon")
        }
        composable(NavigationRoute.Help.route) {
            Text("Help Screen - Coming Soon")
        }
        composable(NavigationRoute.ComplianceScreen.route) {
            Text("Compliance Screen - Coming Soon")
        }
        composable(NavigationRoute.FowlTraceability.route) {
            Text("Fowl Management Screen - Coming Soon")
        }
        composable(NavigationRoute.DiagnosisHelp.route) {
            Text("Diagnostics Screen - Coming Soon")
        }
        composable(NavigationRoute.HealthRecords.route) {
            Text("Health Management Screen - Coming Soon")
        }
        composable(NavigationRoute.MarketplaceListingCreate.route) {
            Text("Marketplace Listing Create Screen - Coming Soon")
        }
        composable("edit_listing/{listingId}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            Text("Marketplace Listing Edit Screen - Coming Soon")
        }
        composable("listing_detail/{listingId}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            Text("Marketplace Listing Detail Screen - Coming Soon")
        }
        composable(NavigationRoute.Community.route) {
            Text("Community Screen - Coming Soon")
        }
        composable(NavigationRoute.FlockMonitoring.route) {
            Text("Flock Monitoring Screen - Coming Soon")
        }
    }
}
