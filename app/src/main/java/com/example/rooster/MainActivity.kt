package com.example.rooster

// Import existing screens
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.rooster.auth.AuthViewModel
import com.example.rooster.payment.DummyPaymentScreen
import com.example.rooster.screens.AuctionsScreen
import com.example.rooster.screens.FarmerHomeScreen
import com.example.rooster.screens.FlockMonitoringScreen
import com.example.rooster.screens.HighLevelHomeScreen
import com.example.rooster.screens.MarketplaceScreen
import com.example.rooster.screens.SimpleSellBirdsScreen
import com.example.rooster.screens.SimpleViewBirdsScreen
import com.example.rooster.ui.navigation.AuthNavigation
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
            "farmer" -> "farmer_home"
            "highlevel", "high_level" -> "high_level_home"
            else -> "marketplace"
        }
    } else {
        "auth"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screen
        composable("auth") {
            AuthNavigation(navController = navController)
        }

        // Marketplace Screen
        composable("marketplace") {
            MarketplaceScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        // Farmer Home Screen
        composable("farmer_home") {
            FarmerHomeScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        // High Level Home Screen
        composable("high_level_home") {
            HighLevelHomeScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        // Simple View Birds Screen - Fix navigation crash
        composable(
            "simple_view_birds",
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/simple_view_birds" }
            )
        ) {
            SimpleViewBirdsScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        // Simple Sell Birds Screen - Fix navigation crash
        composable(
            "simple_sell_birds",
            deepLinks = listOf(
                navDeepLink { uriPattern = "android-app://androidx.navigation/simple_sell_birds" }
            )
        ) {
            SimpleSellBirdsScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        // Payment Screen with parameters
        composable("payment/{listingId}/{amount}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            DummyPaymentScreen(
                navController = navController,
                listingId = listingId,
                amount = amount,
                isTeluguMode = isTeluguMode
            )
        }

        // Direct payment screen
        composable("payment") {
            DummyPaymentScreen(
                navController = navController,
                listingId = "",
                amount = "0",
                isTeluguMode = isTeluguMode
            )
        }

        // Auctions Screen - Fix navigation crash
        composable(
        "auctions",
        deepLinks = listOf(
        navDeepLink { uriPattern = "android-app://androidx.navigation/auctions" }
        )
        ) {
        AuctionsScreen(
        navController = navController,
        isTeluguMode = isTeluguMode,
        onLanguageToggle = { isTeluguMode = !isTeluguMode }
        )
        }

        composable(NavigationRoute.MarketplaceListingCreate.route) {
            MarketplaceListingCreateScreen(navController = navController, isTeluguMode = isTeluguMode)
        }
        composable(NavigationRoute.MarketplaceListingEdit.base) {
        val listingId = it.arguments?.getString("listingId") ?: ""
        MarketplaceListingEditScreen(navController = navController, listingId = listingId, isTeluguMode = isTeluguMode)
        }
        composable(NavigationRoute.MarketplaceListingDetail.base) {
                    val listingId = it.arguments?.getString("listingId") ?: ""
                    MarketplaceListingDetailScreen(navController = navController, listingId = listingId, isTeluguMode = isTeluguMode)
                }

                // Flock Monitoring Screen - Fix navigation crash
                composable("flock_monitoring") {
                    FlockMonitoringScreen(
                        navController = navController,
                        isTeluguMode = isTeluguMode,
                        onLanguageToggle = { isTeluguMode = !isTeluguMode }
                    )
                }
    }
}
