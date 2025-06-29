package com.example.rooster.feature.marketplace.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.data.local.model.OrderEntity
import com.example.rooster.feature.marketplace.data.local.model.ProductListingEntity
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class MarketplaceSyncWorkerTest {

    private lateinit var context: Context
    private lateinit var mockProductListingRepository: ProductListingRepository
    private lateinit var mockOrderRepository: OrderRepository

    private val testListingId1 = "listing1"
    private val testListingEntity1 = mockk<ProductListingEntity>(relaxed = true)
    private val testListingDomain1 = mockk<ProductListing>(relaxed = true)

    private val testOrderId1 = "order1"
    private val testOrderEntity1 = mockk<OrderEntity>(relaxed = true)
    private val testOrderDomain1 = mockk<Order>(relaxed = true)


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockProductListingRepository = mockk()
        mockOrderRepository = mockk()

        every { testListingEntity1.id } returns testListingId1
        every { testListingEntity1.syncAttempts } returns 0
        every { testListingDomain1.id } returns testListingId1

        every { testOrderEntity1.orderId } returns testOrderId1
        every { testOrderEntity1.syncAttempts } returns 0
        every { testOrderDomain1.id } returns testOrderId1 // Assuming Order domain model has 'id', if not use orderId
    }

    private fun createWorker(): MarketplaceSyncWorker {
        val workerParameters = WorkerParameters.DEFAULT_ ಬೆಂಗಳമല്ല
        return TestListenableWorkerBuilder<MarketplaceSyncWorker>(context)
            .setWorkerParams(workerParameters)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker? {
                    if (workerClassName == MarketplaceSyncWorker::class.java.name) {
                        return MarketplaceSyncWorker(
                            appContext,
                            workerParameters,
                            mockProductListingRepository,
                            mockOrderRepository
                        )
                    }
                    return null
                }
            })
            .build()
    }

    @Test
    fun `doWork when no unsynced items should return success`() = runBlocking {
        // Arrange
        coEvery { mockProductListingRepository.getUnsyncedProductListingEntities() } returns emptyList()
        coEvery { mockOrderRepository.getUnsyncedOrderEntities() } returns emptyList()

        val worker = createWorker()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork syncs listing successfully`() = runBlocking {
        // Arrange
        coEvery { mockProductListingRepository.getUnsyncedProductListingEntities() } returns listOf(testListingEntity1)
        coEvery { mockProductListingRepository.mapListingEntityToDomain(testListingEntity1) } returns testListingDomain1
        coEvery { mockProductListingRepository.syncListingRemote(testListingDomain1) } returns Result.Success(Unit)
        coEvery { mockProductListingRepository.updateLocalListing(any()) } just runs // For attempt update and final sync status update
        coEvery { mockOrderRepository.getUnsyncedOrderEntities() } returns emptyList()


        val worker = createWorker()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerifySequence {
            mockProductListingRepository.getUnsyncedProductListingEntities()
            mockProductListingRepository.updateLocalListing(match { it.id == testListingId1 && it.syncAttempts == 1 }) // Before sync
            mockProductListingRepository.mapListingEntityToDomain(match { it.id == testListingId1 && it.syncAttempts == 1 })
            mockProductListingRepository.syncListingRemote(testListingDomain1)
            mockProductListingRepository.updateLocalListing(match { it.id == testListingId1 && !it.needsSync && it.syncAttempts == 0 }) // After sync
        }
    }

    @Test
    fun `doWork fails to sync listing should retry`() = runBlocking {
        // Arrange
        coEvery { mockProductListingRepository.getUnsyncedProductListingEntities() } returns listOf(testListingEntity1)
        coEvery { mockProductListingRepository.mapListingEntityToDomain(testListingEntity1) } returns testListingDomain1
        coEvery { mockProductListingRepository.syncListingRemote(testListingDomain1) } returns Result.Error(Exception("Remote sync failed"))
        coEvery { mockProductListingRepository.updateLocalListing(any()) } just runs // For attempt update
        coEvery { mockOrderRepository.getUnsyncedOrderEntities() } returns emptyList()

        val worker = createWorker()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.retry(), result)
        coVerify { mockProductListingRepository.updateLocalListing(match { it.id == testListingId1 && it.syncAttempts == 1 }) }
        coVerify(exactly = 0) { mockProductListingRepository.updateLocalListing(match { !it.needsSync }) } // Should not be marked as synced
    }

    @Test
    fun `doWork skips listing after max sync attempts`() = runBlocking {
        // Arrange
        every { testListingEntity1.syncAttempts } returns 5 // MAX_SYNC_ATTEMPTS
        coEvery { mockProductListingRepository.getUnsyncedProductListingEntities() } returns listOf(testListingEntity1)
        coEvery { mockOrderRepository.getUnsyncedOrderEntities() } returns emptyList()

        val worker = createWorker()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.retry(), result) // Retry because item still needs sync, even if skipped this round
        coVerify(exactly = 0) { mockProductListingRepository.updateLocalListing(any()) } // No update attempt because it's skipped
        coVerify(exactly = 0) { mockProductListingRepository.syncListingRemote(any()) }
    }

    // TODO: Add similar tests for Order syncing (success, failure, max attempts)
    // TODO: Add tests for mixed scenarios (some listings succeed, some orders fail, etc.)
}
