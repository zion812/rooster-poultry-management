package com.example.rooster.core.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

// Graph Routes
const val ROOT_GRAPH_ROUTE = "root_graph" // Not strictly necessary but can be useful for clarity
const val AUTH_GRAPH_ROUTE = "auth_graph"
const val FARMER_GRAPH_ROUTE = "farmer_graph"
const val GENERAL_USER_GRAPH_ROUTE = "general_user_graph" // For Buyers
const val HIGH_LEVEL_USER_GRAPH_ROUTE = "high_level_graph" // For Admin
const val VET_USER_GRAPH_ROUTE = "vet_graph" // For Veterinarians

// Base URI for deep links
const val DEEP_LINK_URI = "https://rooster.example.com"

// Screen Routes
sealed class Screen(val route: String, val navArguments: List<NamedNavArgument> = emptyList()) {
    object Splash : Screen("splash_screen")

    // Authentication Flow
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object CheckEmail : Screen(
        route = "check_email_screen/{email}",
        navArguments = listOf(navArgument("email") { type = NavType.StringType })
    ) {
        fun createRoute(email: String) = "check_email_screen/$email"
        val deepLink = navDeepLink { uriPattern = "$DEEP_LINK_URI/verify_email/{email}" }
    }
    object ForgotPassword : Screen("forgot_password_screen") // Example, if needed

    // Farmer Flow (Examples)
    object FarmerDashboard : Screen("farmer_dashboard_screen")
    object AddChicken : Screen("add_chicken_screen")
    object ChickenList : Screen("chicken_list_screen")
    object FarmerProfile : Screen("farmer_profile_screen") {
        val deepLink = navDeepLink { uriPattern = "$DEEP_LINK_URI/farmer/profile" }
    }


    // Buyer (General User) Flow (Examples)
    object BuyerDashboard : Screen("buyer_dashboard_screen")
    object Marketplace : Screen("marketplace_screen")
    object ProductDetails : Screen(
        route = "product_details_screen/{productId}",
        navArguments = listOf(navArgument("productId") { type = NavType.StringType })
    ) {
        fun createRoute(productId: String) = "product_details_screen/$productId"
        val deepLink = navDeepLink { uriPattern = "$DEEP_LINK_URI/products/{productId}" }
    }
    object BuyerProfile : Screen("buyer_profile_screen")

    // Admin (High Level User) Flow (Examples)
    object AdminDashboard : Screen("admin_dashboard_screen")
    object UserManagement : Screen("user_management_screen")
    object AdminSettings : Screen("admin_settings_screen")

    // Vet (Veterinarian User) Flow (Examples)
    object VetDashboard : Screen("vet_dashboard_screen")
    object AppointmentList : Screen("appointment_list_screen")
    object VetProfile : Screen("vet_profile_screen")

    // Common Screens (can be part of multiple graphs or root)
    object Settings : Screen("settings_screen")
    object Help : Screen("help_screen")
    object Notifications : Screen("notifications_screen") {
        val deepLink = navDeepLink { uriPattern = "$DEEP_LINK_URI/notifications" }
    }

}

// Helper function to build full routes if needed, though direct usage of Screen.route is common
fun String.withGraph(graphRoute: String): String {
    return "$graphRoute/$this"
}

// Example of how a screen within a graph might be defined if you prefer explicit graph prefixes
// object FarmerDashboard : Screen(FARMER_GRAPH_ROUTE + "/dashboard")
// However, NavHost structure usually handles graph routing implicitly.
// The `Screen` objects define unique routes; nesting is done in NavHost.
