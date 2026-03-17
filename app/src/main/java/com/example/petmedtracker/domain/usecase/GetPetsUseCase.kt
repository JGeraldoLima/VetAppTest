package com.example.petmedtracker.domain.usecase

import com.example.petmedtracker.domain.repository.PetRepository
import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.flow.Flow

class GetPetsUseCase constructor(
    private val petRepository: PetRepository
) {
    operator fun invoke(): Flow<List<Pet>> = petRepository.getPets()
}
