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
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.models.UserRole
import com.example.rooster.ui.theme.RoosterTheme
import com.example.rooster.viewmodel.AuthViewModel
import com.example.rooster.core.navigation.AppNavHost
import com.example.rooster.core.navigation.AppScreens
import com.example.rooster.feature.farm.navigation.farmFeatureGraph
import com.example.rooster.feature.auctions.navigation.auctionsFeatureGraph
import com.example.rooster.feature.marketplace.navigation.marketplaceFeatureGraph
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.rooster.core.common.event.AppEventBus
import com.example.rooster.core.common.event.PaymentEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.rooster.app.navigation.NavigationRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    @Inject
    lateinit var eventBus: AppEventBus

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Log.d("MainActivity", "Payment successful: $razorpayPaymentId")
        GlobalScope.launch {
            if (razorpayPaymentId != null) {
                eventBus.publishPaymentEvent(
                    PaymentEvent.Success(
                        paymentId = razorpayPaymentId,
                        orderId = null,
                        signature = null
                    )
                )
            } else {
                eventBus.publishPaymentEvent(
                    PaymentEvent.Failure(
                        -1,
                        "Payment ID null in onPaymentSuccess",
                        null
                    )
                )
            }
        }
    }

    override fun onPaymentError(code: Int, description: String?) {
        Log.e("MainActivity", "Payment failed: Code $code, Response $description")
        val errorMessage = description ?: when (code) {
            Checkout.NETWORK_ERROR -> "Network error - Please check your connection"
            Checkout.INVALID_OPTIONS -> "Invalid payment options"
            Checkout.PAYMENT_CANCELED -> "Payment cancelled by user"
            Checkout.TLS_ERROR -> "TLS error during payment"
            else -> "Payment failed: $description"
        }
        GlobalScope.launch {
            eventBus.publishPaymentEvent(PaymentEvent.Failure(code, errorMessage, null))
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

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    val startDestination =
        if (authState.isAuthenticated) {
            when (authViewModel.normalizedUserRole) {
                UserRole.FARMER -> NavigationRoute.FARMER_HOME.name
                UserRole.HIGH_LEVEL -> NavigationRoute.HIGH_LEVEL_HOME.name
                else -> NavigationRoute.MARKETPLACE.name
            }
        } else {
            NavigationRoute.AUTH.name
        }

    AppNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationRoute.AUTH.name) {
            AuthScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        composable(NavigationRoute.MARKETPLACE.name) {
            MarketplaceScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        composable(NavigationRoute.FARMER_HOME.name) {
            FarmerHomeScreen(navController = navController)
        }

        composable(NavigationRoute.HIGH_LEVEL_HOME.name) {
            HighLevelHomeScreen(
                navController = navController,
                isTeluguMode = isTeluguMode,
                onLanguageToggle = { isTeluguMode = !isTeluguMode }
            )
        }

        farmFeatureGraph(navController)
        auctionsFeatureGraph(navController, isTeluguMode)
        marketplaceFeatureGraph(navController)

        composable(NavigationRoute.SIMPLE_VIEW_BIRDS.name) {
            Text("Simple View Birds Screen - Coming Soon")
        }
        composable(NavigationRoute.SIMPLE_SELL_BIRDS.name) {
            Text("Simple Sell Birds Screen - Coming Soon")
        }
        composable("payment/{listingId}/{amount}") { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            Text("Payment Screen - Coming Soon")
        }
        composable("payment") {
            Text("Payment Screen - Coming Soon")
        }
        composable(NavigationRoute.AUCTIONS.name) {
            Text("Auctions Screen - Coming Soon")
        }
        composable(NavigationRoute.CART.name) {
            Text("Cart Screen - Coming Soon")
        }
        composable(NavigationRoute.ORDER_HISTORY.name) {
            Text("Order History Screen - Coming Soon")
        }
        composable(NavigationRoute.PROFILE.name) {
            Text("Profile Screen - Coming Soon")
        }
        composable(NavigationRoute.HELP.name) {
            Text("Help Screen - Coming Soon")
        }
        composable(NavigationRoute.COMPLIANCE_SCREEN.name) {
            Text("Compliance Screen - Coming Soon")
        }
        composable(NavigationRoute.FOWL_TRACEABILITY.name) {
            Text("Fowl Management Screen - Coming Soon")
        }
        composable(NavigationRoute.DIAGNOSIS_HELP.name) {
            Text("Diagnostics Screen - Coming Soon")
        }
        composable(NavigationRoute.HEALTH_RECORDS.name) {
            Text("Health Management Screen - Coming Soon")
        }
        composable(NavigationRoute.MARKETPLACE_LISTING_CREATE.name) {
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
        composable(NavigationRoute.COMMUNITY.name) {
            Text("Community Screen - Coming Soon")
        }
        composable(NavigationRoute.FLOCK_MONITORING.name) {
            Text("Flock Monitoring Screen - Coming Soon")
        }
    }
}

@Composable
fun MarketplaceScreen(
    navController: androidx.navigation.NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit
) {
    Button(
        onClick = { navController.navigate(NavigationRoute.AUCTIONS.name) },
    ) {
        Text(if (isTeluguMode) "వేలాలు చూడండి" else "View Auctions")
    }
}
