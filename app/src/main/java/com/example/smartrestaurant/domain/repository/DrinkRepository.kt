package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Drink

/**
 * Repository interface for Drink operations.
 * Validates: Requirements 7.1, 7.3, 7.6, 7.7
 */
interface DrinkRepository {
    suspend fun getDrinks(page: Int): Result<List<Drink>>
    suspend fun getDrinkById(id: String): Result<Drink>
    suspend fun createDrink(drink: Drink): Result<Drink>
    suspend fun updateDrink(id: String, drink: Drink): Result<Drink>
    suspend fun deleteDrink(id: String): Result<Unit>
}
