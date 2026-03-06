package com.example.smartrestaurant.presentation.dish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishFormScreen(
    dishId: String? = null,
    viewModel: DishViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(dishId) {
        if (dishId != null) viewModel.loadDishForEdit(dishId)
        else viewModel.resetFormState()
        viewModel.loadCategoriesAndProducts()
    }

    LaunchedEffect(formState.submitSuccess) {
        if (formState.submitSuccess) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (dishId == null) "Nuevo Plato" else "Editar Plato") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre") },
                isError = formState.nameError != null,
                supportingText = formState.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = formState.price,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Precio") },
                isError = formState.priceError != null,
                supportingText = formState.priceError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.preparationTime,
                onValueChange = viewModel::onPreparationTimeChange,
                label = { Text("Tiempo de preparación (min)") },
                isError = formState.preparationTimeError != null,
                supportingText = formState.preparationTimeError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Category selector
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = formState.selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    isError = formState.categoryError != null,
                    supportingText = formState.categoryError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
            }

            // Recipe section
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Receta", style = MaterialTheme.typography.titleMedium)
                    if (formState.recipeError != null) {
                        Text(formState.recipeError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    formState.recipe.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.product.name, Modifier.weight(1f))
                            OutlinedTextField(
                                value = item.quantity,
                                onValueChange = { viewModel.updateIngredientQuantity(item.product, it) },
                                modifier = Modifier.width(100.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            IconButton(onClick = { viewModel.removeIngredient(item.product) }) {
                                Icon(Icons.Default.Delete, "Eliminar")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.submitDish(onNavigateBack) },
                enabled = !formState.isSubmitting && formState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (dishId == null) "Crear" else "Actualizar")
                }
            }

            formState.submitError?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
