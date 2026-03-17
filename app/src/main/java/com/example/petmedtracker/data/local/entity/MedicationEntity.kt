package com.example.petmedtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class MedicationEntity(
    @PrimaryKey
    val id: String,
    val petId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val notesInstructions: String = "",
    val startDate: String,
    val duration: String
)
