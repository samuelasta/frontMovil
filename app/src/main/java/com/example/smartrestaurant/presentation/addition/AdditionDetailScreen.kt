package com.example.smartrestaurant.presentation.addition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartrestaurant.presentation.common.components.*

/**
 * AdditionDetailScreen composable
 * Validates: Requirements 8.4
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionDetailScreen(
    additionId: String,
    viewModel: AdditionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(additionId) { viewModel.loadAdditionById(additionId) }
    DisposableEffect(Unit) { onDispose { viewModel.resetDetailState() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Adición") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (detailState.addition != null) {
                        IconButton(onClick = { onNavigateToEdit(additionId) }) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirmation() }) {
                            Icon(Icons.Default.Delete, "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> LoadingIndicator()
            detailState.error != null -> ErrorMessage(
                message = detailState.error!!,
                onRetry = { viewModel.loadAdditionById(additionId) }
            )
            detailState.addition != null -> {
                val addition = detailState.addition!!
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AsyncImage(model = addition.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
                            Text(addition.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Divider()
                            Text("Descripción: ${addition.description}")
                            Text("Precio: ${addition.price}")
                            Text("Estado: ${addition.state.name}")
                        }
                    }
                }
            }
        }
    }

    if (detailState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Eliminar Adición",
            message = "¿Está seguro que desea eliminar esta adición?",
            onConfirm = { viewModel.deleteAddition(onNavigateBack) },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }
}
