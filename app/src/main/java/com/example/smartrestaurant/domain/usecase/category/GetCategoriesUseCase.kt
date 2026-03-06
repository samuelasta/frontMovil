package com.example.smartrestaurant.domain.usecase.category

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for retrieving list of categories.
 * Validates: Requirements 5.1
 */
class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repository.getCategories()
    }
}
