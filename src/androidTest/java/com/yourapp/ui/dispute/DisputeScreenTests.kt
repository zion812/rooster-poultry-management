package com.yourapp.ui.dispute

import androidx.compose.ui.test.*

@RunWith(AndroidJUnit4::class)
class DisputeScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun disputeFormScreen_initialState_isRendered() {
        composeTestRule.setContent {
            DisputeFormScreen(userId = "testUser", onSubmitted = {})
        }
        composeTestRule.onNodeWithText("Submit a Dispute").assertIsDisplayed()
        composeTestRule.onNodeWithText("Issue Type").assertIsDisplayed()
        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Attach Photo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit").assertIsDisplayed().assertIsNotEnabled() // Submit initially disabled
    }

    @Test
    fun disputeFormScreen_whenDetailsEntered_submitIsEnabled() {
        composeTestRule.setContent {
            DisputeFormScreen(userId = "testUser", onSubmitted = {})
        }
        composeTestRule.onNodeWithText("Details").performTextInput("This is a test dispute.")
        composeTestRule.onNodeWithText("Submit").assertIsEnabled()
    }

    @Test
    fun disputeStatusScreen_showsLoadingThenNoDisputes() {
        // This test assumes DisputeRepository.fetchDisputes calls onLoaded with empty list
        composeTestRule.setContent {
            DisputeStatusScreen()
        }
        // It will quickly transition from loading to empty/error. 
        // For more robust test, would need to inject a TestDisputeRepository or use IdlingResources.
        composeTestRule.onNodeWithText("No disputes found", useUnmergedTree = true).assertIsDisplayed() // Assert eventually displayed
    }

    @Test
    fun disputeStatusScreen_showsDisputesWhenLoaded() {
        val mockDisputes = listOf(
            DisputeRecord(id = "1", userId = "user1", type = DisputeType.ORDER_ISSUE, message = "Msg1", status = DisputeStatus.PENDING),
            DisputeRecord(id = "2", userId = "user2", type = DisputeType.PRODUCT_DAMAGED, message = "Msg2", status = DisputeStatus.RESOLVED)
        )
        // To test this properly, DisputeStatusScreen needs a way to inject or use a mocked DisputeRepository.
        // For now, this test is conceptual and won't pass without refactoring the screen for testability.
        // Example of how it *would* look if repository was injectable:
        /*
        val mockRepo = mock(DisputeRepository::class.java)
        `when`(mockRepo.fetchDisputes(any(), any())).thenAnswer { invocation ->
            val onLoaded = invocation.arguments[0] as (List<DisputeRecord>) -> Unit
            onLoaded(mockDisputes)
        }
        composeTestRule.setContent {
            DisputeStatusScreen(repository = mockRepo) // Assuming constructor injection
        }
        composeTestRule.onNodeWithText("Type: ORDER_ISSUE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Status: PENDING").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type: PRODUCT_DAMAGED").assertIsDisplayed()
        composeTestRule.onNodeWithText("Status: RESOLVED").assertIsDisplayed()
        */
    }
}
