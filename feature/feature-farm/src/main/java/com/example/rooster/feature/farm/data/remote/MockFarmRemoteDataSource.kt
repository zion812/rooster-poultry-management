package com.example.rooster.feature.farm.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.farm.data.local.LineageLinkEntity
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockStatus
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.model.AgeGroup
import com.example.rooster.feature.farm.domain.model.Gender
import com.example.rooster.feature.farm.domain.model.HealthStatus
import com.example.rooster.feature.farm.domain.model.Purpose
import com.example.rooster.feature.farm.domain.model.VaccinationStatus
import com.example.rooster.feature.farm.domain.model.VerificationLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.Date
import java.util.Random
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockFarmRemoteDataSource @Inject constructor() : IFarmRemoteDataSource {

    private val random = Random()

    override fun getFlockStream(flockId: String): Flow<Result<Flock?>> = flow {
        delay(50) // Simulate network delay
        Timber.d("Mock: Streaming flock $flockId")
        emit(Result.Success(createMockFlockDomain(flockId)))
    }

    override fun getFlocksByOwnerStream(ownerId: String): Flow<Result<List<Flock>>> = flow {
        delay(50)
        Timber.d("Mock: Streaming flocks for owner $ownerId")
        emit(Result.Success(List(random.nextInt(5) + 1) { // 1 to 5 flocks
            createMockFlockDomain("flock_owner_${ownerId}_$it")
        }))
    }

    override suspend fun saveFlock(flock: Flock): Result<Unit> {
        delay(50)
        Timber.d("Mock: Saved flock ${flock.id} - ${flock.name}")
        // In a real mock, you might store this in a local list for verification
        return Result.Success(Unit)
    }

    override suspend fun deleteFlock(flockId: String): Result<Unit> {
        delay(50)
        Timber.d("Mock: Deleted flock $flockId")
        return Result.Success(Unit)
    }

    override suspend fun saveLineageLink(link: LineageLinkEntity): Result<Unit> {
        delay(50)
        Timber.d("Mock: Saved lineage link ${link.childFlockId} (${link.relationshipType}) ${link.parentFlockId}")
        return Result.Success(Unit)
    }

    override suspend fun deleteLineageLink(childFlockId: String, parentFlockId: String, relationshipTypeName: String): Result<Unit> {
        delay(50)
        Timber.d("Mock: Deleted lineage link $childFlockId -> $parentFlockId ($relationshipTypeName)")
        return Result.Success(Unit)
    }

    override fun getLineageLinksForChildStream(childFlockId: String): Flow<Result<List<LineageLinkEntity>>> = flow {
        delay(50)
        Timber.d("Mock: Streaming lineage links for child $childFlockId")
        emit(Result.Success(emptyList())) // Simple mock, can be expanded
    }

    override fun getLineageLinksForParentStream(parentFlockId: String): Flow<Result<List<LineageLinkEntity>>> = flow {
        delay(50)
        Timber.d("Mock: Streaming lineage links for parent $parentFlockId")
        emit(Result.Success(emptyList())) // Simple mock, can be expanded
    }

    private fun createMockFlockDomain(flockIdFromParam: String? = null): Flock {
        val flockId = flockIdFromParam ?: UUID.randomUUID().toString()
        return Flock(
            id = flockId,
            ownerId = "mock_owner_${random.nextInt(10)}",
            fatherId = if(random.nextBoolean()) "mock_father_${random.nextInt(10)}" else null,
            motherId = if(random.nextBoolean()) "mock_mother_${random.nextInt(10)}" else null,
            type = FlockType.values()[random.nextInt(FlockType.values().size)],
            name = "Mock ${FlockType.values()[random.nextInt(FlockType.values().size)].name.toLowerCase().capitalize()} $flockId".take(20),
            breed = listOf("Nattu Kodi", "Aseel", "Kadaknath", "Broiler", "Layer").random(),
            weight = random.nextFloat() * 3 + 1, // 1-4 kg
            height = random.nextFloat() * 20 + 30, // 30-50 cm
            color = listOf("Red", "Black", "White", "Mixed Brown", "Spotted").random(),
            gender = Gender.values()[random.nextInt(Gender.values().size)],
            certified = random.nextBoolean(),
            verified = random.nextBoolean(),
            verificationLevel = VerificationLevel.values()[random.nextInt(VerificationLevel.values().size)],
            traceable = random.nextBoolean(),
            ageGroup = AgeGroup.values()[random.nextInt(AgeGroup.values().size)],
            dateOfBirth = Date(System.currentTimeMillis() - random.nextInt(365 * 3) * 24L * 60 * 60 * 1000), // Up to 3 years old
            placeOfBirth = "Mock Village ${random.nextInt(5)}",
            currentAge = random.nextInt(150*7), // days
            vaccinationStatus = VaccinationStatus.values()[random.nextInt(VaccinationStatus.values().size)],
            lastVaccinationDate = Date(System.currentTimeMillis() - random.nextInt(90) * 24L * 60 * 60 * 1000),
            healthStatus = HealthStatus.values()[random.nextInt(HealthStatus.values().size)],
            lastHealthCheck = Date(System.currentTimeMillis() - random.nextInt(30) * 24L * 60 * 60 * 1000),
            identification = "TAG-${random.nextInt(10000)}",
            registryNumber = if(random.nextBoolean()) "REG-${random.nextInt(1000)}" else null,
            proofs = if(random.nextBoolean()) List(random.nextInt(3) + 1) { "http://example.com/proof${random.nextInt(100)}.jpg" } else emptyList(),
            specialty = if(random.nextBoolean()) listOf("Good fighter", "High egg yield", "Calm temperament").random() else null,
            productivityScore = random.nextInt(101),
            growthRate = random.nextDouble(),
            feedConversionRatio = random.nextDouble() * 2 + 1,
            status = FlockStatus.values()[random.nextInt(FlockStatus.values().size)],
            forSale = random.nextBoolean(),
            price = if(random.nextBoolean()) random.nextDouble() * 1000 + 500 else null,
            purpose = if(random.nextBoolean()) List(random.nextInt(2)+1) { Purpose.values()[random.nextInt(Purpose.values().size)] } else emptyList(),
            createdAt = Date(System.currentTimeMillis() - random.nextInt(730) * 24L * 60 * 60 * 1000), // Up to 2 years created
            updatedAt = Date(System.currentTimeMillis() - random.nextInt(30) * 24L * 60 * 60 * 1000) // Updated within last month
        )
    }
}
