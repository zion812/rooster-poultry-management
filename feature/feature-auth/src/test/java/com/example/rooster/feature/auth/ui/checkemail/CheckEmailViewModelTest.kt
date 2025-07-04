package com.example.rooster.feature.auth.ui.checkemail

import androidx.lifecycle.SavedStateHandle
import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CheckEmailViewModelTest {

    private lateinit var viewModel: CheckEmailViewModel
    private lateinit var mockAuthRepository: MockAuthRepository
    private lateinit var savedStateHandle: SavedStateHandle

    // Using StandardTestDispatcher for modern coroutines testing
    private val testDispatcher = StandardTestDispatcher()

    // Mock AuthRepository implementation for testing
    class MockAuthRepository : AuthRepository {
        var sendVerificationEmailShouldSucceed: Boolean = true
        var reloadUserShouldSucceed: Boolean = true
        var isEmailVerifiedAfterReload: Boolean = false
        var sendVerificationEmailCallCount: Int = 0
        var reloadCurrentUserCallCount: Int = 0

        private val mockUserFlow = MutableStateFlow<User?>(
            User("testId", "test@example.com", "Test User", UserRole.FARMER, null, false)
        )

        override suspend fun sendCurrentUserEmailVerification(): Result<Unit> {
            sendVerificationEmailCallCount++
            return if (sendVerificationEmailShouldSucceed) Result.success(Unit)
            else Result.failure(Exception("Failed to send email"))
        }

        override suspend fun reloadCurrentUser(): Result<User?> {
            reloadCurrentUserCallCount++
            return if (reloadUserShouldSucceed) {
                val currentUser = mockUserFlow.value
                Result.success(currentUser?.copy(isEmailVerified = isEmailVerifiedAfterReload))
            } else Result.failure(Exception("Failed to reload user"))
        }

        // Unused methods for this ViewModel's tests
        override suspend fun signIn(email: String, password: String): Result<User> = TODO()
        override suspend fun signUp(email: String, password: String, name: String, role: UserRole, phoneNumber: String?): Result<User> = TODO()
        override suspend fun signOut() {}
        override suspend fun resetPassword(email: String): Result<Unit> = TODO()
        override fun getCurrentUser(): Flow<User?> = mockUserFlow
        override suspend fun updateProfile(user: User): Result<User> = TODO()
        override suspend fun isUserSignedIn(): Boolean = TODO()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for ViewModelScope
        mockAuthRepository = MockAuthRepository()
    }

    private fun initViewModel(email: String?) {
        savedStateHandle = SavedStateHandle().apply {
            email?.let { set("email", it) }
        }
        viewModel = CheckEmailViewModel(mockAuthRepository, savedStateHandle)
    }

    @Test
    fun `init with valid email triggers sendVerificationEmail and sets email in state`() = runTest {
        val testEmail = "test@example.com"
        initViewModel(testEmail)

        advanceUntilIdle() // Allow coroutines launched in init to complete

        assertEquals(testEmail, viewModel.uiState.value.email)
        assertEquals(1, mockAuthRepository.sendVerificationEmailCallCount)
        assertNotNull(viewModel.uiState.value.verificationEmailSentMessage)
        assertEquals(R.string.verification_email_sent, viewModel.uiState.value.verificationEmailSentMessage)
        assertTrue(viewModel.uiState.value.isLoading.not()) // Should not be loading after initial send
    }

    @Test
    fun `init with null email sets error state`() = runTest {
        initViewModel(null)
        advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.email)
        assertEquals(0, mockAuthRepository.sendVerificationEmailCallCount)
        assertEquals(R.string.error_email_missing_for_verification, viewModel.uiState.value.errorResId)
    }

    @Test
    fun `sendVerificationEmail success updates state and starts verification check`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle() // Initial send

        mockAuthRepository.sendVerificationEmailCallCount = 0 // Reset for this specific test call
        viewModel.sendVerificationEmail(isInitialSend = false) // Trigger explicitly
        advanceUntilIdle()

        assertEquals(1, mockAuthRepository.sendVerificationEmailCallCount)
        assertEquals(R.string.verification_email_resent, viewModel.uiState.value.verificationEmailSentMessage)
        assertTrue(viewModel.uiState.value.isLoading.not())
        assertTrue(mockAuthRepository.reloadCurrentUserCallCount > 0) // Verification check should have started
    }

    @Test
    fun `sendVerificationEmail failure updates error state`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle()

        mockAuthRepository.sendVerificationEmailShouldSucceed = false
        mockAuthRepository.sendVerificationEmailCallCount = 0
        viewModel.sendVerificationEmail(isInitialSend = false)
        advanceUntilIdle()

        assertEquals(1, mockAuthRepository.sendVerificationEmailCallCount)
        assertEquals(R.string.error_sending_verification_email, viewModel.uiState.value.errorResId)
        assertTrue(viewModel.uiState.value.isLoading.not())
    }

    @Test
    fun `resendVerificationEmail starts countdown and calls sendVerificationEmail`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle() // Initial send

        mockAuthRepository.sendVerificationEmailCallCount = 0 // Reset for resend
        viewModel.resendVerificationEmail()
        advanceUntilIdle()

        assertEquals(1, mockAuthRepository.sendVerificationEmailCallCount)
        assertEquals(R.string.verification_email_resent, viewModel.uiState.value.verificationEmailSentMessage)
        assertTrue(viewModel.uiState.value.countdownSeconds > 0)

        // Advance time to ensure countdown works
        advanceTimeBy(61_000)
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.countdownSeconds)
    }

    @Test
    fun `resendVerificationEmail does nothing if countdown is active`() = runTest {
         initViewModel("test@example.com")
        advanceUntilIdle() // Initial send

        viewModel.resendVerificationEmail() // First resend, starts countdown
        advanceUntilIdle()

        mockAuthRepository.sendVerificationEmailCallCount = 0 // Reset count
        viewModel.resendVerificationEmail() // Attempt second resend while countdown active
        advanceUntilIdle()

        assertEquals(0, mockAuthRepository.sendVerificationEmailCallCount) // Should not have called again
        assertTrue(viewModel.uiState.value.countdownSeconds > 0)
    }

    @Test
    fun `emailVerificationCheck updates isEmailVerified on success`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle() // Initial send and start of check

        mockAuthRepository.isEmailVerifiedAfterReload = true
        // Let the periodic check run (default is 5s interval)
        advanceTimeBy(5_100) // Advance past one interval
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmailVerified)
        assertTrue(mockAuthRepository.reloadCurrentUserCallCount >= 1)
    }

    @Test
    fun `emailVerificationCheck continues if user not yet verified`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle()

        mockAuthRepository.isEmailVerifiedAfterReload = false
        val initialReloadCount = mockAuthRepository.reloadCurrentUserCallCount

        advanceTimeBy(5_100) // First check
        advanceUntilIdle()
        assertTrue(mockAuthRepository.reloadCurrentUserCallCount > initialReloadCount)
        assertTrue(viewModel.uiState.value.isEmailVerified.not())

        val secondReloadCount = mockAuthRepository.reloadCurrentUserCallCount
        advanceTimeBy(5_100) // Second check
        advanceUntilIdle()
        assertTrue(mockAuthRepository.reloadCurrentUserCallCount > secondReloadCount)
        assertTrue(viewModel.uiState.value.isEmailVerified.not())
    }

    @Test
    fun `emailVerificationCheck stops after verification`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle()

        mockAuthRepository.isEmailVerifiedAfterReload = true
        advanceTimeBy(5_100) // Trigger verification
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmailVerified)
        val callCountAfterVerification = mockAuthRepository.reloadCurrentUserCallCount

        advanceTimeBy(10_000) // Advance more time
        advanceUntilIdle()

        assertEquals(callCountAfterVerification, mockAuthRepository.reloadCurrentUserCallCount) // Count should not increase
    }

    @Test
    fun `onNavigationComplete resets relevant state`() = runTest {
        initViewModel("test@example.com")
        advanceUntilIdle()
        mockAuthRepository.isEmailVerifiedAfterReload = true
        advanceTimeBy(5_100); advanceUntilIdle() // Get to verified state

        assertTrue(viewModel.uiState.value.isEmailVerified)
        assertNotNull(viewModel.uiState.value.verificationEmailSentMessage)

        viewModel.onNavigationComplete()

        assertTrue(viewModel.uiState.value.isEmailVerified.not())
        assertNull(viewModel.uiState.value.verificationEmailSentMessage)
    }

    @Test
    fun `clearError resets error state`() = runTest {
        initViewModel(null) // This will set an error
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorResId)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorResId)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }
}
```
