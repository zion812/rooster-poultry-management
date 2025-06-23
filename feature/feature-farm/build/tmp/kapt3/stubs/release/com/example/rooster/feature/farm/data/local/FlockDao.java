package com.example.rooster.feature.farm.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\f2\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\f2\u0006\u0010\u000f\u001a\u00020\tH\'J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\f2\u0006\u0010\u0011\u001a\u00020\tH\'J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\f2\u0006\u0010\u0013\u001a\u00020\tH\'J\u0016\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/feature/farm/data/local/FlockDao;", "", "delete", "", "entity", "Lcom/example/rooster/feature/farm/data/local/FlockEntity;", "(Lcom/example/rooster/feature/farm/data/local/FlockEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "Lkotlinx/coroutines/flow/Flow;", "getByOwner", "", "ownerId", "getByType", "type", "getOffspring", "parentId", "insert", "update", "feature-farm_release"})
@androidx.room.Dao()
public abstract interface FlockDao {
    
    @androidx.room.Query(value = "SELECT * FROM flocks WHERE id = :id")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.example.rooster.feature.farm.data.local.FlockEntity> getById(@org.jetbrains.annotations.NotNull()
    java.lang.String id);
    
    @androidx.room.Query(value = "SELECT * FROM flocks WHERE ownerId = :ownerId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.FlockEntity>> getByOwner(@org.jetbrains.annotations.NotNull()
    java.lang.String ownerId);
    
    @androidx.room.Query(value = "SELECT * FROM flocks WHERE type = :type")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.FlockEntity>> getByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type);
    
    @androidx.room.Query(value = "SELECT * FROM flocks WHERE fatherId = :parentId OR motherId = :parentId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.rooster.feature.farm.data.local.FlockEntity>> getOffspring(@org.jetbrains.annotations.NotNull()
    java.lang.String parentId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FlockEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FlockEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.data.local.FlockEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM flocks WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}