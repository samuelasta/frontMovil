package com.example.smartrestaurant.domain.usecase.category

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for updating an existing category.
 * Validates: Requirements 5.6
 */
class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String, category: Category): Result<Category> {
        return repository.updateCategory(id, category)
    }
}
