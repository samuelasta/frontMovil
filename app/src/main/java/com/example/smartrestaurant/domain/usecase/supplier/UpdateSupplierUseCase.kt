package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.repository.SupplierRepository
import javax.inject.Inject

/**
 * Use case for updating an existing supplier.
 * Validates: Requirements 4.6
 */
class UpdateSupplierUseCase @Inject constructor(
    private val repository: SupplierRepository
) {
    suspend operator fun invoke(id: String, supplier: Supplier): Result<Supplier> {
        return repository.updateSupplier(id, supplier)
    }
}
