package com.example.rooster.services

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseUser
import com.parse.SaveCallback

/**
 * TokenService handles purchase, deduction, and refund of tokens stored on ParseUser.
 * Tokens are stored in the ParseUser field "tokenBalance" (Int).
 */
object TokenService {
    private const val TOKEN_FIELD = "tokenBalance"

    /**
     * Loads the current user's token balance.
     * Returns 0 if not set or user not logged in.
     */
    fun loadTokenBalance(onResult: (Int) -> Unit) {
        val user = ParseUser.getCurrentUser()
        val balance = user?.getInt(TOKEN_FIELD) ?: 0
        onResult(balance)
    }

    /**
     * Deducts one token from the user's balance. Fails silently if balance is insufficient.
     */
    fun deductToken(onResult: (Boolean) -> Unit) {
        val user = ParseUser.getCurrentUser()
        if (user == null) {
            onResult(false)
            return
        }
        val current = user.getInt(TOKEN_FIELD)
        if (current <= 0) {
            onResult(false)
            return
        }
        val updated = current - 1
        user.put(TOKEN_FIELD, updated)
        user.saveInBackground(
            SaveCallback { e ->
                if (e != null) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    onResult(false)
                } else {
                    onResult(true)
                }
            },
        )
    }

    /**
     * Refunds one token to the specified userId. Intended for token return on timeout.
     */
    fun refundToken(
        userId: String,
        onResult: (Boolean) -> Unit,
    ) {
        // Query user by ID
        val query = ParseUser.getQuery()
        query.getInBackground(userId) { user, e ->
            if (e != null || user == null) {
                FirebaseCrashlytics.getInstance().recordException(e ?: Exception("User not found"))
                onResult(false)
            } else {
                val current = user.getInt(TOKEN_FIELD)
                user.put(TOKEN_FIELD, current + 1)
                user.saveInBackground(
                    SaveCallback { saveError ->
                        if (saveError != null) {
                            FirebaseCrashlytics.getInstance().recordException(saveError)
                            onResult(false)
                        } else {
                            onResult(true)
                        }
                    },
                )
            }
        }
    }

    /**
     * Purchase tokens: increments tokenBalance by [count].
     * The caller is responsible for handling payment logic separately.
     */
    fun addTokens(
        count: Int,
        onResult: (Boolean) -> Unit,
    ) {
        val user = ParseUser.getCurrentUser()
        if (user == null) {
            onResult(false)
            return
        }
        val current = user.getInt(TOKEN_FIELD)
        user.put(TOKEN_FIELD, current + count)
        user.saveInBackground(
            SaveCallback { e ->
                if (e != null) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    onResult(false)
                } else {
                    onResult(true)
                }
            },
        )
    }
}
