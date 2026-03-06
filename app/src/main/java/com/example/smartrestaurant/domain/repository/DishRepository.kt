package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Dish

/**
 * Repository interface for Dish operations.
 * Defines the contract for dish data access following Clean Architecture principles.
 * All methods return Result<T> for proper error handling.
 * Supports pagination for dish lists.
 * 
 * Validates: Requirements 6.1, 6.4, 6.7, 6.8
 */
interface DishRepository {
    
    /**
     * Get paginated list of dishes.
     * @param page Page number for pagination
     * @return Result containing list of Dish or error
     */
    suspend fun getDishes(page: Int): Result<List<Dish>>
    
    /**
     * Get a single dish by ID with complete recipe.
     * @param id Dish ID
     * @return Result containing Dish or error
     */
    suspend fun getDishById(id: String): Result<Dish>
    
    /**
     * Create a new dish with recipe.
     * @param dish Dish to create
     * @return Result containing created Dish or error
     */
    suspend fun createDish(dish: Dish): Result<Dish>
    
    /**
     * Update an existing dish with recipe.
     * @param id Dish ID
     * @param dish Dish with updated data
     * @return Result containing updated Dish or error
     */
    suspend fun updateDish(id: String, dish: Dish): Result<Dish>
    
    /**
     * Delete a dish (soft delete - marks as INACTIVE).
     * @param id Dish ID
     * @return Result with Unit on success or error
     */
    suspend fun deleteDish(id: String): Result<Unit>
}
