package com.example.rooster.data.authprovider

import com.example.rooster.core.common.user.UserIdProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserIdProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserIdProvider {

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override val currentUserIdFlow: Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        // Emit the initial state
        trySend(firebaseAuth.currentUser?.uid)

        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }
}
