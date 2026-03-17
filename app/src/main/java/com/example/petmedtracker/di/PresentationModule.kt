package com.example.petmedtracker.di

import androidx.lifecycle.SavedStateHandle
import com.example.petmedtracker.presentation.addmedication.AddMedicationViewModel
import com.example.petmedtracker.presentation.addpet.AddPetViewModel
import com.example.petmedtracker.presentation.petdetail.PetDetailViewModel
import com.example.petmedtracker.presentation.petlist.PetListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { PetListViewModel(get()) }
    viewModel { PetDetailViewModel(get(), get(), get(), get()) }
    viewModel { AddMedicationViewModel(get(), get(), get()) }
    viewModel { AddPetViewModel(get()) }
}
