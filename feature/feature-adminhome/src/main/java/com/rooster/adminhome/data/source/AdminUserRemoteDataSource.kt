package com.rooster.adminhome.data.source

import com.rooster.adminhome.domain.model.UserManagementInfo
import kotlinx.coroutines.flow.Flow

interface AdminUserRemoteDataSource {
    fun getUserManagementSummary(): Flow<UserManagementInfo>
}
