package com.example.smartrestaurant.domain.usecase.dish

import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.repository.DishRepository
import javax.inject.Inject

/**
 * Use case for updating an existing dish with recipe.
 * Validates: Requirements 6.7
 */
class UpdateDishUseCase @Inject constructor(
    private val repository: DishRepository
) {
    suspend operator fun invoke(id: String, dish: Dish): Result<Dish> {
        // Validate that recipe is not empty
        if (dish.recipe.isEmpty()) {
            return Result.failure(IllegalArgumentException("Dish must have at least one ingredient in the recipe"))
        }
        
        return repository.updateDish(id, dish)
    }
}
