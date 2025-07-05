package com.rooster.adminhome.domain.repository

import com.rooster.adminhome.domain.model.ContentModerationItem
import kotlinx.coroutines.flow.Flow

interface AdminContentModerationRepository {
    fun getPendingModerationItems(count: Int = 10): Flow<List<ContentModerationItem>>
    // suspend fun approveContent(itemId: String): Result<Unit>
    // suspend fun rejectContent(itemId: String, reason: String): Result<Unit>
}
