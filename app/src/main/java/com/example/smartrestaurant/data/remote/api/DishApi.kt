package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.DishDto
import retrofit2.http.*

/**
 * Retrofit API interface for Dish endpoints.
 * Defines REST API operations for dish management including CRUD operations with pagination.
 * Validates: Requirements 6.1, 6.4, 6.7, 6.8, 11.1
 */
interface DishApi {
    
    /**
     * Get paginated list of dishes.
     * @param page Page number for pagination (default: 0)
     * @return ApiResponse containing list of DishDto
     */
    @GET("api/dishes")
    suspend fun getDishes(@Query("page") page: Int = 0): ApiResponse<List<DishDto>>
    
    /**
     * Get a single dish by ID.
     * @param id Dish ID
     * @return ApiResponse containing DishDto with complete recipe
     */
    @GET("api/dishes/{id}")
    suspend fun getDishById(@Path("id") id: String): ApiResponse<DishDto>
    
    /**
     * Create a new dish.
     * @param dish DishDto with dish data including recipe
     * @return ApiResponse containing created DishDto
     */
    @POST("api/dishes")
    suspend fun createDish(@Body dish: DishDto): ApiResponse<DishDto>
    
    /**
     * Update an existing dish.
     * @param id Dish ID
     * @param dish DishDto with updated dish data including recipe
     * @return ApiResponse containing updated DishDto
     */
    @PUT("api/dishes/{id}")
    suspend fun updateDish(
        @Path("id") id: String,
        @Body dish: DishDto
    ): ApiResponse<DishDto>
    
    /**
     * Delete a dish (soft delete - marks as INACTIVE).
     * @param id Dish ID
     * @return ApiResponse with Unit
     */
    @DELETE("api/dishes/{id}")
    suspend fun deleteDish(@Path("id") id: String): ApiResponse<Unit>
}
