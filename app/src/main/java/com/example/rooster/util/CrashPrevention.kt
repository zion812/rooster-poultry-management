package com.example.rooster.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashPrevention {
    fun safeExecute(operationName: String, operation: () -> Unit) {
        try {
            operation()
        } catch (e: Exception) {
            Log.e("CrashPrevention", "Error in operation: $operationName", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    inline fun <T> safeCall(operationName: String, defaultValue: T, operation: () -> T): T {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e("CrashPrevention", "Error in operation: $operationName", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            defaultValue
        }
    }
}