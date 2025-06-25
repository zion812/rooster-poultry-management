package com.example.rooster.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * A worker that is aware of the application's lifecycle.
 * This worker is currently a placeholder and does not perform any work.
 */
class LifecycleWorker(appContext: Context, workerParams: WorkerParameters) :
    ListenableWorker(appContext, workerParams) {
    override fun startWork(): ListenableFuture<Result> {
        // This is a placeholder implementation.
        // TODO: Implement the actual work to be performed by this worker.
        return Futures.immediateFuture(Result.success())
    }
}
