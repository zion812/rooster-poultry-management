package com.rooster.farmerhome.data.local.utils

import androidx.room.TypeConverter
import com.rooster.farmerhome.domain.model.AlertSeverity
import com.rooster.farmerhome.domain.model.MetricTrend

class FarmerHomeTypeConverters {

    // MetricTrend Converters
    @TypeConverter
    fun fromMetricTrend(trend: MetricTrend?): String? {
        return trend?.name
    }

    @TypeConverter
    fun toMetricTrend(trendName: String?): MetricTrend? {
        return trendName?.let { enumValueOf<MetricTrend>(it) }
    }

    // AlertSeverity Converters
    @TypeConverter
    fun fromAlertSeverity(severity: AlertSeverity?): String? {
        return severity?.name
    }

    @TypeConverter
    fun toAlertSeverity(severityName: String?): AlertSeverity? {
        // Provide a default or handle null if severityName could be invalid from DB
        return severityName?.let {
            try {
                enumValueOf<AlertSeverity>(it)
            } catch (e: IllegalArgumentException) {
                null // Or a default severity like AlertSeverity.LOW
            }
        }
    }
}
