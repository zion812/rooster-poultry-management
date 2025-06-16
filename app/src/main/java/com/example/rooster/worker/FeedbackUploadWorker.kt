package com.example.rooster.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.data.model.UATFeedback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FeedbackUploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val KEY_FEEDBACK_DATA_JSON = "FEEDBACK_DATA_JSON"
        private const val TAG = "FeedbackUploadWorker"
    }

    override suspend fun doWork(): Result {
        val feedbackDataJson =
            inputData.getString(KEY_FEEDBACK_DATA_JSON) ?: return Result.failure()
        Log.d(TAG, "Received feedback data for upload: $feedbackDataJson")

        return try {
            val typeToken = object : TypeToken<Map<String, Any?>>() {}.type
            val feedbackMap: Map<String, Any?> = Gson().fromJson(feedbackDataJson, typeToken)

            // Get photo upload job IDs from feedback data
            val photoUploadJobIds = feedbackMap["photoUploadJobIds"] as? List<String> ?: emptyList()
            Log.d(TAG, "Photo upload job IDs to resolve: $photoUploadJobIds")

            // Resolve job IDs to uploaded ParseFile URLs using the local Room queue
            val dao = com.example.rooster.App.getPhotoUploadDao()
            val finalPhotoUrls = mutableListOf<String>()
            var allUrlsReady = true

            for (jobId in photoUploadJobIds) {
                val entity = dao.getById(jobId)
                val url = entity?.parseFileUrl
                if (url.isNullOrEmpty()) {
                    allUrlsReady = false
                    break
                } else {
                    finalPhotoUrls.add(url)
                }
            }

            if (!allUrlsReady) {
                Log.d(TAG, "Not all photo uploads are completed yet. Retrying later.")
                return Result.retry()
            }

            Log.d(TAG, "Resolved final photo URLs: $finalPhotoUrls")

            val uatFeedback =
                UATFeedback().apply {
                    category = feedbackMap["category"] as? String
                    priority = feedbackMap["priority"] as? String
                    message = feedbackMap["message"] as? String
                    starRating = (feedbackMap["starRating"] as? Double)?.toInt() ?: 0
                    deviceInfo = feedbackMap["deviceInfo"] as? String
                    networkQuality = feedbackMap["networkQuality"] as? String
                    photoUrls = finalPhotoUrls
                    userId = feedbackMap["userId"] as? String
                    appVersion = feedbackMap["appVersion"] as? String
                }

            // Synchronously save the feedback object
            uatFeedback.save()
            Log.i(TAG, "UATFeedback submitted successfully to Parse: ${uatFeedback.objectId}")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading feedback: ${e.message}", e)
            Result.retry()
        }
    }
}
