package com.rooster.adminhome.data.repository

import com.rooster.adminhome.data.source.AdminContentModerationRemoteDataSource
import com.rooster.adminhome.domain.model.ContentModerationItem
import com.rooster.adminhome.domain.repository.AdminContentModerationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminContentModerationRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdminContentModerationRemoteDataSource
    // TODO: Add localDataSource for caching if needed (less likely for moderation queue)
) : AdminContentModerationRepository {
    override fun getPendingModerationItems(count: Int): Flow<List<ContentModerationItem>> {
        return remoteDataSource.getPendingModerationItems(count)
    }
}
