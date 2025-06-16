package com.example.rooster.feature.farm.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock remote data source for real-time farm data
 * TODO: Replace with actual Firebase implementation
 */
@Singleton
class FarmRemoteDataSource @Inject constructor() {

    // Real-time Flock Data Fetching (Mock Implementation)
    fun getFlockRealTime(flockId: String): Flow<Result<Map<String, Any>?>> = flow {
        while (true) {
            emit(Result.success(createMockFlockData(flockId)))
            delay(5000) // Update every 5 seconds
        }
    }

    // Real-time Flock Collection by Owner
    fun getFlocksByOwnerRealTime(ownerId: String): Flow<Result<List<Map<String, Any>>>> = flow {
        while (true) {
            emit(Result.success(createMockFlocksList(ownerId)))
            delay(10000) // Update every 10 seconds
        }
    }

    // Real-time Mortality Records
    fun getMortalityRecordsRealTime(fowlId: String): Flow<Result<List<Map<String, Any>>>> = flow {
        while (true) {
            emit(Result.success(createMockMortalityRecords(fowlId)))
            delay(15000) // Update every 15 seconds
        }
    }

    // Real-time Vaccination Records
    fun getVaccinationRecordsRealTime(fowlId: String): Flow<Result<List<Map<String, Any>>>> = flow {
        while (true) {
            emit(Result.success(createMockVaccinationRecords(fowlId)))
            delay(20000) // Update every 20 seconds
        }
    }

    // Real-time Sensor Data Stream
    fun getSensorDataRealTime(deviceId: String): Flow<Result<List<Map<String, Any>>>> = flow {
        while (true) {
            emit(Result.success(createMockSensorData(deviceId)))
            delay(2000) // Update every 2 seconds for sensor data
        }
    }

    // Real-time Update Records
    fun getUpdateRecordsRealTime(fowlId: String): Flow<Result<List<Map<String, Any>>>> = flow {
        while (true) {
            emit(Result.success(createMockUpdateRecords(fowlId)))
            delay(30000) // Update every 30 seconds
        }
    }

    // Write Operations with Real-time Updates
    suspend fun saveFlock(flockData: Map<String, Any>): Result<Unit> {
        return try {
            // Mock save operation
            delay(500)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMortalityRecord(recordData: Map<String, Any>): Result<Unit> {
        return try {
            delay(300)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveVaccinationRecord(recordData: Map<String, Any>): Result<Unit> {
        return try {
            delay(300)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSensorData(sensorData: Map<String, Any>): Result<Unit> {
        return try {
            delay(100) // Fast save for sensor data
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUpdateRecord(updateData: Map<String, Any>): Result<Unit> {
        return try {
            delay(300)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete Operations
    suspend fun deleteFlock(flockId: String): Result<Unit> {
        return try {
            delay(300)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMortalityRecord(recordId: String): Result<Unit> {
        return try {
            delay(200)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVaccinationRecord(recordId: String): Result<Unit> {
        return try {
            delay(200)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUpdateRecord(recordId: String): Result<Unit> {
        return try {
            delay(200)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mock Data Creation Methods
    private fun createMockFlockData(flockId: String): Map<String, Any> {
        return mapOf(
            "id" to flockId,
            "ownerId" to "owner_${Random().nextInt(100)}",
            "name" to "Flock $flockId",
            "type" to "FOWL",
            "breed" to "Rhode Island Red",
            "weight" to (2.5 + Random().nextDouble() * 2.0),
            "certified" to Random().nextBoolean(),
            "verified" to Random().nextBoolean(),
            "createdAt" to System.currentTimeMillis(),
            "updatedAt" to System.currentTimeMillis()
        )
    }

    private fun createMockFlocksList(ownerId: String): List<Map<String, Any>> {
        return (1..5).map { index ->
            mapOf(
                "id" to "flock_${ownerId}_$index",
                "ownerId" to ownerId,
                "name" to "Flock $index",
                "type" to listOf("FOWL", "HEN", "BREEDER", "CHICK").random(),
                "breed" to listOf("Rhode Island Red", "Leghorn", "Bantam").random(),
                "weight" to (2.0 + Random().nextDouble() * 3.0),
                "certified" to Random().nextBoolean(),
                "verified" to Random().nextBoolean(),
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
        }
    }

    private fun createMockMortalityRecords(fowlId: String): List<Map<String, Any>> {
        return if (Random().nextBoolean()) {
            listOf(
                mapOf(
                    "id" to "mort_${UUID.randomUUID()}",
                    "fowlId" to fowlId,
                    "cause" to listOf("Disease", "Predator", "Natural", "Unknown").random(),
                    "description" to "Mock mortality record",
                    "weight" to (1.5 + Random().nextDouble() * 2.0),
                    "photos" to "photo1.jpg,photo2.jpg",
                    "recordedAt" to System.currentTimeMillis(),
                    "createdAt" to System.currentTimeMillis()
                )
            )
        } else {
            emptyList()
        }
    }

    private fun createMockVaccinationRecords(fowlId: String): List<Map<String, Any>> {
        return (1..3).map { index ->
            mapOf(
                "id" to "vacc_${UUID.randomUUID()}",
                "fowlId" to fowlId,
                "vaccineName" to "Vaccine $index",
                "dosage" to "1ml",
                "veterinarian" to "Dr. Smith",
                "nextDueDate" to System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
                "notes" to "Vaccination notes",
                "photos" to "vacc_photo.jpg",
                "recordedAt" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )
        }
    }

    private fun createMockSensorData(deviceId: String): List<Map<String, Any>> {
        return (1..10).map { index ->
            mapOf(
                "id" to "sensor_${UUID.randomUUID()}",
                "deviceId" to deviceId,
                "temperature" to (20.0 + Random().nextDouble() * 15.0),
                "humidity" to (40.0 + Random().nextDouble() * 40.0),
                "airQuality" to (50.0 + Random().nextDouble() * 50.0),
                "lightLevel" to (100.0 + Random().nextDouble() * 900.0),
                "noiseLevel" to (30.0 + Random().nextDouble() * 40.0),
                "timestamp" to System.currentTimeMillis() - (index * 60000), // 1 minute intervals
                "createdAt" to System.currentTimeMillis()
            )
        }
    }

    private fun createMockUpdateRecords(fowlId: String): List<Map<String, Any>> {
        return (1..2).map { index ->
            mapOf(
                "id" to "update_${UUID.randomUUID()}",
                "fowlId" to fowlId,
                "updateType" to listOf("HEALTH", "WEIGHT", "BEHAVIOR", "FEED").random(),
                "title" to "Update $index",
                "description" to "Mock update record $index",
                "weight" to (2.0 + Random().nextDouble() * 3.0),
                "photos" to "update_photo.jpg",
                "recordedAt" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )
        }
    }
}
