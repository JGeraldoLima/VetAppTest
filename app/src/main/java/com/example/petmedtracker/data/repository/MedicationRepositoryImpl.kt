package com.example.petmedtracker.data.repository

import com.example.petmedtracker.data.local.dao.MedicationDao
import com.example.petmedtracker.data.mapper.toEntity
import com.example.petmedtracker.data.mapper.toProto
import com.example.petmedtracker.domain.repository.MedicationRepository
import com.example.petmedtracker.models.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class MedicationRepositoryImpl constructor(
    private val medicationDao: MedicationDao
) : MedicationRepository {

    override fun getMedicationsForPet(petId: String): Flow<List<Medication>> =
        medicationDao.getMedicationsByPetId(petId).map { entities ->
            entities.map { it.toProto() }
        }

    override suspend fun addMedication(medication: Medication): Result<Unit> = runCatching {
        medicationDao.insert(medication.toEntity())
    }
}
