package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.MedicationRepository
import com.example.petmedtracker.models.Medication

class GetMedicationByIdUseCase constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(id: String): Medication? =
        medicationRepository.getMedicationById(id)
}
