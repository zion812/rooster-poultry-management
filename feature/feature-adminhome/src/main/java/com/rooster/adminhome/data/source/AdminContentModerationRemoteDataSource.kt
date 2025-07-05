package com.rooster.adminhome.data.source

import com.rooster.adminhome.domain.model.ContentModerationItem
import kotlinx.coroutines.flow.Flow

interface AdminContentModerationRemoteDataSource {
    fun getPendingModerationItems(count: Int): Flow<List<ContentModerationItem>>
}
