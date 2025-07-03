package com.example.rooster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.auth.domain.model.AuthState
import com.example.rooster.core.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(
                    authState = if (user != null) AuthState.Authenticated else AuthState.Unauthenticated,
                    currentUser = user,
                    isLoading = false
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(
                authState = AuthState.Unauthenticated,
                currentUser = null
            )
        }
    }
}

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val currentUser: com.example.rooster.core.auth.domain.model.User? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)