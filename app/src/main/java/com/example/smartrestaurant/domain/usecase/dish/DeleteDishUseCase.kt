package com.example.smartrestaurant.domain.usecase.dish

import com.example.smartrestaurant.domain.repository.DishRepository
import javax.inject.Inject

/**
 * Use case for deleting a dish (soft delete).
 * Validates: Requirements 6.8
 */
class DeleteDishUseCase @Inject constructor(
    private val repository: DishRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteDish(id)
    }
}
