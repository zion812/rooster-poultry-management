package com.example.rooster.core.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface TokenProvider {
    suspend fun getToken(): String?
}

@Singleton
class FirebaseTokenProvider @Inject constructor() : TokenProvider {
    override suspend fun getToken(): String? {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return try {
            user?.getIdToken(false)?.await()?.token // false to not force refresh if token is still valid
        } catch (e: Exception) {
            Timber.e(e, "Error fetching Firebase ID token")
            null
        }
    }
}
