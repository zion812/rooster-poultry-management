package com.example.rooster.feature.farm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

// Local Screen data class for navigation routes
data class Screen(val route: String, val createRoute: ((String) -> String)? = null)

// Local PlaceholderScreen composable
@Composable
fun PlaceholderScreen(name: String) {
    Text(text = name)
}

object FarmScreens {
    const val FARM_FEATURE_ROUTE = "farm_feature_graph"
    val FarmDashboard = Screen("farm_dashboard_actual")
    val FlockRegistry = Screen("flock_registry")
    val FlockDetail = Screen("flock_detail/{flockId}") { flockId -> "flock_detail/$flockId" }
}

fun NavGraphBuilder.farmFeatureGraph(navController: NavController) {
    navigation(
        route = FarmScreens.FARM_FEATURE_ROUTE,
        startDestination = FarmScreens.FarmDashboard.route
    ) {
        composable(FarmScreens.FarmDashboard.route) {
            PlaceholderScreen(name = "Actual Farm Dashboard (from feature-farm)")
        }
        composable(FarmScreens.FlockRegistry.route) {
            PlaceholderScreen(name = "Flock Registry (from feature-farm)")
        }
        composable(FarmScreens.FlockDetail.route) { backStackEntry ->
            val flockId = backStackEntry.arguments?.getString("flockId")
            PlaceholderScreen(name = "Flock Detail: $flockId (from feature-farm)")
        }
    }
}
