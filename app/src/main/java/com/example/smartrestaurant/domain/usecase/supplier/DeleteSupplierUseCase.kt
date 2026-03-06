package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.repository.SupplierRepository
import javax.inject.Inject

/**
 * Use case for deleting a supplier (soft delete).
 * Validates: Requirements 4.7
 */
class DeleteSupplierUseCase @Inject constructor(
    private val repository: SupplierRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteSupplier(id)
    }
}
