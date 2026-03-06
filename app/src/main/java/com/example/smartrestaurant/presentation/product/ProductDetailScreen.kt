package com.example.smartrestaurant.presentation.product

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
import coil.compose.AsyncImage
import com.example.smartrestaurant.presentation.common.components.ConfirmationDialog
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator

/**
 * Product Detail Screen
 * Validates: Requirements 1.4, 2.1, 2.2, 2.4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDetailState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (detailState.product != null) {
                        IconButton(onClick = { onNavigateToEdit(productId) }) {
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
                    onRetry = { viewModel.loadProductById(productId) }
                )
            }
            detailState.product != null -> {
                ProductDetailContent(
                    state = detailState,
                    onAddStock = { viewModel.showAddStockDialog() },
                    onSubtractStock = { viewModel.showSubtractStockDialog() },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Producto",
            message = "¿Está seguro que desea eliminar este producto?",
            onConfirm = {
                viewModel.deleteProduct(onSuccess = onNavigateBack)
            },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }

    // Add stock dialog
    if (detailState.showAddStockDialog) {
        StockDialog(
            title = "Añadir Stock",
            quantity = detailState.stockQuantity,
            reason = detailState.stockReason,
            quantityError = detailState.stockQuantityError,
            operationError = detailState.stockOperationError,
            isSubmitting = detailState.isSubmittingStock,
            onQuantityChange = viewModel::onStockQuantityChange,
            onReasonChange = viewModel::onStockReasonChange,
            onConfirm = { viewModel.addStock() },
            onDismiss = { viewModel.hideAddStockDialog() }
        )
    }

    // Subtract stock dialog
    if (detailState.showSubtractStockDialog) {
        StockDialog(
            title = "Descontar Stock",
            quantity = detailState.stockQuantity,
            reason = detailState.stockReason,
            quantityError = detailState.stockQuantityError,
            operationError = detailState.stockOperationError,
            isSubmitting = detailState.isSubmittingStock,
            onQuantityChange = viewModel::onStockQuantityChange,
            onReasonChange = viewModel::onStockReasonChange,
            onConfirm = { viewModel.subtractStock() },
            onDismiss = { viewModel.hideSubtractStockDialog() }
        )
    }
}

@Composable
private fun ProductDetailContent(
    state: ProductDetailUiState,
    onAddStock: () -> Unit,
    onSubtractStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = state.product ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Product image
        if (product.imageUrl != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Product name and description
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Product details
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información del Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                DetailRow(label = "Peso", value = "${product.weight} ${product.unit}")
                DetailRow(label = "Precio", value = "$${product.price}")
                DetailRow(label = "Estado", value = product.state.name)
            }
        }

        // Stock information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = if (product.isLowStock) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            } else {
                CardDefaults.cardColors()
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (product.isLowStock) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Stock bajo",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                DetailRow(
                    label = "Stock actual",
                    value = "${product.stock} ${product.unit}",
                    valueColor = if (product.isLowStock) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                DetailRow(
                    label = "Stock mínimo",
                    value = "${product.minimumStock} ${product.unit}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddStock,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir")
                    }

                    OutlinedButton(
                        onClick = onSubtractStock,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Descontar")
                    }
                }
            }
        }

        // Supplier information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Proveedor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                DetailRow(label = "Nombre", value = product.supplier.name)
                DetailRow(label = "Email", value = product.supplier.email)
                DetailRow(label = "Teléfono", value = product.supplier.phone)
                DetailRow(label = "Dirección", value = product.supplier.address)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
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
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/**
 * Stock modification dialog
 * Validates: Requirements 2.2, 2.4
 */
@Composable
private fun StockDialog(
    title: String,
    quantity: String,
    reason: String,
    quantityError: String?,
    operationError: String?,
    isSubmitting: Boolean,
    onQuantityChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Cantidad") },
                    isError = quantityError != null,
                    supportingText = quantityError?.let { { Text(it) } },
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    label = { Text("Razón (opcional)") },
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                if (operationError != null) {
                    Text(
                        text = operationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancelar")
            }
        }
    )
}
