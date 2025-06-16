package com.example.rooster.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.NetworkQualityLevel
import com.example.rooster.util.NetworkQualityManager
import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DataSyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val networkQualityManager: NetworkQualityManager,
    ) : CoroutineWorker(context, workerParams) {
        companion object {
            const val WORK_NAME = "data_sync_work"
            private const val TAG = "DataSyncWorker"

            fun schedule(context: Context) {
                val constraints =
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()

                val request =
                    androidx.work.PeriodicWorkRequestBuilder<DataSyncWorker>(
                        6,
                        java.util.concurrent.TimeUnit.HOURS,
                    )
                        .setConstraints(constraints)
                        .build()

                androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
            }
        }

        override suspend fun doWork(): Result =
            withContext(Dispatchers.IO) {
                return@withContext try {
                    val networkQuality = networkQualityManager.getCurrentNetworkQuality()

                    // Determine query limits based on network quality
                    val queryLimit =
                        when (networkQuality) {
                            NetworkQualityLevel.EXCELLENT -> 50
                            NetworkQualityLevel.GOOD -> 30
                            NetworkQualityLevel.FAIR -> 20
                            NetworkQualityLevel.POOR -> 10
                            NetworkQualityLevel.OFFLINE -> 5
                        }

                    // Sync critical data
                    syncFowlRecords(queryLimit)
                    syncMarketplaceListings(queryLimit)
                    syncCommunityMessages(queryLimit)
                    syncTraditionalMarkets(queryLimit)

                    Result.success()
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "DataSync failed", e)
                    com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)

                    // Retry on network errors, fail on other errors
                    if (e.message?.contains("network", ignoreCase = true) == true) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            }

        private suspend fun syncFowlRecords(limit: Int) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Fowl")
                query.limit = limit
                query.orderByDescending("updatedAt")

                val fowlRecords = query.find()

                // Pin to local datastore for offline access
                ParseObject.pinAllInBackground("fowl_cache", fowlRecords)

                android.util.Log.d(TAG, "Synced ${fowlRecords.size} fowl records")
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to sync fowl records", e)
            }
        }

        private suspend fun syncMarketplaceListings(limit: Int) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Listing")
                query.limit = limit
                query.whereEqualTo("isActive", true)
                query.orderByDescending("createdAt")

                val listings = query.find()

                // Pin to local datastore for offline access
                ParseObject.pinAllInBackground("marketplace_cache", listings)

                android.util.Log.d(TAG, "Synced ${listings.size} marketplace listings")
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to sync marketplace listings", e)
            }
        }

        private suspend fun syncCommunityMessages(limit: Int) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Post")
                query.limit = limit
                query.include("author")
                query.orderByDescending("createdAt")

                val posts = query.find()

                // Pin to local datastore for offline access
                ParseObject.pinAllInBackground("community_cache", posts)

                android.util.Log.d(TAG, "Synced ${posts.size} community posts")
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to sync community messages", e)
            }
        }

        private suspend fun syncTraditionalMarkets(limit: Int) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("TraditionalMarket")
                query.limit = limit
                query.whereEqualTo("isActive", true)
                query.orderByAscending("nextMarketDate")

                val markets = query.find()

                // Pin to local datastore for offline access
                ParseObject.pinAllInBackground("traditional_markets_cache", markets)

                android.util.Log.d(TAG, "Synced ${markets.size} traditional markets")
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to sync traditional markets", e)
            }
        }
    }
