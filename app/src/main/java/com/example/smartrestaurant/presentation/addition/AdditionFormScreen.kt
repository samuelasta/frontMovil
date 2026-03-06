package com.example.smartrestaurant.presentation.addition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.presentation.common.components.ImagePicker

/**
 * Addition Form Screen for creating and editing additions
 * Validates: Requirements 8.2, 8.3, 8.5, 8.6, 10.1, 10.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionFormScreen(
    additionId: String? = null,
    viewModel: AdditionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()

    // Load addition for editing if additionId is provided
    LaunchedEffect(additionId) {
        if (additionId != null) {
            viewModel.loadAdditionForEdit(additionId)
        } else {
            viewModel.resetFormState()
        }
    }

    // Navigate back on successful submission
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (additionId == null) "Nueva Adición" else "Editar Adición"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre *") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description field
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Price field
            OutlinedTextField(
                value = state.price,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Precio *") },
                isError = state.priceError != null,
                supportingText = state.priceError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                prefix = { Text("$") }
            )

            // Image picker with preview
            ImagePicker(
                imageUri = state.imageUri,
                onImageSelected = { uri ->
                    viewModel.onImageSelected(uri.toString())
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit button with loading state
            Button(
                onClick = { viewModel.submitAddition(onNavigateBack) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text(if (additionId == null) "Crear Adición" else "Actualizar Adición")
                }
            }

            // Error message display
            state.submitError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
