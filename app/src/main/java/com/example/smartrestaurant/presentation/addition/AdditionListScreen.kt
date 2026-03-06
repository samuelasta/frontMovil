package com.example.smartrestaurant.presentation.addition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.presentation.common.components.*

/**
 * AdditionListScreen composable
 * Validates: Requirements 8.1, 11.2, 11.3, 11.4, 15.1, 15.2
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionListScreen(
    viewModel: AdditionViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Adiciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Crear adición")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            SearchBar(
                query = listState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Buscar adiciones..."
            )

            when {
                listState.isLoading && listState.additions.isEmpty() -> LoadingIndicator()
                listState.error != null && listState.additions.isEmpty() -> {
                    ErrorMessage(
                        message = listState.error!!,
                        onRetry = { viewModel.retryLastOperation() }
                    )
                }
                listState.filteredAdditions.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay adiciones registradas")
                    }
                }
                else -> {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(listState.filteredAdditions.size, key = { index ->
                            listState.filteredAdditions[index].id ?: listState.filteredAdditions[index].hashCode()
                        }) { index ->
                            val addition = listState.filteredAdditions[index]
                            AdditionItem(addition) { onNavigateToDetail(addition.id!!) }
                        }
                        
                        if (listState.hasMorePages && !listState.isLoading) {
                            item {
                                LaunchedEffect(Unit) {
                                    viewModel.loadAdditions(listState.currentPage + 1)
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
 * AdditionItem composable displaying addition information
 * Shows: name, price, and image
 * Validates: Requirements 8.1, 11.3, 11.4
 */
@Composable
fun AdditionItem(addition: Addition, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = addition.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(addition.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${addition.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
