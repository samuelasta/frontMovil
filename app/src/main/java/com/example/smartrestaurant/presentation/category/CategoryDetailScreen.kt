package com.example.smartrestaurant.presentation.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.presentation.common.components.ConfirmationDialog
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator

/**
 * Category Detail Screen
 * Validates: Requirements 5.4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: String,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryById(categoryId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDetailState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Categoría") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (detailState.category != null) {
                        IconButton(onClick = { onNavigateToEdit(categoryId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirmation() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> {
                LoadingIndicator()
            }
            detailState.error != null -> {
                ErrorMessage(
                    message = detailState.error!!,
                    onRetry = { viewModel.loadCategoryById(categoryId) }
                )
            }
            detailState.category != null -> {
                CategoryDetailContent(
                    state = detailState,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Categoría",
            message = "¿Está seguro que desea eliminar esta categoría?",
            onConfirm = {
                viewModel.deleteCategory(onSuccess = onNavigateBack)
            },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}

@Composable
private fun CategoryDetailContent(
    state: CategoryDetailUiState,
    modifier: Modifier = Modifier
) {
    val category = state.category ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category information card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Divider()

                DetailRow(label = "Descripción", value = category.description)
                DetailRow(label = "Estado", value = category.state.name)
            }
        }

        // Associated items section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Platos y Bebidas Asociados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge {
                        Text(text = state.associatedItemsCount.toString())
                    }
                }

                if (state.associatedItemsCount == 0) {
                    Text(
                        text = "No hay platos o bebidas asociados a esta categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "Esta categoría tiene ${state.associatedItemsCount} platos/bebidas asociados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
