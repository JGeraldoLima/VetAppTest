package com.example.petmedtracker.domain.repository

import com.example.petmedtracker.models.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getMedicationsForPet(petId: String): Flow<List<Medication>>
    suspend fun addMedication(medication: Medication): Result<Unit>
}
