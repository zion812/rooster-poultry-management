package com.example.rooster.feature.farm.data.remote

import com.example.rooster.core.common.Result
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
    private val flocksCollection = firestore.collection("flocks_v2") // Using a potentially versioned collection
    private val lineageLinksCollection = firestore.collection("lineage_links")


    fun getFlockRealTime(flockId: String): Flow<Result<Map<String, Any>?>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val data = snapshot.getValue() as? Map<String, Any>
                trySend(Result.Success(data))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Error(error.toException()))
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
                        val value = child.getValue() as? Map<String, Any> ?: continue
                        flocks.add(value)
                    }
                    trySend(Result.Success(flocks))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.Error(error.toException()))
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
                        val value = child.getValue() as? Map<String, Any> ?: continue
                        records.add(value)
                    }
                    trySend(Result.Success(records))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.Error(error.toException()))
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
                        val value = child.getValue() as? Map<String, Any> ?: continue
                        readings.add(value)
                    }
                    trySend(Result.Success(readings))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.Error(error.toException()))
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
            // Ensure 'id' is part of the map being saved if it was generated.
            val dataToSave = flockData.toMutableMap()
            dataToSave["id"] = id // Ensure ID is in the map
            dataToSave["updatedAt"] = ServerValue.TIMESTAMP

            // Save to both Firestore and Realtime Database
            flocksCollection.document(id).set(dataToSave).await()
            realtimeDatabase.child("flocks_v2").child(id).setValue(dataToSave).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveMortalityRecord(recordData: Map<String, Any>): Result<Unit> {
        return try {
            val id = recordData["id"] as? String ?: UUID.randomUUID().toString()
            val dataWithTimestamp = recordData.toMutableMap().apply {
                this["createdAt"] = ServerValue.TIMESTAMP
                this["id"] = id
            }

            firestore.collection("mortality_records").document(id).set(dataWithTimestamp).await()
            // Assuming mortality also goes to a versioned path or specific path
            realtimeDatabase.child("mortality_records_v2").child(id).setValue(dataWithTimestamp)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveSensorData(sensorData: Map<String, Any>): Result<Unit> {
        return try {
            val id = sensorData["id"] as? String ?: UUID.randomUUID().toString()
            val dataWithTimestamp = sensorData.toMutableMap().apply {
                this["timestamp"] = ServerValue.TIMESTAMP
                this["id"] = id
            }

            // Only save to Realtime Database for sensor data (for performance)
            realtimeDatabase.child("sensor_data_v2").child(id).setValue(dataWithTimestamp).await() // Use versioned RTDB path

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteFlock(flockId: String): Result<Unit> {
        return try {
            flocksCollection.document(flockId).delete().await() // Use defined collection
            realtimeDatabase.child("flocks_v2").child(flockId).removeValue().await() // Use versioned RTDB path
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Lineage Link Methods ---
    suspend fun saveLineageLink(link: com.example.rooster.feature.farm.data.local.LineageLinkEntity): Result<Unit> {
        return try {
            // Create a unique ID for the link document, e.g., childId_parentId_type
            val documentId = "${link.childFlockId}_${link.parentFlockId}_${link.relationshipType.name}"
            // Store a map representation, excluding needsSync or handling it if remote also tracks sync
            val remoteLinkData = mapOf(
                "childFlockId" to link.childFlockId,
                "parentFlockId" to link.parentFlockId,
                "relationshipType" to link.relationshipType.name,
                "timestamp" to FieldValue.serverTimestamp() // Add a timestamp for auditing
            )
            lineageLinksCollection.document(documentId).set(remoteLinkData).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteLineageLink(childFlockId: String, parentFlockId: String, relationshipTypeName: String): Result<Unit> {
        return try {
            val documentId = "${childFlockId}_${parentFlockId}_${relationshipTypeName}"
            lineageLinksCollection.document(documentId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception)
        {
            Result.Error(e)
        }
    }
}
