package com.example.petmedtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petmedtracker.data.local.dao.MedicationDao
import com.example.petmedtracker.data.local.dao.PetDao
import com.example.petmedtracker.data.local.entity.MedicationEntity
import com.example.petmedtracker.data.local.entity.PetEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE pets ADD COLUMN birthday TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE medications ADD COLUMN voiceNotePath TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [PetEntity::class, MedicationEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun medicationDao(): MedicationDao
}
