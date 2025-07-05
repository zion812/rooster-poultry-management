package com.example.rooster.ui.farm

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class SimpleFarmerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allButtons_navigateOrActCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = NavigationRoute.SimpleFarmer.route) {
                composable(NavigationRoute.SimpleFarmer.route) { SimpleFarmerScreen(navController) }
                // Add other composable destinations as needed for navigation testing
            }
        }
        // Simulate clicks and add assertions
        composeTestRule.onNodeWithText("Add Birds").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.SimpleAddBirds.route)
        composeTestRule.onNodeWithText("View Birds").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.SimpleViewBirds.route)
        composeTestRule.onNodeWithText("Sell Birds").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.SimpleSellBirds.route)
        composeTestRule.onNodeWithText("Help").performClick()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.SimpleHelp.route)
        // Add more click simulations and assertions for other buttons/links if applicable
    }
}
