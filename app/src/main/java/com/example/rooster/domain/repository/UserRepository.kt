package com.example.rooster.domain.repository

import com.example.rooster.data.entities.User

/**
 * Domain interface for user operations
 */
interface UserRepository {

    suspend fun getCurrentUser(): User

    suspend fun getUser(userId: String): User?

    suspend fun updateUser(user: User): User

    suspend fun updateUserCoins(userId: String, coins: Int): User

    suspend fun searchUsers(query: String): List<User>
}
