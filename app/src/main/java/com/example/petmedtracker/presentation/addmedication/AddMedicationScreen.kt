package com.example.petmedtracker.presentation.addmedication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navBackStackEntry: NavBackStackEntry?,
    onBack: () -> Unit,
    viewModel: AddMedicationViewModel = koinViewModel(
        viewModelStoreOwner = navBackStackEntry!!,
        parameters = { parametersOf(navBackStackEntry.savedStateHandle) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add medication${if (uiState.petName.isNotEmpty()) " for ${uiState.petName}" else ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AddMedicationContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }
}

@Composable
private fun AddMedicationContent(
    modifier: Modifier = Modifier,
    uiState: AddMedicationUiState,
    onAction: (AddMedicationAction) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedTextField(
            value = uiState.medicationName,
            onValueChange = { onAction(AddMedicationAction.UpdateMedicationName(it)) },
            label = { Text("Medication name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.dosage,
            onValueChange = { onAction(AddMedicationAction.UpdateDosage(it)) },
            label = { Text("Dosage") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.frequency,
            onValueChange = { onAction(AddMedicationAction.UpdateFrequency(it)) },
            label = { Text("Frequency (e.g. twice daily, every 8 hours, as needed)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        StartDatePickerField(
            startDate = uiState.startDate,
            onDateSelected = { onAction(AddMedicationAction.UpdateStartDate(it)) }
        )
        OutlinedTextField(
            value = uiState.duration,
            onValueChange = { onAction(AddMedicationAction.UpdateDuration(it)) },
            label = { Text("Duration (e.g. 7 days, 2 weeks, until finished)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = { onAction(AddMedicationAction.UpdateNotes(it)) },
            label = { Text("Notes & instructions") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            minLines = 2
        )
        Button(
            onClick = { onAction(AddMedicationAction.Save) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Save medication")
        }
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartDatePickerField(
    startDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val initialMillis = startDate.takeIf { it.isNotBlank() }?.let { str ->
        try {
            dateFormat.parse(str)?.time
        } catch (_: Exception) {
            null
        }
    } ?: System.currentTimeMillis()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        initialDisplayedMonthMillis = null
    )

    OutlinedTextField(
        value = startDate,
        onValueChange = { },
        readOnly = true,
        label = { Text("Start date") },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(Modifier),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick date"
                )
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(dateFormat.format(Date(millis)))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
