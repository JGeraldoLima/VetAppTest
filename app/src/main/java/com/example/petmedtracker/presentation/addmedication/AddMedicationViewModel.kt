package com.example.petmedtracker.presentation.addmedication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.domain.usecase.AddMedicationUseCase
import com.example.petmedtracker.domain.usecase.GetPetByIdUseCase
import com.example.petmedtracker.models.Medication
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddMedicationViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val addMedicationUseCase: AddMedicationUseCase
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"]) { "petId is required" }

    private val _uiState = MutableStateFlow(AddMedicationUiState())
    val uiState: StateFlow<AddMedicationUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack: SharedFlow<Unit> = _navigateBack.asSharedFlow()

    init {
        viewModelScope.launch {
            val pet = getPetByIdUseCase(petId)
            _uiState.update { it.copy(petName = pet?.name ?: "") }
        }
    }

    fun onAction(action: AddMedicationAction) {
        when (action) {
            is AddMedicationAction.UpdateMedicationName -> _uiState.update { it.copy(medicationName = action.value, errorMessage = null) }
            is AddMedicationAction.UpdateDosage -> _uiState.update { it.copy(dosage = action.value, errorMessage = null) }
            is AddMedicationAction.UpdateFrequency -> _uiState.update { it.copy(frequency = action.value, errorMessage = null) }
            is AddMedicationAction.UpdateNotes -> _uiState.update { it.copy(notes = action.value, errorMessage = null) }
            is AddMedicationAction.UpdateStartDate -> _uiState.update { it.copy(startDate = action.value, errorMessage = null) }
            is AddMedicationAction.UpdateDuration -> _uiState.update { it.copy(duration = action.value, errorMessage = null) }
            AddMedicationAction.Save -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        val name = state.medicationName.trim()
        val dosage = state.dosage.trim()
        val frequency = state.frequency.trim()
        val startDate = state.startDate.trim()
        val duration = state.duration.trim()
        if (name.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || startDate.isEmpty() || duration.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }
        viewModelScope.launch {
            val medication = Medication.newBuilder()
                .setId("med-${UUID.randomUUID()}")
                .setPetId(petId)
                .setMedicationName(name)
                .setDosage(dosage)
                .setFrequency(frequency)
                .setNotesInstructions(state.notes.trim())
                .setStartDate(startDate)
                .setDuration(duration)
                .build()
            addMedicationUseCase(medication)
                .onSuccess { _navigateBack.emit(Unit) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}

data class AddMedicationUiState(
    val petName: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val notes: String = "",
    val startDate: String = "",
    val duration: String = "",
    val errorMessage: String? = null
)

sealed interface AddMedicationAction {
    data class UpdateMedicationName(val value: String) : AddMedicationAction
    data class UpdateDosage(val value: String) : AddMedicationAction
    data class UpdateFrequency(val value: String) : AddMedicationAction
    data class UpdateNotes(val value: String) : AddMedicationAction
    data class UpdateStartDate(val value: String) : AddMedicationAction
    data class UpdateDuration(val value: String) : AddMedicationAction
    data object Save : AddMedicationAction
}
