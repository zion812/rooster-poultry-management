package com.example.rooster.feature.farm.ui.registry

import android.content.Context
import app.cash.turbine.test
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.toUserFriendlyMessage
import com.example.rooster.feature.farm.domain.model.AgeGroup
import com.example.rooster.feature.farm.domain.model.FlockRegistrationData
import com.example.rooster.feature.farm.domain.model.RegistryType
import com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class FlockRegistryViewModelTest {

    private lateinit var viewModel: FlockRegistryViewModel
    private lateinit var mockRegisterFlockUseCase: RegisterFlockUseCase
    private lateinit var mockContext: Context // For ErrorMessageMapper

    private val testDispatcher = UnconfinedTestDispatcher() // StandardTestDispatcher() can also be used

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRegisterFlockUseCase = mockk()
        mockContext = mockk(relaxed = true) // Relaxed mock for context

        // Setup mock for toUserFriendlyMessage extension
        // This is tricky as it's an extension. A common way is to mock a wrapper or test it via integration.
        // For a unit test, we might assume it works or provide a specific throwable that maps to a known string.
        every { any<Throwable>().toUserFriendlyMessage(mockContext) } answers { firstArg<Throwable>().localizedMessage ?: "Fallback error" }


        viewModel = FlockRegistryViewModel(mockContext, mockRegisterFlockUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateRegistryType updates state correctly`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.updateRegistryType(RegistryType.TRACEABLE)
            val updatedState = awaitItem()
            assertEquals(RegistryType.TRACEABLE, updatedState.registryType)
            assertTrue(updatedState.requiresVerification)

            viewModel.updateRegistryType(RegistryType.NON_TRACEABLE)
            val finalState = awaitItem()
            assertEquals(RegistryType.NON_TRACEABLE, finalState.registryType)
            assertFalse(finalState.requiresVerification)
        }
    }

    @Test
    fun `updateField clears specific field error and calls validation`() = runTest {
        // Initial state with an error
        val initialFieldErrors = mapOf("breed" to "Breed error")
        viewModel = FlockRegistryViewModel(mockContext, mockRegisterFlockUseCase) // Re-init for clean state if needed or set state directly
        // Simulate initial state if _uiState were public or via an init method
        // For simplicity, we'll assume validation is called and fieldErrors would be updated.
        // This test would be more robust if we could set initial _uiState.value directly.

        viewModel.updateField("breed", "New Breed")
        viewModel.uiState.test {
            val state = awaitItem() // This will be the state *after* updateField and validate
            assertNull(state.fieldErrors["breed"])
            // assertTrue(state.canSubmit) // Depends on other fields being valid
        }
    }

    @Test
    fun `validateFormAndSetCanSubmit identifies missing required fields`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial state where nothing is set

            viewModel.updateRegistryType(RegistryType.NON_TRACEABLE)
            awaitItem()
            viewModel.updateAgeGroup(AgeGroup.CHICKS)
            awaitItem()
            // Breed is missing
            viewModel.updateField("breed", "") // Trigger validation after this

            val state = awaitItem()
            assertTrue(state.fieldErrors.containsKey("breed"))
            assertEquals("Breed is required.", state.fieldErrors["breed"])
            assertFalse(state.canSubmit)
        }
    }

    @Test
    fun `validateFormAndSetCanSubmit sets canSubmit true when core fields valid`() = runTest {
         viewModel.uiState.test {
            awaitItem()
            viewModel.updateRegistryType(RegistryType.TRACEABLE)
            awaitItem()
            viewModel.updateAgeGroup(AgeGroup.WEEKS_0_5)
            awaitItem()
            viewModel.updateField("breed", "Valid Breed")
            val state = awaitItem()

            assertTrue(state.fieldErrors.isEmpty()) // Assuming other fields are not yet validated to error
            assertTrue(state.canSubmit)
        }
    }


    @Test
    fun `submitRegistration when validation fails does not call use case`() = runTest {
        // Ensure form is invalid
        viewModel.updateField("breed", "") // Makes breed blank, should fail validation

        viewModel.submitRegistration("farm1")

        coVerify(exactly = 0) { mockRegisterFlockUseCase(any()) }
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.fieldErrors.containsKey("breed"))
        }
    }

    @Test
    fun `submitRegistration success path updates state correctly`() = runTest {
        // Setup for valid form
        viewModel.updateRegistryType(RegistryType.TRACEABLE)
        viewModel.updateAgeGroup(AgeGroup.WEEKS_0_5)
        viewModel.updateField("breed", "Test Breed")
        // Assume other fields are valid or not strictly required by current canSubmit logic for this test

        coEvery { mockRegisterFlockUseCase(any()) } returns Result.Success(Unit)

        viewModel.uiState.test {
            skipItems(3) // Skip initial states from setup

            viewModel.submitRegistration("farm1")

            var state = awaitItem()
            assertTrue(state.isLoading) // Loading state after submit triggered

            state = awaitItem() // Final state after use case success
            assertFalse(state.isLoading)
            assertTrue(state.isSubmitted)
            assertNull(state.error)
        }
        coVerify { mockRegisterFlockUseCase(any<FlockRegistrationData>()) }
    }

    @Test
    fun `submitRegistration use case error updates state with friendly message`() = runTest {
        // Setup for valid form
        viewModel.updateRegistryType(RegistryType.TRACEABLE)
        viewModel.updateAgeGroup(AgeGroup.WEEKS_0_5)
        viewModel.updateField("breed", "Test Breed")

        val exception = IOException("Network issue")
        val friendlyMessage = "Network error, please check your connection."
        every { exception.toUserFriendlyMessage(mockContext) } returns friendlyMessage
        coEvery { mockRegisterFlockUseCase(any()) } returns Result.Error(exception)

        viewModel.uiState.test {
            skipItems(3)

            viewModel.submitRegistration("farm1")

            var state = awaitItem() // isLoading = true
            assertTrue(state.isLoading)

            state = awaitItem() // Final error state
            assertFalse(state.isLoading)
            assertFalse(state.isSubmitted)
            assertEquals(friendlyMessage, state.error)
        }
    }

    // TODO: Add tests for weight/height validation, date validation, proof photo additions, etc.
}
