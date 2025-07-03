package com.example.rooster.core.auth

import com.example.rooster.core.common.model.User
import com.example.rooster.core.common.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Listen to Firebase auth state changes
        firebaseAuth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                _authState.value = AuthState.Loading
                // Use coroutine scope for fetching user profile
                kotlinx.coroutines.GlobalScope.launch {
                    try {
                        val user = fetchUserProfile(firebaseUser)
                        if (user != null) {
                            _currentUser.value = user
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Failed to fetch user profile")
                        }
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error(e.message ?: "Auth error")
                    }
                }
            } else {
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = fetchUserProfile(firebaseUser)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Failed to fetch user profile")
                }
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            AuthResult.Error(e.message ?: "Authentication failed")
        }
    }

    /**
     * Sign up with email and password
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        role: UserRole = UserRole.FARMER
    ): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Create user profile in Firestore
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    role = role,
                    isEmailVerified = firebaseUser.isEmailVerified,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )

                // Save to Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)

                // Send verification email
                firebaseUser.sendEmailVerification().await()

                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to create account")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    /**
     * Sign in with phone number
     */
    suspend fun signInWithPhone(phoneNumber: String, verificationCode: String): AuthResult {
        // Implementation for phone auth would go here
        // This requires more complex OTP handling
        return AuthResult.Error("Phone authentication not implemented yet")
    }

    /**
     * Sign out current user
     */
    suspend fun signOut(): AuthResult {
        return try {
            firebaseAuth.signOut()
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
            AuthResult.Success(null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign out failed")
        }
    }

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(user: User): AuthResult {
        return try {
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()

            _currentUser.value = user
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Profile update failed")
        }
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password reset failed")
        }
    }

    /**
     * Send email verification
     */
    suspend fun sendEmailVerification(): AuthResult {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Email verification failed")
        }
    }

    /**
     * Check if user has specific role
     */
    fun hasRole(role: UserRole): Boolean {
        return _currentUser.value?.role == role
    }

    /**
     * Check if user has any of the specified roles
     */
    fun hasAnyRole(vararg roles: UserRole): Boolean {
        val userRole = _currentUser.value?.role
        return userRole != null && roles.contains(userRole)
    }

    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean = hasRole(UserRole.ADMIN)

    /**
     * Check if user is farmer
     */
    fun isFarmer(): Boolean = hasRole(UserRole.FARMER)

    /**
     * Check if user is buyer
     */
    fun isBuyer(): Boolean = hasRole(UserRole.BUYER)

    /**
     * Fetch complete user profile from Firestore
     */
    private suspend fun fetchUserProfile(firebaseUser: FirebaseUser): User? {
        return try {
            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                user?.copy(
                    isEmailVerified = firebaseUser.isEmailVerified,
                    lastLoginAt = System.currentTimeMillis()
                )
            } else {
                // Create basic user profile if not exists
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    isEmailVerified = firebaseUser.isEmailVerified,
                    lastLoginAt = System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                user
            }
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Authentication state sealed class
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Authentication result sealed class
 */
sealed class AuthResult {
    data class Success(val user: User?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}