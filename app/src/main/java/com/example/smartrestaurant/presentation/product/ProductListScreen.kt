package com.example.smartrestaurant.presentation.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator
import com.example.smartrestaurant.presentation.common.components.SearchBar

/**
 * Product List Screen
 * Validates: Requirements 1.1, 3.1, 3.2, 3.3, 3.4, 11.3, 13.1, 15.2, 15.6
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()
    val lazyListState = rememberLazyListState()

    // Infinite scroll implementation
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= listState.filteredProducts.size - 2 &&
                    !listState.isLoading &&
                    listState.hasMorePages
                ) {
                    viewModel.loadProducts(listState.currentPage + 1)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                actions = {
                    // Low stock filter toggle
                    IconButton(
                        onClick = { viewModel.toggleLowStockFilter() }
                    ) {
                        Icon(
                            imageVector = if (listState.showLowStockOnly) {
                                Icons.Default.FilterAlt
                            } else {
                                Icons.Default.FilterAltOff
                            },
                            contentDescription = if (listState.showLowStockOnly) {
                                "Mostrar todos"
                            } else {
                                "Filtrar stock bajo"
                            },
                            tint = if (listState.showLowStockOnly) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear producto"
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
                placeholder = "Buscar productos..."
            )

            // Low stock counter
            if (listState.lowStockCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "${listState.lowStockCount} producto(s) con stock bajo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Active filters indicator
            if (listState.showLowStockOnly || listState.selectedSupplier != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (listState.showLowStockOnly) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.toggleLowStockFilter() },
                            label = { Text("Stock bajo") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar filtro",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    if (listState.selectedSupplier != null) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.filterBySupplier(null) },
                            label = { Text(listState.selectedSupplier!!.name) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar filtro",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Content
            when {
                listState.isLoading && listState.products.isEmpty() -> {
                    LoadingIndicator()
                }
                listState.error != null && listState.products.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (listState.searchQuery.isNotEmpty() || listState.showLowStockOnly) {
                                "No se encontraron productos"
                            } else {
                                "No hay productos registrados"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(
                            items = listState.filteredProducts,
                            key = { it.id ?: it.hashCode() }
                        ) { product ->
                            ProductItem(
                                product = product,
                                onClick = { onNavigateToDetail(product.id!!) }
                            )
                        }

                        // Loading indicator for pagination
                        if (listState.isLoading && listState.products.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Product Item composable
 * Validates: Requirements 1.1, 3.1, 3.3
 */
@Composable
fun ProductItem(
    product: Product,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = product.supplier.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Stock: ${product.stock} ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.isLowStock) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (product.isLowStock) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    Text(
                        text = "Mín: ${product.minimumStock} ${product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Low stock indicator
            if (product.isLowStock) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Stock bajo",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
