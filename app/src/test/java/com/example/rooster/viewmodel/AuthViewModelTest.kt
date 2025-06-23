package com.example.rooster.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.rooster.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        viewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `initial state is not authenticated`() = testScope.runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isAuthenticated)
        }
    }

    // Add more tests for sendOtp, verifyOtp, etc. as needed
}
