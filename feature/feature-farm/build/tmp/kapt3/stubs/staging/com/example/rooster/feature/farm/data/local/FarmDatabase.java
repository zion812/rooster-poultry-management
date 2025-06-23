package com.example.rooster.feature.farm.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\r"}, d2 = {"Lcom/example/rooster/feature/farm/data/local/FarmDatabase;", "Landroidx/room/RoomDatabase;", "()V", "flockDao", "Lcom/example/rooster/feature/farm/data/local/FlockDao;", "mortalityDao", "Lcom/example/rooster/feature/farm/data/local/MortalityDao;", "sensorDataDao", "Lcom/example/rooster/feature/farm/data/local/SensorDataDao;", "updateDao", "Lcom/example/rooster/feature/farm/data/local/UpdateDao;", "vaccinationDao", "Lcom/example/rooster/feature/farm/data/local/VaccinationDao;", "feature-farm_staging"})
@androidx.room.Database(entities = {com.example.rooster.feature.farm.data.local.FlockEntity.class, com.example.rooster.feature.farm.data.local.MortalityEntity.class, com.example.rooster.feature.farm.data.local.VaccinationEntity.class, com.example.rooster.feature.farm.data.local.SensorDataEntity.class, com.example.rooster.feature.farm.data.local.UpdateEntity.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters(value = {com.example.rooster.feature.farm.data.local.Converters.class})
public abstract class FarmDatabase extends androidx.room.RoomDatabase {
    
    public FarmDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.feature.farm.data.local.FlockDao flockDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.feature.farm.data.local.MortalityDao mortalityDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.feature.farm.data.local.VaccinationDao vaccinationDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.feature.farm.data.local.SensorDataDao sensorDataDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.feature.farm.data.local.UpdateDao updateDao();
}