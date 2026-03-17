package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.PetRepository
import com.example.petmedtracker.models.Pet

class AddPetUseCase constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet): Result<Unit> =
        petRepository.addPet(pet)
}
