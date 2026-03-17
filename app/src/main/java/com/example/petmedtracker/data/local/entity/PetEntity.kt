package com.example.petmedtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val species: String = "",
    val breed: String = "",
    val birthday: String = ""
)
