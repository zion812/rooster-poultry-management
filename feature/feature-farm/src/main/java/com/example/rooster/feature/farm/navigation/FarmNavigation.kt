package com.example.rooster.feature.farm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.rooster.core.navigation.Screen // Assuming Screen is accessible or a similar pattern is used
// Import actual farm screens here when they are confirmed/created
// e.g., import com.example.rooster.feature.farm.ui.board.FarmDashboardScreen
// e.g., import com.example.rooster.feature.farm.ui.registry.FlockRegistryScreen
import com.example.rooster.core.navigation.PlaceholderScreen // Using placeholder for now

object FarmScreens {
    const val FARM_FEATURE_ROUTE = "farm_feature_graph" // Route for the whole farm feature graph

    // Specific screens within the farm feature
    val FarmDashboard = Screen("farm_dashboard_actual") // Make distinct from core Screen.FarmDashboard if that's just a general route
    val FlockRegistry = Screen("flock_registry")
    val FlockDetail = Screen("flock_detail/{flockId}") { flockId -> "flock_detail/$flockId" }
    // Add other screens like MortalityList, VaccinationSchedule, SensorDataView etc.
}

fun NavGraphBuilder.farmFeatureGraph(navController: NavController) {
    navigation(
        route = FarmScreens.FARM_FEATURE_ROUTE,
        startDestination = FarmScreens.FarmDashboard.route
    ) {
        composable(FarmScreens.FarmDashboard.route) {
            // Replace with actual FarmDashboardScreen(navController) from feature-farm UI
            PlaceholderScreen(name = "Actual Farm Dashboard (from feature-farm)")
        }
        composable(FarmScreens.FlockRegistry.route) {
            // Replace with actual FlockRegistryScreen(navController)
            PlaceholderScreen(name = "Flock Registry (from feature-farm)")
        }
        composable(FarmScreens.FlockDetail.route) { backStackEntry ->
            val flockId = backStackEntry.arguments?.getString("flockId")
            // Replace with actual FlockDetailScreen(navController, flockId)
            PlaceholderScreen(name = "Flock Detail: $flockId (from feature-farm)")
        }
        // Define other composable routes for the farm feature here
    }
}
