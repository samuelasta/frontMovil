package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single product by ID.
 * Validates: Requirements 1.1
 */
class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: String): Result<Product> {
        return repository.getProductById(id)
    }
}
