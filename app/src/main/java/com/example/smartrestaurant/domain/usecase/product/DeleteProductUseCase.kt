package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for deleting a product (soft delete).
 * Validates: Requirements 1.7
 */
class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteProduct(id)
    }
}
