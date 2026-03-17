package com.example.petmedtracker.domain.repository

import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getPets(): Flow<List<Pet>>
    suspend fun getPetById(id: String): Pet?
    suspend fun addPet(pet: Pet): Result<Unit>
}
