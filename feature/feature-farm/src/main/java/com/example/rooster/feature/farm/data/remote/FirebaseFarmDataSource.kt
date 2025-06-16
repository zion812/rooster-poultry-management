package com.example.rooster.feature.farm.data.remote

import com.google.firebase.database.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFarmDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val realtimeDatabase: DatabaseReference
) {

    fun getFlockRealTime(flockId: String): Flow<Result<Map<String, Any>?>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val data = snapshot.getValue() as? Map<String, Any>
                trySend(Result.success(data))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }

        realtimeDatabase.child("flocks").child(flockId).addValueEventListener(listener)

        awaitClose {
            realtimeDatabase.child("flocks").child(flockId).removeEventListener(listener)
        }
    }

    fun getFlocksByOwnerRealTime(ownerId: String): Flow<Result<List<Map<String, Any>>>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val flocks = mutableListOf<Map<String, Any>>()
                    for (child in snapshot.children) {
                        @Suppress("UNCHECKED_CAST")
                        child.getValue()?.let { value ->
                            flocks.add(value as Map<String, Any>)
                        }
                    }
                    trySend(Result.success(flocks))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(error.toException()))
                }
            }

            realtimeDatabase.child("flocks")
                .orderByChild("ownerId")
                .equalTo(ownerId)
                .addValueEventListener(listener)

            awaitClose {
                realtimeDatabase.child("flocks").removeEventListener(listener)
            }
        }

    fun getMortalityRecordsRealTime(fowlId: String): Flow<Result<List<Map<String, Any>>>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val records = mutableListOf<Map<String, Any>>()
                    for (child in snapshot.children) {
                        @Suppress("UNCHECKED_CAST")
                        child.getValue()?.let { value ->
                            records.add(value as Map<String, Any>)
                        }
                    }
                    trySend(Result.success(records))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(error.toException()))
                }
            }

            realtimeDatabase.child("mortality_records")
                .orderByChild("fowlId")
                .equalTo(fowlId)
                .addValueEventListener(listener)

            awaitClose {
                realtimeDatabase.child("mortality_records").removeEventListener(listener)
            }
        }

    fun getSensorDataRealTime(deviceId: String): Flow<Result<List<Map<String, Any>>>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readings = mutableListOf<Map<String, Any>>()
                    for (child in snapshot.children) {
                        @Suppress("UNCHECKED_CAST")
                        child.getValue()?.let { value ->
                            readings.add(value as Map<String, Any>)
                        }
                    }
                    trySend(Result.success(readings))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(error.toException()))
                }
            }

            realtimeDatabase.child("sensor_data")
                .orderByChild("deviceId")
                .equalTo(deviceId)
                .limitToLast(100)
                .addValueEventListener(listener)

            awaitClose {
                realtimeDatabase.child("sensor_data").removeEventListener(listener)
            }
        }

    suspend fun saveFlock(flockData: Map<String, Any>): Result<Unit> {
        return try {
            val id = flockData["id"] as? String ?: UUID.randomUUID().toString()
            val dataWithTimestamp = flockData.toMutableMap().apply {
                put("updatedAt", ServerValue.TIMESTAMP)
                put("id", id)
            }

            // Save to both Firestore and Realtime Database
            firestore.collection("flocks").document(id).set(dataWithTimestamp).await()
            realtimeDatabase.child("flocks").child(id).setValue(dataWithTimestamp).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMortalityRecord(recordData: Map<String, Any>): Result<Unit> {
        return try {
            val id = recordData["id"] as? String ?: UUID.randomUUID().toString()
            val dataWithTimestamp = recordData.toMutableMap().apply {
                put("createdAt", ServerValue.TIMESTAMP)
                put("id", id)
            }

            firestore.collection("mortality_records").document(id).set(dataWithTimestamp).await()
            realtimeDatabase.child("mortality_records").child(id).setValue(dataWithTimestamp)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSensorData(sensorData: Map<String, Any>): Result<Unit> {
        return try {
            val id = sensorData["id"] as? String ?: UUID.randomUUID().toString()
            val dataWithTimestamp = sensorData.toMutableMap().apply {
                put("timestamp", ServerValue.TIMESTAMP)
                put("id", id)
            }

            // Only save to Realtime Database for sensor data (for performance)
            realtimeDatabase.child("sensor_data").child(id).setValue(dataWithTimestamp).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFlock(flockId: String): Result<Unit> {
        return try {
            firestore.collection("flocks").document(flockId).delete().await()
            realtimeDatabase.child("flocks").child(flockId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}