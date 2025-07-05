package com.example.rooster.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Sealed class to define app navigation routes (extensible by feature modules)
sealed class Screen(val route: String) {
    data object Splash : Screen("splash") // Example
    data object Login : Screen("login")   // Example
    data object Home : Screen("home")     // Example

    // Farm feature routes (example, will be defined in feature-farm or a shared contract)
    data object FarmDashboard : Screen("farm_dashboard")
    // Add other farm-specific routes here or in feature-farm's own sealed class

    // Placeholder for other features
    data object Marketplace : Screen("marketplace")
    data object Auctions : Screen("auctions")
}

// Import the farm feature graph builder function
// Note: This will cause a build error until feature-farm is a dependency of core:navigation
// or this AppNavHost is moved to the :app module which can depend on both.
// For now, we'll assume a mechanism where feature graphs are registered or passed.
// A better approach is for :app to build the graph using navigation builders from features.
// Let's adjust Screen.FarmDashboard to point to the graph route.

// A placeholder for a screen, replace with actual screen composables
@Composable
fun PlaceholderScreen(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Screen: $name", modifier = modifier)
}


// Redefine Screen.FarmDashboard to be the route of the farm graph itself for easier calling
sealed class AppScreens(val route: String) {
    data object Splash : AppScreens("splash")
    data object Login : AppScreens("login")
    data object Register : AppScreens("register")
    data object CheckEmail : AppScreens("check_email/{email}") {
        fun createRoute(email: String) = "check_email/$email"
    }
    data object ForgotPassword : AppScreens("forgot_password") // Added for completeness

    // Generic Home, actual navigation will be to role-specific graphs
    data object Home : AppScreens("home_placeholder")

    // Feature graph routes (can also be constants)
    // These are more like "graph" routes rather than individual "screens"
    // data object FarmFeature : AppScreens("farm_feature_graph")
    // data object MarketplaceFeature : AppScreens("marketplace_feature_graph")
    // data object AuctionsFeature : AppScreens("auctions_feature_graph")
}

// Top-level graph routes
const val AUTH_GRAPH_ROUTE = "auth_graph"
const val FARMER_USER_GRAPH_ROUTE = "farmer_user_graph"
const val GENERAL_USER_GRAPH_ROUTE = "general_user_graph" // For Buyers, etc.
const val HIGH_LEVEL_USER_GRAPH_ROUTE = "high_level_user_graph" // For Admins
const val VET_USER_GRAPH_ROUTE = "vet_user_graph" // For Veterinarians

// Example specific screen routes within role graphs (if needed for deep linking etc.)
// These would typically live within their respective feature modules' navigation files.
// object FarmerHomeScreenRoutes {
//    const val Dashboard = "farmer_dashboard"
//    const val FlockManagement = "farmer_flock_management"
// }


/**
 * Defines the main navigation graph for the application.
 * Feature modules will typically define their own nested navigation graphs
 * which can be included here.
 *
 * IMPORTANT: For this to work cleanly, either:
 * 1. The :core:navigation module should NOT call specific feature graph builders directly
 *    (to avoid circular dependencies if features also depend on core:navigation for Screen definitions).
 * 2. Or, the NavHost construction should happen in the :app module, which can depend on all features
 *    and core:navigation.
 *
 * Let's assume option 2 for a robust setup. This AppNavHost is a template.
 * The actual NavHost in :app will call farmFeatureGraph (and others).
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppScreens.Splash.route, // Default start, can be dynamic
    // This lambda allows features to register their graphs
    // This is a common pattern to keep :core:navigation independent of features.
    graphBuilder: NavGraphBuilder.() -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppScreens.Splash.route) {
            PlaceholderScreen("Splash") // Replace with actual SplashScreen
        }
        composable(AppScreens.Login.route) {
            PlaceholderScreen("Login") // Replace with actual LoginScreen
        }
        composable(AppScreens.Home.route) {
            PlaceholderScreen("Home") // Replace with actual HomeScreen
        }

        // Apply graphs contributed by features (or the app module)
        graphBuilder()

        // Example direct linking (if core:navigation *could* depend on feature-farm, which is not ideal)
        // This is illustrative. The farmFeatureGraph() call would ideally be in the app module's NavHost.
        // farmFeatureGraph(navController) // This would be from: import com.example.rooster.feature.farm.navigation.farmFeatureGraph
    }
}

// The following demonstrates how the :app module would use this:
// In app/src/main/java/com/example/rooster/MainActivity.kt (or similar app-level NavHost setup)
//
// val navController = rememberNavController()
// AppNavHost(navController = navController) {
//   // This 'this' is a NavGraphBuilder
//   farmFeatureGraph(navController) // from feature-farm
//   marketplaceFeatureGraph(navController) // from feature-marketplace (when created)
//   // etc.
// }
