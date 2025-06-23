package com.example.rooster.viewmodel

import app.cash.turbine.test
import com.example.rooster.viewmodel.FarmerDashboardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FarmerDashboardViewModelTest {
    private lateinit var viewModel: FarmerDashboardViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        viewModel = FarmerDashboardViewModel()
    }

    @Test
    fun `initial active auctions is empty`() = testScope.runTest {
        viewModel.activeAuctions.test {
            val auctions = awaitItem()
            assertEquals(emptyList<String>(), auctions)
        }
    }

    // Add more tests for loading events, bids, kycRequests, etc.
}
