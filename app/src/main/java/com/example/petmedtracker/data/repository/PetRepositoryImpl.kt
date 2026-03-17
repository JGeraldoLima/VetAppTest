package com.example.petmedtracker.data.repository

import com.example.petmedtracker.data.local.dao.PetDao
import com.example.petmedtracker.data.mapper.toEntity
import com.example.petmedtracker.data.mapper.toProto
import com.example.petmedtracker.domain.repository.PetRepository
import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class PetRepositoryImpl constructor(
    private val petDao: PetDao
) : PetRepository {

    override fun getPets(): Flow<List<Pet>> =
        petDao.getPets().map { entities -> entities.map { it.toProto() } }

    override suspend fun getPetById(id: String): Pet? =
        petDao.getPetByIdOnce(id)?.toProto()

    override suspend fun addPet(pet: Pet): Result<Unit> = runCatching {
        petDao.insert(pet.toEntity())
    }

    override suspend fun deletePet(id: String): Result<Unit> = runCatching {
        petDao.deleteById(id)
    }
}
