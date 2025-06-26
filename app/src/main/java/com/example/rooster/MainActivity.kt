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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.models.UserRole
import com.example.rooster.ui.theme.RoosterTheme
import com.example.rooster.viewmodel.AuthViewModel
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.rooster.core.common.event.AppEventBus
import com.example.rooster.core.common.event.PaymentEvent
import kotlinx.coroutines.GlobalScope // Use a proper scope if this needs to be tied to lifecycle
import kotlinx.coroutines.launch
import com.razorpay.PaymentData // For accessing orderId and signature

// Import the screens we are about to use

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    @Inject
    lateinit var eventBus: AppEventBus

    // Remove old callback mechanism
    // private var onPaymentResult: ((Boolean, String?, String?) -> Unit)? = null
    // fun setPaymentResultCallback(callback: (Boolean, String?, String?) -> Unit) {
    //     onPaymentResult = callback
    // }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) { // Updated signature
        Log.d("MainActivity", "Payment successful: $razorpayPaymentId, Data: ${paymentData?.data}")
        GlobalScope.launch { // Use a ViewModel scope or lifecycleScope in real app
            if (razorpayPaymentId != null) {
                eventBus.publishPaymentEvent(
                    PaymentEvent.Success(
                        paymentId = razorpayPaymentId,
                        orderId = paymentData?.orderId,
                        signature = paymentData?.signature
                    )
                )
            } else {
                 // This case should ideally not happen if Razorpay calls onPaymentSuccess
                eventBus.publishPaymentEvent(PaymentEvent.Failure(-1, "Payment ID null in onPaymentSuccess", paymentData?.orderId))
            }
        }
    }

    override fun onPaymentError(code: Int, description: String?, paymentData: PaymentData?) { // Updated signature
        Log.e("MainActivity", "Payment failed: Code $code, Response $description, Data: ${paymentData?.data}")
        val errorMessage = description ?: when (code) {
            Checkout.NETWORK_ERROR -> "Network error - Please check your connection"
            Checkout.INVALID_OPTIONS -> "Invalid payment options"
            Checkout.PAYMENT_CANCELED -> "Payment cancelled by user"
            Checkout.TLS_ERROR -> "TLS error during payment"
            // Add more specific Razorpay error codes if needed
            else -> "Payment failed: $description"
        }
        GlobalScope.launch { // Use a ViewModel scope or lifecycleScope in real app
            eventBus.publishPaymentEvent(PaymentEvent.Failure(code, errorMessage, paymentData?.orderId))
        }
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
            NavigationRoute.Auth.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Authentication Screen
        composable(NavigationRoute.Auth.route) {
            com.example.rooster.ui.screens.AuthScreen(authViewModel)
        }

        // Marketplace Screen
        composable(NavigationRoute.Marketplace.route) {
            MarketplaceScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode },
            )
        }

        // Farmer Home Screen
        composable(NavigationRoute.FarmerHome.route) {
            FarmerHomeScreen(navController = navController)
        }

        // High Level Home Screen
        composable(NavigationRoute.HighLevelHome.route) {
            HighLevelHomeScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode },
            )
        }

        // Simple View Birds Screen - Fix navigation crash
        composable(NavigationRoute.SimpleViewBirds.route) {
            Text("Simple View Birds Screen - Coming Soon")
        }

        // Simple Sell Birds Screen - Fix navigation crash
        composable(NavigationRoute.SimpleSellBirds.route) {
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
        composable(NavigationRoute.Auctions.route) {
            Text("Auctions Screen - Coming Soon")
        }

        // Cart Screen
        composable(NavigationRoute.Cart.route) {
            Text("Cart Screen - Coming Soon")
        }

        // Order History Screen
        composable(NavigationRoute.OrderHistory.route) {
            Text("Order History Screen - Coming Soon")
        }

        // Profile Screen
        composable(NavigationRoute.Profile.route) {
            Text("Profile Screen - Coming Soon")
        }

        // Help Screen
        composable(NavigationRoute.Help.route) {
            Text("Help Screen - Coming Soon")
        }

        // Compliance Screen
        composable(NavigationRoute.ComplianceScreen.route) {
            Text("Compliance Screen - Coming Soon")
        }

        // Fowl Traceability Screen
        composable(NavigationRoute.FowlTraceability.route) {
            Text("Fowl Management Screen - Coming Soon")
        }

        // Diagnosis Help Screen
        composable(NavigationRoute.DiagnosisHelp.route) {
            Text("Diagnostics Screen - Coming Soon")
        }

        // Health Records Screen
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

        // Community Screen
        composable(NavigationRoute.Community.route) {
            Text("Community Screen - Coming Soon")
        }

        // Flock Monitoring Screen - Fix navigation crash
        composable(NavigationRoute.FlockMonitoring.route) {
            Text("Flock Monitoring Screen - Coming Soon")
        }
    }
}
