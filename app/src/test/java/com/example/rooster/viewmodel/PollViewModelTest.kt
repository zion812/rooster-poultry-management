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
class PollViewModelTest {
    private lateinit var viewModel: PollViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        viewModel = PollViewModel()
    }

    @Test
    fun `initial results are empty`() =
        testScope.runTest {
            viewModel.results.test {
                val results = awaitItem()
                assertEquals(emptyMap<String, Int>(), results)
            }
        }
    // Add more tests for loadResults, submitVote, etc.
}
