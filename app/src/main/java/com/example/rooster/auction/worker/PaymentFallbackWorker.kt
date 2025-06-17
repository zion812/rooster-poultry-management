package com.example.rooster.auction.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.rooster.auction.repo.AuctionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PaymentFallbackWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val repo: AuctionRepository,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val auctionId = inputData.getString("auctionId") ?: return Result.failure()
            val amount = inputData.getDouble("amount", 0.0)
            return if (repo.placeBid(auctionId, amount).isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        }

        companion object {
            fun schedule(
                context: Context,
                auctionId: String,
                amount: Double,
            ) {
                val data = workDataOf("auctionId" to auctionId, "amount" to amount)
                WorkManager.getInstance(context)
                    .enqueue(
                        OneTimeWorkRequestBuilder<PaymentFallbackWorker>()
                            .setInputData(data)
                            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                            .build(),
                    )
            }
        }
    }
