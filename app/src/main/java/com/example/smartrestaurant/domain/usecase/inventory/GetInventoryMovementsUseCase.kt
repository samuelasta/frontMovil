package com.example.smartrestaurant.domain.usecase.inventory

import com.example.smartrestaurant.domain.model.InventoryMovement
import com.example.smartrestaurant.domain.repository.InventoryRepository
import javax.inject.Inject

/**
 * Use case for retrieving all inventory movements.
 * Validates: Requirements 9.2
 */
class GetInventoryMovementsUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(): Result<List<InventoryMovement>> = repository.getInventoryMovements()
}
