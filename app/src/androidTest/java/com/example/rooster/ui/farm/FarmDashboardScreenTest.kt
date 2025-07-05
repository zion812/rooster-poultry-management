package com.example.rooster.ui.farm

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class FarmDashboardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allButtons_navigateOrActCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = NavigationRoute.FarmDashboard.route) {
                composable(NavigationRoute.FarmDashboard.route) { FarmDashboardScreen(navController = navController) }
                composable(NavigationRoute.FarmNewBatch.route) { /* FarmNewBatchScreen() */ }
                composable(NavigationRoute.FarmNewBird.route) { /* FarmNewBirdScreen() */ }
                composable(NavigationRoute.FarmNewEggs.route) { /* FarmNewEggsScreen() */ }
                composable(NavigationRoute.FarmNewBreeding.route) { /* FarmNewBreedingScreen() */ }
                composable(NavigationRoute.FarmNewChicks.route) { /* FarmNewChicksScreen() */ }
                composable(NavigationRoute.FarmNewFowl.route) { /* FarmNewFowlScreen() */ }
                composable(NavigationRoute.FarmNewIncubation.route) { /* FarmNewIncubationScreen() */ }
                composable(NavigationRoute.FarmReportMortality.route) { /* FarmReportMortalityScreen() */ }
                composable(NavigationRoute.FarmMortalityRecords.route) { /* FarmMortalityRecordsScreen() */ }
                composable(NavigationRoute.FarmUpdateChicks.route) { /* FarmUpdateChicksScreen() */ }
                composable(NavigationRoute.FarmUpdateAdults.route) { /* FarmUpdateAdultsScreen() */ }
                composable(NavigationRoute.FarmUpdateBreeding.route) { /* FarmUpdateBreedingScreen() */ }
                composable(NavigationRoute.FarmUpdateIncubation.route) { /* FarmUpdateIncubationScreen() */ }
                composable(NavigationRoute.FarmUpdateBreeders.route) { /* FarmUpdateBreedersScreen() */ }
                composable(NavigationRoute.FarmUpdateEggs.route) { /* FarmUpdateEggsScreen() */ }
            }
        }

        // Open side navigation panel
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        // --- New Section ---
        composeTestRule.onNodeWithText("New Batch").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewBatch.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Bird").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewBird.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Eggs").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewEggs.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Breeding").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewBreeding.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Chicks").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewChicks.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Flock").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewFowl.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("New Incubation").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmNewIncubation.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        // --- Mortality Section ---
        composeTestRule.onNodeWithText("Report Mortality").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmReportMortality.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Mortality Records").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmMortalityRecords.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        // --- Updates Section ---
        composeTestRule.onNodeWithText("Update Chicks").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateChicks.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Update Adults").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateAdults.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Update Breeding").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateBreeding.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Update Incubation").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateIncubation.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Update Breeders").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateBreeders.route)
        composeTestRule.onNodeWithContentDescription("Side Navigation").performClick()

        composeTestRule.onNodeWithText("Update Eggs").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.FarmUpdateEggs.route)
        // Side navigation panel will auto-dismiss after last click
    }
}
