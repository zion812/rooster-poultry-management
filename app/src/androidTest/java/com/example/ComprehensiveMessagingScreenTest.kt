package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ComprehensiveMessagingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun comprehensiveMessagingScreen_displaysTitleAndFab() {
        composeTestRule.setContent {
            ComprehensiveMessagingScreen()
        }
        // Check for the title
        composeTestRule.onNodeWithText("Messaging").assertIsDisplayed()
        // Check for the New Message FAB (update content description if needed)
        composeTestRule.onNodeWithContentDescription("New Message").assertExists()
    }
}
