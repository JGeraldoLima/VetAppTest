package com.example.petmedtracker.data.mapper

import com.example.petmedtracker.data.local.entity.PetEntity
import com.example.petmedtracker.models.Pet

fun PetEntity.toProto(): Pet = Pet.newBuilder()
    .setId(id)
    .setName(name)
    .setSpecies(species)
    .setBreed(breed)
    .setBirthday(birthday)
    .build()

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    name = name,
    species = species,
    breed = breed,
    birthday = birthday
)
