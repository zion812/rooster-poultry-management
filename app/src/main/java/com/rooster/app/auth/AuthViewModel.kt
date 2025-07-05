package com.rooster.app.auth

// Placeholder for AuthViewModel
// In a real app, this would likely extend androidx.lifecycle.ViewModel
class AuthViewModel {
    // Placeholder properties and functions can be added here as needed
    fun getCurrentUserRole(): com.rooster.app.models.UserRole {
        // Return a default role for now
        return com.rooster.app.models.UserRole.GUEST
    }

    fun isLoggedIn(): Boolean {
        return false
    }
}
