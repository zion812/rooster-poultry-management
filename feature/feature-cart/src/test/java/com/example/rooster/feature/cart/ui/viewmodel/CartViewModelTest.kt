package com.example.rooster.feature.cart.ui.viewmodel

import app.cash.turbine.test
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.CartItem
import com.example.rooster.feature.cart.domain.repository.CartRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private lateinit var mockCartRepository: CartRepository

    // TestCoroutineDispatcher is deprecated, use StandardTestDispatcher or UnconfinedTestDispatcher
    private val testDispatcher = StandardTestDispatcher() // Or UnconfinedTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for tests
        mockCartRepository = mockk(relaxed = true) // relaxed = true to avoid mocking all funcs initially

        // Default behavior for repository calls
        coEvery { mockCartRepository.getCartItems(any()) } returns flowOf(emptyList())
        coEvery { mockCartRepository.getCartTotal(any()) } returns flowOf(0.0)

        viewModel = CartViewModel(mockCartRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
        unmockkAll()
    }

    @Test
    fun `init loads cart items and calculates totals correctly`() = runTest(testDispatcher) {
        val cartItems = listOf(
            CartItem(id = 1, userId = "temp_user_id", productId = "P1", quantity = 2, productName = "Product 1", productPrice = 10.0),
            CartItem(id = 2, userId = "temp_user_id", productId = "P2", quantity = 1, productName = "Product 2", productPrice = 20.0)
        )
        val expectedSubtotal = 40.0 // (2*10) + (1*20)
        val expectedDelivery = 50.0 // Mocked
        val expectedDiscount = 0.0   // Mocked, subtotal < 500
        val expectedTotal = expectedSubtotal + expectedDelivery - expectedDiscount

        coEvery { mockCartRepository.getCartItems("temp_user_id") } returns flowOf(cartItems)
        coEvery { mockCartRepository.getCartTotal("temp_user_id") } returns flowOf(expectedSubtotal)

        // Re-initialize ViewModel or trigger load manually if init isn't re-callable easily
        // For this test, let's create a new instance to test init block
        viewModel = CartViewModel(mockCartRepository)

        viewModel.uiState.test {
            val initialState = awaitItem() // Initial state before flow collection
            assertTrue(initialState.isLoading, "Initial state should be loading")

            val loadedState = awaitItem() // State after flows are collected

            assertFalse(loadedState.isLoading, "isLoading should be false after loading")
            assertEquals(cartItems, loadedState.cartItems, "Cart items do not match")
            assertEquals(expectedSubtotal, loadedState.subtotal, "Subtotal does not match")
            assertEquals(expectedDelivery, loadedState.deliveryFee, "Delivery fee does not match")
            assertEquals(expectedDiscount, loadedState.discount, "Discount does not match")
            assertEquals(expectedTotal, loadedState.total, "Total does not match")
            assertNull(loadedState.error, "Error should be null on successful load")
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateQuantity success path`() = runTest(testDispatcher) {
        val productId = "P1"
        val newQuantity = 3
        coEvery { mockCartRepository.updateItemQuantity("temp_user_id", productId, newQuantity) } returns Result.Success(Unit)

        viewModel.updateQuantity(productId, newQuantity)

        viewModel.uiState.test {
            // We are testing the action, not the resulting state from combined flows here directly,
            // as that's covered by the init test. Just ensure no error and itemUpdating is cleared.
            // A more thorough test would check the itemUpdating states.
            assertEquals(null, awaitItem().itemUpdating) // Check initial state or after update
            coVerify { mockCartRepository.updateItemQuantity("temp_user_id", productId, newQuantity) }
            cancelAndConsumeRemainingEvents()
        }
         // Assert that itemUpdating was set and then cleared
        // This requires a more complex state emission observation or separate state flows for such transient states.
        // For simplicity, focusing on repository interaction.
    }

    @Test
    fun `updateQuantity to zero removes item`() = runTest(testDispatcher) {
        val productId = "P1"
        coEvery { mockCartRepository.removeItemFromCart("temp_user_id", productId) } returns Result.Success(Unit)

        viewModel.updateQuantity(productId, 0)

        coVerify { mockCartRepository.removeItemFromCart("temp_user_id", productId) }
        coVerify(exactly = 0) { mockCartRepository.updateItemQuantity(any(), any(), any()) } // Ensure updateItemQuantity is NOT called
    }

    @Test
    fun `removeItem success path`() = runTest(testDispatcher) {
        val productId = "P1"
        coEvery { mockCartRepository.removeItemFromCart("temp_user_id", productId) } returns Result.Success(Unit)

        viewModel.removeItem(productId)

        viewModel.uiState.test {
             assertEquals(null, awaitItem().itemRemoving)
             coVerify { mockCartRepository.removeItemFromCart("temp_user_id", productId) }
             cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearCart success path`() = runTest(testDispatcher) {
        coEvery { mockCartRepository.clearCart("temp_user_id") } returns Result.Success(Unit)

        viewModel.clearCart()

        // Verify repository interaction
        coVerify { mockCartRepository.clearCart("temp_user_id") }

        // UI state should reflect loading then update from combined flow,
        // which would show an empty cart eventually.
        // This test primarily focuses on the action dispatch.
    }

    @Test
    fun `loadCartContent handles repository error`() = runTest(testDispatcher) {
        val errorMessage = "Test Repository Error"
        coEvery { mockCartRepository.getCartItems(any()) } returns flowOf(emptyList()) // Still need this for combine
        coEvery { mockCartRepository.getCartTotal(any()) } returns flow { throw Exception(errorMessage) } // Error from total flow

        // Re-initialize to trigger init block with erroring flow
        viewModel = CartViewModel(mockCartRepository)

        viewModel.uiState.test {
            skipItems(1) // Skip initial loading state if any before error
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
            cancelAndConsumeRemainingEvents()
        }
    }
}
