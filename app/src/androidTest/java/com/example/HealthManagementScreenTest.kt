package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class HealthManagementScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun healthManagementScreen_displaysTitle() {
        composeTestRule.setContent {
            HealthManagementScreen()
        }
        // Check for the Health Management title
        composeTestRule.onNodeWithText("Health Management").assertIsDisplayed()
    }
}
