package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Use case for retrieving paginated list of products.
 * Validates: Requirements 1.1
 */
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Product>> {
        return repository.getProducts(page)
    }
}
