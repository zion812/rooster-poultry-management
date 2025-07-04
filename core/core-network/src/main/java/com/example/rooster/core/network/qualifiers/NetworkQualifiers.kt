package com.example.rooster.core.network.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentApiBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FarmManagementApiBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendBaseUrl // Adding this one too as it's in build.gradle

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FarmManagementApi // Qualifier for the Retrofit instance / ApiService
