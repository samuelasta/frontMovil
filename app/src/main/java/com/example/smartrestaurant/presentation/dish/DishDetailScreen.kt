package com.example.smartrestaurant.presentation.dish

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    dishId: String,
    viewModel: DishViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(dishId) { viewModel.loadDishById(dishId) }
    DisposableEffect(Unit) { onDispose { viewModel.resetDetailState() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Plato") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (detailState.dish != null) {
                        IconButton(onClick = { onNavigateToEdit(dishId) }) {
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
                onRetry = { viewModel.loadDishById(dishId) }
            )
            detailState.dish != null -> {
                val dish = detailState.dish!!
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AsyncImage(model = dish.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
                            Text(dish.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Divider()
                            Text("Descripción: ${dish.description}")
                            Text("Precio: $${dish.price}")
                            Text("Categoría: ${dish.category.name}")
                            Text("Tiempo de preparación: ${dish.preparationTime} min")
                        }
                    }

                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Receta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            dish.recipe.forEach { item ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.product.name)
                                    Text("${item.quantity} ${item.product.unit}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Plato",
            message = "¿Está seguro que desea eliminar este plato?",
            onConfirm = { viewModel.deleteDish(onNavigateBack) },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}
