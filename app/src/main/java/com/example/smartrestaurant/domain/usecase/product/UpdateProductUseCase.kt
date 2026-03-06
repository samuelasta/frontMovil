package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for updating an existing product.
 * Validates: Requirements 1.6
 */
class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: String, product: Product): Result<Product> {
        return repository.updateProduct(id, product)
    }
}
