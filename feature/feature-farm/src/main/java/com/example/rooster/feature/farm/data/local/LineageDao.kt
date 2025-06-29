package com.example.rooster.feature.farm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LineageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LineageLinkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<LineageLinkEntity>)

    @Query("SELECT * FROM lineage_links WHERE childFlockId = :childFlockId")
    fun getParents(childFlockId: String): Flow<List<LineageLinkEntity>>

    @Query("SELECT * FROM lineage_links WHERE parentFlockId = :parentFlockId")
    fun getChildren(parentFlockId: String): Flow<List<LineageLinkEntity>>

    @Query("SELECT * FROM lineage_links WHERE childFlockId = :childFlockId AND relationshipType = :type")
    suspend fun getSpecificParentOfType(childFlockId: String, type: RelationshipType): LineageLinkEntity?

    @Query("SELECT * FROM lineage_links WHERE needsSync = 1")
    suspend fun getUnsyncedLinks(): List<LineageLinkEntity>

    @Query("DELETE FROM lineage_links WHERE childFlockId = :childFlockId AND parentFlockId = :parentFlockId AND relationshipType = :type")
    suspend fun deleteLink(childFlockId: String, parentFlockId: String, type: RelationshipType)

    @Query("DELETE FROM lineage_links WHERE childFlockId = :childFlockId OR parentFlockId = :childFlockId")
    suspend fun deleteAllLinksForFlock(childFlockId: String)
}
