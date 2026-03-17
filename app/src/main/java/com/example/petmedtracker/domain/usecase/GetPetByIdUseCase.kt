package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.PetRepository
import com.example.petmedtracker.models.Pet

class GetPetByIdUseCase constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(id: String): Pet? = petRepository.getPetById(id)
}
