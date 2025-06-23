package com.example.rooster.domain.repository

import com.example.rooster.models.UserRole

interface UserRepository {
    suspend fun getUserRole(userId: String): UserRole
    suspend fun updateUserRole(userId: String, role: UserRole): Boolean
    suspend fun getCurrentUser(): String?
}
