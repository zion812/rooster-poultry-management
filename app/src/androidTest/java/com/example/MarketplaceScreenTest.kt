package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MarketplaceScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun marketplaceScreen_displaysTitleAndFAB() {
        composeTestRule.setContent {
            MarketplaceScreen()
        }
        // Check for the title
        composeTestRule.onNodeWithText("Marketplace").assertIsDisplayed()
        // Check for the Add Item button (by content description, update if needed)
        composeTestRule.onNodeWithContentDescription("Add Item").assertExists()
    }
}
