package com.example.rooster.feature.iot.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SensorDataDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData if used, good practice for Room

    private lateinit var database: IoTDatabase
    private lateinit var dao: SensorDataDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IoTDatabase::class.java
        ).allowMainThreadQueries().build() // allowMainThreadQueries for testing only
        dao = database.sensorDataDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTemperatureReading() = runTest {
        val reading = TemperatureReadingEntity("id1", "dev1", System.currentTimeMillis(), 25.5, "C", false)
        dao.insertTemperatureReadings(listOf(reading))

        val readings = dao.getTemperatureReadingsForDevice("dev1").first()
        assertThat(readings).contains(reading)
    }

    @Test
    fun insertAndGetDeviceInfo() = runTest {
        val deviceInfo = DeviceInfoEntity("dev1", "Thermostat", "Sensor", "Living Room", "online", System.currentTimeMillis(), 100)
        dao.insertDeviceInfos(listOf(deviceInfo))

        val retrievedInfo = dao.getDeviceInfo("dev1").first()
        assertThat(retrievedInfo).isEqualTo(deviceInfo)
    }

    @Test
    fun getTemperatureReadingsInRange() = runTest {
        val timeNow = System.currentTimeMillis()
        val reading1 = TemperatureReadingEntity("id1", "dev1", timeNow - 5000, 22.0, "C", false) // In range
        val reading2 = TemperatureReadingEntity("id2", "dev1", timeNow - 10000, 23.0, "C", false) // In range
        val reading3 = TemperatureReadingEntity("id3", "dev1", timeNow - 15000, 24.0, "C", false) // Out of range
        dao.insertTemperatureReadings(listOf(reading1, reading2, reading3))

        val startTime = timeNow - 12000 // Should fetch reading1 and reading2
        val endTime = timeNow

        val readings = dao.getTemperatureReadingsForDeviceInRange("dev1", startTime, endTime).first()
        assertThat(readings).hasSize(2)
        assertThat(readings).containsExactly(reading1, reading2) // Order is DESC by timestamp
    }

    @Test
    fun deleteOldTemperatureReadings() = runTest {
        val timeNow = System.currentTimeMillis()
        val oldReading = TemperatureReadingEntity("id_old", "dev1", timeNow - 200000, 20.0, "C", false)
        val newReading = TemperatureReadingEntity("id_new", "dev1", timeNow - 10000, 21.0, "C", false)
        dao.insertTemperatureReadings(listOf(oldReading, newReading))

        dao.deleteOldTemperatureReadings(timeNow - 100000) // Delete readings older than this timestamp

        val readings = dao.getTemperatureReadingsForDevice("dev1").first()
        assertThat(readings).containsExactly(newReading)
        assertThat(readings).doesNotContain(oldReading)
    }

    @Test
    fun insertAndGetAlerts() = runTest {
        val alert1 = AlertInfoEntity("alert1", "dev1", null, "HIGH_TEMP", "CRITICAL", "Temp too high!", System.currentTimeMillis() - 100, false)
        val alert2 = AlertInfoEntity("alert2", "dev2", null, "LOW_FEED", "WARNING", "Feed low", System.currentTimeMillis(), true)
        dao.insertAlerts(listOf(alert1, alert2))

        val allAlerts = dao.getAllAlerts().first()
        assertThat(allAlerts).hasSize(2)

        val unacknowledged = dao.getUnacknowledgedAlerts().first()
        assertThat(unacknowledged).containsExactly(alert1) // Order is DESC
    }

    @Test
    fun updateAlert() = runTest {
        val alert = AlertInfoEntity("alert1", "dev1", null, "HIGH_TEMP", "CRITICAL", "Temp too high!", System.currentTimeMillis(), false)
        dao.insertAlerts(listOf(alert))

        var unacknowledged = dao.getUnacknowledgedAlerts().first()
        assertThat(unacknowledged).hasSize(1)

        val updatedAlert = alert.copy(acknowledged = true)
        dao.updateAlert(updatedAlert)

        unacknowledged = dao.getUnacknowledgedAlerts().first()
        assertThat(unacknowledged).isEmpty()
    }

    // TODO: Add tests for other DAOs:
    // - Humidity, FeedLevel, WaterConsumption, LightLevel (similar to Temperature)
    // - DeviceConfig (insert, get, getDeviceConfigsToSync, update)
}
