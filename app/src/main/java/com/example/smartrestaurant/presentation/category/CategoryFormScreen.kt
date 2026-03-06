package com.example.smartrestaurant.presentation.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Category Form Screen (Create/Edit)
 * Validates: Requirements 5.2, 5.3, 5.5, 5.6
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: String? = null,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    // Load category for editing
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.loadCategoryForEdit(categoryId)
        } else {
            viewModel.resetFormState()
        }
    }

    // Navigate back on success
    LaunchedEffect(formState.submitSuccess) {
        if (formState.submitSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (categoryId == null) "Nueva Categoría" else "Editar Categoría"
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
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre") },
                isError = formState.nameError != null,
                supportingText = formState.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description field
            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción") },
                isError = formState.descriptionError != null,
                supportingText = formState.descriptionError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit button
            Button(
                onClick = { viewModel.submitCategory(onNavigateBack) },
                enabled = !formState.isSubmitting && formState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (categoryId == null) "Crear" else "Actualizar")
                }
            }

            // Submit error message
            formState.submitError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
