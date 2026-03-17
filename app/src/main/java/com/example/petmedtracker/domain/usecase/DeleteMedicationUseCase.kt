package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.MedicationRepository

class DeleteMedicationUseCase constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(medicationId: String): Result<Unit> =
        medicationRepository.deleteMedication(medicationId)
}
