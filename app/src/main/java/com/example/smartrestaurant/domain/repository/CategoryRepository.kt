package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Category

/**
 * Repository interface for Category operations.
 * Defines the contract for category data access following Clean Architecture principles.
 * All methods return Result<T> for proper error handling.
 * 
 * Validates: Requirements 5.1, 5.3, 5.6, 5.7
 */
interface CategoryRepository {
    
    /**
     * Get list of categories.
     * @return Result containing list of Category or error
     */
    suspend fun getCategories(): Result<List<Category>>
    
    /**
     * Get a single category by ID.
     * @param id Category ID
     * @return Result containing Category or error
     */
    suspend fun getCategoryById(id: String): Result<Category>
    
    /**
     * Create a new category.
     * @param category Category to create
     * @return Result containing created Category or error
     */
    suspend fun createCategory(category: Category): Result<Category>
    
    /**
     * Update an existing category.
     * @param id Category ID
     * @param category Category with updated data
     * @return Result containing updated Category or error
     */
    suspend fun updateCategory(id: String, category: Category): Result<Category>
    
    /**
     * Delete a category (soft delete - marks as INACTIVE).
     * @param id Category ID
     * @return Result with Unit on success or error
     */
    suspend fun deleteCategory(id: String): Result<Unit>
}
