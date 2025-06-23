package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class FowlScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fowlScreen_displaysTitleAndAddButton() {
        composeTestRule.setContent {
            FowlScreen()
        }
        // Check for the title
        composeTestRule.onNodeWithText("Fowl Management").assertIsDisplayed()
        // Check for the Add Fowl button (by content description)
        composeTestRule.onNodeWithContentDescription("Add Fowl").assertIsDisplayed()
    }
}
