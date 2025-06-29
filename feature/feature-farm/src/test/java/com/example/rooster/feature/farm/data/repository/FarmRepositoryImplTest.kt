package com.example.rooster.feature.farm.data.repository

import app.cash.turbine.test
import com.example.rooster.core.common.Result
import com.example.rooster.feature.farm.data.local.FlockDao
import com.example.rooster.feature.farm.data.local.FlockEntity
import com.example.rooster.feature.farm.data.local.LineageDao
import com.example.rooster.feature.farm.data.remote.FirebaseFarmDataSource
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class FarmRepositoryImplTest {

    private lateinit var repository: FarmRepositoryImpl
    private lateinit var mockFlockDao: FlockDao
    private lateinit var mockLineageDao: LineageDao
    private lateinit var mockRemoteDataSource: FirebaseFarmDataSource

    private val testFlockId = "testFlock1"
    private val localSyncedFlockEntity = FlockEntity(
        id = testFlockId,
        ownerId = "owner1",
        fatherId = null,
        motherId = null,
        type = FlockType.ROOSTER.name,
        name = "Local Synced Rooster",
        breed = "Aseel",
        weight = 3.5f,
        certified = false,
        verified = true,
        createdAt = Date().time,
        updatedAt = Date().time,
        needsSync = false, // Synced
        syncAttempts = 0,
        lastSyncAttemptTimestamp = 0L
    )
    private val localUnsyncedFlockEntity = localSyncedFlockEntity.copy(
        name = "Local Unsynced Rooster",
        needsSync = true // Unsynced
    )
    private val remoteFlockDataMap = mapOf(
        "id" to testFlockId,
        "ownerId" to "owner1",
        "type" to FlockType.ROOSTER.name,
        "name" to "Remote Rooster Data",
        "breed" to "Aseel",
        "weight" to 4.0f,
        "certified" to true,
        "verified" to true,
        "createdAt" to Date().time,
        "updatedAt" to Date().time + 1000 // Remote is newer
    )

    @Before
    fun setUp() {
        mockFlockDao = mockk(relaxed = true)
        mockLineageDao = mockk(relaxed = true)
        mockRemoteDataSource = mockk(relaxed = true)
        repository = FarmRepositoryImpl(mockFlockDao, mockLineageDao, mockRemoteDataSource)
    }

    @Test
    fun `getFlockById when local is unsynced should emit local data and ignore remote`() = runBlocking {
        // Arrange
        coEvery { mockFlockDao.getById(testFlockId) } returns flowOf(localUnsyncedFlockEntity)
        coEvery { mockRemoteDataSource.getFlockRealTime(testFlockId) } returns flowOf(Result.Success(remoteFlockDataMap))

        // Act & Assert
        repository.getFlockById(testFlockId).test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            assertEquals(localUnsyncedFlockEntity.name, (result as Result.Success).data?.name)
            // TODO: Verify mockFlockDao.insert was NOT called with remote data
            awaitComplete()
        }
    }

    @Test
    fun `getFlockById when local is synced should emit remote data and update cache`() = runBlocking {
        // Arrange
        coEvery { mockFlockDao.getById(testFlockId) } returns flowOf(localSyncedFlockEntity) // Initially synced
        coEvery { mockRemoteDataSource.getFlockRealTime(testFlockId) } returns flowOf(Result.Success(remoteFlockDataMap))

        // Act & Assert
        repository.getFlockById(testFlockId).test {
            val result = awaitItem() // Might get initial local, then remote
            // Depending on combine behavior, we might get multiple emissions.
            // We are interested in the one that reflects the remote data.
            // For simplicity, let's assume the test focuses on the outcome after remote sync.
            // A more robust test would check all emissions.

            // Skip initial local emission if any, wait for the one influenced by remote
            val finalResult = if ((result as? Result.Success)?.data?.name == localSyncedFlockEntity.name && localSyncedFlockEntity.name != remoteFlockDataMap["name"]) {
                awaitItem() // If first item was purely local, get next
            } else {
                result
            }

            assertTrue(finalResult is Result.Success)
            assertEquals(remoteFlockDataMap["name"], (finalResult as Result.Success).data?.name)
            // TODO: Verify mockFlockDao.insert WAS called with data derived from remoteFlockDataMap and needsSync=false
            awaitComplete()
        }
    }

    @Test
    fun `getFlockById when local is synced and remote is null (deleted) should delete local`() = runBlocking {
        // Arrange
        coEvery { mockFlockDao.getById(testFlockId) } returns flowOf(localSyncedFlockEntity)
        coEvery { mockRemoteDataSource.getFlockRealTime(testFlockId) } returns flowOf(Result.Success(null)) // Remote deleted

        // Act & Assert
        repository.getFlockById(testFlockId).test {
            val result = awaitItem() // Could be initial local
             val finalResult = if ((result as? Result.Success)?.data?.name == localSyncedFlockEntity.name) {
                awaitItem() // If first item was purely local, get next which should be null
            } else {
                result
            }
            assertTrue(finalResult is Result.Success && finalResult.data == null)
            // TODO: Verify mockFlockDao.deleteById(testFlockId) was called
            awaitComplete()
        }
    }

     @Test
    fun `getFlockById when local is unsynced and remote is null (deleted) should prioritize local`() = runBlocking {
        // Arrange
        coEvery { mockFlockDao.getById(testFlockId) } returns flowOf(localUnsyncedFlockEntity)
        coEvery { mockRemoteDataSource.getFlockRealTime(testFlockId) } returns flowOf(Result.Success(null)) // Remote deleted

        // Act & Assert
        repository.getFlockById(testFlockId).test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            assertEquals(localUnsyncedFlockEntity.name, (result as Result.Success).data?.name)
            // TODO: Verify mockFlockDao.deleteById was NOT called
            // TODO: Verify mockFlockDao.insert was NOT called with needsSync=false
            awaitComplete()
        }
    }

    // TODO: Add tests for registerFlock (success, local failure, remote sync failure)
    // TODO: Add tests for getLineageInfo (happy path, broken links)
    // TODO: Add tests for mappers (especially enum handling if UNKNOWN states are added)
}
