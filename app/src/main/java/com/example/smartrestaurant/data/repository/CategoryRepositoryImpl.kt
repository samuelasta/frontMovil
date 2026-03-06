package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.CategoryMapper
import com.example.smartrestaurant.data.remote.api.CategoryApi
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.repository.CategoryRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of CategoryRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 5.1, 5.3, 5.6, 5.7, 13.3, 13.4
 */
class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi,
    private val mapper: CategoryMapper
) : CategoryRepository {
    
    /**
     * Get list of categories.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @return Result containing list of Category or error
     */
    override suspend fun getCategories(): Result<List<Category>> = try {
        val response = api.getCategories()
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
     * Get a single category by ID.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Category ID
     * @return Result containing Category or error
     */
    override suspend fun getCategoryById(id: String): Result<Category> = try {
        val response = api.getCategoryById(id)
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
     * Create a new category.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param category Category to create
     * @return Result containing created Category or error
     */
    override suspend fun createCategory(category: Category): Result<Category> = try {
        val categoryDto = mapper.toDto(category)
        val response = api.createCategory(categoryDto)
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
     * Update an existing category.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Category ID
     * @param category Category with updated data
     * @return Result containing updated Category or error
     */
    override suspend fun updateCategory(id: String, category: Category): Result<Category> = try {
        val categoryDto = mapper.toDto(category)
        val response = api.updateCategory(id, categoryDto)
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
     * Delete a category (soft delete - marks as INACTIVE).
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Category ID
     * @return Result with Unit on success or error
     */
    override suspend fun deleteCategory(id: String): Result<Unit> = try {
        val response = api.deleteCategory(id)
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
