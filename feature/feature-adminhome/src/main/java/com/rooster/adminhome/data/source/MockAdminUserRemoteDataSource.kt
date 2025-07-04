package com.rooster.adminhome.data.source

import com.example.rooster.testing.MockDataProvider // Using the centralized provider
import com.rooster.adminhome.domain.model.UserManagementInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockAdminUserRemoteDataSource @Inject constructor() : AdminUserRemoteDataSource {
    override fun getUserManagementSummary(): Flow<UserManagementInfo> = flow {
        delay(400) // Simulate delay
        emit(MockDataProvider.Admin.userManagementSummary())
    }
}
