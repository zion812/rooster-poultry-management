import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.example.rooster.NavigationRoute
import org.junit.Rule
import org.junit.Test

class MarketplaceListingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToAuctionDetails() {
        composeTestRule.setContent {
            // Test content setup
        }
        composeTestRule.onNodeWithContentDescription("Marketplace Listing").assertIsDisplayed()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.MarketplaceListingDetail.route)
    }

    @Test
    fun testMarketplaceDashboardNavigation() {
        composeTestRule.setContent {
            // Test content set for FarmDashboard
        }
        composeTestRule.onNodeWithContentDescription("Gavel Icon").assertIsDisplayed()
        assert(composeTestRule.navController.currentBackStackEntry?.destination?.route == NavigationRoute.Marketplace.route)
    }
}

class MarketplaceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMarketplaceNavigation() {
        composeTestRule.setContent {
            // Composable content setup here
        }

        // Example of a test composable usage
        composeTestRule.onNodeWithContentDescription("Gavel Icon").assertIsDisplayed()

        // Correcting the 'sert' typo to 'assert'
        composeTestRule.onNodeWithContentDescription("Marketplace Listing").assert {
            it.compositionContext.navController.currentBackStackEntry?.destination?.route == NavigationRoute.MarketplaceListing.route
        }
    }
}
