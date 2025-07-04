package com.example.rooster.feature.iot.utils

import com.example.rooster.feature.iot.data.model.TemperatureReading
import com.example.rooster.feature.iot.data.model.HumidityReading
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvExporterTest {

    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    @Test
    fun `generateCsvContent for TemperatureReadings creates correct CSV string`() {
        val time1 = System.currentTimeMillis() - 10000
        val time2 = System.currentTimeMillis()
        val readings = listOf(
            TemperatureReading("r1", "dev1", time1, 25.5, "°C", false),
            TemperatureReading("r2", "dev1", time2, 26.0, "°C", false)
        )

        val expectedHeader = "Timestamp,DeviceID,SensorType,Value,Unit\n"
        val expectedRow1 = "${dateTimeFormatter.format(Date(time1))},dev1,Temperature,25.5,°C"
        val expectedRow2 = "${dateTimeFormatter.format(Date(time2))},dev1,Temperature,26.0,°C"
        val expectedCsv = expectedHeader + expectedRow1 + "\n" + expectedRow2

        val actualCsv = CsvExporter.generateCsvContent(readings, "Temperature")
        assertEquals(expectedCsv, actualCsv)
    }

    @Test
    fun `generateCsvContent for HumidityReadings creates correct CSV string`() {
        val time1 = System.currentTimeMillis() - 5000
        val readings = listOf(
            HumidityReading("h1", "dev2", time1, 55.2, "%", false)
        )

        val expectedHeader = "Timestamp,DeviceID,SensorType,Value,Unit\n"
        val expectedRow1 = "${dateTimeFormatter.format(Date(time1))},dev2,Humidity,55.2,%"
        val expectedCsv = expectedHeader + expectedRow1

        val actualCsv = CsvExporter.generateCsvContent(readings, "Humidity")
        assertEquals(expectedCsv, actualCsv)
    }

    @Test
    fun `generateCsvContent with empty list returns only header`() {
        val readings = emptyList<TemperatureReading>()
        val expectedCsv = "Timestamp,DeviceID,SensorType,Value,Unit\n"
        val actualCsv = CsvExporter.generateCsvContent(readings, "Temperature")
        assertEquals(expectedCsv, actualCsv)
    }

    // Note: Testing shareCsvFile requires Android Context and mocking FileProvider,
    // which is better suited for an instrumented test (androidTest).
    // This unit test focuses on the CSV string generation logic.
}
