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
class CommunityViewModelTest {
    private lateinit var viewModel: CommunityViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        viewModel = CommunityViewModel()
    }

    @Test
    fun `initial groups are empty`() =
        testScope.runTest {
            viewModel.groups.test {
                val groups = awaitItem()
                assertEquals(emptyList<Any>(), groups)
            }
        }
    // Add more tests for loading, error, pagination, etc.
}
