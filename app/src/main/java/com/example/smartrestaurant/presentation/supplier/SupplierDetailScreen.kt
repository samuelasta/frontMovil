package com.example.smartrestaurant.presentation.supplier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.presentation.common.components.ConfirmationDialog
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator

/**
 * Supplier Detail Screen
 * Validates: Requirements 4.4, 4.7
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDetailScreen(
    supplierId: String,
    viewModel: SupplierViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToProduct: (String) -> Unit = {}
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(supplierId) {
        viewModel.loadSupplierById(supplierId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDetailState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Proveedor") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (detailState.supplier != null) {
                        IconButton(onClick = { onNavigateToEdit(supplierId) }) {
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
                    onRetry = { viewModel.loadSupplierById(supplierId) }
                )
            }
            detailState.supplier != null -> {
                SupplierDetailContent(
                    state = detailState,
                    onProductClick = onNavigateToProduct,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Proveedor",
            message = "¿Está seguro que desea eliminar este proveedor?",
            onConfirm = {
                viewModel.deleteSupplier(onSuccess = onNavigateBack)
            },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}

@Composable
private fun SupplierDetailContent(
    state: SupplierDetailUiState,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val supplier = state.supplier ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Supplier information card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = supplier.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Divider()

                DetailRow(label = "Email", value = supplier.email)
                DetailRow(label = "Teléfono", value = supplier.phone)
                DetailRow(label = "Dirección", value = supplier.address)
                DetailRow(label = "Estado", value = supplier.state.name)
            }
        }

        // Associated products section
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
                        text = "Productos Asociados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge {
                        Text(text = state.associatedProducts.size.toString())
                    }
                }

                if (state.associatedProducts.isEmpty()) {
                    Text(
                        text = "No hay productos asociados a este proveedor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.associatedProducts.forEach { product ->
                            AssociatedProductItem(
                                product = product,
                                onClick = { onProductClick(product.id!!) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Associated product item
 * Validates: Requirements 4.4
 */
@Composable
private fun AssociatedProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Stock: ${product.stock} ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.isLowStock) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (product.isLowStock) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Stock bajo",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
