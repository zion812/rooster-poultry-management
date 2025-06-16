package com.example.rooster.worker

import android.content.Context
import androidx.work.*
import com.example.rooster.data.model.*
import com.parse.ParseQuery
import java.util.*
import java.util.concurrent.TimeUnit

class LifecycleWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()

            // Eggs → Chicks after 21 days
            ParseQuery.getQuery(EggBatch::class.java)
                .whereLessThanOrEqualTo("laidDate", Date(now - 21L * 24 * 3600 * 1000))
                .find().forEach { egg ->
                    ChickBatch().apply {
                        eggBatchId = egg.objectId
                        hatchDate = Date()
                        chickCount = egg.eggCount
                    }.saveInBackground()
                }

            // Chicks → update cycle status after 14 days
            ParseQuery.getQuery(ChickBatch::class.java)
                .whereLessThanOrEqualTo("hatchDate", Date(now - 14L * 24 * 3600 * 1000))
                .find().forEach { chick ->
                    BreedingCycle().apply {
                        objectId = chick.eggBatchId
                        status = "BROODED"
                    }.saveInBackground()
                }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun getInstance(context: Context) {
            val req =
                PeriodicWorkRequestBuilder<LifecycleWorker>(1, TimeUnit.DAYS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build(),
                    )
                    .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("lifecycle", ExistingPeriodicWorkPolicy.KEEP, req)
        }
    }
}
