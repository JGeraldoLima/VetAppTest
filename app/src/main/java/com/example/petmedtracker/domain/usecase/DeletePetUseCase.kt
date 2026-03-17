package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.PetRepository

class DeletePetUseCase constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String): Result<Unit> =
        petRepository.deletePet(petId)
}
