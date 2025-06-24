package com.example.rooster.data.repositories

import com.example.rooster.domain.repository.UserRepository
import com.example.rooster.models.UserRole
import com.parse.ParseUser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository
 */
@Singleton
class UserRepositoryImpl
    @Inject
    constructor() : UserRepository {
        // Returns the userId of the current ParseUser as specified by the interface
        override suspend fun getCurrentUser(): String? {
            val parseUser = ParseUser.getCurrentUser()
            return parseUser?.objectId
        }

        // Get the UserRole for a given user ID; stub returns FARMER for now
        override suspend fun getUserRole(userId: String): UserRole {
            // TODO: Replace with real lookup from ParseUser or local cache
            return UserRole.FARMER
        }

        // Update the UserRole for a user; stub returns true for now
        override suspend fun updateUserRole(
            userId: String,
            role: UserRole,
        ): Boolean {
            // TODO: Implement actual update logic to data backend
            return true
        }

        // ================== EXTRA USER-CENTRIC METHODS ==================
        // The below methods do not belong to the UserRepository interface.
        // They are commented out for future modularization/refactoring.

    /*
    override suspend fun getUser(userId: String): User? {
        // Mock implementation - in real app would query Parse Server
        return User(
            userId = userId,
            username = "User $userId",
            email = "$userId@example.com"
        )
    }

    override suspend fun updateUser(user: User): User {
        // Mock implementation - in real app would update Parse Server
        return user
    }

    override suspend fun updateUserCoins(userId: String, coins: Int): User {
        // Mock implementation - in real app would update Parse Server
        return User(
            userId = userId,
            username = "User $userId",
            email = "$userId@example.com",
            coins = coins
        )
    }

    override suspend fun searchUsers(query: String): List<User> {
        // Mock implementation - in real app would search Parse Server
        return listOf(
            User(
                userId = "user1",
                username = query,
                email = "$query@example.com"
            )
        )
    }
     */
    }
