package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.InventoryMovement

/**
 * Repository interface for Inventory operations.
 * Validates: Requirements 9.2
 */
interface InventoryRepository {
    suspend fun getInventoryMovements(): Result<List<InventoryMovement>>
}
