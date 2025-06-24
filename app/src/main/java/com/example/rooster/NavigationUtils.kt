package com.example.rooster

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rooster.models.UserRole

/**
 * Navigation Utilities and Safety Extensions
 *
 * This file provides safe navigation methods and centralized route management
 * to prevent IllegalArgumentException with deep links and navigation destinations.
 *
 * Common Issues Prevented:
 * - "Navigation destination cannot be found" exceptions
 * - Route mismatch between navigation calls and graph definitions
 * - Deep link URI inconsistencies
 * - Hardcoded string routes that can become outdated
 */

/**
 * Safe navigation extension that validates routes before navigation
 * and provides fallback handling for missing destinations.
 */
fun NavController.safeNavigate(
    route: String,
    onNavigationFailed: ((String) -> Unit)? = null,
) {
    try {
        // Validate that the route exists in the navigation graph
        val destination = graph.findNode(route)

        if (destination != null) {
            Log.d("Navigation", "Navigating safely to: $route")
            navigate(route) {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            Log.e("Navigation", "Route not found in navigation graph: $route")
            onNavigationFailed?.invoke("Route '$route' not found in navigation graph")
        }
    } catch (e: Exception) {
        Log.e("Navigation", "Navigation failed for route: $route", e)
        onNavigationFailed?.invoke("Navigation failed: ${e.message}")
    }
}

/**
 * Safe navigation using NavigationRoute to ensure type safety
 * and prevent route string mismatches.
 */
fun NavController.safeNavigate(
    navigationRoute: NavigationRoute,
    onNavigationFailed: ((String) -> Unit)? = null,
) {
    safeNavigate(navigationRoute.route, onNavigationFailed)
}

/**
 * Safe navigation with simple error handling and user feedback
 */
fun NavController.safeNavigateWithFallback(
    route: String,
    fallbackRoute: String = NavigationRoute.Home.route,
) {
    safeNavigate(route) { error ->
        Log.w("Navigation", "Navigation to $route failed, using fallback: $fallbackRoute")
        try {
            navigate(fallbackRoute)
        } catch (e: Exception) {
            Log.e("Navigation", "Fallback navigation also failed", e)
        }
    }
}

/**
 * Centralized route validation to check if a route exists in the navigation graph
 */
fun NavController.isRouteValid(route: String): Boolean {
    return when (val navRoute = NavigationRoute.fromRoute(route)) {
        is NavigationRoute -> graph.findNode(navRoute.route) != null
        else -> false
    }
}

/**
 * Get all available routes in the current navigation graph for debugging
 */
fun NavController.getAvailableRoutes(): List<String> {
    return try {
        val routes = mutableListOf<String>()

        fun collectRoutes(destination: NavDestination) {
            destination.route?.let { routes.add(it) }
            // Recursively collect from nested graphs if needed
        }

        graph.forEach { destination ->
            collectRoutes(destination)
        }

        routes.toList()
    } catch (e: Exception) {
        Log.e("Navigation", "Error collecting available routes", e)
        emptyList()
    }
}

/**
 * Debug helper to log navigation state and available routes
 */
fun NavController.debugNavigationState() {
    try {
        val currentRoute = currentBackStackEntry?.destination?.route
        val availableRoutes = getAvailableRoutes()

        Log.d("Navigation", "=== Navigation Debug Info ===")
        Log.d("Navigation", "Current route: $currentRoute")
        Log.d("Navigation", "Available routes: ${availableRoutes.joinToString(", ")}")
        Log.d("Navigation", "========================")
    } catch (e: Exception) {
        Log.e("Navigation", "Error debugging navigation state", e)
    }
}

/**
 * Navigation Route Registry
 *
 * Centralized registry of all routes to prevent hardcoded strings
 * and ensure consistency between navigation calls and graph definitions.
 */
object NavigationRouteRegistry {
    /**
     * All routes that should be available in the navigation graph
     */
    val ALL_ROUTES =
        listOf(
            NavigationRoute.Auth.route,
            NavigationRoute.Home.route,
            NavigationRoute.FarmerHome.route,
            NavigationRoute.HighLevelHome.route,
            NavigationRoute.Marketplace.route,
            NavigationRoute.Community.route,
            NavigationRoute.Profile.route,
            NavigationRoute.Transfers.route,
            NavigationRoute.Settings.route,
            NavigationRoute.Help.route,
            NavigationRoute.VetConsultation.route,
            NavigationRoute.IoTDashboard.route,
            NavigationRoute.Chat.base,
            NavigationRoute.TransferDetail.base,
            NavigationRoute.ActivityVerification.route,
            "explore",
            "dashboard",
            NavigationRoute.Cart.route,
            NavigationRoute.Auctions.route,
            "${NavigationRoute.AuctionDetail.base}",
        )

    /**
     * Validate that all registered routes exist in the navigation graph
     */
    fun validateAllRoutes(navController: NavController): List<String> {
        val missingRoutes = mutableListOf<String>()

        ALL_ROUTES.forEach { route ->
            // Skip parameterized routes for validation
            if (!route.contains("{") && !navController.isRouteValid(route)) {
                missingRoutes.add(route)
            }
        }

        if (missingRoutes.isNotEmpty()) {
            Log.e(
                "Navigation",
                "Missing routes in navigation graph: ${missingRoutes.joinToString(", ")}",
            )
        } else {
            Log.d("Navigation", "All routes validated successfully")
        }

        return missingRoutes
    }
}

/**
 * Deep Link URI Builder
 *
 * Helps build consistent deep link URIs to prevent android-app://androidx.navigation
 * format mismatches.
 */
object DeepLinkBuilder {
    private const val SCHEME = "android-app"
    private const val AUTHORITY = "androidx.navigation"

    /**
     * Build a deep link URI for a given route
     */
    fun buildDeepLink(route: String): String {
        return "$SCHEME://$AUTHORITY/$route"
    }

    /**
     * Build a deep link URI using NavigationRoute enum
     */
    fun buildDeepLink(navigationRoute: NavigationRoute): String {
        return buildDeepLink(navigationRoute.route)
    }

    /**
     * Extract route from deep link URI
     */
    fun extractRoute(deepLinkUri: String): String? {
        return try {
            if (deepLinkUri.startsWith("$SCHEME://$AUTHORITY/")) {
                deepLinkUri.removePrefix("$SCHEME://$AUTHORITY/")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Navigation", "Error extracting route from deep link: $deepLinkUri", e)
            null
        }
    }
}

/**
 * Navigation Safety Checker
 *
 * Comprehensive validation and safety checks for navigation operations
 */
object NavigationSafetyChecker {
    /**
     * Perform comprehensive navigation health check
     */
    fun performHealthCheck(navController: NavController): NavigationHealthReport {
        val missingRoutes = NavigationRouteRegistry.validateAllRoutes(navController)
        val availableRoutes = navController.getAvailableRoutes()
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        return NavigationHealthReport(
            isHealthy = missingRoutes.isEmpty(),
            missingRoutes = missingRoutes,
            availableRoutes = availableRoutes,
            currentRoute = currentRoute,
            totalRegisteredRoutes = NavigationRouteRegistry.ALL_ROUTES.size,
        )
    }
}

/**
 * Navigation health report data class
 */
data class NavigationHealthReport(
    val isHealthy: Boolean,
    val missingRoutes: List<String>,
    val availableRoutes: List<String>,
    val currentRoute: String?,
    val totalRegisteredRoutes: Int,
) {
    fun logReport() {
        Log.d("Navigation", "=== Navigation Health Report ===")
        Log.d("Navigation", "Health Status: ${if (isHealthy) "HEALTHY" else "ISSUES FOUND"}")
        Log.d("Navigation", "Current Route: $currentRoute")
        Log.d("Navigation", "Total Registered Routes: $totalRegisteredRoutes")
        Log.d("Navigation", "Available Routes: ${availableRoutes.size}")

        if (missingRoutes.isNotEmpty()) {
            Log.e("Navigation", "Missing Routes: ${missingRoutes.joinToString(", ")}")
        }

        Log.d("Navigation", "================================")
    }
}

/**
 * Navigation utilities for optimized navigation throughout the app.
 * Extension function to safely navigate with optimized parameters
 */
fun NavHostController.safeNavigate(
    route: String,
    popUpToRoute: String? = null,
    inclusive: Boolean = false,
    saveState: Boolean = true,
    restoreState: Boolean = true,
    launchSingleTop: Boolean = true,
) {
    navigate(route) {
        popUpToRoute?.let { popUpTo ->
            popUpTo(popUpTo) {
                this.inclusive = inclusive
                this.saveState = saveState
            }
        } ?: run {
            popUpTo(graph.findStartDestination().id) {
                this.saveState = saveState
            }
        }
        this.launchSingleTop = launchSingleTop
        this.restoreState = restoreState
    }
}

/**
 * Extension function to navigate to auth screen with proper cleanup
 */
fun NavHostController.navigateToAuth() {
    navigate(NavigationRoute.Auth.route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
    }
}

/**
 * Extension function to navigate to home screen based on user role
 */
fun NavHostController.navigateToHome(userRole: UserRole) {
    val destination =
        when (userRole) {
            UserRole.FARMER, UserRole.HIGH_LEVEL, UserRole.GENERAL -> "home" // Use Instagram home route
            UserRole.UNKNOWN -> NavigationRoute.Auth.route
        }

    safeNavigate(
        route = destination,
        popUpToRoute = NavigationRoute.Auth.route,
        inclusive = true,
    )
}

/**
 * Composable function to get current route safely
 */
@Composable
fun NavHostController.currentRouteAsState(): androidx.compose.runtime.State<String?> {
    val navBackStackEntry by currentBackStackEntryAsState()
    return remember {
        derivedStateOf {
            navBackStackEntry?.destination?.route
        }
    }
}

/**
 * Extension function to check if a route is currently selected
 */
fun NavDestination?.isRouteSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}

/**
 * Data class for navigation arguments
 */
data class NavigationArgs(
    val orderId: String? = null,
    val userId: String? = null,
    val fowlId: String? = null,
    val listingId: String? = null,
)

/**
 * Navigation routes with parameter support
 */
object NavigationRoutes {
    val Auth = NavigationRoute.Auth.route
    val Home = NavigationRoute.Home.route
    val Marketplace = NavigationRoute.Marketplace.route
    val Explore = "explore"
    val Create = "create"
    val Cart = NavigationRoute.Cart.route
    val Community = NavigationRoute.Community.route
    val Dashboard = "dashboard"
    val Transfers = NavigationRoute.Transfers.route
    val Profile = NavigationRoute.Profile.route
    val Settings = NavigationRoute.Settings.route
    val Help = NavigationRoute.Help.route
    val VetConsultation = NavigationRoute.VetConsultation.route
    val IotDashboard = NavigationRoute.IoTDashboard.route
    val Chat = NavigationRoute.Chat.base
    val TransferDetail = NavigationRoute.TransferDetail.base
    val ActivityVerification = NavigationRoute.ActivityVerification.route
    val FarmerHome = NavigationRoute.FarmerHome.route
    val HighLevelHome = NavigationRoute.HighLevelHome.route
    val Auctions = NavigationRoute.Auctions.route
    val AuctionDetail = NavigationRoute.AuctionDetail.base

    // Routes with parameters
    val TransferVerification = "transferVerification/{orderId}"
    val FowlDetails = "fowlDetails/{fowlId}"
    val ListingDetails = "listingDetails/{listingId}"
    val UserProfile = "userProfile/{userId}"

    // Functions to create routes with parameters
    fun transferVerification(orderId: String) = "transferVerification/$orderId"

    fun fowlDetails(fowlId: String) = "fowlDetails/$fowlId"

    fun listingDetails(listingId: String) = "listingDetails/$listingId"

    fun userProfile(userId: String) = "userProfile/$userId"
}

/**
 * Navigation state holder for complex navigation logic
 */
class NavigationManager(private val navController: NavHostController) {
    fun navigateBack(): Boolean {
        return navController.popBackStack()
    }

    fun navigateToAuth() {
        navController.navigateToAuth()
    }

    fun navigateToHome(userRole: UserRole) {
        navController.navigateToHome(userRole)
    }

    fun navigateToTransferVerification(orderId: String) {
        navController.safeNavigate(NavigationRoutes.transferVerification(orderId))
    }

    fun navigateToFowlDetails(fowlId: String) {
        navController.safeNavigate(NavigationRoutes.fowlDetails(fowlId))
    }

    fun navigateToListingDetails(listingId: String) {
        navController.safeNavigate(NavigationRoutes.listingDetails(listingId))
    }

    fun navigateToUserProfile(userId: String) {
        navController.safeNavigate(NavigationRoutes.userProfile(userId))
    }

    fun navigateToBottomNavDestination(route: NavigationRoute) {
        navController.safeNavigate(
            route = route.route,
            popUpToRoute = navController.graph.findStartDestination().route,
            inclusive = false,
            saveState = true,
            restoreState = true,
        )
    }
}

/**
 * Extension function to create NavigationManager
 */
@Composable
fun NavHostController.rememberNavigationManager(): NavigationManager {
    return remember(this) {
        NavigationManager(this)
    }
}

/**
 * Utility function to validate navigation routes
 */
fun validateNavigationRoute(route: String?): Boolean {
    return route != null && route == NavigationRoute.IoTDashboard.route
}
