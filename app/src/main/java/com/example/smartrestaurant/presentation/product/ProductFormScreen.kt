package com.example.smartrestaurant.presentation.product

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.presentation.common.components.ImagePicker

/**
 * Product Form Screen for creating and editing products
 * Validates: Requirements 1.2, 1.3, 1.5, 1.6, 10.1, 10.2, 12.1, 12.5, 12.6
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: String? = null,
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()

    // Load product for editing if productId is provided
    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProductForEdit(productId)
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
                        text = if (productId == null) "Nuevo Producto" else "Editar Producto"
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

            // Weight field
            OutlinedTextField(
                value = state.weight,
                onValueChange = viewModel::onWeightChange,
                label = { Text("Peso *") },
                isError = state.weightError != null,
                supportingText = state.weightError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Unit field
            UnitSelector(
                selectedUnit = state.unit,
                onUnitSelected = viewModel::onUnitChange,
                error = state.unitError
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

            // Minimum Stock field
            OutlinedTextField(
                value = state.minimumStock,
                onValueChange = viewModel::onMinimumStockChange,
                label = { Text("Stock Mínimo *") },
                isError = state.minimumStockError != null,
                supportingText = state.minimumStockError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Supplier selector
            SupplierSelector(
                selectedSupplier = state.selectedSupplier,
                onSupplierSelected = viewModel::onSupplierSelected,
                error = state.supplierError
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
                onClick = { viewModel.submitProduct(onNavigateBack) },
                enabled = !state.isSubmitting && state.isFormValid,
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
                    Text(if (productId == null) "Crear Producto" else "Actualizar Producto")
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

/**
 * Unit selector dropdown
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitSelector(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("kg", "g", "L", "ml", "unidad")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            label = { Text("Unidad *") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar unidad"
                )
            },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Supplier selector dropdown
 * Note: For now, this is a simplified version. In a full implementation,
 * this would load suppliers from the API via a use case.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupplierSelector(
    selectedSupplier: Supplier?,
    onSupplierSelected: (Supplier) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // TODO: Load suppliers from API
    // For now, using mock data as placeholder
    val mockSuppliers = remember {
        listOf(
            Supplier(
                id = "1",
                name = "Proveedor A",
                email = "proveedora@example.com",
                phone = "123456789",
                address = "Dirección A",
                state = com.example.smartrestaurant.domain.model.SupplierState.ACTIVE
            ),
            Supplier(
                id = "2",
                name = "Proveedor B",
                email = "proveedorb@example.com",
                phone = "987654321",
                address = "Dirección B",
                state = com.example.smartrestaurant.domain.model.SupplierState.ACTIVE
            )
        )
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedSupplier?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Proveedor *") },
            placeholder = { Text("Seleccionar proveedor") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar proveedor"
                )
            },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            mockSuppliers.forEach { supplier ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = supplier.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = supplier.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSupplierSelected(supplier)
                        expanded = false
                    }
                )
            }
        }
    }
}
