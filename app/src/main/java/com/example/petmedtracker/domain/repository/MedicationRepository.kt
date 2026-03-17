package com.example.petmedtracker.domain.repository

import com.example.petmedtracker.models.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getMedicationsForPet(petId: String): Flow<List<Medication>>
    suspend fun getMedicationById(id: String): Medication?
    suspend fun addMedication(medication: Medication): Result<Unit>
    suspend fun deleteMedication(id: String): Result<Unit>
}
