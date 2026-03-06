package com.example.smartrestaurant.presentation.drink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import coil.compose.AsyncImage
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.presentation.common.components.*

/**
 * DrinkListScreen composable
 * Validates: Requirements 7.1, 11.2, 11.3, 11.4, 15.1, 15.2, 15.5
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkListScreen(
    viewModel: DrinkViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bebidas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Crear bebida")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchBar(
                query = listState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Buscar bebidas..."
            )

            // Category filter chips
            val availableCategories = listState.drinks.map { it.category }.distinctBy { it.id }
            if (availableCategories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = listState.selectedCategory == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("Todas") }
                        )
                    }
                    items(availableCategories, key = { it.id ?: it.hashCode() }) { category ->
                        FilterChip(
                            selected = listState.selectedCategory?.id == category.id,
                            onClick = { viewModel.filterByCategory(category) },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            when {
                listState.isLoading && listState.drinks.isEmpty() -> LoadingIndicator()
                listState.error != null && listState.drinks.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredDrinks.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay bebidas registradas")
                    }
                }
                else -> {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(listState.filteredDrinks, key = { it.id ?: it.hashCode() }) { drink ->
                            DrinkItem(drink) { onNavigateToDetail(drink.id!!) }
                        }
                        
                        if (listState.hasMorePages && !listState.isLoading) {
                            item {
                                LaunchedEffect(Unit) {
                                    viewModel.loadDrinks(listState.currentPage + 1)
                                }
                            }
                        }
                        
                        if (listState.isLoading) {
                            item { CircularProgressIndicator(Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

/**
 * DrinkItem composable displaying drink information
 * Shows: name, price, category, stockUnits, and image
 * Validates: Requirements 7.1, 11.3, 11.4
 */
@Composable
fun DrinkItem(drink: Drink, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = drink.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(drink.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(drink.category.name, style = MaterialTheme.typography.bodySmall)
                Text("$${drink.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text("Stock: ${drink.stockUnits} unidades", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
