package com.example.rooster.core.common

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.IOException
import java.util.concurrent.TimeoutException

// TODO: Replace hardcoded strings with R.string references for localization

fun Throwable.toUserFriendlyMessage(context: Context): String {
    // It's better if core-common doesn't directly depend on core-network's NetworkError.
    // For now, we'll check for common exception types.
    // A more robust solution would involve a domain-level ErrorEntity that different layers can map to.

    return when (this) {
        is IOException -> {
            // Could be a general network issue or file issue.
            context.getString(android.R.string.httpErrorBadUrl)
        }
        is TimeoutException -> "The request timed out. Please try again." // R.string.error_timeout
        is FirebaseAuthException -> when (this.errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials. Please try again." // R.string.error_auth_invalid_credentials
            "ERROR_USER_NOT_FOUND" -> "User not found." // R.string.error_auth_user_not_found
            "ERROR_WRONG_PASSWORD" -> "Incorrect password." // R.string.error_auth_wrong_password
            "ERROR_USER_DISABLED" -> "This user account has been disabled." // R.string.error_auth_user_disabled
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email address is already in use." // R.string.error_auth_email_in_use
            "ERROR_WEAK_PASSWORD" -> "The password is too weak." // R.string.error_auth_weak_password
            // TODO: Add more FirebaseAuthException error codes
            else -> this.localizedMessage ?: "Authentication failed. Please try again." // R.string.error_auth_failed
        }
        is FirebaseFirestoreException -> when (this.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> "Cannot connect to the server. Please check your connection." // R.string.error_firestore_unavailable
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You don't have permission for this action." // R.string.error_firestore_permission_denied
            FirebaseFirestoreException.Code.NOT_FOUND -> "The requested item was not found." // R.string.error_firestore_not_found
            FirebaseFirestoreException.Code.ALREADY_EXISTS -> "This item already exists." // R.string.error_firestore_already_exists
            // TODO: Add more FirebaseFirestoreException codes
            else -> this.localizedMessage ?: "A database error occurred. Please try again." // R.string.error_firestore_general
        }
        is FirebaseException -> { // Catch-all for other Firebase exceptions
            this.localizedMessage ?: "A Firebase service error occurred." // R.string.error_firebase_general
        }
        else -> this.localizedMessage ?: "Unknown error"
    }
}
