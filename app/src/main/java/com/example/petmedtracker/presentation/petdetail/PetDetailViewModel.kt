package com.example.petmedtracker.presentation.petdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.data.pdf.MedicationPdfExporter
import com.example.petmedtracker.domain.usecase.GetMedicationsForPetUseCase
import com.example.petmedtracker.domain.usecase.GetPetByIdUseCase
import com.example.petmedtracker.models.Medication
import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri

class PetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val getMedicationsForPetUseCase: GetMedicationsForPetUseCase,
    private val pdfExporter: MedicationPdfExporter
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"]) { "petId is required" }

    private val _uiState = MutableStateFlow<PetDetailUiState>(PetDetailUiState.Loading)
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    private val _sharePdfEvent = MutableSharedFlow<Uri>()
    val sharePdfEvent: SharedFlow<Uri> = _sharePdfEvent.asSharedFlow()

    init {
        loadPetAndMedications()
    }

    fun onAction(action: PetDetailAction) {
        when (action) {
            PetDetailAction.Retry -> loadPetAndMedications()
            PetDetailAction.SharePdf -> sharePdf()
        }
    }

    private fun loadPetAndMedications() {
        viewModelScope.launch {
            val pet = getPetByIdUseCase(petId)
            if (pet == null) {
                _uiState.update { PetDetailUiState.Error("Pet not found") }
                return@launch
            }
            getMedicationsForPetUseCase(petId)
                .onEach { medications ->
                    _uiState.update {
                        PetDetailUiState.Content(pet = pet, medications = medications)
                    }
                }
                .catch { e ->
                    _uiState.update { PetDetailUiState.Error(e.message) }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun sharePdf() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state !is PetDetailUiState.Content) return@launch
            val uri = pdfExporter.exportToPdf(state.pet, state.medications)
            if (uri != null) _sharePdfEvent.emit(uri)
        }
    }
}

sealed interface PetDetailUiState {
    data object Loading : PetDetailUiState
    data class Content(val pet: Pet, val medications: List<Medication>) : PetDetailUiState
    data class Error(val message: String?) : PetDetailUiState
}

sealed interface PetDetailAction {
    data object Retry : PetDetailAction
    data object SharePdf : PetDetailAction
}
