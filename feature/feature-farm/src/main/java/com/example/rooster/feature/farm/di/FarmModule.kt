package com.example.rooster.feature.farm.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.farm.data.local.FarmDatabase
import com.example.rooster.feature.farm.data.repository.FarmRepository
import com.example.rooster.feature.farm.data.repository.FarmRepositoryImpl
import com.example.rooster.feature.farm.data.repository.MortalityRepository
import com.example.rooster.feature.farm.data.repository.MortalityRepositoryImpl
import com.example.rooster.feature.farm.data.repository.SensorDataRepository
import com.example.rooster.feature.farm.data.repository.SensorDataRepositoryImpl
import com.example.rooster.feature.farm.data.repository.VaccinationRepository
import com.example.rooster.feature.farm.data.repository.VaccinationRepositoryImpl
import com.example.rooster.feature.farm.data.repository.UpdateRepository
import com.example.rooster.feature.farm.data.repository.UpdateRepositoryImpl
import com.example.rooster.feature.farm.data.remote.FarmRemoteDataSource
import com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCase
import com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase
import com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase
import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase
import com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetSensorDataByDeviceUseCase
import com.example.rooster.feature.farm.domain.usecase.GetSensorDataByDeviceUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.GetVaccinationRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.GetVaccinationRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase
import com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.SaveVaccinationRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.SaveVaccinationRecordsUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCase
import com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCase
import com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCaseImpl
import com.example.rooster.feature.farm.domain.usecase.DeleteVaccinationRecordUseCase
import com.example.rooster.feature.farm.domain.usecase.DeleteVaccinationRecordUseCaseImpl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FarmBindsModule {

    @Binds
    @Singleton
    abstract fun bindFarmRepository(
        impl: FarmRepositoryImpl
    ): FarmRepository

    @Binds
    @Singleton
    abstract fun bindGetFarmDetailsUseCase(
        impl: GetFarmDetailsUseCaseImpl
    ): GetFarmDetailsUseCase

    @Binds
    @Singleton
    abstract fun bindGetFlocksByTypeUseCase(
        impl: GetFlocksByTypeUseCaseImpl
    ): GetFlocksByTypeUseCase

    @Binds
    @Singleton
    abstract fun bindSensorDataRepository(
        impl: SensorDataRepositoryImpl
    ): SensorDataRepository

    @Binds
    @Singleton
    abstract fun bindGetAllSensorDataUseCase(
        impl: GetAllSensorDataUseCaseImpl
    ): GetAllSensorDataUseCase

    @Binds
    @Singleton
    abstract fun bindGetSensorDataByDeviceUseCase(
        impl: GetSensorDataByDeviceUseCaseImpl
    ): GetSensorDataByDeviceUseCase

    @Binds
    @Singleton
    abstract fun bindVaccinationRepository(
        impl: VaccinationRepositoryImpl
    ): VaccinationRepository

    @Binds
    @Singleton
    abstract fun bindGetVaccinationRecordsUseCase(
        impl: GetVaccinationRecordsUseCaseImpl
    ): GetVaccinationRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindSaveVaccinationRecordsUseCase(
        impl: SaveVaccinationRecordsUseCaseImpl
    ): SaveVaccinationRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteVaccinationRecordUseCase(
        impl: DeleteVaccinationRecordUseCaseImpl
    ): DeleteVaccinationRecordUseCase

    @Binds
    @Singleton
    abstract fun bindGetFamilyTreeUseCase(
        impl: GetFamilyTreeUseCaseImpl
    ): GetFamilyTreeUseCase

    @Binds
    @Singleton
    abstract fun bindRegisterFlockUseCase(
        impl: RegisterFlockUseCaseImpl
    ): RegisterFlockUseCase

    @Binds
    @Singleton
    abstract fun bindMortalityRepository(
        impl: MortalityRepositoryImpl
    ): MortalityRepository

    @Binds
    @Singleton
    abstract fun bindGetMortalityRecordsUseCase(
        impl: GetMortalityRecordsUseCaseImpl
    ): GetMortalityRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindSaveMortalityRecordsUseCase(
        impl: SaveMortalityRecordsUseCaseImpl
    ): SaveMortalityRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteMortalityRecordUseCase(
        impl: DeleteMortalityRecordUseCaseImpl
    ): DeleteMortalityRecordUseCase

    @Binds
    @Singleton
    abstract fun bindUpdateRepository(
        impl: UpdateRepositoryImpl
    ): UpdateRepository

    @Binds
    @Singleton
    abstract fun bindGetUpdateRecordsUseCase(
        impl: GetUpdateRecordsUseCaseImpl
    ): GetUpdateRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindSaveUpdateRecordsUseCase(
        impl: SaveUpdateRecordsUseCaseImpl
    ): SaveUpdateRecordsUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteUpdateRecordUseCase(
        impl: DeleteUpdateRecordUseCaseImpl
    ): DeleteUpdateRecordUseCase
}

@Module
@InstallIn(SingletonComponent::class)
object FarmProvidesModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FarmDatabase {
        return Room.databaseBuilder(
            context,
            FarmDatabase::class.java,
            "farm_database"
        ).build()
    }

    @Provides
    fun provideFlockDao(db: FarmDatabase) = db.flockDao()

    @Provides
    fun provideMortalityDao(db: FarmDatabase) = db.mortalityDao()

    @Provides
    fun provideVaccinationDao(db: FarmDatabase) = db.vaccinationDao()

    @Provides
    fun provideSensorDataDao(db: FarmDatabase) = db.sensorDataDao()

    @Provides
    fun provideUpdateDao(db: FarmDatabase) = db.updateDao()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): DatabaseReference = FirebaseDatabase.getInstance().reference
}
