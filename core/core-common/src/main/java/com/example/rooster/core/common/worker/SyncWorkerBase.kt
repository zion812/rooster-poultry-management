package com.example.rooster.core.common.worker

/**
 * Base class for sync workers
 * This provides the foundation for background synchronization tasks
 */
abstract class SyncWorkerBase {

    companion object {
        const val COMMUNITY_SYNC_WORKER = "community_sync_worker"
        const val MARKETPLACE_SYNC_WORKER = "marketplace_sync_worker"
        const val AUCTION_SYNC_WORKER = "auction_sync_worker"
        const val FARM_DATA_SYNC_WORKER = "farm_data_sync_worker"
    }

    abstract suspend fun doWork(): Result

    sealed class Result {
        object Success : Result()
        object Retry : Result()
        data class Failure(val error: String) : Result()
    }
}

/**
 * Mock implementations for sync workers
 * These will be replaced with actual WorkManager implementations
 */
class CommunitySyncWorker : SyncWorkerBase() {
    override suspend fun doWork(): Result {
        // Mock implementation - will sync community data
        return Result.Success
    }
}

class MarketplaceSyncWorker : SyncWorkerBase() {
    override suspend fun doWork(): Result {
        // Mock implementation - will sync marketplace data
        return Result.Success
    }
}

class AuctionSyncWorker : SyncWorkerBase() {
    override suspend fun doWork(): Result {
        // Mock implementation - will sync auction data
        return Result.Success
    }
}

class FarmDataSyncWorker : SyncWorkerBase() {
    override suspend fun doWork(): Result {
        // Mock implementation - will sync farm data
        return Result.Success
    }
}