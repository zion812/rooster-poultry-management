package com.rooster.farmerhome.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.rooster.farmerhome.data.local.db.FarmerHomeDatabase
import com.rooster.farmerhome.data.local.model.WeatherDataEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat // Using Truth for assertions

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData if used, good practice for DAOs

    private lateinit var database: FarmerHomeDatabase
    private lateinit var weatherDao: WeatherDao

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FarmerHomeDatabase::class.java
        )
        .setTransactionExecutor(testDispatcher.asExecutor()) // Use test dispatcher for transactions
        .setQueryExecutor(testDispatcher.asExecutor()) // Use test dispatcher for queries
        .allowMainThreadQueries() // Allowing for simplicity in tests, not for production
        .build()
        weatherDao = database.weatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertWeatherDataAndGetIt() = testScope.runTest {
        val weatherDataEntity = WeatherDataEntity(
            id = "test_weather",
            temperature = "25C",
            humidity = "60%",
            precipitation = "0mm",
            windSpeed = "10kmh",
            description = "Sunny",
            location = "Test Location",
            timestamp = System.currentTimeMillis(),
            apiError = null
        )
        weatherDao.insertWeatherData(weatherDataEntity)

        weatherDao.getWeatherData("test_weather").test {
            val retrieved = awaitItem()
            assertThat(retrieved).isNotNull()
            assertThat(retrieved?.id).isEqualTo(weatherDataEntity.id)
            assertThat(retrieved?.temperature).isEqualTo(weatherDataEntity.temperature)
            awaitComplete() // or cancelAndConsumeRemainingEvents() if flow doesn't complete
        }
    }

    @Test
    fun insertWeatherDataReplacesOnConflict() = testScope.runTest {
        val initialEntity = WeatherDataEntity("current_weather", "20C", "50%", "1mm", "5kmh", "Cloudy", "Old Loc", System.currentTimeMillis() - 1000)
        weatherDao.insertWeatherData(initialEntity)

        val newEntity = WeatherDataEntity("current_weather", "30C", "70%", "0mm", "15kmh", "Very Sunny", "New Loc", System.currentTimeMillis())
        weatherDao.insertWeatherData(newEntity)

        weatherDao.getWeatherData("current_weather").test {
            val retrieved = awaitItem()
            assertThat(retrieved).isNotNull()
            assertThat(retrieved?.temperature).isEqualTo(newEntity.temperature)
            assertThat(retrieved?.location).isEqualTo(newEntity.location)
            awaitComplete()
        }
    }

    @Test
    fun deleteWeatherDataRemovesIt() = testScope.runTest {
        val weatherDataEntity = WeatherDataEntity("to_delete", "22C", "55%", "0.5mm", "7kmh", "Partly Cloudy", "Delete Loc", System.currentTimeMillis())
        weatherDao.insertWeatherData(weatherDataEntity)

        // Ensure it's there first
        weatherDao.getWeatherData("to_delete").test {
            assertThat(awaitItem()).isNotNull()
            awaitComplete()
        }

        weatherDao.deleteWeatherData("to_delete")

        weatherDao.getWeatherData("to_delete").test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }

    @Test
    fun getWeatherDataReturnsNullIfNotExist() = testScope.runTest {
         weatherDao.getWeatherData("non_existent_id").test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }
}
