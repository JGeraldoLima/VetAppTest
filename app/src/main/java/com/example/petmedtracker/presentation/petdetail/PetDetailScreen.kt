package com.example.petmedtracker.presentation.petdetail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.petmedtracker.models.Medication
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    navBackStackEntry: NavBackStackEntry?,
    onAddMedication: (String) -> Unit,
    onSharePdf: () -> Unit,
    onBack: () -> Unit,
    viewModel: PetDetailViewModel = koinViewModel(
        viewModelStoreOwner = navBackStackEntry!!,
        parameters = { parametersOf(navBackStackEntry.savedStateHandle) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sharePdfEvent.collect { uri ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share medication schedule"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is PetDetailUiState.Content) {
                FloatingActionButton(
                    onClick = { onAddMedication((uiState as PetDetailUiState.Content).pet.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add medication"
                    )
                }
            }
        }
    ) { paddingValues ->
        PetDetailContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onRetry = { viewModel.onAction(PetDetailAction.Retry) },
            onSharePdf = { viewModel.onAction(PetDetailAction.SharePdf) }
        )
    }
}

@Composable
private fun PetDetailContent(
    modifier: Modifier = Modifier,
    uiState: PetDetailUiState,
    onRetry: () -> Unit,
    onSharePdf: () -> Unit
) {
    when (uiState) {
        is PetDetailUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PetDetailUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.message ?: "Error")
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Retry")
                    }
                }
            }
        }
        is PetDetailUiState.Content -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = uiState.pet.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (uiState.pet.species.isNotEmpty()) {
                    Text(
                        text = "${uiState.pet.species}${if (uiState.pet.breed.isNotEmpty()) " • ${uiState.pet.breed}" else ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(
                    onClick = onSharePdf,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Share as PDF")
                }
                Text(
                    text = "Medications",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.medications,
                        key = { it.id }
                    ) { med ->
                        MedicationCard(medication = med)
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = medication.medicationName,
                style = MaterialTheme.typography.titleSmall
            )
            Text(text = "Dosage: ${medication.dosage}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Frequency: ${medication.frequency}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Start: ${medication.startDate}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duration: ${medication.duration}", style = MaterialTheme.typography.bodySmall)
            if (medication.notesInstructions.isNotEmpty()) {
                Text(
                    text = "Notes: ${medication.notesInstructions}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
