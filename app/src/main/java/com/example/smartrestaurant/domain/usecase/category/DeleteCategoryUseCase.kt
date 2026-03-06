package com.example.smartrestaurant.domain.usecase.category

import com.example.smartrestaurant.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for deleting a category (soft delete).
 * Validates: Requirements 5.7
 */
class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteCategory(id)
    }
}
