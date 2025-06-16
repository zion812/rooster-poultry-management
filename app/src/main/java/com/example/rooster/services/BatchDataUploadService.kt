package com.example.rooster.services

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class BatchRecord(
    val type: String, // "vaccination", "growth", "health"
    val fowlId: String,
    val data: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis(),
    val tempId: String = java.util.UUID.randomUUID().toString()
)

@Singleton
class BatchDataUploadService @Inject constructor(
    private val context: Context
) {
    private val TAG = "BatchDataUploadService"
    
    // Upload queue state
    private val _uploadQueue = MutableStateFlow<List<BatchRecord>>(emptyList())
    val uploadQueue: StateFlow<List<BatchRecord>> = _uploadQueue.asStateFlow()
    
    // Upload status
    private val _uploadStatus = MutableStateFlow<Map<String, String>>(emptyMap())
    val uploadStatus: StateFlow<Map<String, String>> = _uploadStatus.asStateFlow()
    
    // Sync statistics
    private val _syncStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val syncStats: StateFlow<Map<String, Int>> = _syncStats.asStateFlow()
    
    companion object {
        private const val BATCH_UPLOAD_WORK = "batch_upload_work"
        private const val BATCH_SIZE = 20 // Records per batch
        private const val SYNC_INTERVAL_MINUTES = 15L
    }
    
    /**
     * Initialize batch upload service
     */
    fun initialize() {
        try {
            scheduleBatchUpload()
            Log.d(TAG, "Batch upload service initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize batch upload service", e)
        }
    }
    
    /**
     * Add vaccination record to batch queue
     */
    fun addVaccinationRecord(
        fowlId: String,
        vaccineName: String,
        vaccineDate: Long,
        veterinarian: String? = null,
        batchNumber: String? = null,
        notes: String? = null
    ) {
        val data = mutableMapOf<String, Any>(
            "vaccineName" to vaccineName,
            "vaccineDate" to vaccineDate
        )
        veterinarian?.let { data["veterinarian"] = it }
        batchNumber?.let { data["batchNumber"] = it }
        notes?.let { data["notes"] = it }

        addRecord(BatchRecord("vaccination", fowlId, data))
    }
    
    /**
     * Add growth update to batch queue
     */
    fun addGrowthUpdate(
        fowlId: String,
        weight: Double,
        height: Double? = null,
        recordDate: Long,
        notes: String? = null,
        healthStatus: String = "normal"
    ) {
        val data = mutableMapOf<String, Any>(
            "weight" to weight,
            "recordDate" to recordDate,
            "healthStatus" to healthStatus
        )
        height?.let { data["height"] = it }
        notes?.let { data["notes"] = it }

        addRecord(BatchRecord("growth", fowlId, data))
    }
    
    /**
     * Add health record to batch queue
     */
    fun addHealthRecord(
        fowlId: String,
        recordType: String,
        date: Long,
        symptoms: List<String> = emptyList(),
        treatment: String? = null,
        veterinarian: String? = null,
        followUpDate: Long? = null
    ) {
        val data = mutableMapOf<String, Any>(
            "recordType" to recordType,
            "date" to date,
            "symptoms" to symptoms
        )
        treatment?.let { data["treatment"] = it }
        veterinarian?.let { data["veterinarian"] = it }
        followUpDate?.let { data["followUpDate"] = it }

        addRecord(BatchRecord("health", fowlId, data))
    }
    
    /**
     * Process batch upload queue
     */
    suspend fun processBatchUpload(): Result<Int> {
        return try {
            val queue = _uploadQueue.value
            if (queue.isEmpty()) {
                return Result.success(0)
            }

            Log.d(TAG, "Processing batch upload: ${queue.size} records")

            val currentUser = ParseUser.getCurrentUser()
            if (currentUser == null) {
                return Result.failure(IllegalStateException("No current user"))
            }

            var totalUploaded = 0

            // Group records by type for efficient batching
            val groupedRecords = queue.groupBy { it.type }

            groupedRecords.forEach { (type, records) ->
                when (type) {
                    "vaccination" -> {
                        val result = uploadVaccinationRecords(records, currentUser)
                        result.fold(
                            onSuccess = { count -> totalUploaded += count },
                            onFailure = { e ->
                                Log.e(
                                    TAG,
                                    "Failed to upload vaccination records",
                                    e
                                )
                            }
                        )
                    }
                    "growth" -> {
                        val result = uploadGrowthRecords(records, currentUser)
                        result.fold(
                            onSuccess = { count -> totalUploaded += count },
                            onFailure = { e -> Log.e(TAG, "Failed to upload growth records", e) }
                        )
                    }
                    "health" -> {
                        val result = uploadHealthRecords(records, currentUser)
                        result.fold(
                            onSuccess = { count -> totalUploaded += count },
                            onFailure = { e -> Log.e(TAG, "Failed to upload health records", e) }
                        )
                    }
                }
            }

            // Clear uploaded records from queue
            if (totalUploaded > 0) {
                _uploadQueue.value = emptyList()

                // Update sync statistics
                updateSyncStats("last_sync_count", totalUploaded)
                updateSyncStats(
                    "total_synced",
                    (_syncStats.value["total_synced"] ?: 0) + totalUploaded
                )
                updateSyncStats("last_sync_time", System.currentTimeMillis().toInt())
            }
            
            Log.d(TAG, "Batch upload completed: $totalUploaded records")
            Result.success(totalUploaded)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process batch upload", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get current queue size
     */
    fun getTotalQueueSize(): Int = _uploadQueue.value.size
    
    /**
     * Clear all queued data
     */
    fun clearQueue() {
        _uploadQueue.value = emptyList()
        Log.d(TAG, "Upload queue cleared")
    }
    
    /**
     * Get sync statistics
     */
    fun getSyncStats(): Map<String, Any> {
        val stats = _syncStats.value
        val queue = _uploadQueue.value

        return mapOf(
            "queue_size" to queue.size,
            "last_sync_count" to (stats["last_sync_count"] ?: 0),
            "total_synced" to (stats["total_synced"] ?: 0),
            "last_sync_time" to (stats["last_sync_time"] ?: 0),
            "pending_vaccinations" to queue.count { it.type == "vaccination" },
            "pending_growth_updates" to queue.count { it.type == "growth" },
            "pending_health_records" to queue.count { it.type == "health" }
        )
    }

    private fun addRecord(record: BatchRecord) {
        try {
            val currentQueue = _uploadQueue.value.toMutableList()
            currentQueue.add(record)
            _uploadQueue.value = currentQueue

            updateStatus(record.tempId, "queued")
            Log.d(TAG, "${record.type} record queued: ${record.fowlId}")

            // Trigger immediate sync if queue is getting large
            if (getTotalQueueSize() >= BATCH_SIZE) {
                triggerImmediateSync()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to add ${record.type} record", e)
            updateStatus(record.tempId, "error")
        }
    }

    private suspend fun uploadVaccinationRecords(
        records: List<BatchRecord>,
        user: ParseUser
    ): Result<Int> {
        return try {
            val parseObjects = records.map { record ->
                ParseObject("VaccinationRecord").apply {
                    put("fowlId", record.fowlId)
                    put("vaccineName", record.data["vaccineName"] as String)
                    put("vaccineDate", java.util.Date(record.data["vaccineDate"] as Long))
                    record.data["veterinarian"]?.let { put("veterinarian", it as String) }
                    record.data["batchNumber"]?.let { put("batchNumber", it as String) }
                    record.data["notes"]?.let { put("notes", it as String) }
                    put("owner", user)
                }
            }
            
            ParseObject.saveAll(parseObjects)
            records.forEach { updateStatus(it.tempId, "uploaded") }

            Log.d(TAG, "Uploaded ${records.size} vaccination records")
            Result.success(records.size)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload vaccination records", e)
            records.forEach { updateStatus(it.tempId, "error") }
            Result.failure(e)
        }
    }

    private suspend fun uploadGrowthRecords(
        records: List<BatchRecord>,
        user: ParseUser
    ): Result<Int> {
        return try {
            val parseObjects = records.map { record ->
                ParseObject("GrowthRecord").apply {
                    put("fowlId", record.fowlId)
                    put("weight", record.data["weight"] as Double)
                    record.data["height"]?.let { put("height", it as Double) }
                    put("recordDate", java.util.Date(record.data["recordDate"] as Long))
                    record.data["notes"]?.let { put("notes", it as String) }
                    put("healthStatus", record.data["healthStatus"] as String)
                    put("owner", user)
                }
            }
            
            ParseObject.saveAll(parseObjects)
            records.forEach { updateStatus(it.tempId, "uploaded") }

            Log.d(TAG, "Uploaded ${records.size} growth records")
            Result.success(records.size)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload growth records", e)
            records.forEach { updateStatus(it.tempId, "error") }
            Result.failure(e)
        }
    }

    private suspend fun uploadHealthRecords(
        records: List<BatchRecord>,
        user: ParseUser
    ): Result<Int> {
        return try {
            val parseObjects = records.map { record ->
                ParseObject("HealthRecord").apply {
                    put("fowlId", record.fowlId)
                    put("recordType", record.data["recordType"] as String)
                    put("date", java.util.Date(record.data["date"] as Long))
                    put("symptoms", record.data["symptoms"] as List<String>)
                    record.data["treatment"]?.let { put("treatment", it as String) }
                    record.data["veterinarian"]?.let { put("veterinarian", it as String) }
                    record.data["followUpDate"]?.let {
                        put(
                            "followUpDate",
                            java.util.Date(it as Long)
                        )
                    }
                    put("owner", user)
                }
            }
            
            ParseObject.saveAll(parseObjects)
            records.forEach { updateStatus(it.tempId, "uploaded") }

            Log.d(TAG, "Uploaded ${records.size} health records")
            Result.success(records.size)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload health records", e)
            records.forEach { updateStatus(it.tempId, "error") }
            Result.failure(e)
        }
    }
    
    private fun scheduleBatchUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val periodicWorkRequest = PeriodicWorkRequestBuilder<BatchUploadWorker>(
            SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                BATCH_UPLOAD_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }
    
    private fun triggerImmediateSync() {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<BatchUploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
        Log.d(TAG, "Immediate sync triggered")
    }
    
    private fun updateStatus(tempId: String, status: String) {
        val currentStatus = _uploadStatus.value.toMutableMap()
        currentStatus[tempId] = status
        _uploadStatus.value = currentStatus
    }
    
    private fun updateSyncStats(key: String, value: Int) {
        val currentStats = _syncStats.value.toMutableMap()
        currentStats[key] = value
        _syncStats.value = currentStats
    }
}

/**
 * Background worker for batch uploads
 */
class BatchUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val TAG = "BatchUploadWorker"
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting batch upload worker")
            Log.d(TAG, "Batch upload worker completed")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "Batch upload worker failed", e)
            Result.retry()
        }
    }
}
