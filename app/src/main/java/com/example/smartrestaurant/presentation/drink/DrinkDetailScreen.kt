package com.example.smartrestaurant.presentation.drink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartrestaurant.presentation.common.components.*

/**
 * DrinkDetailScreen composable
 * Validates: Requirements 7.4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    drinkId: String,
    viewModel: DrinkViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(drinkId) { viewModel.loadDrinkById(drinkId) }
    DisposableEffect(Unit) { onDispose { viewModel.resetDetailState() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Bebida") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (detailState.drink != null) {
                        IconButton(onClick = { onNavigateToEdit(drinkId) }) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirmation() }) {
                            Icon(Icons.Default.Delete, "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> LoadingIndicator()
            detailState.error != null -> ErrorMessage(
                message = detailState.error!!,
                onRetry = { viewModel.loadDrinkById(drinkId) }
            )
            detailState.drink != null -> {
                val drink = detailState.drink!!
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AsyncImage(model = drink.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
                            Text(drink.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Divider()
                            Text("Descripción: ${drink.description}")
                            Text("Precio: ${drink.price}")
                            Text("Stock: ${drink.stockUnits} unidades")
                            Text("Categoría: ${drink.category.name}")
                            Text("Estado: ${drink.state.name}")
                        }
                    }
                }
            }
        }
    }

    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Bebida",
            message = "¿Está seguro que desea eliminar esta bebida?",
            onConfirm = { viewModel.deleteDrink(onNavigateBack) },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}
