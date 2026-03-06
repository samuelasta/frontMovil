package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for creating a new product.
 * Validates: Requirements 1.3
 */
class CreateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Product> {
        return repository.createProduct(product)
    }
}
