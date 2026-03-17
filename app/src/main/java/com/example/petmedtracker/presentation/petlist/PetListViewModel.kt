package com.example.petmedtracker.presentation.petlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petmedtracker.domain.usecase.GetPetsUseCase
import com.example.petmedtracker.models.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PetListViewModel(
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PetListUiState>(PetListUiState.Loading)
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()

    init {
        loadPets()
    }

    fun onAction(action: PetListAction) {
        when (action) {
            PetListAction.Retry -> loadPets()
        }
    }

    private fun loadPets() {
        getPetsUseCase()
            .onEach { pets ->
                _uiState.update {
                    PetListUiState.Content(pets = pets)
                }
            }
            .catch { e ->
                _uiState.update { PetListUiState.Error(e.message) }
            }
            .launchIn(viewModelScope)
    }
}

sealed interface PetListUiState {
    data object Loading : PetListUiState
    data class Content(val pets: List<Pet>) : PetListUiState
    data class Error(val message: String?) : PetListUiState
}

sealed interface PetListAction {
    data object Retry : PetListAction
}
