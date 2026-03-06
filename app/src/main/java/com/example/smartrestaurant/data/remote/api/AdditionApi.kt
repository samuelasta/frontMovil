package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.AdditionDto
import retrofit2.http.*

/**
 * Retrofit API interface for Addition endpoints.
 * Defines REST API operations for addition management including CRUD operations with pagination.
 * Validates: Requirements 8.1, 8.3, 8.6, 8.7, 11.1
 */
interface AdditionApi {
    
    /**
     * Get paginated list of additions.
     * @param page Page number for pagination (default: 0)
     * @return ApiResponse containing list of AdditionDto
     */
    @GET("api/additions")
    suspend fun getAdditions(@Query("page") page: Int = 0): ApiResponse<List<AdditionDto>>
    
    /**
     * Get a single addition by ID.
     * @param id Addition ID
     * @return ApiResponse containing AdditionDto
     */
    @GET("api/additions/{id}")
    suspend fun getAdditionById(@Path("id") id: String): ApiResponse<AdditionDto>
    
    /**
     * Create a new addition.
     * @param addition AdditionDto with addition data
     * @return ApiResponse containing created AdditionDto
     */
    @POST("api/additions")
    suspend fun createAddition(@Body addition: AdditionDto): ApiResponse<AdditionDto>
    
    /**
     * Update an existing addition.
     * @param id Addition ID
     * @param addition AdditionDto with updated addition data
     * @return ApiResponse containing updated AdditionDto
     */
    @PUT("api/additions/{id}")
    suspend fun updateAddition(
        @Path("id") id: String,
        @Body addition: AdditionDto
    ): ApiResponse<AdditionDto>
    
    /**
     * Delete an addition (soft delete - marks as INACTIVE).
     * @param id Addition ID
     * @return ApiResponse with Unit
     */
    @DELETE("api/additions/{id}")
    suspend fun deleteAddition(@Path("id") id: String): ApiResponse<Unit>
}
