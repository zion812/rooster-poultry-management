package com.example.rooster.feature.iot.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.rooster.feature.iot.data.model.BaseReading
import com.example.rooster.feature.iot.data.model.FeedLevelReading
import com.example.rooster.feature.iot.data.model.HumidityReading
import com.example.rooster.feature.iot.data.model.LightLevelReading
import com.example.rooster.feature.iot.data.model.TemperatureReading
import com.example.rooster.feature.iot.data.model.WaterConsumptionReading
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun <T : BaseReading> generateCsvContent(readings: List<T>, sensorType: String): String {
        val header = "Timestamp,DeviceID,SensorType,Value,Unit\n"
        val rows = readings.map { reading ->
            val value = when (reading) {
                is TemperatureReading -> reading.temperature.toString()
                is HumidityReading -> reading.humidity.toString()
                is FeedLevelReading -> reading.levelPercentage.toString()
                is WaterConsumptionReading -> reading.volumeConsumed.toString()
                is LightLevelReading -> reading.lux.toString()
                else -> "N/A"
            }
            "${dateTimeFormatter.format(Date(reading.timestamp))},${reading.deviceId},$sensorType,$value,${reading.unit}"
        }
        return header + rows.joinToString("\n")
    }

    fun shareCsvFile(context: Context, csvContent: String, fileNamePrefix: String) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${fileNamePrefix}_${timestamp}.csv"
            val csvFile = File(context.cacheDir, fileName)
            FileOutputStream(csvFile).use {
                it.write(csvContent.toByteArray())
            }

            val fileUri: Uri? = try {
                FileProvider.getUriForFile(context, "${context.packageName}.provider", csvFile)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (fileUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    type = "text/csv"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Export CSV via"))
            } else {
                // Handle error: Unable to get File URI
                // You might want to show a Toast message to the user
                Log.e("CsvExporter", "Failed to create content URI for CSV file.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error: File I/O or other issues
            Log.e("CsvExporter", "Error generating or sharing CSV: ${e.message}")
        }
    }
}
