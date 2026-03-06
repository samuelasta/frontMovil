package com.example.smartrestaurant.domain.usecase.category

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single category by ID.
 * Validates: Requirements 5.1
 */
class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String): Result<Category> {
        return repository.getCategoryById(id)
    }
}
