package com.rooster.adminhome.data.repository

import com.rooster.adminhome.data.source.AdminUserRemoteDataSource
import com.rooster.adminhome.domain.model.UserManagementInfo
import com.rooster.adminhome.domain.repository.AdminUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminUserRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdminUserRemoteDataSource
    // TODO: Add localDataSource for caching
) : AdminUserRepository {
    override fun getUserManagementSummary(): Flow<UserManagementInfo> {
        // TODO: Implement caching
        return remoteDataSource.getUserManagementSummary()
    }
}
