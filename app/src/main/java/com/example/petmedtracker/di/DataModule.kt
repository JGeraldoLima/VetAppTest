package com.example.petmedtracker.di

import com.example.petmedtracker.data.local.AppDatabase
import com.example.petmedtracker.data.local.MIGRATION_1_2
import com.example.petmedtracker.data.local.MIGRATION_2_3
import com.example.petmedtracker.data.local.dao.MedicationDao
import com.example.petmedtracker.data.local.dao.PetDao
import com.example.petmedtracker.data.pdf.MedicationPdfExporter
import com.example.petmedtracker.data.voice.VoiceNoteHelper
import com.example.petmedtracker.data.repository.MedicationRepositoryImpl
import com.example.petmedtracker.data.repository.PetRepositoryImpl
import com.example.petmedtracker.data.seed.SeedDataLoader
import com.example.petmedtracker.domain.repository.MedicationRepository
import com.example.petmedtracker.domain.repository.PetRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import androidx.room.Room

val dataModule = module {

    single { Gson() }

    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "petmedtracker_db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }

    single<PetDao> { get<AppDatabase>().petDao() }
    single<MedicationDao> { get<AppDatabase>().medicationDao() }

    single<PetRepository> { PetRepositoryImpl(get()) }
    single<MedicationRepository> { MedicationRepositoryImpl(get()) }

    single {
        SeedDataLoader(
            context = androidContext(),
            database = get(),
            gson = get()
        )
    }

    single { MedicationPdfExporter(androidContext()) }
    single { VoiceNoteHelper(androidContext()) }
}
