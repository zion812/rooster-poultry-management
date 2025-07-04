package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.dao.FarmHealthAlertDao
import com.rooster.farmerhome.data.local.model.FarmHealthAlertEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmHealthAlertLocalDataSourceImpl @Inject constructor(
    private val farmHealthAlertDao: FarmHealthAlertDao
) : FarmHealthAlertLocalDataSource {

    override fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlertEntity>> {
        return farmHealthAlertDao.getHealthAlertsForFarm(farmId)
    }

    override suspend fun insertHealthAlerts(alerts: List<FarmHealthAlertEntity>) {
        // Could implement logic here to only insert new or update existing,
        // or simply replace all for a given farm if that's the strategy.
        // For now, assuming DAO's OnConflictStrategy.REPLACE handles it if IDs match.
        // Or, delete old ones for the farm first if this list is meant to be exhaustive.
        // If alerts are fetched incrementally, this would need more care.
        // For a simple cache of "current alerts", replacing might be fine.
        // Let's assume we clear old alerts for the farm and insert the new list.
        if (alerts.isNotEmpty()) {
            val farmId = alerts.first().farmId // All alerts in the list should belong to the same farm
            farmHealthAlertDao.deleteAlertsForFarm(farmId) // Clear old ones for this farm
            farmHealthAlertDao.insertHealthAlerts(alerts)
        } else {
            // If an empty list is fetched, it might mean there are no current alerts.
            // Need to decide if this should clear the cache for a farm.
            // For now, if alerts list is empty, we won't clear existing,
            // assuming the remote source is the source of truth for "current empty".
            // This needs careful consideration based on API behavior.
            // If the list is always the full current list, then clearing for a farmId is good.
            // Let's refine to: if we fetch for farm X and get an empty list, clear farm X's alerts.
            // This requires farmId to be passed to insertHealthAlerts.
        }
    }

    // Refined insert that requires farmId to clear previous alerts for that farm
    suspend fun insertHealthAlertsForFarm(farmId: String, alerts: List<FarmHealthAlertEntity>) {
        farmHealthAlertDao.deleteAlertsForFarm(farmId)
        if (alerts.isNotEmpty()) {
            farmHealthAlertDao.insertHealthAlerts(alerts)
        }
    }


    override suspend fun markAlertAsRead(farmId: String, alertId: String): Int {
        // The DAO's updateReadStatus doesn't need farmId, but the local data source interface
        // and repository layers do, to align with remote data source and business logic.
        // Here, we just pass it through to the DAO method that only needs alertId for the update.
        return farmHealthAlertDao.updateReadStatus(alertId, true)
    }

    override suspend fun markAlertAsUnread(farmId: String, alertId: String): Int { // Implements interface method
        return farmHealthAlertDao.updateReadStatus(alertId, false)
    }

    override suspend fun deleteAlertsForFarm(farmId: String) {
        farmHealthAlertDao.deleteAlertsForFarm(farmId)
    }
}
