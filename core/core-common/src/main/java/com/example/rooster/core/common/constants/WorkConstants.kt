package com.example.rooster.core.common.constants

import java.util.concurrent.TimeUnit

object WorkConstants {
    const val MIN_BACKOFF_MILLIS = 30_000L
    const val MAX_BACKOFF_MILLIS = 300_000L
    const val INITIAL_DELAY_MILLIS = 5_000L

    // Worker names
    const val COMMUNITY_SYNC_WORKER = "community_sync_worker"
    const val MARKETPLACE_SYNC_WORKER = "marketplace_sync_worker"
    const val AUCTION_SYNC_WORKER = "auction_sync_worker"
    const val FARM_DATA_SYNC_WORKER = "farm_data_sync_worker"

    // Work tags
    const val SYNC_WORK_TAG = "sync_work"
    const val PERIODIC_WORK_TAG = "periodic_work"
    const val ONE_TIME_WORK_TAG = "one_time_work"
    const val UNIQUE_WORK_NAME = "rooster_sync_work"
    const val NOTIFICATION_WORK_TAG = "notification_work"
    const val BACKUP_WORK_TAG = "backup_work"
}

object NetworkConstants {
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Time units
    val TIMEOUT_UNIT = TimeUnit.SECONDS
}

object CacheConstants {
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE = 5 * 60 // 5 minutes
    const val CACHE_MAX_STALE = 60 * 60 * 24 // 24 hours
}