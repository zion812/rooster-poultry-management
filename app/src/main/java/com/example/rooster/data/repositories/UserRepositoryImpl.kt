package com.example.rooster.data.repositories

import com.example.rooster.data.entities.User
import com.example.rooster.domain.repository.UserRepository
import com.parse.ParseUser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository
 */
@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    override suspend fun getCurrentUser(): User {
        val parseUser = ParseUser.getCurrentUser()
        return if (parseUser != null) {
            User(
                userId = parseUser.objectId ?: "unknown",
                username = parseUser.username ?: "unknown",
                email = parseUser.email ?: "unknown@example.com",
                displayName = parseUser.getString("displayName"),
                profileImageUrl = parseUser.getString("profileImageUrl"),
                role = parseUser.getString("userRole") ?: "farmer",
                phoneNumber = parseUser.getString("phoneNumber"),
                location = parseUser.getString("location"),
                isActive = parseUser.getBoolean("isActive") ?: true,
                createdAt = parseUser.createdAt?.time ?: System.currentTimeMillis()
            )
        } else {
            User(
                userId = "guest",
                username = "Guest User",
                email = "guest@example.com"
            )
        }
    }

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
}
