package com.example.rooster.core.network.repository

import com.example.rooster.core.common.domain.repository.TokenRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseCloud
import com.parse.ParseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ParseTokenRepositoryImpl @Inject constructor() : TokenRepository {

    override suspend fun loadTokenBalance(onResult: (Int) -> Unit) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser == null) {
            onResult(0)
            return
        }
        try {
            // Fetch the latest user data, especially if tokenBalance can be updated by other means
            currentUser.fetch()
            val balance = currentUser.getInt("tokenBalance") // Assuming 'tokenBalance' field on User class
            onResult(balance)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onResult(0) // Default to 0 on error
        }
    }

    override suspend fun deductTokens(count: Int, onResult: (Boolean) -> Unit) {
        val params = HashMap<String, Any>()
        params["count"] = count

        try {
            val result = suspendCancellableCoroutine<Map<*, *>> { continuation ->
                ParseCloud.callFunctionInBackground<Map<*, *>>("deductUserTokens", params) { response, e ->
                    if (e == null) {
                        if (continuation.isActive) continuation.resume(response ?: emptyMap<Any,Any>())
                    } else {
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }
                }
            }
            // Assuming cloud function returns { success: true, newBalance: ... } or throws Parse.Error
            val success = result["success"] as? Boolean ?: false
            if (success) {
                // Optionally, update local user object or trigger a balance refresh
            }
            onResult(success)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onResult(false)
        }
    }

    override suspend fun addTokens(count: Int, onResult: (Boolean) -> Unit) {
        val params = HashMap<String, Any>()
        params["count"] = count
        // Optionally add source: params["source"] = "app_purchase"

        try {
            val result = suspendCancellableCoroutine<Map<*,*>> { continuation ->
                ParseCloud.callFunctionInBackground<Map<*, *>>("addUserTokens", params) { response, e ->
                     if (e == null) {
                        if (continuation.isActive) continuation.resume(response ?: emptyMap<Any,Any>())
                    } else {
                        if (continuation.isActive) continuation.resumeWithException(e)
                    }
                }
            }
            val success = result["success"] as? Boolean ?: false
            if (success) {
                // Optionally, update local user object or trigger a balance refresh
            }
            onResult(success)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onResult(false)
        }
    }
}
