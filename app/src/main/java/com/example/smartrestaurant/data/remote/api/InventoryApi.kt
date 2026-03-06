package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.InventoryMovementDto
import retrofit2.http.GET

/**
 * Retrofit API interface for Inventory endpoints.
 * Defines REST API operations for inventory movement queries.
 * Validates: Requirements 9.2
 */
interface InventoryApi {
    
    /**
     * Get all inventory movements.
     * @return ApiResponse containing list of InventoryMovementDto
     */
    @GET("api/inventory/all")
    suspend fun getAllInventoryMovements(): ApiResponse<List<InventoryMovementDto>>
}
