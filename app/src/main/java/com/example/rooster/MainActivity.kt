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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.rooster.models.UserRole
import com.example.rooster.ui.navigation.NavigationRoute
import com.example.rooster.viewmodel.AuthViewModel
import com.example.rooster.ui.theme.RoosterTheme
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onPaymentError(code: Int, response: String?) {
        Log.e("MainActivity", "Payment failed: Code $code, Response $response")

        val errorMessage = when (code) {
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
                    color = MaterialTheme.colorScheme.background
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

    val startDestination = if (authState.isAuthenticated) {
        when (authViewModel.normalizedUserRole) {
            UserRole.FARMER -> NavigationRoute.FARMER_HOME.route
            UserRole.HIGH_LEVEL -> NavigationRoute.HIGH_LEVEL_HOME.route
            else -> NavigationRoute.MARKETPLACE.route
        }
    } else {
        NavigationRoute.AUTH.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screen
        composable(NavigationRoute.AUTH.route) {
            Text("Auth Screen - Coming Soon")
        }

        // Marketplace Screen
        composable(NavigationRoute.MARKETPLACE.route) {
            Text("Marketplace Screen - Coming Soon")
        }

        // Farmer Home Screen
        composable(NavigationRoute.FARMER_HOME.route) {
            Text("Farmer Home Screen - Coming Soon")
        }

        // High Level Home Screen
        composable(NavigationRoute.HIGH_LEVEL_HOME.route) {
            Text("High Level Home Screen - Coming Soon")
        }

        // Simple View Birds Screen - Fix navigation crash
        composable(
            NavigationRoute.SIMPLE_VIEW_BIRDS.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/simple_view_birds" }
            )
        ) {
            Text("Simple View Birds Screen - Coming Soon")
        }

        // Simple Sell Birds Screen - Fix navigation crash
        composable(
            NavigationRoute.SIMPLE_SELL_BIRDS.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/simple_sell_birds" }
            )
        ) {
            Text("Simple Sell Birds Screen - Coming Soon")
        }

        // Payment Screen with parameters
        composable("payment/{listingId}/{amount}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            Text("Dummy Payment Screen - Coming Soon")
        }

        // Direct payment screen
        composable("payment") {
            Text("Dummy Payment Screen - Coming Soon")
        }

        // Auctions Screen - Fix navigation crash
        composable(
            NavigationRoute.AUCTIONS.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/auctions" }
            )
        ) {
            Text("Auctions Screen - Coming Soon")
        }

        // Cart Screen
        composable(NavigationRoute.CART.route) {
            Text("Cart Screen - Coming Soon")
        }

        // Order History Screen
        composable(NavigationRoute.ORDER_HISTORY.route) {
            Text("Order History Screen - Coming Soon")
        }

        // Profile Screen
        composable(NavigationRoute.PROFILE.route) {
            Text("Profile Screen - Coming Soon")
        }

        // Help Screen
        composable(NavigationRoute.HELP.route) {
            Text("Help Screen - Coming Soon")
        }

        // Compliance Screen
        composable(NavigationRoute.COMPLIANCE_SCREEN.route) {
            Text("Compliance Screen - Coming Soon")
        }

        // Fowl Traceability Screen
        composable(NavigationRoute.FOWL_TRACEABILITY.route) {
            Text("Fowl Management Screen - Coming Soon")
        }

        // Diagnosis Help Screen
        composable(NavigationRoute.DIAGNOSIS_HELP.route) {
            Text("Diagnostics Screen - Coming Soon")
        }

        // Health Records Screen
        composable(NavigationRoute.HEALTH_RECORDS.route) {
            Text("Health Management Screen - Coming Soon")
        }

        composable(NavigationRoute.MARKETPLACE_LISTING_CREATE.route) {
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

        // Community Screen
        composable(NavigationRoute.COMMUNITY.route) {
            Text("Community Screen - Coming Soon")
        }

        // Flock Monitoring Screen - Fix navigation crash
        composable(NavigationRoute.FLOCK_MONITORING.route) {
            Text("Flock Monitoring Screen - Coming Soon")
        }
    }
}
