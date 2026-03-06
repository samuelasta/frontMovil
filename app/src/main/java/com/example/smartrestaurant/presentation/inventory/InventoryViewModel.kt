package com.example.smartrestaurant.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.MovementType
import com.example.smartrestaurant.domain.usecase.inventory.GetInventoryMovementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for Inventory screens
 * Validates: Requirements 9.2, 9.3, 9.6
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryMovementsUseCase: GetInventoryMovementsUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow(InventoryMovementListUiState())
    val listState: StateFlow<InventoryMovementListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(InventoryMovementDetailUiState())
    val detailState: StateFlow<InventoryMovementDetailUiState> = _detailState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadInventoryMovements()
    }

    // List Screen Methods

    /**
     * Load inventory movements and sort by date descending
     * Validates: Requirements 9.2, 9.3
     */
    fun loadInventoryMovements() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadInventoryMovements() }

            getInventoryMovementsUseCase().fold(
                onSuccess = { movements ->
                    // Sort by timestamp in descending order (most recent first)
                    val sortedMovements = movements.sortedByDescending { it.timestamp }
                    _listState.update {
                        it.copy(
                            movements = sortedMovements,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load inventory movements"
                        )
                    }
                }
            )
        }
    }

    /**
     * Filter by date range
     * Validates: Requirements 9.6
     */
    fun filterByDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        _listState.update {
            it.copy(
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    /**
     * Filter by movement type
     * Validates: Requirements 9.6
     */
    fun filterByMovementType(movementType: MovementType?) {
        _listState.update {
            it.copy(selectedMovementType = movementType)
        }
    }

    /**
     * Clear all filters
     * Validates: Requirements 15.6
     */
    fun clearFilters() {
        _listState.update {
            it.copy(
                startDate = null,
                endDate = null,
                selectedMovementType = null
            )
        }
    }

    // Detail Screen Methods

    /**
     * Load movement by ID
     * Validates: Requirements 9.5
     */
    fun loadMovementById(id: String) {
        val movement = _listState.value.movements.find { it.id == id }
        if (movement != null) {
            _detailState.update {
                it.copy(
                    movement = movement,
                    isLoading = false,
                    error = null
                )
            }
        } else {
            _detailState.update {
                it.copy(
                    isLoading = false,
                    error = "Movement not found"
                )
            }
        }
    }

    /**
     * Retry last failed operation
     * Validates: Requirements 13.5
     */
    fun retryLastOperation() {
        lastFailedOperation?.invoke()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = InventoryMovementDetailUiState()
    }
}
