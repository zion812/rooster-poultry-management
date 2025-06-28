package com.example.rooster.data.datasources.firebase

import com.example.rooster.core.auth.datasources.UserDataSource
import com.example.rooster.core.auth.model.AuthUser
import com.example.rooster.core.auth.model.UserProfileData
import com.example.rooster.core.common.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserDataSource {

    private val usersCollection = firestore.collection("users")

    companion object {
        // Firestore field keys for UserProfileData, matching UserProfileData domain model
        // Some of these might be directly on AuthUser if Firebase Auth profile is used,
        // others will be in the Firestore user document.
        const val KEY_USER_ID = "userId" // Should match AuthUser.uid
        const val KEY_EMAIL = "email"
        const val KEY_DISPLAY_NAME = "displayName"
        const val KEY_PROFILE_PICTURE_URL = "profilePictureUrl"
        const val KEY_BIO = "bio"
        const val KEY_LOCATION = "location"
        const val KEY_FARM_NAME = "farmName"
        const val KEY_INTERESTS = "interests"
        const val KEY_PHONE_NUMBER = "phoneNumber"
        const val KEY_FOLLOWER_COUNT = "followerCount"
        const val KEY_FOLLOWING_COUNT = "followingCount"
        const val KEY_POST_COUNT = "postCount"
        const val KEY_LAST_ACTIVE_TIMESTAMP = "lastActiveTimestamp"
        const val KEY_JOIN_DATE_TIMESTAMP = "joinDateTimestamp"
        const val KEY_IS_VERIFIED_FARMER = "isVerifiedFarmer"
        const val KEY_IS_ENTHUSIAST = "isEnthusiast"
        const val KEY_IS_KYC_VERIFIED = "isKycVerified"
        const val KEY_ADDITIONAL_PROPERTIES = "additionalProperties"
    }

    override suspend fun signUp(email: String, password: String, initialProfileData: Map<String, Any?>?): Result<AuthUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Set display name on FirebaseAuth user profile if provided
                val displayName = initialProfileData?.get(KEY_DISPLAY_NAME) as? String
                if (displayName != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).await()
                }

                // Create user profile document in Firestore
                val userProfile = UserProfileData(
                    userId = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = displayName ?: firebaseUser.displayName, // Use updated display name
                    joinDateTimestamp = System.currentTimeMillis(),
                    // Apply other initialProfileData fields if any, mapping to UserProfileData structure
                    // For simplicity, only displayName handled directly here, rest via updateUserProfile if needed
                )
                usersCollection.document(firebaseUser.uid).set(userProfile).await()
                Timber.d("Firebase SignUp successful & profile created for: $email")
                Result.Success(mapFirebaseUserToAuthUser(firebaseUser, displayName))
            } else {
                Result.Error(Exception("Firebase user is null after signup."))
            }
        } catch (e: Exception) {
            Timber.e(e, "Firebase SignUp error for: $email")
            Result.Error(e)
        }
    }

    override suspend fun logIn(email: String, password: String): Result<AuthUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Timber.d("Firebase LogIn successful for: $email")
                Result.Success(mapFirebaseUserToAuthUser(it))
            } ?: Result.Error(Exception("Firebase user is null after login."))
        } catch (e: Exception) {
            Timber.e(e, "Firebase LogIn error for: $email")
            Result.Error(e)
        }
    }

    override suspend fun logOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Timber.d("Firebase LogOut successful")
            Result.Success(Unit)
        } catch (e: Exception) { // Should not happen with signOut
            Timber.e(e, "Firebase LogOut error")
            Result.Error(e)
        }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.let { mapFirebaseUserToAuthUser(it) }
    }

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.let { mapFirebaseUserToAuthUser(it) })
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        trySend(firebaseAuth.currentUser?.let { mapFirebaseUserToAuthUser(it) }) // Emit initial state
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.d("Firebase Password Reset email sent to: $email")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Firebase Password Reset error for: $email")
            Result.Error(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfileData> {
        return try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            val userProfile = documentSnapshot.toObject<UserProfileData>()
            if (userProfile != null) {
                Result.Success(userProfile)
            } else {
                Result.Error(Exception("User profile not found in Firestore for ID: $userId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile from Firestore for ID: $userId")
            Result.Error(e)
        }
    }

    override fun observeUserProfile(userId: String): Flow<UserProfileData?> = callbackFlow {
        val listenerRegistration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.w(e, "Error observing user profile for ID: $userId")
                    channel.close(e) // Close flow with error
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<UserProfileData>())
            }
        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun updateUserProfile(userId: String, profileData: UserProfileData): Result<Unit> {
        return try {
            // Ensure the profileData's userId matches the document ID for safety
            if (profileData.userId != userId) {
                 return Result.Error(IllegalArgumentException("Profile data UserID does not match target UserID"))
            }
            // Update FirebaseAuth display name and photo URL if they changed and are part of profileData
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null && currentUser.uid == userId) {
                val profileUpdatesBuilder = UserProfileChangeRequest.Builder()
                var needsFirebaseAuthUpdate = false
                if (profileData.displayName != currentUser.displayName) {
                    profileUpdatesBuilder.displayName = profileData.displayName
                    needsFirebaseAuthUpdate = true
                }
                if (profileData.profilePictureUrl != currentUser.photoUrl?.toString()) {
                    profileUpdatesBuilder.photoUri = profileData.profilePictureUrl?.let { Uri.parse(it) }
                    needsFirebaseAuthUpdate = true
                }
                if (needsFirebaseAuthUpdate) {
                    currentUser.updateProfile(profileUpdatesBuilder.build()).await()
                }
            }

            // Using SetOptions.merge() to only update provided fields or create if not exists.
            // Note: UserProfileData has default values, so all fields will be written unless
            // it's converted to a Map<String, Any> with only non-null/changed fields.
            // For simplicity, full object set with merge is used.
            usersCollection.document(userId).set(profileData, SetOptions.merge()).await()
            Timber.d("Firestore UserProfile updated successfully for: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating user profile in Firestore for ID: $userId")
            Result.Error(e)
        }
    }

    override fun getCurrentUserId(): String? {
         // This method is part of UserDataSource, but UserIdProvider is the main source for just the ID.
         // Keeping it consistent with how UserIdProvider would get it.
        return firebaseAuth.currentUser?.uid
    }

    // --- Mappers ---
    private fun mapFirebaseUserToAuthUser(firebaseUser: FirebaseUser, overrideDisplayName: String? = null): AuthUser {
        return AuthUser(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            isEmailVerified = firebaseUser.isEmailVerified,
            displayName = overrideDisplayName ?: firebaseUser.displayName
        )
    }
    // UserProfileData is directly used with Firestore's toObject<UserProfileData>()
    // and set(userProfileData), assuming field names match.
}
