package com.example.rooster.core.common.worker

package com.example.rooster.core.common.worker // Ensure package is correct if file moved/refactored

import com.example.rooster.core.common.Result // Using the common Result type

/**
 * Base class for conceptual sync operations.
 * Actual workers should extend ListenableWorker or CoroutineWorker from WorkManager.
 * This base class can hold common logic or constants if needed, but primarily serves
 * as a conceptual placeholder as per the original code.
 *
 * The doWork() method here returns the common Result<Unit> for consistency,
 * though actual WorkManager workers use ListenableWorker.Result.
 */
abstract class SyncWorkerBase {

    companion object {
        // These constants might be useful for naming WorkManager unique work
        const val COMMUNITY_SYNC_WORKER_TAG = "community_sync_worker"
        const val MARKETPLACE_SYNC_WORKER_TAG = "marketplace_sync_worker"
        const val AUCTION_SYNC_WORKER_TAG = "auction_sync_worker"
        const val FARM_DATA_SYNC_WORKER_TAG = "farm_data_sync_worker"
    }

    /**
     * Performs the synchronization work.
     * Implementations should interact with repositories to sync data.
     * @return Result.Success(Unit) if successful, Result.Error otherwise.
     *         Actual WorkManager workers will map this to ListenableWorker.Result.SUCCESS,
     *         ListenableWorker.Result.RETRY, or ListenableWorker.Result.FAILURE.
     */
    abstract suspend fun doWork(): Result<Unit>

    // Removed internal sealed class Result as we are using com.example.rooster.core.common.Result
}

// Mock implementations are removed.
// Actual Worker implementations will reside in feature modules or the app module
// and will extend androidx.work.CoroutineWorker or androidx.work.Worker.