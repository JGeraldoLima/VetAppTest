package com.example.petmedtracker.presentation.petdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.data.pdf.MedicationPdfExporter
import com.example.petmedtracker.data.voice.VoiceNoteHelper
import com.example.petmedtracker.domain.usecase.AddMedicationUseCase
import com.example.petmedtracker.domain.usecase.DeleteMedicationUseCase
import com.example.petmedtracker.domain.usecase.DeletePetUseCase
import com.example.petmedtracker.domain.usecase.GetMedicationByIdUseCase
import com.example.petmedtracker.domain.usecase.GetMedicationsForPetUseCase
import com.example.petmedtracker.domain.usecase.GetPetByIdUseCase
import com.example.petmedtracker.models.Medication
import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
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
    private val pdfExporter: MedicationPdfExporter,
    private val deletePetUseCase: DeletePetUseCase,
    private val deleteMedicationUseCase: DeleteMedicationUseCase,
    private val voiceNoteHelper: VoiceNoteHelper,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val addMedicationUseCase: AddMedicationUseCase
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"]) { "petId is required" }

    private val _uiState = MutableStateFlow<PetDetailUiState>(PetDetailUiState.Loading)
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    private val _sharePdfEvent = MutableSharedFlow<Uri>()
    val sharePdfEvent: SharedFlow<Uri> = _sharePdfEvent.asSharedFlow()

    private val _petDeletedEvent = MutableSharedFlow<Unit>()
    val petDeletedEvent: SharedFlow<Unit> = _petDeletedEvent.asSharedFlow()

    init {
        loadPetAndMedications()
    }

    fun onAction(action: PetDetailAction) {
        when (action) {
            PetDetailAction.Retry -> loadPetAndMedications()
            PetDetailAction.SharePdf -> sharePdf()
            is PetDetailAction.DeletePet -> deletePet()
            is PetDetailAction.DeleteMedication -> deleteMedication(action.medicationId)
            is PetDetailAction.StartRecordingVoiceNote -> startRecordingVoiceNote(action.medicationId)
            is PetDetailAction.StopRecordingVoiceNote -> stopRecordingVoiceNote(action.medicationId)
            is PetDetailAction.PlayVoiceNote -> playVoiceNote(action.path)
            is PetDetailAction.DeleteVoiceNote -> deleteVoiceNote(action.medicationId)
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
                    _uiState.update { current ->
                        val currentRecording = (current as? PetDetailUiState.Content)?.recordingForMedicationId
                        PetDetailUiState.Content(pet = pet, medications = medications, recordingForMedicationId = currentRecording)
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

    private fun deletePet() {
        viewModelScope.launch {
            deletePetUseCase(petId)
                .onSuccess { _petDeletedEvent.emit(Unit) }
                .onFailure { e -> _uiState.update { PetDetailUiState.Error(e.message) } }
        }
    }

    private fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            val med = getMedicationByIdUseCase(medicationId)
            med?.voiceNotePath?.takeIf { it.isNotEmpty() }?.let { path ->
                withContext(Dispatchers.IO) { voiceNoteHelper.deleteFile(path) }
            }
            deleteMedicationUseCase(medicationId)
                .onSuccess { loadPetAndMedications() }
                .onFailure { e -> _uiState.update { PetDetailUiState.Error(e.message) } }
        }
    }

    private fun startRecordingVoiceNote(medicationId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { voiceNoteHelper.startRecording() }
            _uiState.update { state ->
                if (state is PetDetailUiState.Content) state.copy(recordingForMedicationId = medicationId)
                else state
            }
        }
    }

    private fun stopRecordingVoiceNote(medicationId: String) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) { voiceNoteHelper.stopRecording() }
            _uiState.update { state ->
                if (state is PetDetailUiState.Content) state.copy(recordingForMedicationId = null)
                else state
            }
            if (path.isNullOrEmpty()) return@launch
            val med = getMedicationByIdUseCase(medicationId) ?: return@launch
            val updated = med.toBuilder().setVoiceNotePath(path).build()
            addMedicationUseCase(updated)
                .onSuccess { loadPetAndMedications() }
                .onFailure { e -> _uiState.update { PetDetailUiState.Error(e.message) } }
        }
    }

    private fun playVoiceNote(path: String) {
        if (path.isEmpty()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) { voiceNoteHelper.play(path) }
        }
    }

    private fun deleteVoiceNote(medicationId: String) {
        viewModelScope.launch {
            val med = getMedicationByIdUseCase(medicationId) ?: return@launch
            val path = med.voiceNotePath
            if (path.isNotEmpty()) {
                withContext(Dispatchers.IO) { voiceNoteHelper.deleteFile(path) }
            }
            val updated = med.toBuilder().setVoiceNotePath("").build()
            addMedicationUseCase(updated)
                .onSuccess { loadPetAndMedications() }
                .onFailure { e -> _uiState.update { PetDetailUiState.Error(e.message) } }
        }
    }
}

sealed interface PetDetailUiState {
    data object Loading : PetDetailUiState
    data class Content(
        val pet: Pet,
        val medications: List<Medication>,
        val recordingForMedicationId: String? = null
    ) : PetDetailUiState
    data class Error(val message: String?) : PetDetailUiState
}

sealed interface PetDetailAction {
    data object Retry : PetDetailAction
    data object SharePdf : PetDetailAction
    data object DeletePet : PetDetailAction
    data class DeleteMedication(val medicationId: String) : PetDetailAction
    data class StartRecordingVoiceNote(val medicationId: String) : PetDetailAction
    data class StopRecordingVoiceNote(val medicationId: String) : PetDetailAction
    data class PlayVoiceNote(val path: String) : PetDetailAction
    data class DeleteVoiceNote(val medicationId: String) : PetDetailAction
}
