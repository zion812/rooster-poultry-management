package com.example.rooster.core.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface TokenProvider {
    /**
     * Retrieves the current Firebase ID token.
     * This might return a cached token if it's still valid.
     * @param forceRefresh If true, forces token refresh.
     * @return The ID token string, or null if fetching fails or no user is signed in.
     */
    suspend fun getToken(forceRefresh: Boolean = false): String?
}

@Singleton
class FirebaseTokenProvider @Inject constructor(private val firebaseAuth: FirebaseAuth) : TokenProvider {
    override suspend fun getToken(forceRefresh: Boolean): String? {
        val user: FirebaseUser = firebaseAuth.currentUser ?: return null
        return try {
            user.getIdToken(forceRefresh).await()?.token
        } catch (e: Exception) {
            Timber.e(e, "Error fetching Firebase ID token (forceRefresh: $forceRefresh)")
            // Attempt to sign out the user if token refresh fails due to certain critical auth errors
            if (e is com.google.firebase.FirebaseApiNotAvailableException || e is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                 Timber.w(e, "Critical Firebase auth error during token refresh, signing out user.")
                 // Potentially sign out user here if desired, or notify a higher level service
                 // For now, just log and return null
            }
            null
        }
    }
}
