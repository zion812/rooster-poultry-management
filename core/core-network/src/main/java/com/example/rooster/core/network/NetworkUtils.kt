package com.example.rooster.core.network

import com.example.rooster.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Enterprise-grade network error handling
 * Converts network exceptions to user-friendly error messages
 */

sealed class NetworkError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class NoInternet(override val cause: Throwable? = null) :
        NetworkError("No internet connection available", cause)

    data class Timeout(override val cause: Throwable? = null) :
        NetworkError("Request timed out", cause)

    data class ServerError(val code: Int, override val cause: Throwable? = null) :
        NetworkError("Server error: $code", cause)

    data class ClientError(val code: Int, override val cause: Throwable? = null) :
        NetworkError("Client error: $code", cause)

    data class UnknownError(override val cause: Throwable? = null) :
        NetworkError("Unknown network error", cause)
}

/**
 * Converts throwable to appropriate NetworkError
 */
fun Throwable.toNetworkError(): NetworkError {
    return when (this) {
        is UnknownHostException -> NetworkError.NoInternet(this)
        is SocketTimeoutException -> NetworkError.Timeout(this)
        is IOException -> NetworkError.NoInternet(this)
        is HttpException -> {
            when (code()) {
                in 400..499 -> NetworkError.ClientError(code(), this)
                in 500..599 -> NetworkError.ServerError(code(), this)
                else -> NetworkError.UnknownError(this)
            }
        }

        else -> NetworkError.UnknownError(this)
    }
}

/**
 * Extension to safely execute network calls
 */
suspend fun <T> safeNetworkCall(
    call: suspend () -> T
): Result<T> {
    return try {
        Result.Success(call())
    } catch (throwable: Throwable) {
        Result.Error(throwable.toNetworkError())
    }
}

/**
 * Extension for Flow to handle network errors
 */
fun <T> Flow<T>.asNetworkResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it.toNetworkError())) }
}

/**
 * Network connectivity checker
 */
interface NetworkConnectivityChecker {
    fun isConnected(): Boolean
    fun isConnectedToWifi(): Boolean
    fun isConnectedToMobile(): Boolean
    fun getConnectionType(): ConnectionType
}

enum class ConnectionType {
    WIFI,
    MOBILE,
    ETHERNET,
    NONE
}

/**
 * Retry mechanism for failed network calls
 */
class NetworkRetryStrategy {
    companion object {
        const val DEFAULT_MAX_RETRIES = 3
        const val DEFAULT_INITIAL_DELAY = 1000L
        const val DEFAULT_BACKOFF_MULTIPLIER = 2.0
    }

    suspend fun <T> retryWithBackoff(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        initialDelay: Long = DEFAULT_INITIAL_DELAY,
        backoffMultiplier: Double = DEFAULT_BACKOFF_MULTIPLIER,
        shouldRetry: (Exception) -> Boolean = { it is NetworkError.Timeout || it is NetworkError.NoInternet },
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay

        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1 || !shouldRetry(e)) {
                    throw e
                }
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * backoffMultiplier).toLong()
            }
        }

        // This should never be reached, but compiler requires it
        throw IllegalStateException("Retry strategy failed")
    }
}

/**
 * Request/Response interceptor for analytics and monitoring
 */
class NetworkAnalyticsInterceptor : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        try {
            val response = chain.proceed(request)
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // Log successful request
            logNetworkRequest(
                url = request.url.toString(),
                method = request.method,
                responseCode = response.code,
                duration = duration,
                isSuccess = true
            )

            return response
        } catch (e: Exception) {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // Log failed request
            logNetworkRequest(
                url = request.url.toString(),
                method = request.method,
                responseCode = null,
                duration = duration,
                isSuccess = false,
                error = e.message
            )

            throw e
        }
    }

    private fun logNetworkRequest(
        url: String,
        method: String,
        responseCode: Int?,
        duration: Long,
        isSuccess: Boolean,
        error: String? = null
    ) {
        // Log to analytics service (Firebase Analytics, Crashlytics, etc.)
        val logData = mapOf(
            "url" to url,
            "method" to method,
            "response_code" to responseCode,
            "duration_ms" to duration,
            "is_success" to isSuccess,
            "error" to error
        )

        // TODO: Implement actual analytics logging
        println("Network Analytics: $logData")
    }
}