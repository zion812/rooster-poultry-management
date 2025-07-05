package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class DiagnosticsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun diagnosticsScreen_displaysTitle() {
        composeTestRule.setContent {
            DiagnosticsScreen()
        }
        // Check for the Diagnostics title
        composeTestRule.onNodeWithText("Diagnostics").assertIsDisplayed()
    }
}
