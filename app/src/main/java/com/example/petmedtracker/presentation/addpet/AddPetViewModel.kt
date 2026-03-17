package com.example.petmedtracker.presentation.addpet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.domain.usecase.AddPetUseCase
import com.example.petmedtracker.models.Pet
import com.example.petmedtracker.presentation.common.Species
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddPetViewModel(
    private val addPetUseCase: AddPetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPetUiState())
    val uiState: StateFlow<AddPetUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack: SharedFlow<Unit> = _navigateBack.asSharedFlow()

    fun onAction(action: AddPetAction) {
        when (action) {
            is AddPetAction.UpdateName -> _uiState.update { it.copy(name = action.value, errorMessage = null) }
            is AddPetAction.UpdateSpecies -> _uiState.update { it.copy(species = action.value, errorMessage = null) }
            is AddPetAction.UpdateBirthday -> _uiState.update { it.copy(birthday = action.value, errorMessage = null) }
            AddPetAction.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        val name = state.name.trim()
        val species = state.species
        val birthday = state.birthday.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet name") }
            return
        }
        if (species == null) {
            _uiState.update { it.copy(errorMessage = "Please select a species") }
            return
        }
        viewModelScope.launch {
            val pet = Pet.newBuilder()
                .setId("pet-${UUID.randomUUID()}")
                .setName(name)
                .setSpecies(species?.displayName ?: "")
                .setBreed("")
                .setBirthday(birthday)
                .build()
            addPetUseCase(pet)
                .onSuccess { _navigateBack.emit(Unit) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}

data class AddPetUiState(
    val name: String = "",
    val species: Species? = null,
    val birthday: String = "",
    val errorMessage: String? = null
)

sealed interface AddPetAction {
    data class UpdateName(val value: String) : AddPetAction
    data class UpdateSpecies(val value: Species?) : AddPetAction
    data class UpdateBirthday(val value: String) : AddPetAction
    data object Save : AddPetAction
}
