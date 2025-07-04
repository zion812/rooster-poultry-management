package com.example.rooster.core.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Replace this with a proper token provider that securely retrieves the auth token.
// This might involve injecting a data store or an auth state manager.
// For example, using runBlocking with a suspend function from a repository:
// class AuthInterceptor @Inject constructor(private val tokenRepository: TokenRepository) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val token = runBlocking { tokenRepository.getAuthToken() } // Be cautious with runBlocking here
//        val requestBuilder = chain.request().newBuilder()
//        token?.let {
//            requestBuilder.addHeader("Authorization", "Bearer $it")
//        }
//        return chain.proceed(requestBuilder.build())
//    }
// }

/**
 * Interceptor to add Authorization token to requests.
 * The `tokenProvider` lambda should be implemented to retrieve the current valid token.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: () -> String? // Lambda to provide the token
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        val requestBuilder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        // else: No token available, proceed without Authorization header.
        // The server will then reject if the endpoint is protected.

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
