package com.example.petmedtracker.presentation.petlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.petmedtracker.models.Pet
import com.example.petmedtracker.presentation.common.Species
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onPetClick: (String) -> Unit,
    onAddPet: () -> Unit,
    viewModel: PetListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pets") })
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = onAddPet) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add pet"
                )
            }
        }
    ) { paddingValues ->
        PetListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onPetClick = onPetClick,
            onRetry = { viewModel.onAction(PetListAction.Retry) }
        )
    }
}

@Composable
private fun PetListContent(
    modifier: Modifier = Modifier,
    uiState: PetListUiState,
    onPetClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    when (uiState) {
        is PetListUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PetListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.message ?: "Error loading pets",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        is PetListUiState.Content -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.pets,
                    key = { it.id }
                ) { pet ->
                    PetItem(
                        pet = pet,
                        onClick = { onPetClick(pet.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PetItem(
    pet: Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = androidx.compose.ui.semantics.Role.Button
            ),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "Pet: ${pet.name}${if (pet.species.isNotEmpty()) ", ${pet.species}" else ""}"
                }
        ) {
            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (pet.species.isNotEmpty()) {
                val speciesIcon = Species.fromProtoValue(pet.species)?.icon ?: ""
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = pet.species,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (speciesIcon.isNotEmpty()) {
                        Text(text = speciesIcon, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
