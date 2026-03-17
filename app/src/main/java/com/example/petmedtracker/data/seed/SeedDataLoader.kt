package com.example.petmedtracker.data.seed

import android.content.Context
import com.example.petmedtracker.data.local.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class SeedDataLoader(
    private val context: Context,
    private val database: AppDatabase,
    private val gson: Gson
) {

    suspend fun loadIfNeeded() = withContext(Dispatchers.IO) {
        val petDao = database.petDao()
        val medicationDao = database.medicationDao()
        val existingPets = petDao.getPetByIdOnce("pet-1")
        if (existingPets != null) return@withContext

        context.assets.open(PETS_FILE).use { input ->
            val type = object : TypeToken<List<PetSeedDto>>() {}.type
            val dtos: List<PetSeedDto> = gson.fromJson(InputStreamReader(input), type)
            petDao.insertAll(dtos.map { it.toEntity() })
        }
        context.assets.open(MEDICATIONS_FILE).use { input ->
            val type = object : TypeToken<List<MedicationSeedDto>>() {}.type
            val dtos: List<MedicationSeedDto> = gson.fromJson(InputStreamReader(input), type)
            medicationDao.insertAll(dtos.map { it.toEntity() })
        }
    }

    private companion object {
        const val PETS_FILE = "pets.json"
        const val MEDICATIONS_FILE = "medications.json"
    }
}
