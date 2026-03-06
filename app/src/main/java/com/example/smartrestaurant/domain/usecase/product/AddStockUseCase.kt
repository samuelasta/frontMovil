package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for adding stock to a product.
 * Validates business rule: quantity must be positive.
 * Validates: Requirements 2.3, 12.3
 */
class AddStockUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        productId: String,
        quantity: Double,
        reason: String? = null
    ): Result<Product> {
        // Validate that quantity is positive
        if (quantity <= 0) {
            return Result.failure(
                IllegalArgumentException("Quantity must be positive")
            )
        }
        
        return repository.addStock(productId, quantity, reason)
    }
}
