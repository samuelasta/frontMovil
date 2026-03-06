package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.DishMapper
import com.example.smartrestaurant.data.remote.api.DishApi
import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.repository.DishRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of DishRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 6.1, 6.4, 6.7, 6.8, 11.1, 13.3, 13.4
 */
class DishRepositoryImpl @Inject constructor(
    private val api: DishApi,
    private val mapper: DishMapper
) : DishRepository {
    
    /**
     * Get paginated list of dishes.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param page Page number for pagination
     * @return Result containing list of Dish or error
     */
    override suspend fun getDishes(page: Int): Result<List<Dish>> = try {
        val response = api.getDishes(page)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(response.message.map { mapper.toDomain(it) })
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    /**
     * Get a single dish by ID with complete recipe.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Dish ID
     * @return Result containing Dish or error
     */
    override suspend fun getDishById(id: String): Result<Dish> = try {
        val response = api.getDishById(id)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    /**
     * Create a new dish with recipe.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param dish Dish to create
     * @return Result containing created Dish or error
     */
    override suspend fun createDish(dish: Dish): Result<Dish> = try {
        val dishDto = mapper.toDto(dish)
        val response = api.createDish(dishDto)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    /**
     * Update an existing dish with recipe.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Dish ID
     * @param dish Dish with updated data
     * @return Result containing updated Dish or error
     */
    override suspend fun updateDish(id: String, dish: Dish): Result<Dish> = try {
        val dishDto = mapper.toDto(dish)
        val response = api.updateDish(id, dishDto)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(mapper.toDomain(response.message))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
    
    /**
     * Delete a dish (soft delete - marks as INACTIVE).
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Dish ID
     * @return Result with Unit on success or error
     */
    override suspend fun deleteDish(id: String): Result<Unit> = try {
        val response = api.deleteDish(id)
        if (response.error) {
            Result.failure(Exception(response.message.toString()))
        } else {
            Result.success(Unit)
        }
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: HttpException) {
        Result.failure(Exception("Server error: ${e.code()} - ${e.message()}"))
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
}
