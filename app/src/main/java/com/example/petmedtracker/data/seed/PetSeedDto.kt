package com.example.petmedtracker.data.seed

import com.example.petmedtracker.data.local.entity.PetEntity
import com.google.gson.annotations.SerializedName

data class PetSeedDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("species") val species: String? = null,
    @SerializedName("breed") val breed: String? = null,
    @SerializedName("birthday") val birthday: String? = null
) {
    fun toEntity(): PetEntity = PetEntity(
        id = id,
        name = name,
        species = species ?: "",
        breed = breed ?: "",
        birthday = birthday ?: ""
    )
}
