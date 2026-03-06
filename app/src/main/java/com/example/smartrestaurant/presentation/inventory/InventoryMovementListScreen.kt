package com.example.smartrestaurant.presentation.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.domain.model.InventoryMovement
import com.example.smartrestaurant.domain.model.MovementType
import com.example.smartrestaurant.presentation.common.components.*
import java.time.format.DateTimeFormatter

/**
 * InventoryMovementListScreen composable
 * Validates: Requirements 9.1, 9.3, 9.4, 9.6, 15.6
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryMovementListScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos de Inventario") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filtros")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Active filters indicator
            if (listState.hasActiveFilters) {
                ActiveFiltersChips(
                    state = listState,
                    onClearFilters = { viewModel.clearFilters() },
                    onRemoveMovementTypeFilter = { viewModel.filterByMovementType(null) },
                    onRemoveDateFilter = { viewModel.filterByDateRange(null, null) }
                )
            }

            when {
                listState.isLoading && listState.movements.isEmpty() -> LoadingIndicator()
                listState.error != null && listState.movements.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredMovements.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay movimientos de inventario")
                    }
                }
                else -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(listState.filteredMovements.size, key = { index ->
                            listState.filteredMovements[index].id ?: listState.filteredMovements[index].hashCode()
                        }) { index ->
                            val movement = listState.filteredMovements[index]
                            InventoryMovementItem(movement) { onNavigateToDetail(movement.id!!) }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            state = listState,
            onDismiss = { showFilterDialog = false },
            onApplyFilters = { startDate, endDate, movementType ->
                viewModel.filterByDateRange(startDate, endDate)
                viewModel.filterByMovementType(movementType)
                showFilterDialog = false
            }
        )
    }
}

/**
 * InventoryMovementItem composable displaying movement information
 * Shows: date, product name, movement type, quantity, and reason
 * Visual differentiation between ENTRY and EXIT movements
 * Validates: Requirements 9.1, 9.4
 */
@Composable
fun InventoryMovementItem(movement: InventoryMovement, onClick: () -> Unit) {
    val backgroundColor = when (movement.movementType) {
        MovementType.ENTRY -> Color(0xFFE8F5E9) // Light green
        MovementType.EXIT -> Color(0xFFFFEBEE) // Light red
    }
    
    val iconColor = when (movement.movementType) {
        MovementType.ENTRY -> Color(0xFF4CAF50) // Green
        MovementType.EXIT -> Color(0xFFF44336) // Red
    }
    
    val icon = when (movement.movementType) {
        MovementType.ENTRY -> Icons.Default.ArrowDownward
        MovementType.EXIT -> Icons.Default.ArrowUpward
    }
    
    val movementTypeText = when (movement.movementType) {
        MovementType.ENTRY -> "Entrada"
        MovementType.EXIT -> "Salida"
    }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .background(backgroundColor)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = movementTypeText,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    movement.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    movementTypeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = iconColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Cantidad: ${movement.quantity} ${movement.product.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (movement.reason != null) {
                    Text(
                        "Razón: ${movement.reason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    movement.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Active filters chips display
 * Validates: Requirements 15.6
 */
@Composable
fun ActiveFiltersChips(
    state: InventoryMovementListUiState,
    onClearFilters: () -> Unit,
    onRemoveMovementTypeFilter: () -> Unit,
    onRemoveDateFilter: () -> Unit
) {
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            AssistChip(
                onClick = onClearFilters,
                label = { Text("Limpiar filtros") },
                leadingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
            )
        }
        
        if (state.selectedMovementType != null) {
            item {
                FilterChip(
                    selected = true,
                    onClick = onRemoveMovementTypeFilter,
                    label = {
                        Text(
                            when (state.selectedMovementType) {
                                MovementType.ENTRY -> "Entrada"
                                MovementType.EXIT -> "Salida"
                            }
                        )
                    },
                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
                )
            }
        }
        
        if (state.startDate != null || state.endDate != null) {
            item {
                FilterChip(
                    selected = true,
                    onClick = onRemoveDateFilter,
                    label = {
                        val dateText = when {
                            state.startDate != null && state.endDate != null ->
                                "${state.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${state.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                            state.startDate != null ->
                                "Desde ${state.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                            else ->
                                "Hasta ${state.endDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                        }
                        Text(dateText)
                    },
                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
                )
            }
        }
    }
}

/**
 * Filter dialog for date range and movement type
 * Validates: Requirements 9.6
 */
@Composable
fun FilterDialog(
    state: InventoryMovementListUiState,
    onDismiss: () -> Unit,
    onApplyFilters: (startDate: java.time.LocalDate?, endDate: java.time.LocalDate?, movementType: MovementType?) -> Unit
) {
    var selectedMovementType by remember { mutableStateOf(state.selectedMovementType) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar movimientos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Tipo de movimiento", style = MaterialTheme.typography.titleSmall)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedMovementType == null,
                        onClick = { selectedMovementType = null },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = selectedMovementType == MovementType.ENTRY,
                        onClick = { selectedMovementType = MovementType.ENTRY },
                        label = { Text("Entrada") }
                    )
                    FilterChip(
                        selected = selectedMovementType == MovementType.EXIT,
                        onClick = { selectedMovementType = MovementType.EXIT },
                        label = { Text("Salida") }
                    )
                }
                
                Text(
                    "Nota: Los filtros de fecha se implementarán con date pickers en una versión futura",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApplyFilters(state.startDate, state.endDate, selectedMovementType)
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
