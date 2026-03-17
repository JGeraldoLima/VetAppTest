package com.example.petmedtracker.presentation.petdetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
    onEditMedication: (String) -> Unit = {},
    onEditPet: (String) -> Unit = {},
    onSharePdf: () -> Unit,
    onBack: () -> Unit,
    onPetDeleted: () -> Unit = {},
    viewModel: PetDetailViewModel = koinViewModel(
        viewModelStoreOwner = navBackStackEntry!!,
        parameters = { parametersOf(navBackStackEntry.savedStateHandle) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.petDeletedEvent.collect { onPetDeleted() }
    }
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
            var showDeletePetConfirm by remember { mutableStateOf(false) }
            var showMenu by remember { mutableStateOf(false) }
            TopAppBar(
                title = { Text("Pet detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is PetDetailUiState.Content) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit pet") },
                                onClick = {
                                    showMenu = false
                                    onEditPet((uiState as PetDetailUiState.Content).pet.id)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete pet") },
                                onClick = {
                                    showMenu = false
                                    showDeletePetConfirm = true
                                }
                            )
                        }
                    }
                }
            )
            if (showDeletePetConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeletePetConfirm = false },
                    title = { Text("Delete pet?") },
                    text = { Text("This will permanently remove the pet and all their medications.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeletePetConfirm = false
                                viewModel.onAction(PetDetailAction.DeletePet)
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeletePetConfirm = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
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
            onSharePdf = { viewModel.onAction(PetDetailAction.SharePdf) },
            onDeletePet = { viewModel.onAction(PetDetailAction.DeletePet) },
            onEditMedication = onEditMedication,
            onDeleteMedication = { viewModel.onAction(PetDetailAction.DeleteMedication(it)) },
            onStartRecordingVoiceNote = { viewModel.onAction(PetDetailAction.StartRecordingVoiceNote(it)) },
            onStopRecordingVoiceNote = { viewModel.onAction(PetDetailAction.StopRecordingVoiceNote(it)) },
            onPlayVoiceNote = { viewModel.onAction(PetDetailAction.PlayVoiceNote(it)) },
            onDeleteVoiceNote = { viewModel.onAction(PetDetailAction.DeleteVoiceNote(it)) }
        )
    }
}

@Composable
private fun PetDetailContent(
    modifier: Modifier = Modifier,
    uiState: PetDetailUiState,
    onRetry: () -> Unit,
    onSharePdf: () -> Unit,
    onDeletePet: () -> Unit,
    onEditMedication: (String) -> Unit,
    onDeleteMedication: (String) -> Unit,
    onStartRecordingVoiceNote: (String) -> Unit,
    onStopRecordingVoiceNote: (String) -> Unit,
    onPlayVoiceNote: (String) -> Unit,
    onDeleteVoiceNote: (String) -> Unit
) {
    val context = LocalContext.current
    var pendingRecordMedicationId by remember { mutableStateOf<String?>(null) }
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingRecordMedicationId?.let { id ->
            if (granted) onStartRecordingVoiceNote(id)
            pendingRecordMedicationId = null
        }
    }
    fun requestRecordAndStart(medicationId: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onStartRecordingVoiceNote(medicationId)
        } else {
            pendingRecordMedicationId = medicationId
            recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
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
                        MedicationCard(
                            medication = med,
                            isRecording = uiState.recordingForMedicationId == med.id,
                            onEdit = { onEditMedication(med.id) },
                            onDelete = { onDeleteMedication(med.id) },
                            onStartRecording = { requestRecordAndStart(med.id) },
                            onStopRecording = { onStopRecordingVoiceNote(med.id) },
                            onPlayVoiceNote = { onPlayVoiceNote(med.voiceNotePath) },
                            onDeleteVoiceNote = { onDeleteVoiceNote(med.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(
    medication: Medication,
    isRecording: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayVoiceNote: () -> Unit,
    onDeleteVoiceNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val hasVoiceNote = medication.voiceNotePath.isNotEmpty()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.medicationName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit medication")
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete medication"
                    )
                }
            }
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
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voice note:",
                    style = MaterialTheme.typography.labelSmall
                )
                if (isRecording) {
                    Button(onClick = onStopRecording) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Stop")
                    }
                } else if (hasVoiceNote) {
                    IconButton(onClick = onPlayVoiceNote) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                    IconButton(onClick = onDeleteVoiceNote) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete voice note")
                    }
                } else {
                    Button(onClick = onStartRecording) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Add voice note")
                    }
                }
            }
        }
    }
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete medication?") },
            text = { Text("This will permanently remove \"${medication.medicationName}\".") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
