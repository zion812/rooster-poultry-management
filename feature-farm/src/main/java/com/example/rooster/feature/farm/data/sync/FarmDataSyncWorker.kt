package com.example.rooster.feature.farm.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.core.common.RoosterResult
import com.example.rooster.feature.farm.domain.repository.FarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FarmDataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val farmRepository: FarmRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "FarmDataSyncWorker"
        private const val TAG = "FarmDataSyncWorker"
    }

    override suspend fun doWork(): Result {
        Log.i(TAG, "Farm data synchronization starting...")

        return try {
            when (val syncResult = farmRepository.synchronizeFarms()) {
                is RoosterResult.Success -> {
                    val summary = syncResult.data
                    Log.i(TAG, "Farm data synchronization successful. Summary: " +
                            "New Uploaded: ${summary.newItemsUploaded}, " +
                            "Updated Upstream: ${summary.itemsUpdatedUpstream}, " +
                            "Deleted Upstream: ${summary.itemsDeletedUpstream}, " +
                            "New Downloaded: ${summary.newItemsDownloaded}, " +
                            "Updated Locally: ${summary.itemsUpdatedLocallyFromServer}, " +
                            "Conflicts: ${summary.conflictsEncountered}")

                    if (summary.errors.isNotEmpty()) {
                        Log.w(TAG, "Sync completed with errors: ${summary.errors.joinToString()}")
                        // Depending on error severity, might still be a partial success
                        // For now, if any repository-level error occurred, consider it a retry.
                        Result.retry()
                    } else {
                        Result.success()
                    }
                }
                is RoosterResult.Error -> {
                    Log.e(TAG, "Farm data synchronization failed: ${syncResult.message}", syncResult.exception)
                    Result.retry() // Use WorkManager's retry mechanism
                }
                is RoosterResult.Loading -> {
                    // This state should not be returned by synchronizeFarms, but handle defensively
                    Log.d(TAG, "Farm data synchronization in loading state unexpectedly.")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled exception during farm data synchronization", e)
            Result.failure() // Or Result.retry() depending on the nature of the exception
        }
    }
}
