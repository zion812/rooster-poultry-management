package com.example.rooster.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Data Models
data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val loading: Boolean = false,
    val resetLoading: Boolean = false,
    val userRole: String = "",
    val userName: String = "",
    val userId: String = "",
    val phoneNumber: String = "",
    val errorMessage: String = "",
    val successMessage: String = "",
    val showOtpDialog: Boolean = false,
    val verificationId: String = "",
    val isVerifyingOtp: Boolean = false
)

sealed class AuthState {
    object Idle : AuthState()
    object Processing : AuthState()
    object AwaitingOtp : AuthState()
    object Success : AuthState()
    data class Failed(val message: String) : AuthState()
}

// ViewModel
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    
    init {
        // Set up offline persistence
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.w("AuthViewModel", "Firebase persistence already enabled", e)
        }
        
        // Check current authentication state
        checkAuthState()
    }
    
    fun checkAuthState() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // User is signed in, get their role and profile
                    loadUserProfile(currentUser.uid)
                } else {
                    // User is not signed in
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        userRole = "",
                        userName = "",
                        userId = "",
                        phoneNumber = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking auth state", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    private suspend fun loadUserProfile(userId: String) {
        try {
            val userSnapshot = usersRef.child(userId).get().await()
            if (userSnapshot.exists()) {
                val userRole = userSnapshot.child("role").getValue(String::class.java) ?: "farmer"
                val userName = userSnapshot.child("name").getValue(String::class.java) ?: "రైతు గారు"
                val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    userRole = userRole,
                    userName = userName,
                    userId = userId,
                    phoneNumber = phoneNumber,
                    loading = false
                )
                
                Log.d("AuthViewModel", "User profile loaded: $userRole")
            } else {
                // User exists in Firebase Auth but not in database, create profile
                createUserProfile(userId, "farmer", "రైతు గారు", auth.currentUser?.phoneNumber ?: "")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error loading user profile", e)
            _uiState.value = _uiState.value.copy(
                loading = false,
                errorMessage = "Failed to load user profile: ${e.message}"
            )
        }
    }
    
    private suspend fun createUserProfile(userId: String, role: String, name: String, phoneNumber: String) {
        try {
            val userProfile = mapOf(
                "id" to userId,
                "name" to name,
                "role" to role,
                "phoneNumber" to phoneNumber,
                "createdAt" to System.currentTimeMillis(),
                "isActive" to true,
                "district" to "Krishna", // Default district
                "preferredLanguage" to "te"
            )
            
            usersRef.child(userId).setValue(userProfile).await()
            
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                userRole = role,
                userName = name,
                userId = userId,
                phoneNumber = phoneNumber,
                loading = false,
                successMessage = "Profile created successfully!"
            )
            
            FirebaseCrashlytics.getInstance().log("User profile created: $userId - $role")
            
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error creating user profile", e)
            _uiState.value = _uiState.value.copy(
                loading = false,
                errorMessage = "Failed to create user profile: ${e.message}"
            )
        }
    }
    
    fun loginWithPhone(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    errorMessage = "",
                    successMessage = ""
                )
                
                val formattedNumber = if (phoneNumber.startsWith("+91")) {
                    phoneNumber
                } else {
                    "+91$phoneNumber"
                }
                
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(formattedNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // Auto-verification completed
                            signInWithCredential(credential)
                        }
                        
                        override fun onVerificationFailed(error: FirebaseException) {
                            Log.e("AuthViewModel", "Phone verification failed", error)
                            _uiState.value = _uiState.value.copy(
                                loading = false,
                                errorMessage = when (error) {
                                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
                                    is FirebaseTooManyRequestsException -> "Too many requests. Please try again later"
                                    else -> "Verification failed: ${error.message}"
                                }
                            )
                        }
                        
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            // Code sent successfully
                            resendToken = token
                            _uiState.value = _uiState.value.copy(
                                loading = false,
                                showOtpDialog = true,
                                verificationId = verificationId,
                                successMessage = "OTP sent to $formattedNumber"
                            )
                            
                            Log.d("AuthViewModel", "OTP sent successfully")
                        }
                    })
                    .build()
                
                PhoneAuthProvider.verifyPhoneNumber(options)
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error initiating phone login", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = "Login failed: ${e.message}"
                )
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isVerifyingOtp = true,
                    errorMessage = ""
                )
                
                val verificationId = _uiState.value.verificationId
                if (verificationId.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isVerifyingOtp = false,
                        errorMessage = "Verification ID not found. Please request OTP again."
                    )
                    return@launch
                }
                
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithCredential(credential)
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error verifying OTP", e)
                _uiState.value = _uiState.value.copy(
                    isVerifyingOtp = false,
                    errorMessage = "OTP verification failed: ${e.message}"
                )
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                val user = result.user
                
                if (user != null) {
                    Log.d("AuthViewModel", "Phone authentication successful: ${user.uid}")
                    
                    // Check if user profile exists
                    loadUserProfile(user.uid)
                    
                    _uiState.value = _uiState.value.copy(
                        isVerifyingOtp = false,
                        showOtpDialog = false,
                        successMessage = "Login successful!"
                    )
                    
                    FirebaseCrashlytics.getInstance().log("Phone authentication successful: ${user.uid}")
                    
                } else {
                    _uiState.value = _uiState.value.copy(
                        isVerifyingOtp = false,
                        errorMessage = "Authentication failed - no user returned"
                    )
                }
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error signing in with credential", e)
                _uiState.value = _uiState.value.copy(
                    isVerifyingOtp = false,
                    errorMessage = when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "Invalid OTP. Please check and try again."
                        else -> "Sign in failed: ${e.message}"
                    }
                )
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    fun resendOtp(phoneNumber: String, activity: Activity) {
        if (resendToken == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Cannot resend OTP. Please start verification again."
            )
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    errorMessage = ""
                )
                
                val formattedNumber = if (phoneNumber.startsWith("+91")) {
                    phoneNumber
                } else {
                    "+91$phoneNumber"
                }
                
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(formattedNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setForceResendingToken(resendToken!!)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            signInWithCredential(credential)
                        }
                        
                        override fun onVerificationFailed(error: FirebaseException) {
                            _uiState.value = _uiState.value.copy(
                                loading = false,
                                errorMessage = "Resend failed: ${error.message}"
                            )
                        }
                        
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            resendToken = token
                            _uiState.value = _uiState.value.copy(
                                loading = false,
                                verificationId = verificationId,
                                successMessage = "OTP resent to $formattedNumber"
                            )
                        }
                    })
                    .build()
                
                PhoneAuthProvider.verifyPhoneNumber(options)
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error resending OTP", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = "Resend failed: ${e.message}"
                )
            }
        }
    }
    
    // Legacy login method for existing Parse users (fallback)
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    errorMessage = "",
                    successMessage = ""
                )
                
                // For MVP, create a mock phone-based login
                // This would be replaced with actual Parse server authentication
                kotlinx.coroutines.delay(1500) // Simulate network call
                
                // Mock successful login
                val mockUserId = "mock_${username}_${System.currentTimeMillis()}"
                createUserProfile(mockUserId, "farmer", username, "+919876543210")
                
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    successMessage = "Mock login successful! Please use phone login for production."
                )
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = "Login failed: ${e.message}"
                )
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    // Legacy register method
    fun register(username: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    errorMessage = "",
                    successMessage = ""
                )
                
                // Mock registration for MVP
                kotlinx.coroutines.delay(1500)
                
                val mockUserId = "mock_${username}_${System.currentTimeMillis()}"
                createUserProfile(mockUserId, role, username, "+919876543210")
                
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    successMessage = "Mock registration successful! Please use phone login for production."
                )
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration error", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = "Registration failed: ${e.message}"
                )
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _uiState.value = AuthUiState() // Reset to initial state
                FirebaseCrashlytics.getInstance().log("User logged out")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout error", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = "",
            successMessage = ""
        )
    }
    
    fun dismissOtpDialog() {
        _uiState.value = _uiState.value.copy(
            showOtpDialog = false,
            verificationId = "",
            isVerifyingOtp = false
        )
    }
    
    fun togglePasswordReset(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            resetLoading = show
        )
    }
    
    fun validateInput(username: String, password: String, email: String, isLogin: Boolean): String? {
        return when {
            username.isBlank() -> "Username cannot be empty"
            password.isBlank() -> "Password cannot be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            !isLogin && email.isBlank() -> "Email cannot be empty"
            !isLogin && !email.contains("@") -> "Invalid email format"
            else -> null
        }
    }
    
    fun validatePhoneNumber(phoneNumber: String): String? {
        val cleanNumber = phoneNumber.replace("+91", "").replace(" ", "").replace("-", "")
        return when {
            cleanNumber.isBlank() -> "Phone number cannot be empty"
            cleanNumber.length != 10 -> "Phone number must be 10 digits"
            !cleanNumber.all { it.isDigit() } -> "Phone number can only contain digits"
            !cleanNumber.startsWith("6") && !cleanNumber.startsWith("7") && 
            !cleanNumber.startsWith("8") && !cleanNumber.startsWith("9") -> 
                "Invalid Indian mobile number"
            else -> null
        }
    }
}
