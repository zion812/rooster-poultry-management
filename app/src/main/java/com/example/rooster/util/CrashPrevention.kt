package com.example.rooster.util

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Comprehensive crash prevention and error handling utility
 * for the Rooster Poultry Management Application
 */
object CrashPrevention {
    private const val TAG = "CrashPrevention"

    // Safe coroutine scope with exception handling
    private val safeScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.Main +
                CoroutineExceptionHandler { _, throwable ->
                    handleException("CoroutineScope", throwable)
                },
        )

    /**
     * Execute a block of code safely with comprehensive error handling
     */
    fun safeExecute(operation: String, block: () -> Unit): Unit? {
        return try {
            block()
        } catch (e: Exception) {
            handleException(operation, e)
            null
        }
    }

    /**
     * Execute a block of code safely with comprehensive error handling and return a result
     */
    fun <T> safeExecuteWithResult(
        operation: String,
        fallback: T,
        block: () -> T,
    ): T {
        return try {
            block()
        } catch (e: Exception) {
            handleException(operation, e)
            fallback
        }
    }

    /**
     * Execute async operations safely
     */
    fun safeAsync(
        operation: String,
        onError: ((Exception) -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        safeScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleException(operation, e)
                onError?.invoke(e)
            }
        }
    }

    /**
     * Safe navigation with null checking
     */
    fun safeNavigate(
        navController: androidx.navigation.NavController?,
        route: String,
        onError: (() -> Unit)? = null,
    ) {
        safeExecute("Navigation to $route") {
            navController?.navigate(route)
        } ?: onError?.invoke()
    }

    /**
     * Safe Parse object access
     */
    fun <T> safeParseGet(
        parseObject: com.parse.ParseObject?,
        key: String,
        defaultValue: T,
    ): T {
        return safeExecuteWithResult("Parse get $key", defaultValue) {
            when (defaultValue) {
                is String -> parseObject?.getString(key) as? T ?: defaultValue
                is Int -> parseObject?.getInt(key) as? T ?: defaultValue
                is Double -> parseObject?.getDouble(key) as? T ?: defaultValue
                is Boolean -> parseObject?.getBoolean(key) as? T ?: defaultValue
                else -> parseObject?.get(key) as? T ?: defaultValue
            }
        }
    }

    /**
     * Safe list access with bounds checking
     */
    fun <T> safeListGet(
        list: List<T>?,
        index: Int,
    ): T? {
        return safeExecuteWithResult("List access at index $index", null) {
            if (list != null && index >= 0 && index < list.size) {
                list[index]
            } else {
                null
            }
        }
    }

    /**
     * Safe context operations
     */
    fun <T> safeContext(
        context: Context?,
        operation: String,
        block: (Context) -> T,
    ): T? {
        return if (context != null) {
            safeExecute("Context $operation", block)
        } else {
            Log.w(TAG, "Context is null for operation: $operation")
            null
        }
    }

    /**
     * Handle exceptions comprehensively
     */
    internal fun handleException(
        operation: String,
        throwable: Throwable,
    ) {
        val errorMessage = "Error in $operation: ${throwable.message}"

        // Log locally
        Log.e(TAG, errorMessage, throwable)

        // Send to Crashlytics if available
        try {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("operation", operation)
                setCustomKey("error_type", throwable.javaClass.simpleName)
                recordException(throwable)
                log(errorMessage)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log to Crashlytics", e)
        }
    }

    /**
     * Network operation safety wrapper
     */
    fun <T> safeNetworkCall(
        operation: String,
        onSuccess: (T) -> Unit,
        onError: (Exception) -> Unit = {},
        block: () -> T,
    ) {
        safeAsync(operation, { onError(it) }) {
            try {
                val result = safeExecuteWithResult(operation, null as T?, block)
                result?.let { onSuccess(it) }
            } catch (e: Exception) {
                throw e // Re-throw to be caught by safeAsync
            }
        }
    }

    /**
     * UI operation safety wrapper
     */
    fun safeUI(
        operation: String,
        block: () -> Unit,
    ) {
        safeExecute("UI $operation", block)
    }

    /**
     * Database operation safety wrapper
     */
    fun <T> safeDatabase(
        operation: String,
        fallback: T? = null,
        block: () -> T,
    ): T? {
        return safeExecuteWithResult("Database $operation", fallback) {
            block()
        }
    }

    /**
     * Initialize crash prevention system
     */
    fun getInstance(context: Context) {
        safeExecute("CrashPrevention initialization", block = {
            // Set up global exception handler
            Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
                handleException("UncaughtException on ${thread.name}", exception)

                // Call the default handler to maintain normal crash behavior
                Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, exception)
            }

            Log.i(TAG, "CrashPrevention system initialized")
        })
    }
}

/**
 * Extension functions for safer operations
 */

// Safe string operations
fun String?.safeSubstring(
    start: Int,
    end: Int? = null,
): String {
    return CrashPrevention.safeExecuteWithResult("String substring", "") {
        if (this != null && start >= 0 && start < length) {
            if (end != null && end <= length && end > start) {
                substring(start, end)
            } else {
                substring(start)
            }
        } else {
            ""
        }
    }
}

// Safe collection operations
fun <T> Collection<T>?.safeIsEmpty(): Boolean {
    return this?.isEmpty() ?: true
}

fun <T> List<T>?.safeGet(index: Int): T? {
    return CrashPrevention.safeListGet(this, index)
}

// Safe numeric operations
fun String?.safeToInt(default: Int = 0): Int {
    return CrashPrevention.safeExecuteWithResult("String to Int conversion", default) {
        this?.toIntOrNull() ?: default
    }
}

fun String?.safeToDouble(default: Double = 0.0): Double {
    return CrashPrevention.safeExecuteWithResult("String to Double conversion", default) {
        this?.toDoubleOrNull() ?: default
    }
}

// Safe Parse operations
fun com.parse.ParseObject?.safeGetString(
    key: String,
    default: String = "",
): String {
    return CrashPrevention.safeParseGet(this, key, default)
}

fun com.parse.ParseObject?.safeGetInt(
    key: String,
    default: Int = 0,
): Int {
    return CrashPrevention.safeParseGet(this, key, default)
}

fun com.parse.ParseObject?.safeGetDouble(
    key: String,
    default: Double = 0.0,
): Double {
    return CrashPrevention.safeParseGet(this, key, default)
}

fun com.parse.ParseObject?.safeGetBoolean(
    key: String,
    default: Boolean = false,
): Boolean {
    return CrashPrevention.safeParseGet(this, key, default)
}
