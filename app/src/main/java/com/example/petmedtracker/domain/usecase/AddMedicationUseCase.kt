package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.MedicationRepository
import com.example.petmedtracker.models.Medication

class AddMedicationUseCase constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(medication: Medication): Result<Unit> =
        medicationRepository.addMedication(medication)
}
