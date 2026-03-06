package com.example.smartrestaurant.domain.usecase.dish

import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.repository.DishRepository
import javax.inject.Inject

/**
 * Use case for creating a new dish with recipe.
 * Validates that the dish has at least one ingredient in the recipe.
 * Validates: Requirements 6.4, 6.9
 */
class CreateDishUseCase @Inject constructor(
    private val repository: DishRepository
) {
    suspend operator fun invoke(dish: Dish): Result<Dish> {
        // Validate that recipe is not empty
        if (dish.recipe.isEmpty()) {
            return Result.failure(IllegalArgumentException("Dish must have at least one ingredient in the recipe"))
        }
        
        return repository.createDish(dish)
    }
}
