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
class OrderViewModelTest {
    private lateinit var viewModel: OrderViewModel
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        viewModel = OrderViewModel()
    }

    @Test
    fun `initial orders list is empty`() =
        testScope.runTest {
            viewModel.ordersList.test {
                val orders = awaitItem()
                assertEquals(emptyList<Any>(), orders)
            }
        }
    // Add more tests for loadOrders, submitFeedback, etc.
}
