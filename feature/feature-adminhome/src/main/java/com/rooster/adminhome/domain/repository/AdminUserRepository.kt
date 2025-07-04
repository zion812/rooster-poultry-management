package com.rooster.adminhome.domain.repository

import com.rooster.adminhome.domain.model.UserManagementInfo
import kotlinx.coroutines.flow.Flow

interface AdminUserRepository {
    fun getUserManagementSummary(): Flow<UserManagementInfo>
    // Potentially add methods for fetching user lists, user details, etc.
}
