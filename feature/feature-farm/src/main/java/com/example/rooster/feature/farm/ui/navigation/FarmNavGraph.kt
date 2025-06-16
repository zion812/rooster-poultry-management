package com.example.rooster.feature.farm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rooster.feature.farm.ui.details.FarmDetailsScreen
import com.example.rooster.feature.farm.ui.board.FarmBoardScreen
import com.example.rooster.feature.farm.ui.mortality.MortalityScreen
import com.example.rooster.feature.farm.ui.updates.UpdateScreen
import com.example.rooster.feature.farm.ui.registry.FlockRegistryScreen

// TODO: import other screens

object FarmRoutes {
    const val Details = "farm_details/{farmId}"
    const val Board = "farm_board/{farmId}"
    const val Mortality = "farm_mortality/{farmId}"
    const val Updates = "farm_updates/{farmId}"
    const val Registry = "farm_registry/{farmId}"
    // add: Monitoring, Growth, Actions, Vaccination, FamilyTree
}

@Composable
fun FarmNavGraph(startFarmId: String, onBack: () -> Unit, onError: (String) -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = FarmRoutes.Details) {
        composable(
            FarmRoutes.Details,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("farmId") ?: startFarmId
            FarmDetailsScreen(farmId = id, onBack = onBack)
        }
        composable(
            FarmRoutes.Board,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("farmId") ?: startFarmId
            FarmBoardScreen(farmId = id, onBack = { navController.popBackStack() })
        }
        composable(
            FarmRoutes.Mortality,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("farmId") ?: startFarmId
            MortalityScreen(
                fowlId = id,
                onBack = onBack,
                onError = onError
            )
        }
        composable(
            FarmRoutes.Updates,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("farmId") ?: startFarmId
            UpdateScreen(fowlId = id)
        }
        composable(
            FarmRoutes.Registry,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("farmId") ?: startFarmId
            FlockRegistryScreen(
                farmId = id,
                onBack = { navController.popBackStack() },
                onError = onError
            )
        }
        // TODO: add other composable destinations
    }
}
