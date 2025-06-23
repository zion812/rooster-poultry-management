package com.example.rooster.feature.farm.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\b\u0010\u000e\u001a\u00020\u000fH\u0007J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\u0004H\u0007\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/feature/farm/di/FarmProvidesModule;", "", "()V", "provideDatabase", "Lcom/example/rooster/feature/farm/data/local/FarmDatabase;", "context", "Landroid/content/Context;", "provideFirestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "provideFlockDao", "Lcom/example/rooster/feature/farm/data/local/FlockDao;", "db", "provideMortalityDao", "Lcom/example/rooster/feature/farm/data/local/MortalityDao;", "provideRealtimeDatabase", "Lcom/google/firebase/database/DatabaseReference;", "provideSensorDataDao", "Lcom/example/rooster/feature/farm/data/local/SensorDataDao;", "provideUpdateDao", "Lcom/example/rooster/feature/farm/data/local/UpdateDao;", "provideVaccinationDao", "Lcom/example/rooster/feature/farm/data/local/VaccinationDao;", "feature-farm_staging"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class FarmProvidesModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.feature.farm.di.FarmProvidesModule INSTANCE = null;
    
    private FarmProvidesModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.FarmDatabase provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.FlockDao provideFlockDao(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FarmDatabase db) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.MortalityDao provideMortalityDao(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FarmDatabase db) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.VaccinationDao provideVaccinationDao(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FarmDatabase db) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.SensorDataDao provideSensorDataDao(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FarmDatabase db) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.data.local.UpdateDao provideUpdateDao(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FarmDatabase db) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.firebase.firestore.FirebaseFirestore provideFirestore() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.firebase.database.DatabaseReference provideRealtimeDatabase() {
        return null;
    }
}