package com.example.smartrestaurant.presentation.dish

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.presentation.common.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishListScreen(
    viewModel: DishViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Platos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Crear plato")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchBar(
                query = listState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Buscar platos..."
            )
            
            // Category filter chips
            val availableCategories = listState.dishes.map { it.category }.distinctBy { it.id }
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
                listState.isLoading && listState.dishes.isEmpty() -> LoadingIndicator()
                listState.error != null && listState.dishes.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredDishes.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay platos registrados")
                    }
                }
                else -> {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(listState.filteredDishes, key = { it.id ?: it.hashCode() }) { dish ->
                            DishItem(dish) { onNavigateToDetail(dish.id!!) }
                        }
                        
                        if (listState.hasMorePages && !listState.isLoading) {
                            item {
                                LaunchedEffect(Unit) {
                                    viewModel.loadDishes(listState.currentPage + 1)
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

@Composable
fun DishItem(dish: Dish, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = dish.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(dish.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dish.category.name, style = MaterialTheme.typography.bodySmall)
                Text("$${dish.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
