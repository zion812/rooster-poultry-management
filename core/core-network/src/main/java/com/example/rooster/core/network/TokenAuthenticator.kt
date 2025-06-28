package com.example.rooster.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider // Use the interface
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Do not retry if the request already failed with an Authorization header (to prevent loops)
        // This simple check might need to be more sophisticated, e.g. by comparing the token.
        if (response.request.header("Authorization") != null && response.priorResponse != null) {
            Timber.w("Authentication failed even after attaching a token. Not retrying.")
            return null // Give up if we've already tried to authenticate.
        }

        Timber.d("Authentication required. Attempting to refresh token.")

        val newToken: String? = runBlocking { // Bridge suspend call to synchronous Authenticator
            tokenProvider.getToken(forceRefresh = true)
        }

        return if (newToken != null) {
            Timber.d("Token refreshed successfully. Retrying request.")
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            Timber.w("Failed to refresh token. Cannot authenticate.")
            null // Token refresh failed
        }
    }
}
