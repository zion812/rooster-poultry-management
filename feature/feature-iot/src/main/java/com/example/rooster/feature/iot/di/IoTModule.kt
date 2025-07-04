package com.example.rooster.feature.iot.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.iot.data.local.IoTDatabase
import com.example.rooster.feature.iot.data.local.SensorDataDao
import com.example.rooster.feature.iot.data.repository.IoTRepository
import com.example.rooster.feature.iot.data.repository.IoTRepositoryImpl
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IoTDatabaseModule {

    @Provides
    @Singleton
    fun provideIoTDatabase(@ApplicationContext context: Context): IoTDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            IoTDatabase::class.java,
            "iot_database"
        )
        // .fallbackToDestructiveMigration() // Add proper migrations in production
        .build()
    }

    @Provides
    @Singleton
    fun provideSensorDataDao(iotDatabase: IoTDatabase): SensorDataDao {
        return iotDatabase.sensorDataDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase {
        return Firebase.database // Ensure google-services.json has databaseURL
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class IoTRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIoTRepository(
        iotRepositoryImpl: IoTRepositoryImpl
    ): IoTRepository
}
