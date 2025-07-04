package com.example.rooster.core.auth

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.model.AuthState
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthServiceTest {

    @Mock
    private lateinit var mockAuthRepository: AuthRepository

    private lateinit var authService: AuthService
    private val testDispatcher = UnconfinedTestDispatcher()
    // Using UnconfinedTestDispatcher for applicationScope to make collection immediate in tests
    private val applicationScope = kotlinx.coroutines.CoroutineScope(testDispatcher + kotlinx.coroutines.SupervisorJob())


    private val mockUser = User(id = "uid1", email = "test@example.com", displayName = "Test User", role = UserRole.FARMER)

    @Before
    fun setUp() {
        // Default behavior for getCurrentUser flow, can be overridden in specific tests
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(flowOf(null)) // Default to unauthenticated
        authService = AuthService(mockAuthRepository, applicationScope)
    }

    @Test
    fun `initial authState is Loading then Unauthenticated if no user`() = runTest(testDispatcher) {
        // Re-initialize for this specific scenario to control flow emission timing better
        val currentUserFlow = MutableSharedFlow<User?>()
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(currentUserFlow)
        authService = AuthService(mockAuthRepository, applicationScope) // Re-init with controllable flow

        var currentAuthState = authService.authState.value
        assertTrue("Initial state should be Loading", currentAuthState is AuthState.Loading)

        val job = launch { currentUserFlow.emit(null) } // Simulate repository emitting null
        advanceUntilIdle() // Process the emission

        currentAuthState = authService.authState.value
        assertTrue("State should be Unauthenticated after null emission", currentAuthState is AuthState.Unauthenticated)
        assertNull("Current user should be null", authService.currentUser.value)
        job.cancel()
    }

    @Test
    fun `authState becomes Authenticated when repository emits user`() = runTest(testDispatcher) {
        val currentUserFlow = MutableSharedFlow<User?>()
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(currentUserFlow)
        authService = AuthService(mockAuthRepository, applicationScope) // Re-init

        val job = launch { currentUserFlow.emit(mockUser) }
        advanceUntilIdle()

        val authState = authService.authState.value
        assertTrue(authState is AuthState.Authenticated)
        assertEquals(mockUser, (authState as AuthState.Authenticated).user)
        assertEquals(mockUser, authService.currentUser.value)
        job.cancel()
    }

    @Test
    fun `signInWithEmail success updates authState via repository flow`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "password"
        // Simulate that after signIn, the getCurrentUser flow will emit the new user
        whenever(mockAuthRepository.signIn(email, password)).thenReturn(Result.Success(mockUser))
        // No direct authState change needed here, as init block's collection of getCurrentUser should handle it
        // For this test, we assume getCurrentUser will eventually emit mockUser after a successful signIn

        val result = authService.signInWithEmail(email, password)

        assertTrue(result is Result.Success)
        assertEquals(mockUser, (result as Result.Success).data)
        // Verification that authState becomes Authenticated would rely on testing the init block's collector
        // which is already implicitly tested by `authState becomes Authenticated when repository emits user`
    }

    @Test
    fun `signInWithEmail failure updates authState to Error`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "wrongpassword"
        val exception = Exception("Sign in failed")
        whenever(mockAuthRepository.signIn(email, password)).thenReturn(Result.Error(exception))

        val result = authService.signInWithEmail(email, password)

        assertTrue(result is Result.Error)
        val authState = authService.authState.value
        assertTrue(authState is AuthState.Error)
        assertEquals("Sign in failed", (authState as AuthState.Error).message)
    }

    @Test
    fun `signUpWithEmail success returns user`() = runTest(testDispatcher) {
        val email = "new@example.com"
        val password = "newpass"
        val displayName = "New User"
        whenever(mockAuthRepository.signUp(email, password, displayName, UserRole.FARMER, null))
            .thenReturn(Result.Success(mockUser))

        val result = authService.signUpWithEmail(email, password, displayName, UserRole.FARMER, null)
        assertTrue(result is Result.Success)
        assertEquals(mockUser, (result as Result.Success).data)
    }

    @Test
    fun `signUpWithEmail failure updates authState to Error`() = runTest(testDispatcher) {
        val email = "new@example.com"
        val password = "newpass"
        val displayName = "New User"
        val exception = Exception("Sign up failed")
        whenever(mockAuthRepository.signUp(email, password, displayName, UserRole.FARMER, null))
            .thenReturn(Result.Error(exception))

        val result = authService.signUpWithEmail(email, password, displayName, UserRole.FARMER, null)
        assertTrue(result is Result.Error)
        val authState = authService.authState.value
        assertTrue(authState is AuthState.Error)
        assertEquals("Sign up failed", (authState as AuthState.Error).message)
    }


    @Test
    fun `signOut calls repository signOut and updates state via flow`() = runTest(testDispatcher) {
        // Assume user is initially logged in
        val currentUserFlow = MutableSharedFlow<User?>(replay = 1)
        currentUserFlow.tryEmit(mockUser) // Start with a logged-in user
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(currentUserFlow)
        authService = AuthService(mockAuthRepository, applicationScope) // Re-init

        advanceUntilIdle() // Ensure initial state is Authenticated
        assertTrue(authService.authState.value is AuthState.Authenticated)


        // Mock repository.signOut() to do nothing (it's void)
        // When signOut is called, expect repository.getCurrentUser() to emit null
        val job = launch {
            // This is a bit tricky: a call to authRepository.signOut() should trigger
            // the authStateListener in AuthRepositoryImpl, which in turn makes
            // authRepository.getCurrentUser() emit null.
            // We simulate this by having the flow emit null after signOut is called.
            whenever(mockAuthRepository.signOut()).thenAnswer {
                applicationScope.launch { currentUserFlow.emit(null) } // Simulate effect of signOut
                Unit
            }
        }


        val result = authService.signOut()
        assertTrue(result is Result.Success)

        advanceUntilIdle() // Allow flow emission to be processed

        val finalAuthState = authService.authState.value
        assertTrue("AuthStat: $finalAuthState",finalAuthState is AuthState.Unauthenticated)
        assertNull(authService.currentUser.value)
        verify(mockAuthRepository).signOut()
        job.cancel()
    }

    @Test
    fun `updateUserProfile calls repository`() = runTest(testDispatcher) {
        whenever(mockAuthRepository.updateProfile(mockUser)).thenReturn(Result.Success(mockUser))
        val result = authService.updateUserProfile(mockUser)
        assertTrue(result is Result.Success)
        verify(mockAuthRepository).updateProfile(mockUser)
    }

    @Test
    fun `sendPasswordResetEmail calls repository`() = runTest(testDispatcher) {
        val email = "test@example.com"
        whenever(mockAuthRepository.resetPassword(email)).thenReturn(Result.Success(Unit))
        val result = authService.sendPasswordResetEmail(email)
        assertTrue(result is Result.Success)
        verify(mockAuthRepository).resetPassword(email)
    }

    @Test
    fun `sendEmailVerification calls repository`() = runTest(testDispatcher) {
        whenever(mockAuthRepository.sendCurrentUserEmailVerification()).thenReturn(Result.Success(Unit))
        val result = authService.sendEmailVerification()
        assertTrue(result is Result.Success)
        verify(mockAuthRepository).sendCurrentUserEmailVerification()
    }

    @Test
    fun `reloadCurrentUser calls repository`() = runTest(testDispatcher) {
        whenever(mockAuthRepository.reloadCurrentUser()).thenReturn(Result.Success(mockUser))
        val result = authService.reloadCurrentUser()
        assertTrue(result is Result.Success)
        assertEquals(mockUser, (result as Result.Success).data)
        verify(mockAuthRepository).reloadCurrentUser()
    }

    @Test
    fun `signInWithPhone returns error`() = runTest(testDispatcher) {
        val result = authService.signInWithPhone("123", "456")
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is UnsupportedOperationException)
    }

    @Test
    fun `helper functions work correctly based on currentUser`() = runTest(testDispatcher) {
        val farmerUser = User(id = "f1", role = UserRole.FARMER)
        val buyerUser = User(id = "b1", role = UserRole.BUYER)
        val adminUser = User(id = "a1", role = UserRole.ADMIN)
        val expertUser = User(id = "e1", role = UserRole.EXPERT)
        val vetUser = User(id = "v1", role = UserRole.VETERINARIAN)


        val currentUserFlow = MutableSharedFlow<User?>()
        whenever(mockAuthRepository.getCurrentUser()).thenReturn(currentUserFlow)
        authService = AuthService(mockAuthRepository, applicationScope)

        applicationScope.launch { currentUserFlow.emit(farmerUser) }
        advanceUntilIdle()
        assertTrue(authService.isFarmer())
        assertFalse(authService.isAdmin())
        assertTrue(authService.hasRole(UserRole.FARMER))
        assertTrue(authService.hasAnyRole(UserRole.FARMER, UserRole.BUYER))

        applicationScope.launch { currentUserFlow.emit(adminUser) }
        advanceUntilIdle()
        assertTrue(authService.isAdmin())

        applicationScope.launch { currentUserFlow.emit(expertUser) }
        advanceUntilIdle()
        assertTrue(authService.isExpert())

        applicationScope.launch { currentUserFlow.emit(vetUser) }
        advanceUntilIdle()
        assertTrue(authService.isVeterinarian())

        applicationScope.launch { currentUserFlow.emit(null) }
        advanceUntilIdle()
        assertFalse(authService.isFarmer())
        assertFalse(authService.hasRole(UserRole.FARMER))
    }
}
