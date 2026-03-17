package com.example.petmedtracker.data.mapper

import com.example.petmedtracker.data.local.entity.MedicationEntity
import com.example.petmedtracker.models.Medication

fun MedicationEntity.toProto(): Medication = Medication.newBuilder()
    .setId(id)
    .setPetId(petId)
    .setMedicationName(medicationName)
    .setDosage(dosage)
    .setFrequency(frequency)
    .setNotesInstructions(notesInstructions)
    .setStartDate(startDate)
    .setDuration(duration)
    .build()

fun Medication.toEntity(): MedicationEntity = MedicationEntity(
    id = id,
    petId = petId,
    medicationName = medicationName,
    dosage = dosage,
    frequency = frequency,
    notesInstructions = notesInstructions,
    startDate = startDate,
    duration = duration
)
