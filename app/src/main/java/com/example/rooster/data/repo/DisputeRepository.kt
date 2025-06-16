package com.example.rooster.data.repo

import com.example.rooster.data.model.DisputeRecord
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.FunctionCallback
import com.parse.ParseCloud
import com.parse.ParseException
import com.parse.ParseObject

object DisputeRepository {
    fun submitDispute(
        record: DisputeRecord,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        FirebaseCrashlytics.getInstance()
            .log("Dispute submit start: ${record.type}")
        // Call Parse Cloud Function 'submitDispute'
        val params =
            mapOf(
                "userId" to record.userId,
                "type" to record.type.name,
                "message" to record.message,
                "mediaUrls" to record.mediaUrls,
                "relatedOrderId" to record.relatedOrderId,
                "productId" to record.productId,
            )
        ParseCloud.callFunctionInBackground(
            "submitDispute",
            params,
            object : FunctionCallback<ParseObject> {
                override fun done(
                    result: ParseObject?,
                    e: ParseException?,
                ) {
                    if (e == null && result != null) {
                        FirebaseCrashlytics.getInstance().log("Dispute submit success: ${result.objectId}")
                        onSuccess()
                    } else {
                        e?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        onError(e ?: Exception("Unknown error"))
                    }
                }
            },
        )
    }

    fun fetchDisputes(
        onLoaded: (List<DisputeRecord>) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        // stub for admin screen
        onLoaded(emptyList())
    }
}
