package com.example.petmedtracker.di

import com.example.petmedtracker.domain.usecase.AddMedicationUseCase
import com.example.petmedtracker.domain.usecase.AddPetUseCase
import com.example.petmedtracker.domain.usecase.GetMedicationsForPetUseCase
import com.example.petmedtracker.domain.usecase.GetPetByIdUseCase
import com.example.petmedtracker.domain.usecase.GetPetsUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { GetPetsUseCase(get()) }
    factory { GetPetByIdUseCase(get()) }
    factory { GetMedicationsForPetUseCase(get()) }
    factory { AddMedicationUseCase(get()) }
    factory { AddPetUseCase(get()) }
}
