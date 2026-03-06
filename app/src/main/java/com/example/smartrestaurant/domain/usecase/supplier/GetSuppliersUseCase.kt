package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.repository.SupplierRepository
import javax.inject.Inject

/**
 * Use case for retrieving list of suppliers.
 * Validates: Requirements 4.1
 */
class GetSuppliersUseCase @Inject constructor(
    private val repository: SupplierRepository
) {
    suspend operator fun invoke(): Result<List<Supplier>> {
        return repository.getSuppliers()
    }
}
