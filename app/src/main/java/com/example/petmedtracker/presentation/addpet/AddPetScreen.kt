package com.example.petmedtracker.presentation.addpet

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petmedtracker.presentation.common.Species
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onBack: () -> Unit,
    viewModel: AddPetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Pet") },
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
        AddPetContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

@Composable
private fun AddPetContent(
    modifier: Modifier = Modifier,
    uiState: AddPetUiState,
    onAction: (AddPetAction) -> Unit
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
            value = uiState.name,
            onValueChange = { onAction(AddPetAction.UpdateName(it)) },
            label = { Text("Pet name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        Text(
            text = "Species",
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        Species.entries.forEach { species ->
            val selected = uiState.species == species
            Button(
                onClick = { onAction(AddPetAction.UpdateSpecies(species)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = if (selected)
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                else
                    androidx.compose.material3.ButtonDefaults.buttonColors()
            ) {
                Text("${species.icon}  ${species.displayName}")
            }
        }
        BirthdayPickerField(
            birthday = uiState.birthday,
            onDateSelected = { onAction(AddPetAction.UpdateBirthday(it)) },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = { onAction(AddPetAction.Save) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Save Pet")
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayPickerField(
    birthday: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val initialMillis = birthday.takeIf { it.isNotBlank() }?.let { str ->
        try {
            dateFormat.parse(str)?.time
        } catch (_: Exception) {
            null
        }
    } ?: System.currentTimeMillis()
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        initialDisplayedMonthMillis = null
    )

    OutlinedTextField(
        value = birthday,
        onValueChange = { },
        readOnly = true,
        label = { Text("Birthday") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick birthday"
                )
            }
        }
    )

    if (showDatePicker) {
        androidx.compose.material3.DatePickerDialog(
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
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }
}
