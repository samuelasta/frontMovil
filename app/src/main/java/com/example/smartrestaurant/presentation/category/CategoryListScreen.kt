package com.example.smartrestaurant.presentation.category

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
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.presentation.common.components.ErrorMessage
import com.example.smartrestaurant.presentation.common.components.LoadingIndicator
import com.example.smartrestaurant.presentation.common.components.SearchBar

/**
 * Category List Screen
 * Validates: Requirements 5.1, 15.1, 15.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear categoría"
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
                placeholder = "Buscar categorías..."
            )

            // Content
            when {
                listState.isLoading && listState.categories.isEmpty() -> {
                    LoadingIndicator()
                }
                listState.error != null && listState.categories.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredCategories.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (listState.searchQuery.isNotEmpty()) {
                                "No se encontraron categorías"
                            } else {
                                "No hay categorías registradas"
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
                            items = listState.filteredCategories,
                            key = { it.id ?: it.hashCode() }
                        ) { category ->
                            CategoryItem(
                                category = category,
                                onClick = { onNavigateToDetail(category.id!!) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Category Item composable
 * Validates: Requirements 5.1
 */
@Composable
fun CategoryItem(
    category: Category,
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
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
