package com.example.smartrestaurant.domain.usecase.category

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for creating a new category.
 * Validates: Requirements 5.3
 */
class CreateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Category> {
        return repository.createCategory(category)
    }
}
