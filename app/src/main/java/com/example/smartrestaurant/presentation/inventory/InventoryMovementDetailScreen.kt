package com.example.smartrestaurant.presentation.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.domain.model.MovementType
import com.example.smartrestaurant.presentation.common.components.*
import java.time.format.DateTimeFormatter

/**
 * InventoryMovementDetailScreen composable
 * Shows complete information about an inventory movement
 * Validates: Requirements 9.5
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryMovementDetailScreen(
    movementId: String,
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(movementId) { viewModel.loadMovementById(movementId) }
    DisposableEffect(Unit) { onDispose { viewModel.resetDetailState() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Movimiento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> LoadingIndicator()
            detailState.error != null -> ErrorMessage(
                message = detailState.error!!,
                onRetry = { viewModel.loadMovementById(movementId) }
            )
            detailState.movement != null -> {
                val movement = detailState.movement!!
                
                val backgroundColor = when (movement.movementType) {
                    MovementType.ENTRY -> Color(0xFFE8F5E9)
                    MovementType.EXIT -> Color(0xFFFFEBEE)
                }
                
                val iconColor = when (movement.movementType) {
                    MovementType.ENTRY -> Color(0xFF4CAF50)
                    MovementType.EXIT -> Color(0xFFF44336)
                }
                
                val icon = when (movement.movementType) {
                    MovementType.ENTRY -> Icons.Default.ArrowDownward
                    MovementType.EXIT -> Icons.Default.ArrowUpward
                }
                
                val movementTypeText = when (movement.movementType) {
                    MovementType.ENTRY -> "Entrada"
                    MovementType.EXIT -> "Salida"
                }
                
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = movementTypeText,
                                    tint = iconColor,
                                    modifier = Modifier.size(48.dp)
                                )
                                Column {
                                    Text(
                                        movementTypeText,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = iconColor
                                    )
                                    Text(
                                        movement.timestamp.format(
                                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Divider()
                            
                            DetailRow("Producto", movement.product.name)
                            DetailRow("Cantidad", "${movement.quantity} ${movement.product.unit}")
                            
                            if (movement.reason != null) {
                                DetailRow("Razón", movement.reason)
                            }
                            
                            Divider()
                            
                            Text(
                                "Información del Producto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            DetailRow("Descripción", movement.product.description)
                            DetailRow("Precio unitario", "$${movement.product.price}")
                            DetailRow("Stock actual", "${movement.product.stock} ${movement.product.unit}")
                            DetailRow("Stock mínimo", "${movement.product.minimumStock} ${movement.product.unit}")
                            DetailRow("Proveedor", movement.product.supplier.name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
