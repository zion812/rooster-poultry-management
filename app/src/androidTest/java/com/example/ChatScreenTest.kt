package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_displaysTitleAndSendButton() {
        composeTestRule.setContent {
            ChatScreen()
        }
        // Check for the title
        composeTestRule.onNodeWithText("Chat").assertIsDisplayed()
        // Check for the Send button (by content description, update if needed)
        composeTestRule.onNodeWithContentDescription("Send").assertExists()
    }
}
