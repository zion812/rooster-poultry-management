package com.rooster.vethome.di

import com.rooster.vethome.data.repository.VetConsultationRepositoryImpl
import com.rooster.vethome.data.repository.VetHealthAlertRepositoryImpl
import com.rooster.vethome.data.repository.VetPatientRepositoryImpl
import com.rooster.vethome.data.source.MockVetConsultationRemoteDataSource
import com.rooster.vethome.data.source.MockVetHealthAlertRemoteDataSource
import com.rooster.vethome.data.source.MockVetPatientRemoteDataSource
import com.rooster.vethome.data.source.VetConsultationRemoteDataSource
import com.rooster.vethome.data.source.VetHealthAlertRemoteDataSource
import com.rooster.vethome.data.source.VetPatientRemoteDataSource
import com.rooster.vethome.domain.repository.VetConsultationRepository
import com.rooster.vethome.domain.repository.VetHealthAlertRepository
import com.rooster.vethome.domain.repository.VetPatientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ActivityRetainedComponent for ViewModel scope
abstract class VetHomeDataModule {

    // Consultation Queue
    @Binds
    @Singleton
    abstract fun bindVetConsultationRepository(impl: VetConsultationRepositoryImpl): VetConsultationRepository
    @Binds
    @Singleton
    abstract fun bindVetConsultationRemoteDataSource(impl: MockVetConsultationRemoteDataSource): VetConsultationRemoteDataSource

    // Patient History
    @Binds
    @Singleton
    abstract fun bindVetPatientRepository(impl: VetPatientRepositoryImpl): VetPatientRepository
    @Binds
    @Singleton
    abstract fun bindVetPatientRemoteDataSource(impl: MockVetPatientRemoteDataSource): VetPatientRemoteDataSource

    // Health Alerts
    @Binds
    @Singleton
    abstract fun bindVetHealthAlertRepository(impl: VetHealthAlertRepositoryImpl): VetHealthAlertRepository
    @Binds
    @Singleton
    abstract fun bindVetHealthAlertRemoteDataSource(impl: MockVetHealthAlertRemoteDataSource): VetHealthAlertRemoteDataSource
}
