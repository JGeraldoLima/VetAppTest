package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.MedicationRepository
import com.example.petmedtracker.models.Medication
import kotlinx.coroutines.flow.Flow

class GetMedicationsForPetUseCase constructor(
    private val medicationRepository: MedicationRepository
) {
    operator fun invoke(petId: String): Flow<List<Medication>> =
        medicationRepository.getMedicationsForPet(petId)
}
