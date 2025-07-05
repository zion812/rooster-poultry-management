package com.example.rooster.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enterprise-grade extension functions
 * Common utilities used across all modules
 */

// String Extensions
fun String?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

fun String?.orDefault(default: String = ""): String = this ?: default

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

// Date Extensions
fun Long.toFormattedDate(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

fun Date.toFormattedString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

// Network Extensions
@SuppressLint("MissingPermission")
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(network)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } else {
        @Suppress("DEPRECATION")
        connectivityManager?.activeNetworkInfo?.isConnected == true
    }
}

// Safe execution extensions
suspend fun <T> safeApiCall(
    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): Result<T> {
    return withContext(dispatcher) {
        try {
            Result.Success(apiCall())
        } catch (throwable: Throwable) {
            Result.Error(throwable)
        }
    }
}

// Collection Extensions
fun <T> List<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()

inline fun <T, R> List<T>.mapSafe(transform: (T) -> R?): List<R> {
    return mapNotNull(transform)
}

// Numeric Extensions
fun Double.formatCurrency(currencySymbol: String = "₹"): String {
    return "$currencySymbol%.2f".format(this)
}

fun Int.toOrdinal(): String {
    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 == 1 -> "${this}st"
        this % 10 == 2 -> "${this}nd"
        this % 10 == 3 -> "${this}rd"
        else -> "${this}th"
    }
}

// Validation Extensions
fun String.isValidEmail(): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return matches(emailPattern.toRegex())
}

fun String.isValidPhoneNumber(): Boolean {
    val phonePattern = "^[6-9]\\d{9}$" // Indian phone number pattern
    return matches(phonePattern.toRegex())
}

// Resource Extensions
fun Int.toDp(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Int.toPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}
