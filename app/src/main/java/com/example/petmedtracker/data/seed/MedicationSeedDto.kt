package com.example.petmedtracker.data.seed

import com.example.petmedtracker.data.local.entity.MedicationEntity
import com.google.gson.annotations.SerializedName

data class MedicationSeedDto(
    @SerializedName("id") val id: String,
    @SerializedName("petId") val petId: String,
    @SerializedName("medicationName") val medicationName: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("notesInstructions") val notesInstructions: String? = null,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("duration") val duration: String
) {
    fun toEntity(): MedicationEntity = MedicationEntity(
        id = id,
        petId = petId,
        medicationName = medicationName,
        dosage = dosage,
        frequency = frequency,
        notesInstructions = notesInstructions ?: "",
        startDate = startDate,
        duration = duration
    )
}
