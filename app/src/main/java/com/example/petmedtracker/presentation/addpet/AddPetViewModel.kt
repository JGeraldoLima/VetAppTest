package com.example.petmedtracker.presentation.addpet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.domain.usecase.AddPetUseCase
import com.example.petmedtracker.domain.usecase.GetPetByIdUseCase
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
    savedStateHandle: SavedStateHandle,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val addPetUseCase: AddPetUseCase
) : ViewModel() {

    private val petId: String? = savedStateHandle["petId"]

    private val _uiState = MutableStateFlow(AddPetUiState())
    val uiState: StateFlow<AddPetUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack: SharedFlow<Unit> = _navigateBack.asSharedFlow()

    val isEditMode: Boolean get() = petId != null

    init {
        petId?.let { id ->
            viewModelScope.launch {
                val pet = getPetByIdUseCase(id)
                if (pet != null) {
                    _uiState.update {
                        it.copy(
                            name = pet.name,
                            species = Species.fromProtoValue(pet.species),
                            birthday = pet.birthday,
                            breed = pet.breed
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: AddPetAction) {
        when (action) {
            is AddPetAction.UpdateName -> _uiState.update { it.copy(name = action.value, errorMessage = null) }
            is AddPetAction.UpdateSpecies -> _uiState.update { it.copy(species = action.value, errorMessage = null) }
            is AddPetAction.UpdateBirthday -> _uiState.update { it.copy(birthday = action.value, errorMessage = null) }
            is AddPetAction.UpdateBreed -> _uiState.update { it.copy(breed = action.value, errorMessage = null) }
            AddPetAction.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        val name = state.name.trim()
        val species = state.species
        val birthday = state.birthday.trim()
        val breed = state.breed.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet name") }
            return
        }
        if (species == null) {
            _uiState.update { it.copy(errorMessage = "Please select a species") }
            return
        }
        viewModelScope.launch {
            val id = petId ?: "pet-${UUID.randomUUID()}"
            val pet = Pet.newBuilder()
                .setId(id)
                .setName(name)
                .setSpecies(species.displayName)
                .setBreed(breed)
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
    val breed: String = "",
    val errorMessage: String? = null
)

sealed interface AddPetAction {
    data class UpdateName(val value: String) : AddPetAction
    data class UpdateSpecies(val value: Species?) : AddPetAction
    data class UpdateBirthday(val value: String) : AddPetAction
    data class UpdateBreed(val value: String) : AddPetAction
    data object Save : AddPetAction
}
