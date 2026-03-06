package com.example.smartrestaurant.presentation.supplier

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

/**
 * Supplier Form Screen (Create/Edit)
 * Validates: Requirements 4.2, 4.3, 4.5, 4.6, 12.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierFormScreen(
    supplierId: String? = null,
    viewModel: SupplierViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    // Load supplier for editing
    LaunchedEffect(supplierId) {
        if (supplierId != null) {
            viewModel.loadSupplierForEdit(supplierId)
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
                        text = if (supplierId == null) "Nuevo Proveedor" else "Editar Proveedor"
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

            // Email field
            OutlinedTextField(
                value = formState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                isError = formState.emailError != null,
                supportingText = formState.emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Phone field
            OutlinedTextField(
                value = formState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Teléfono") },
                isError = formState.phoneError != null,
                supportingText = formState.phoneError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // Address field
            OutlinedTextField(
                value = formState.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Dirección") },
                isError = formState.addressError != null,
                supportingText = formState.addressError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit button
            Button(
                onClick = { viewModel.submitSupplier(onNavigateBack) },
                enabled = !formState.isSubmitting && formState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (supplierId == null) "Crear" else "Actualizar")
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
