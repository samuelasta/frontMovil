package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.repository.SupplierRepository
import javax.inject.Inject

/**
 * Use case for creating a new supplier.
 * Validates: Requirements 4.3
 */
class CreateSupplierUseCase @Inject constructor(
    private val repository: SupplierRepository
) {
    suspend operator fun invoke(supplier: Supplier): Result<Supplier> {
        return repository.createSupplier(supplier)
    }
}
