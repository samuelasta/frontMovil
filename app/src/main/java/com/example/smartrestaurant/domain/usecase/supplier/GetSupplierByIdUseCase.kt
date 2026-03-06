package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.repository.SupplierRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single supplier by ID.
 * Validates: Requirements 4.1
 */
class GetSupplierByIdUseCase @Inject constructor(
    private val repository: SupplierRepository
) {
    suspend operator fun invoke(id: String): Result<Supplier> {
        return repository.getSupplierById(id)
    }
}
