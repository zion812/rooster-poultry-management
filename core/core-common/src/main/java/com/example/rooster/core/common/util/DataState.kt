package com.example.rooster.core.common.util

import java.time.Duration // Using java.time.Duration as suggested

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class DataState<out T> {
    data class Success<T>(val data: T, val isFromCache: Boolean = false, val isStale: Boolean = false) : DataState<T>()
    data class Error<T>(
        val exception: Throwable,
        val cachedData: T? = null, // Potentially show stale data
        val cacheAge: Duration? = null, // How old the cached data is
        val message: String? = null // Custom error message
    ) : DataState<T>()
    data class Loading<T>(val cachedData: T? = null) : DataState<T>() // Can also show cached data while loading fresh

    /**
     * Returns the underlying data if this is a Success or if cachedData is available in Loading/Error states.
     */
    fun getUnderlyingData(): T? {
        return when (this) {
            is Success -> data
            is Error -> cachedData
            is Loading -> cachedData
        }
    }
}
