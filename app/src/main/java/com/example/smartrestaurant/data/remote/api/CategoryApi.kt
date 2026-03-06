package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.CategoryDto
import retrofit2.http.*

/**
 * Retrofit API interface for Category endpoints.
 * Defines REST API operations for category management including CRUD operations.
 */
interface CategoryApi {
    
    /**
     * Get list of categories.
     * @return ApiResponse containing list of CategoryDto
     */
    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryDto>>
    
    /**
     * Get a single category by ID.
     * @param id Category ID
     * @return ApiResponse containing CategoryDto
     */
    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): ApiResponse<CategoryDto>
    
    /**
     * Create a new category.
     * @param category CategoryDto with category data
     * @return ApiResponse containing created CategoryDto
     */
    @POST("api/categories")
    suspend fun createCategory(@Body category: CategoryDto): ApiResponse<CategoryDto>
    
    /**
     * Update an existing category.
     * @param id Category ID
     * @param category CategoryDto with updated category data
     * @return ApiResponse containing updated CategoryDto
     */
    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: CategoryDto
    ): ApiResponse<CategoryDto>
    
    /**
     * Delete a category (soft delete - marks as INACTIVE).
     * @param id Category ID
     * @return ApiResponse with Unit
     */
    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): ApiResponse<Unit>
}
