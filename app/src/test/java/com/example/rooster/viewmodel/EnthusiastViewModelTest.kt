package com.example.rooster.viewmodel

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EnthusiastViewModelTest {
    private lateinit var viewModel: EnthusiastViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        viewModel = EnthusiastViewModel()
    }

    @Test
    fun `initial flock is empty`() =
        testScope.runTest {
            viewModel.flock.test {
                val flock = awaitItem()
                assertEquals(emptyList<Any>(), flock)
            }
        }
    // Add more tests for growth, suggestions, transfers, etc.
}
