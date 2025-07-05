package com.example.rooster

import android.content.Context
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager helper for scheduling coin retry operations
 */
object WorkManagerHelper {
    private const val COIN_RETRY_WORK_NAME = "coin_retry_work"

    /**
     * Schedule a coin retry operation using WorkManager
     */
    fun scheduleCoinRetry(
        context: Context,
        userId: String,
        actionLabel: String,
        amount: Int,
        linkedObjectId: String,
        initialDelayMinutes: Long = 1,
    ) {
        val inputData =
            Data.Builder()
                .putString("userId", userId)
                .putString("actionLabel", actionLabel)
                .putInt("amount", amount)
                .putString("linkedObjectId", linkedObjectId)
                .build()

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val retryRequest =
            OneTimeWorkRequestBuilder<CoinRetryWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS,
                )
                .addTag("coin_retry_$linkedObjectId")
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "${COIN_RETRY_WORK_NAME}_$linkedObjectId",
                ExistingWorkPolicy.REPLACE,
                retryRequest,
            )

        FirebaseCrashlytics.getInstance().log(
            "WorkManagerHelper: Scheduled coin retry for $actionLabel, amount: $amount",
        )
    }

    /**
     * Cancel pending coin retry for a specific object
     */
    fun cancelCoinRetry(
        context: Context,
        linkedObjectId: String,
    ) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("${COIN_RETRY_WORK_NAME}_$linkedObjectId")
    }
}

/**
 * WorkManager worker that handles coin retry operations
 */
class CoinRetryWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val coinManager = CoinManager()
    private val repositoryHelper = RepositoryHelper()

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            try {
                val userId =
                    inputData.getString("userId")
                        ?: return@withContext Result.failure()
                val actionLabel =
                    inputData.getString("actionLabel")
                        ?: return@withContext Result.failure()
                val amount = inputData.getInt("amount", 0)
                val linkedObjectId =
                    inputData.getString("linkedObjectId")
                        ?: return@withContext Result.failure()

                FirebaseCrashlytics.getInstance().log(
                    "CoinRetryWorker: Retrying coin deduction for $actionLabel",
                )

                // Check if this has already been charged to prevent double-charging
                if (coinManager.hasBeenCharged(userId, actionLabel, linkedObjectId)) {
                    FirebaseCrashlytics.getInstance().log(
                        "CoinRetryWorker: Already charged for $actionLabel, marking as complete",
                    )
                    repositoryHelper.markCoinDeducted(linkedObjectId)
                    return@withContext Result.success()
                }

                // Attempt coin deduction
                val success =
                    coinManager.spendCoins(
                        userId = userId,
                        actionLabel = actionLabel,
                        amount = amount,
                        linkedObjectId = linkedObjectId,
                    )

                if (success) {
                    // Update the linked object's coinDeducted flag
                    repositoryHelper.markCoinDeducted(linkedObjectId)

                    FirebaseCrashlytics.getInstance().log(
                        "CoinRetryWorker: Successfully completed coin deduction retry for $actionLabel",
                    )
                    return@withContext Result.success()
                } else {
                    FirebaseCrashlytics.getInstance().log(
                        "CoinRetryWorker: Coin deduction retry failed for $actionLabel, will retry later",
                    )
                    return@withContext Result.retry()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance().log(
                    "CoinRetryWorker: Exception during retry: ${e.message}",
                )
                return@withContext Result.retry()
            }
        }
}

/**
 * Helper class to update coinDeducted flags across different Parse objects
 */
class RepositoryHelper {
    suspend fun markCoinDeducted(objectId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // Try different Parse classes that might have coinDeducted field
                val classes = listOf("ChickenRecord", "Fowl", "TransferRequest", "VerificationRecord")

                for (className in classes) {
                    try {
                        val query = com.parse.ParseQuery.getQuery<com.parse.ParseObject>(className)
                        val obj = query.get(objectId)
                        obj.put("coinDeducted", true)
                        obj.save()

                        FirebaseCrashlytics.getInstance().log(
                            "RepositoryHelper: Successfully marked $className $objectId as coinDeducted",
                        )
                        return@withContext true
                    } catch (e: com.parse.ParseException) {
                        // Object not found in this class, try next
                        continue
                    }
                }

                FirebaseCrashlytics.getInstance().log(
                    "RepositoryHelper: Could not find object $objectId in any known class",
                )
                return@withContext false
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext false
            }
        }
}
