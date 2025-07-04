package com.example.rooster.core.auth.data.repository

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.Result // Import the correct Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [AuthRepository] that uses Firebase Authentication and Firestore
 * as data sources.
 *
 * This repository handles all authentication-related operations such as sign-in, sign-up,
 * sign-out, password reset, and profile management. It also provides a flow for observing
 * the current authenticated user.
 *
 * @property firebaseAuth Instance of [FirebaseAuth] for authentication operations.
 * @property firestore Instance of [FirebaseFirestore] for user profile data storage.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
    // Consider injecting a CoroutineDispatcher for background tasks if not using GlobalScope in callbackFlow
    // @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    private companion object {
        /** Firestore collection name for storing user profiles. */
        const val USERS_COLLECTION = "users"
    }

    /**
     * Signs in a user with the given email and password using Firebase Authentication.
     * Upon successful authentication, fetches the user's profile from Firestore.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return [Result.Success] with the [User] object if sign-in and profile fetch are successful.
     *         [Result.Error] if authentication fails or the user profile cannot be retrieved.
     *         If the user is authenticated but their profile is missing in Firestore, they are signed out
     *         and an error is returned to prevent inconsistent states.
     */
    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.Error(Exception("Firebase user not found after sign in"))

            val user = fetchUserDetails(firebaseUser)
            if (user != null) {
                Result.Success(user)
            } else {
                // If user exists in Auth but not in Firestore, this is an inconsistent state.
                // For now, sign them out and return error. Or create a basic profile.
                // Let's choose to return an error indicating profile fetch failure.
                firebaseAuth.signOut() // Sign out inconsistent user
                Result.Error(Exception("User profile not found in database."))
            }
        } catch (e: FirebaseAuthException) {
            Timber.e(e, "Sign in failed")
            Result.Error(Exception("Sign in failed: ${mapAuthExceptionMessage(e)}", e))
        } catch (e: Exception) {
            Timber.e(e, "Sign in failed with generic exception")
            Result.Error(Exception("Sign in failed: ${e.message}", e))
        }
    }

    /**
     * Creates a new user account with the given email and password using Firebase Authentication.
     * After successful account creation, a user profile is created in Firestore.
     * A verification email is sent to the user on a best-effort basis.
     *
     * @param email The new user's email.
     * @param password The new user's password.
     * @param displayName The new user's display name.
     * @param role The [UserRole] for the new user.
     * @param phoneNumber Optional phone number for the new user.
     * @return [Result.Success] with the newly created [User] object.
     *         [Result.Error] if account creation or profile storage fails.
     */
    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.Error(Exception("Firebase user not found after sign up"))

            // Send email verification
            try {
                firebaseUser.sendEmailVerification().await()
                Timber.d("Verification email sent to $email")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification email for $email")
                // Non-fatal, proceed with user creation
            }

            val newUser = User(
                id = firebaseUser.uid,
                email = email,
                displayName = displayName,
                role = role,
                phoneNumber = phoneNumber ?: "",
                isEmailVerified = firebaseUser.isEmailVerified, // Will be false initially
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
                // Other fields from User model will use default values
            )

            firestore.collection(USERS_COLLECTION).document(newUser.id).set(newUser).await()
            Result.Success(newUser)
        } catch (e: FirebaseAuthException) {
            Timber.e(e, "Sign up failed")
            Result.Error(Exception("Sign up failed: ${mapAuthExceptionMessage(e)}", e))
        } catch (e: Exception) {
            Timber.e(e, "Sign up failed with generic exception")
            Result.Error(Exception("Sign up failed: ${e.message}", e))
        }
    }

    /**
     * Signs out the currently authenticated user from Firebase.
     * This is a local operation and typically does not fail.
     * Errors are logged if they occur.
     */
    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Timber.e(e, "Error during sign out")
            // Sign out is typically a local operation and shouldn't throw often,
            // but good to log if it does. No Result needed as per interface.
        }
    }

    /**
     * Sends a password reset email to the specified email address via Firebase Authentication.
     *
     * @param email The email address to receive the password reset link.
     * @return [Result.Success] if the request to send the email was successful (Firebase usually
     *         succeeds even if the email doesn't exist to prevent account enumeration).
     *         [Result.Error] if the request fails (e.g., invalid email format).
     */
    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Timber.e(e, "Password reset failed for $email")
            Result.Error(Exception("Password reset failed: ${mapAuthExceptionMessage(e)}", e))
        } catch (e: Exception) {
            Timber.e(e, "Password reset failed for $email with generic exception")
            Result.Error(Exception("Password reset failed: ${e.message}", e))
        }
    }

    /**
     * Provides a [Flow] that emits the current [User] object when the authentication state changes.
     * Emits `null` if no user is authenticated.
     * The user object is fetched from Firestore upon authentication.
     *
     * Note: The coroutine launched within this `callbackFlow` for fetching user details uses
     * `kotlinx.coroutines.GlobalScope`. This is marked with a TODO for future improvement,
     * potentially by injecting a repository-specific scope or using a different pattern for
     * managing the lifecycle of this background operation.
     *
     * @return A [Flow] emitting the current [User] or `null`.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                // Fetch full user profile from Firestore when auth state changes
                // TODO: Replace GlobalScope with a more appropriate scope (e.g., injected, or flow's scope if safe)
                // This coroutine is responsible for fetching user details from Firestore.
                // Its lifecycle should ideally be tied to the flow's collection.
                val job = kotlinx.coroutines.GlobalScope.launch {
                    trySend(fetchUserDetails(firebaseUser))
                }
                // It's important to consider how `job` cancellation is handled if the listener is removed
                // or the flow is cancelled, to prevent leaks or unnecessary work.
                // `awaitClose` handles listener removal, but not explicitly this launched job.
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            Timber.d("Removing AuthStateListener from getCurrentUser flow.")
            firebaseAuth.removeAuthStateListener(authStateListener)
            // Consider cancelling any ongoing jobs related to this flow if `GlobalScope.launch` is still used.
        }
    }

    /**
     * Updates the profile of the specified [user] in Firestore and Firebase Authentication (display name only).
     * The user's `updatedAt` timestamp is also updated.
     *
     * @param user The [User] object with updated information. The ID of this user must match the
     *             currently authenticated Firebase user's UID.
     * @return [Result.Success] with the updated [User] object (with new `updatedAt` timestamp).
     *         [Result.Error] if the user is not signed in, the user ID does not match, or the update fails.
     *         Note: Updating email or phone number in Firebase Auth is a more complex process requiring
     *         re-authentication or specific verification flows and is not handled by this method.
     */
    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return Result.Error(Exception("User not signed in"))
            if (currentUser.uid != user.id) {
                return Result.Error(Exception("Cannot update profile for a different user"))
            }

            // Update Firestore document
            firestore.collection(USERS_COLLECTION).document(user.id)
                .set(user.copy(updatedAt = System.currentTimeMillis()), SetOptions.merge())
                .await()

            // Update FirebaseUser's display name if changed (email/password updates are more complex)
            if (currentUser.displayName != user.displayName) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(user.displayName)
                    .build()
                currentUser.updateProfile(profileUpdates).await()
            }
            // Note: Updating email or phone number in Firebase Auth requires re-authentication or specific flows.
            // For now, this method primarily updates Firestore and basic FirebaseUser profile fields.

            Result.Success(user.copy(updatedAt = System.currentTimeMillis()))
        } catch (e: Exception) {
            Timber.e(e, "Failed to update profile for user ${user.id}")
            Result.Error(Exception("Profile update failed: ${e.message}", e))
        }
    }

    /**
     * Checks if a user is currently signed in with Firebase Authentication.
     * @return `true` if a user is signed in, `false` otherwise.
     */
    override suspend fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Sends an email verification link to the currently authenticated Firebase user.
     * @return [Result.Success] if the verification email was successfully sent.
     *         [Result.Error] if no user is signed in or if sending the email fails.
     */
    override suspend fun sendCurrentUserEmailVerification(): Result<Unit> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.Error(Exception("No current user to send verification email."))
            firebaseUser.sendEmailVerification().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send current user email verification")
            Result.Error(Exception("Failed to send verification email: ${e.message}", e))
        }
    }

    /**
     * Reloads the current Firebase user's state from the Firebase servers.
     * This is useful for refreshing properties like [FirebaseUser.isEmailVerified].
     * After reloading the Firebase user, it fetches the updated user profile from Firestore.
     *
     * @return [Result.Success] with the updated [User] object (or `null` if no user is signed in
     *         or if the user becomes null after reload).
     *         [Result.Error] if reloading or fetching the profile fails.
     */
    override suspend fun reloadCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return Result.Success(null) // No user signed in
            firebaseUser.reload().await()
            // After reload, get the potentially updated FirebaseUser instance
            val reloadedFirebaseUser = firebaseAuth.currentUser ?: return Result.Success(null) // Should not happen if reload was successful

            // Fetch updated details from Firestore as role or other details might change server-side too
            val userDetails = fetchUserDetails(reloadedFirebaseUser)
            Result.Success(userDetails)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reload current user")
            Result.Error(Exception("Failed to reload user data: ${e.message}", e))
        }
    }

    /**
     * Fetches user profile details from Firestore for a given [FirebaseUser].
     * This method is used internally to construct the domain [User] model.
     * It ensures that properties like `isEmailVerified` and `phoneNumber` are correctly
     * sourced from the authoritative [FirebaseUser] object, while other details come from Firestore.
     *
     * @param firebaseUser The [FirebaseUser] whose profile is to be fetched.
     * @return The corresponding [User] domain model object, or `null` if the profile
     *         is not found in Firestore or if an error occurs.
     */
    private suspend fun fetchUserDetails(firebaseUser: FirebaseUser): User? {
        return try {
            val documentSnapshot = firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Important: Ensure the User data class has a no-argument constructor
                // or use @PropertyName for fields if names differ from Firestore.
                // kotlinx.serialization with Firestore requires custom adapters or careful field naming.
                // For now, assuming toObject works with the consolidated User model.
                // If User model uses @Serializable, ensure Firestore rules and data structure match.
                // Default values in User constructor are good for toObject().
                documentSnapshot.toObject(User::class.java)?.copy(
                    // Ensure these critical fields are from the FirebaseUser source of truth
                    isEmailVerified = firebaseUser.isEmailVerified,
                    phoneNumber = firebaseUser.phoneNumber ?: "" // Update phone from FirebaseUser if available
                )
            } else {
                Timber.w("User profile not found in Firestore for UID: ${firebaseUser.uid}. A new basic profile might need to be created or this is an error state.")
                // Optionally create a basic profile here if that's the desired behavior
                // For now, returning null to indicate data inconsistency if profile is expected.
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user details from Firestore for UID: ${firebaseUser.uid}")
            null
        }
    }

    /**
     * Maps Firebase [FirebaseAuthException] error codes to more user-friendly messages.
     * This is used to provide clearer error feedback to the user or for logging.
     *
     * @param e The [FirebaseAuthException] to map.
     * @return A user-friendly error message string.
     */
    private fun mapAuthExceptionMessage(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email format."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use a stronger password."
            // TODO: Add more specific mappings for other FirebaseAuthException error codes as needed.
            else -> e.localizedMessage ?: "An unknown authentication error occurred."
        }
    }
}
