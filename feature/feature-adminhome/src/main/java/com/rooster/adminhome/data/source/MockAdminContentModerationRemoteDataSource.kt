package com.rooster.adminhome.data.source

import com.example.rooster.testing.MockDataProvider
import com.rooster.adminhome.domain.model.ContentModerationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockAdminContentModerationRemoteDataSource @Inject constructor() : AdminContentModerationRemoteDataSource {
    override fun getPendingModerationItems(count: Int): Flow<List<ContentModerationItem>> = flow {
        delay(800) // Simulate delay
        emit(MockDataProvider.Admin.contentModerationQueue(count))
    }
}
