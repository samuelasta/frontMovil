package com.example.smartrestaurant.presentation.inventory

import com.example.smartrestaurant.domain.model.InventoryMovement
import com.example.smartrestaurant.domain.model.MovementType
import java.time.LocalDate

/**
 * UI State for Inventory movement list screen
 * Validates: Requirements 9.1, 9.6
 */
data class InventoryMovementListUiState(
    val movements: List<InventoryMovement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val selectedMovementType: MovementType? = null
) {
    /**
     * Get filtered movements based on date range and movement type
     * Validates: Requirements 9.6
     */
    val filteredMovements: List<InventoryMovement>
        get() {
            var filtered = movements
            
            // Filter by date range
            if (startDate != null) {
                filtered = filtered.filter { it.timestamp.toLocalDate() >= startDate }
            }
            if (endDate != null) {
                filtered = filtered.filter { it.timestamp.toLocalDate() <= endDate }
            }
            
            // Filter by movement type
            if (selectedMovementType != null) {
                filtered = filtered.filter { it.movementType == selectedMovementType }
            }
            
            return filtered
        }
    
    /**
     * Check if any filters are active
     * Validates: Requirements 15.6
     */
    val hasActiveFilters: Boolean
        get() = startDate != null || endDate != null || selectedMovementType != null
}

/**
 * UI State for Inventory movement detail screen
 * Validates: Requirements 9.5
 */
data class InventoryMovementDetailUiState(
    val movement: InventoryMovement? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
