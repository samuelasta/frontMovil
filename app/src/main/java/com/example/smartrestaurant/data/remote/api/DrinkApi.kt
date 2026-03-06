package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.DrinkDto
import retrofit2.http.*

/**
 * Retrofit API interface for Drink endpoints.
 * Validates: Requirements 7.1, 7.3, 7.6, 7.7, 11.1
 */
interface DrinkApi {
    
    @GET("api/drinks")
    suspend fun getDrinks(@Query("page") page: Int = 0): ApiResponse<List<DrinkDto>>
    
    @GET("api/drinks/{id}")
    suspend fun getDrinkById(@Path("id") id: String): ApiResponse<DrinkDto>
    
    @POST("api/drinks")
    suspend fun createDrink(@Body drink: DrinkDto): ApiResponse<DrinkDto>
    
    @PUT("api/drinks/{id}")
    suspend fun updateDrink(@Path("id") id: String, @Body drink: DrinkDto): ApiResponse<DrinkDto>
    
    @DELETE("api/drinks/{id}")
    suspend fun deleteDrink(@Path("id") id: String): ApiResponse<Unit>
}
