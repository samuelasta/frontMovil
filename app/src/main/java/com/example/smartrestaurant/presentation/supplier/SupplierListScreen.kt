package com.example.smartrestaurant.presentation.supplier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator
import com.example.smartrestaurant.presentation.common.components.SearchBar

/**
 * Supplier List Screen
 * Validates: Requirements 4.1, 15.1, 15.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListScreen(
    viewModel: SupplierViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proveedores") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear proveedor"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            SearchBar(
                query = listState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Buscar proveedores..."
            )

            // Content
            when {
                listState.isLoading && listState.suppliers.isEmpty() -> {
                    LoadingIndicator()
                }
                listState.error != null && listState.suppliers.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredSuppliers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (listState.searchQuery.isNotEmpty()) {
                                "No se encontraron proveedores"
                            } else {
                                "No hay proveedores registrados"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(
                            items = listState.filteredSuppliers,
                            key = { it.id ?: it.hashCode() }
                        ) { supplier ->
                            SupplierItem(
                                supplier = supplier,
                                onClick = { onNavigateToDetail(supplier.id!!) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Supplier Item composable
 * Validates: Requirements 4.1
 */
@Composable
fun SupplierItem(
    supplier: Supplier,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = supplier.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Email:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = supplier.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Teléfono:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = supplier.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
