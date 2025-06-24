package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class FarmerHomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun farmerHomeScreen_displaysWelcome() {
        composeTestRule.setContent {
            FarmerHomeScreenFixed()
        }
        // Check for the Welcome title or section
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}
