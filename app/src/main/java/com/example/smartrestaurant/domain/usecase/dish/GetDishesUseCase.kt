package com.example.smartrestaurant.domain.usecase.dish

import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.repository.DishRepository
import javax.inject.Inject

/**
 * Use case for retrieving paginated list of dishes.
 * Validates: Requirements 6.1
 */
class GetDishesUseCase @Inject constructor(
    private val repository: DishRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Dish>> {
        return repository.getDishes(page)
    }
}
