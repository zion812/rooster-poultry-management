package com.example.rooster.core.auth

import com.example.rooster.core.auth.di.ApplicationScope
import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.model.AuthState
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.Result // Using the common Result type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class responsible for managing user authentication and session state.
 *
 * This class acts as a facade over the [AuthRepository], providing a simplified API
 * for authentication operations and exposing the current authentication state and user details
 * as reactive flows. It is designed to be a singleton within the application.
 *
 * @property authRepository The repository handling data operations for authentication.
 * @property applicationScope A coroutine scope tied to the application's lifecycle, used for observing
 *                            long-lived flows like the current user state.
 */
@Singleton
class AuthService @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    /**
     * A [StateFlow] emitting the currently authenticated [User], or null if no user is authenticated.
     * This flow is driven by changes from the [AuthRepository].
     */
    val currentUser: StateFlow<User?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    /**
     * A [StateFlow] emitting the current [AuthState] of the application (e.g., Loading, Authenticated, Unauthenticated, Error).
     * This flow is updated based on authentication operations and user state changes from the [AuthRepository].
     */
    val authState: StateFlow<AuthState> = _authState

    init {
        applicationScope.launch {
            authRepository.getCurrentUser()
                .onStart {
                    Timber.d("AuthService: Observing current user. Initial state: Loading.")
                    _authState.value = AuthState.Loading
                }
                .catch { e ->
                    Timber.e(e, "AuthService: Error observing current user.")
                    _authState.value = AuthState.Error("Failed to observe auth state: ${e.message}")
                    _currentUser.value = null
                }
                .collect { user ->
                    Timber.d("AuthService: Current user updated: ${user?.email ?: "null"}")
                    _currentUser.value = user
                    _authState.value = if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }
                }
        }
    }

    /**
     * Attempts to sign in a user with the provided email and password.
     * Updates [authState] to [AuthState.Loading] initially, and to [AuthState.Error] on failure.
     * Successful authentication is reflected through the [currentUser] and [authState] flows via repository updates.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] containing the [User] on success, or an error on failure.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        _authState.value = AuthState.Loading
        val result = authRepository.signIn(email, password)
        if (result is Result.Error) {
            _authState.value = AuthState.Error(result.exception.message ?: "Sign in failed")
        }
        return result
    }

    /**
     * Attempts to register a new user with the provided details.
     * Updates [authState] to [AuthState.Loading] initially, and to [AuthState.Error] on failure.
     * Successful registration and sign-in are reflected through the [currentUser] and [authState] flows.
     *
     * @param email The new user's email address.
     * @param password The new user's password.
     * @param displayName The new user's display name.
     * @param role The role of the new user, defaults to [UserRole.FARMER].
     * @param phoneNumber Optional phone number for the new user.
     * @return A [Result] containing the newly created [User] on success, or an error on failure.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        role: UserRole = UserRole.FARMER,
        phoneNumber: String? = null
    ): Result<User> {
        _authState.value = AuthState.Loading
        val result = authRepository.signUp(email, password, displayName, role, phoneNumber)
        if (result is Result.Error) {
            _authState.value = AuthState.Error(result.exception.message ?: "Sign up failed")
        }
        return result
    }

    /**
     * Attempts to sign in a user with a phone number and verification code.
     * NOTE: This feature is currently not implemented.
     *
     * @param phoneNumber The user's phone number.
     * @param verificationCode The OTP code received by the user.
     * @return [Result.Error] as this feature is not implemented.
     */
    suspend fun signInWithPhone(phoneNumber: String, verificationCode: String): Result<User> {
        Timber.w("signInWithPhone not implemented in AuthService.")
        return Result.Error(UnsupportedOperationException("Phone authentication not implemented yet."))
    }

    /**
     * Signs out the currently authenticated user.
     * Updates [authState] to [AuthState.Loading] initially. The actual state change to
     * [AuthState.Unauthenticated] is handled by the observer of `authRepository.getCurrentUser()`.
     *
     * @return [Result.Success] if the sign-out process was initiated, or [Result.Error] if an issue occurred.
     */
    suspend fun signOut(): Result<Unit> {
        _authState.value = AuthState.Loading
        // The repository's signOut() is called.
        // The change in auth state (to null user) will be picked up by the init block's collector.
        authRepository.signOut()
        return Result.Success(Unit) // Assuming local signOut in repo is mostly non-failing for this mapping
    }

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param user The [User] object with updated profile information.
     * @return A [Result] containing the updated [User] on success, or an error on failure.
     */
    suspend fun updateUserProfile(user: User): Result<User> {
        return authRepository.updateProfile(user)
    }

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email The email address to send the password reset link to.
     * @return [Result.Success] if the email was sent (or request accepted by Firebase), or [Result.Error] on failure.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authRepository.resetPassword(email)
    }

    /**
     * Sends an email verification link to the currently authenticated user.
     *
     * @return [Result.Success] if the verification email was sent, or [Result.Error] on failure.
     */
    suspend fun sendEmailVerification(): Result<Unit> {
        return authRepository.sendCurrentUserEmailVerification()
    }

    /**
     * Reloads the data for the currently authenticated user from the backend.
     * This is useful for refreshing user state, such as email verification status.
     * The [currentUser] and [authState] flows will be updated upon successful reload via the repository.
     *
     * @return A [Result] containing the reloaded [User] (or null if no user signed in after reload),
     *         or an error on failure.
     */
    suspend fun reloadCurrentUser(): Result<User?> {
        return authRepository.reloadCurrentUser()
    }

    /** Checks if the current user has the specified [UserRole]. */
    fun hasRole(role: UserRole): Boolean = _currentUser.value?.role == role

    /** Checks if the current user has any of the specified [UserRole]s. */
    fun hasAnyRole(vararg roles: UserRole): Boolean = _currentUser.value?.role?.let { it in roles } ?: false

    /** Checks if the current user is an [UserRole.ADMIN]. */
    fun isAdmin(): Boolean = hasRole(UserRole.ADMIN)

    /** Checks if the current user is a [UserRole.FARMER]. */
    fun isFarmer(): Boolean = hasRole(UserRole.FARMER)

    /** Checks if the current user is a [UserRole.BUYER]. */
    fun isBuyer(): Boolean = hasRole(UserRole.BUYER)

    /** Checks if the current user is an [UserRole.EXPERT]. */
    fun isExpert(): Boolean = hasRole(UserRole.EXPERT)

    /** Checks if the current user is a [UserRole.VETERINARIAN]. */
    fun isVeterinarian(): Boolean = hasRole(UserRole.VETERINARIAN)
}
