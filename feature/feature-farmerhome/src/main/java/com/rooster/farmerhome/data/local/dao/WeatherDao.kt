package com.rooster.farmerhome.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooster.farmerhome.data.local.model.WeatherDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_data WHERE id = :id")
    fun getWeatherData(id: String = "current_weather"): Flow<WeatherDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

    @Query("DELETE FROM weather_data WHERE id = :id")
    suspend fun deleteWeatherData(id: String = "current_weather")
}
