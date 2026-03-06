package com.example.smartrestaurant.domain.usecase.dish

import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.repository.DishRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single dish by ID with complete recipe.
 * Validates: Requirements 6.1
 */
class GetDishByIdUseCase @Inject constructor(
    private val repository: DishRepository
) {
    suspend operator fun invoke(id: String): Result<Dish> {
        return repository.getDishById(id)
    }
}
