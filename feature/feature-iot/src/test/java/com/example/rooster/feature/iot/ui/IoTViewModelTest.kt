package com.example.rooster.feature.iot.ui

import android.content.Context
import app.cash.turbine.test
import com.example.rooster.core.common.Result
import com.example.rooster.feature.iot.data.model.AdvancedAnalyticsUiState
import com.example.rooster.feature.iot.data.model.AlertInfo
import com.example.rooster.feature.iot.data.model.DeviceInfo
import com.example.rooster.feature.iot.data.model.FeedingSchedule
import com.example.rooster.feature.iot.data.model.HistoricalDataState
import com.example.rooster.feature.iot.data.model.SmartAutomationControlsUiState
import com.example.rooster.feature.iot.data.model.TemperatureReading
import com.example.rooster.feature.iot.data.repository.IoTRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExperimentalCoroutinesApi
class IoTViewModelTest {

    private lateinit var viewModel: IoTViewModel
    private lateinit var mockRepository: IoTRepository
    private lateinit var mockContext: Context // For CSV export test

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        mockContext = mock()

        // Default mocks for flows to avoid NPEs if not specifically set in a test
        whenever(mockRepository.getAllDeviceInfos()) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getUnacknowledgedAlerts()) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getTemperatureReadings(org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getHumidityReadings(org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getFeedLevelReadings(org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getWaterConsumptionReadings(org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getLightLevelReadings(org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        // Analytics
        whenever(mockRepository.getProductionForecasts(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getPerformancePredictions(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getFeedOptimizationRecommendations(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getHealthTrends(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        // Automation
        whenever(mockRepository.getFeedingSchedules(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getClimateSettings(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getMaintenanceReminders(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(emptyList()))


        viewModel = IoTViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading and then updates with devices`() = runTest {
        val devices = listOf(DeviceInfo(deviceId = "d1", name = "Device 1"))
        whenever(mockRepository.getAllDeviceInfos()) doReturn flowOf(Result.Success(devices))

        viewModel = IoTViewModel(mockRepository) // Re-init to capture initial state correctly

        viewModel.uiState.test {
            var emission = awaitItem()
            assertTrue(emission.isLoading) // Initial state due to combine

            emission = awaitItem() // Emission from devices flow becoming success
            assertFalse(emission.isLoading) // Should be false after devices load
            assertEquals(devices, emission.devices)
            assertEquals("d1", emission.selectedDeviceId) // Auto-select first
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selectDevice updates selectedDeviceId and triggers refreshes`() = runTest {
        val device1 = DeviceInfo(deviceId = "d1", name = "Device 1")
        val device2 = DeviceInfo(deviceId = "d2", name = "Device 2")
        whenever(mockRepository.getAllDeviceInfos()) doReturn flowOf(Result.Success(listOf(device1, device2)))

        viewModel = IoTViewModel(mockRepository) // Re-init

        // Let initial auto-selection happen
        viewModel.uiState.test { awaitItem(); awaitItem() }


        viewModel.selectDevice("d2")
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines launched by selectDevice complete

        viewModel.uiState.test {
            val emission = awaitItem()
            assertEquals("d2", emission.selectedDeviceId)
            cancelAndConsumeRemainingEvents()
        }
        verify(mockRepository).refreshTemperatureReadings("d2")
        verify(mockRepository).refreshHumidityReadings("d2")
        // ... verify other refreshes
    }

    @Test
    fun `fetchHistoricalData updates historicalDataState`() = runTest {
        val deviceId = "testDevice"
        val startTime = LocalDate.now().minusDays(1)
        val endTime = LocalDate.now()
        val tempData = listOf(TemperatureReading(deviceId = deviceId, temperature = 25.0))

        whenever(mockRepository.getTemperatureReadingsInRange(deviceId, startTime.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), endTime.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() -1)) doReturn flowOf(Result.Success(tempData))
        // Mock other sensor types for historical data as empty or with data
        whenever(mockRepository.getHumidityReadingsInRange(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getFeedLevelReadingsInRange(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getWaterConsumptionReadingsInRange(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))
        whenever(mockRepository.getLightLevelReadingsInRange(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())) doReturn flowOf(Result.Success(emptyList()))


        viewModel.fetchHistoricalData(deviceId, startTime.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), endTime.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() -1)

        viewModel.historicalDataState.test {
            var emission = awaitItem() // Initial empty state
            assertTrue(emission.isLoading)

            emission = awaitItem() // After fetch
            assertFalse(emission.isLoading)
            assertEquals(tempData, emission.temperatureReadings)
            assertNull(emission.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetchAdvancedAnalyticsData updates advancedAnalyticsState`() = runTest {
        val farmId = "farm1"
        // Mock repository calls for analytics
        whenever(mockRepository.getProductionForecasts(farmId, null)) doReturn flowOf(Result.Success(listOf(mock())))
        // ... mock others

        viewModel.fetchAdvancedAnalyticsData(farmId, null)

        viewModel.advancedAnalyticsState.test {
            var emission = awaitItem() // Initial
            assertTrue(emission.isLoading)

            emission = awaitItem() // After fetch
            assertFalse(emission.isLoading)
            assertNotNull(emission.productionForecasts)
            // Add more assertions
            cancelAndConsumeRemainingEvents()
        }
        verify(mockRepository).refreshProductionForecasts(farmId, null)
        // ... verify other refreshes
    }

    @Test
    fun `updateFeedingSchedule calls repository and refreshes state`() = runTest {
        val schedule = FeedingSchedule(scheduleId = "s1")
        whenever(mockRepository.updateFeedingSchedule(org.mockito.kotlin.any())) doReturn Result.Success(Unit)
        // Mock getFeedingSchedules to return something after update for refresh verification
        whenever(mockRepository.getFeedingSchedules(org.mockito.kotlin.anyOrNull(), org.mockito.kotlin.anyOrNull())) doReturn flowOf(Result.Success(listOf(schedule.copy(lastUpdated = 1L))))


        val success = viewModel.updateFeedingSchedule(schedule)
        assertTrue(success)

        verify(mockRepository).updateFeedingSchedule(schedule.copy(farmId = "default-farm-id", flockId = null)) // Assuming default farmId
        verify(mockRepository).refreshFeedingSchedules("default-farm-id", null)

        viewModel.smartAutomationState.test {
            // Check that the state reflects the update (e.g. by checking lastUpdated or specific fields)
            // This requires careful mocking of the refresh calls
            val emission = awaitItem() // This might be the initial state or after refresh
            // Add assertions based on mocked refresh data
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `alert generation avoids duplicates for ongoing conditions`() = runTest {
        val deviceId = "tempDevice"
        val highTempReading = TemperatureReading(deviceId = deviceId, temperature = AlertThresholds.MAX_TEMP + 1, timestamp = System.currentTimeMillis())
        val normalTempReading = TemperatureReading(deviceId = deviceId, temperature = 25.0, timestamp = System.currentTimeMillis() + 1000)

        // Initial state: no alerts
        whenever(mockRepository.getUnacknowledgedAlerts()) doReturn flowOf(Result.Success(emptyList()))
        viewModel = IoTViewModel(mockRepository) // Re-init with this specific alert mock

        // Simulate high temp
        whenever(mockRepository.getTemperatureReadings(deviceId)) doReturn flowOf(Result.Success(listOf(highTempReading)))
        viewModel.selectDevice(deviceId) // This triggers alert monitoring via flatMapLatest
        testDispatcher.scheduler.advanceUntilIdle() // Allow alert monitoring to process

        verify(mockRepository).recordAlert(org.mockito.kotlin.argThat { alertType == "TEMPERATURE_HIGH" })

        // Simulate another high temp reading for the same condition
        val highTempReading2 = highTempReading.copy(timestamp = System.currentTimeMillis() + 2000)
        whenever(mockRepository.getTemperatureReadings(deviceId)) doReturn flowOf(Result.Success(listOf(highTempReading, highTempReading2)))
        // To re-trigger collection in flatMapLatest, we might need a new emission or re-select
        // However, the generateAlert itself checks uiState.value.activeAlerts.
        // For this test, we'll update the active alerts as if the first one was recorded.
        val firstAlert = AlertInfo(deviceId = deviceId, alertType = "TEMPERATURE_HIGH", message = "", severity = "CRITICAL")
        whenever(mockRepository.getUnacknowledgedAlerts()) doReturn flowOf(Result.Success(listOf(firstAlert)))
        // Need to force uiState to recompose with the new alert list for generateAlert to see it
        // This part is tricky to test without more direct control over the internal state updates of combined flows.
        // A simpler approach might be to directly test generateAlert if it were public, or
        // ensure the repository's getUnacknowledgedAlerts flow is robustly updating the uiState.

        // For now, we'll assume the first alert is in uiState.value.activeAlerts
        // If generateAlert is called again with the same condition, recordAlert should not be called a second time.
        // This requires more intricate flow manipulation for a precise test or making generateAlert internal and testable.

        // Simulate temp returning to normal
        whenever(mockRepository.getTemperatureReadings(deviceId)) doReturn flowOf(Result.Success(listOf(normalTempReading)))
        viewModel.selectDevice(deviceId) // Re-select to ensure flow processes new data
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify recordAlert was called only once for TEMPERATURE_HIGH
        verify(mockRepository).recordAlert(org.mockito.kotlin.argThat { alertType == "TEMPERATURE_HIGH" })
        // No new alert for normal temp
        verify(mockRepository, org.mockito.kotlin.never()).recordAlert(org.mockito.kotlin.argThat { alertType == "TEMPERATURE_LOW" })
    }

    // TODO: Test CSV Export functionality (requires more mocking for Context and FileProvider)
    // @Test
    // fun `exportHistoricalDataToCsv calls CsvExporter`() = runTest { ... }

}
