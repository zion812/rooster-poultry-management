package com.rooster.farmerhome

import app.cash.turbine.test
import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import com.rooster.farmerhome.domain.model.ProductionSummary
import com.rooster.farmerhome.domain.model.WeatherData
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import com.rooster.farmerhome.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// A TestRule for setting and resetting the Main dispatcher
@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: StandardTestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    // Expose the dispatcher for advancing time in tests
    fun advanceUntilIdle() = dispatcher.scheduler.advanceUntilIdle()
    fun advanceTimeBy(delayTimeMillis: Long) = dispatcher.scheduler.advanceTimeBy(delayTimeMillis)
}


@ExperimentalCoroutinesApi
class FarmerHomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var farmHealthAlertRepository: FarmHealthAlertRepository
    private lateinit var productionMetricsRepository: ProductionMetricsRepository
    private lateinit var farmDataRepository: FarmDataRepository
    private lateinit var viewModel: FarmerHomeViewModel

    // Mock data
    private val mockWeatherData = WeatherData("25C", "60%", "0mm", "10kmh", "Sunny", "TestLocation")
    private val mockFarmInfo = FarmBasicInfo("f1", "My Farm", "Farm Location", "Me", 2, 100, "Today")
    private val mockProdSummary = ProductionSummary(2,100,50, 0.5, emptyList())
    private val mockAlerts = listOf(
        FarmHealthAlert("a1", "fl1", "f1", "Alert", "Desc", com.rooster.farmerhome.domain.model.AlertSeverity.LOW, System.currentTimeMillis())
    )


    @Before
    fun setUp() {
        weatherRepository = mockk()
        farmHealthAlertRepository = mockk()
        productionMetricsRepository = mockk()
        farmDataRepository = mockk()

        // Default happy path mocks for init block calls
        every { weatherRepository.getCurrentWeatherForFarm(any()) } returns flowOf(DataState.Success(mockWeatherData))
        every { farmDataRepository.getFarmBasicInfo(any()) } returns flowOf(DataState.Success(mockFarmInfo))
        every { productionMetricsRepository.getProductionSummary(any()) } returns flowOf(DataState.Success(mockProdSummary))
        every { farmHealthAlertRepository.getHealthAlertsForFarm(any()) } returns flowOf(DataState.Success(mockAlerts))


        viewModel = FarmerHomeViewModel(
            weatherRepository,
            farmHealthAlertRepository,
            productionMetricsRepository,
            farmDataRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads all data successfully`() = runTest {
        // ViewModel init calls fetch for all data.
        // Advance dispatcher to allow coroutines in init to complete.
        mainCoroutineRule.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem() // Get the latest state after init
            assertTrue(state.weatherState is DataState.Success)
            assertEquals(mockWeatherData, (state.weatherState as DataState.Success).data)

            assertTrue(state.farmInfoState is DataState.Success)
            assertEquals(mockFarmInfo, (state.farmInfoState as DataState.Success).data)

            assertTrue(state.productionSummaryState is DataState.Success)
            assertEquals(mockProdSummary, (state.productionSummaryState as DataState.Success).data)

            assertTrue(state.healthAlertsState is DataState.Success)
            assertEquals(mockAlerts, (state.healthAlertsState as DataState.Success).data)

            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun `fetchWeatherForFarm success updates weatherState`() = runTest {
        val farmLocation = "Test Farm"
        val successDataState = DataState.Success(mockWeatherData)
        every { weatherRepository.getCurrentWeatherForFarm(farmLocation) } returns flowOf(successDataState)

        viewModel.fetchWeatherForFarm(farmLocation)
        mainCoroutineRule.advanceUntilIdle()


        viewModel.uiState.test {
            assertEquals(successDataState, awaitItem().weatherState)
            cancelAndConsumeRemainingEvents()
        }
        coVerify { weatherRepository.getCurrentWeatherForFarm(farmLocation) }
    }

    @Test
    fun `fetchWeatherForFarm error updates weatherState`() = runTest {
        val farmLocation = "Test Farm"
        val exception = RuntimeException("Network Error")
        val errorDataState = DataState.Error<WeatherData?>(exception, null, null)
        every { weatherRepository.getCurrentWeatherForFarm(farmLocation) } returns flowOf(errorDataState)

        viewModel.fetchWeatherForFarm(farmLocation)
        mainCoroutineRule.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem().weatherState
            assertTrue(state is DataState.Error)
            assertEquals(exception, (state as DataState.Error).exception)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `markAlertAsRead success clears transient message and refreshes list`() = runTest {
        val farmId = "f1"
        val alertId = "a1"
        coEvery { farmHealthAlertRepository.markAlertAsRead(farmId, alertId) } returns Result.success(Unit)
        // Mock the refresh call that happens after successful markAsRead
        every { farmHealthAlertRepository.getHealthAlertsForFarm(farmId) } returns flowOf(DataState.Success(emptyList())) // Simulate list refresh

        viewModel.markAlertAsRead(farmId, alertId)
        mainCoroutineRule.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.transientUserMessage) // Message should be cleared if it was success or not set for success
            // Verify that getHealthAlertsForFarm was called again (implicitly by the flow)
            // This specific verification is tricky with Turbine if the refresh is immediate.
            // We are checking that the list is empty as per the second mock.
            assertTrue((state.healthAlertsState as DataState.Success).data.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
        coVerify(exactly = 1) { farmHealthAlertRepository.markAlertAsRead(farmId, alertId) }
    }

    @Test
    fun `markAlertAsRead failure sets transientUserMessage`() = runTest {
        val farmId = "f1"
        val alertId = "a1"
        val exceptionMessage = "Failed to sync"
        coEvery { farmHealthAlertRepository.markAlertAsRead(farmId, alertId) } returns Result.failure(Exception(exceptionMessage))

        viewModel.markAlertAsRead(farmId, alertId)
        mainCoroutineRule.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.transientUserMessage?.contains(exceptionMessage) == true)
            // healthAlertsState should also reflect the error
            assertTrue(state.healthAlertsState is DataState.Error)
            assertEquals(exceptionMessage, (state.healthAlertsState as DataState.Error).message)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearTransientMessage nullifies message and id`() = runTest {
        // First, set a message
        coEvery { farmHealthAlertRepository.markAlertAsRead(any(), any()) } returns Result.failure(Exception("Test error"))
        viewModel.markAlertAsRead("f1", "a1")
        mainCoroutineRule.advanceUntilIdle()

        var stateWithMessage = viewModel.uiState.value
        assertTrue(stateWithMessage.transientUserMessage != null)
        assertTrue(stateWithMessage.messageId != null)

        // Now clear it
        viewModel.clearTransientMessage()
        mainCoroutineRule.advanceUntilIdle()

        val stateAfterClear = viewModel.uiState.value
        assertNull(stateAfterClear.transientUserMessage)
        assertNull(stateAfterClear.messageId)
    }
}
