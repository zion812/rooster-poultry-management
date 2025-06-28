package com.example.rooster.di

import com.example.rooster.data.repositories.AuthRepositoryImpl
import com.example.rooster.data.repositories.ChatRepositoryImpl
import com.example.rooster.data.repositories.PaymentRepository
import com.example.rooster.data.repositories.UserRepositoryImpl
import com.example.rooster.domain.repository.AuthRepository
import com.example.rooster.domain.repository.ChatRepository
import com.example.rooster.domain.repository.PostRepository
import com.example.rooster.domain.repository.UserRepository
import com.example.rooster.util.ShoppingCartManager
import com.example.rooster.core.common.user.UserIdProvider
import com.example.rooster.core.common.storage.ImageUploadService // Import interface
import com.example.rooster.data.authprovider.FirebaseUserIdProvider
import com.example.rooster.data.storage.FirebaseStorageImageUploadService // Import impl
import com.google.firebase.storage.FirebaseStorage // Import FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.rooster.data.AuthRepository as ConcreteAuthRepository
import com.example.rooster.data.repositories.PostRepository as ConcretePostRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideConcreteAuthRepository(): ConcreteAuthRepository {
        return ConcreteAuthRepository()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun providePaymentRepository(): PaymentRepository = PaymentRepository()

    @Provides
    @Singleton
    fun providePostRepository(impl: ConcretePostRepository): PostRepository = impl

    @Provides
    @Singleton
    fun provideShoppingCartManager(): ShoppingCartManager = ShoppingCartManager()

    @Provides
    @com.example.rooster.core.network.qualifiers.PaymentApiBaseUrl // Fully qualify if not imported
    @Singleton
    fun providePaymentApiBaseUrl(): String {
        return com.example.rooster.BuildConfig.PAYMENT_API_BASE_URL // Fully qualify BuildConfig
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage { // Provide FirebaseStorage
        return FirebaseStorage.getInstance()
    }
}

// AuthBindsModule remains separate for @Binds methods
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindsModule {
    @Binds
    @Singleton
    abstract fun bindUserIdProvider(impl: FirebaseUserIdProvider): UserIdProvider

    @Binds
    @Singleton
    abstract fun bindImageUploadService(impl: FirebaseStorageImageUploadService): ImageUploadService
}
