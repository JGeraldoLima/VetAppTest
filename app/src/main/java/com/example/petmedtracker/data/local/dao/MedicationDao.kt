package com.example.petmedtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petmedtracker.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<MedicationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medication: MedicationEntity)

    @Query("SELECT * FROM medications WHERE petId = :petId ORDER BY startDate DESC")
    fun getMedicationsByPetId(petId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE petId = :petId ORDER BY startDate DESC")
    suspend fun getMedicationsByPetIdOnce(petId: String): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): MedicationEntity?

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteById(id: String)
}
