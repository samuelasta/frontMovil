package com.example.smartrestaurant.presentation.drink

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
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.presentation.common.components.ImagePicker

/**
 * Drink Form Screen for creating and editing drinks
 * Validates: Requirements 7.2, 7.3, 7.5, 7.6, 10.1, 10.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkFormScreen(
    drinkId: String? = null,
    viewModel: DrinkViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsState()

    // Load drink for editing if drinkId is provided
    LaunchedEffect(drinkId) {
        if (drinkId != null) {
            viewModel.loadDrinkForEdit(drinkId)
        } else {
            viewModel.resetFormState()
        }
        viewModel.loadCategories()
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
                        text = if (drinkId == null) "Nueva Bebida" else "Editar Bebida"
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

            // Stock Units field
            OutlinedTextField(
                value = state.stockUnits,
                onValueChange = viewModel::onStockUnitsChange,
                label = { Text("Unidades en Stock *") },
                isError = state.stockUnitsError != null,
                supportingText = state.stockUnitsError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Category selector
            CategorySelector(
                selectedCategory = state.selectedCategory,
                availableCategories = state.availableCategories,
                isLoading = state.isLoadingCategories,
                onCategorySelected = viewModel::onCategorySelected,
                error = state.categoryError
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
                onClick = { viewModel.submitDrink(onNavigateBack) },
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
                    Text(if (drinkId == null) "Crear Bebida" else "Actualizar Bebida")
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
 * Category selector dropdown
 * Validates: Requirements 10.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    selectedCategory: Category?,
    availableCategories: List<Category>,
    isLoading: Boolean,
    onCategorySelected: (Category) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !isLoading && it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría *") },
            placeholder = { Text("Seleccionar categoría") },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Seleccionar categoría"
                    )
                }
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
            availableCategories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            category.description?.let { desc ->
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
