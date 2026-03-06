package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.SupplierDto
import retrofit2.http.*

/**
 * Retrofit API interface for Supplier endpoints.
 * Defines REST API operations for supplier management including CRUD operations.
 */
interface SupplierApi {
    
    /**
     * Get list of suppliers.
     * @return ApiResponse containing list of SupplierDto
     */
    @GET("api/supliers")
    suspend fun getSuppliers(): ApiResponse<List<SupplierDto>>
    
    /**
     * Get a single supplier by ID.
     * @param id Supplier ID
     * @return ApiResponse containing SupplierDto
     */
    @GET("api/supliers/{id}")
    suspend fun getSupplierById(@Path("id") id: String): ApiResponse<SupplierDto>
    
    /**
     * Create a new supplier.
     * @param supplier SupplierDto with supplier data
     * @return ApiResponse containing created SupplierDto
     */
    @POST("api/supliers")
    suspend fun createSupplier(@Body supplier: SupplierDto): ApiResponse<SupplierDto>
    
    /**
     * Update an existing supplier.
     * @param id Supplier ID
     * @param supplier SupplierDto with updated supplier data
     * @return ApiResponse containing updated SupplierDto
     */
    @PUT("api/supliers/{id}")
    suspend fun updateSupplier(
        @Path("id") id: String,
        @Body supplier: SupplierDto
    ): ApiResponse<SupplierDto>
    
    /**
     * Delete a supplier (soft delete - marks as INACTIVE).
     * @param id Supplier ID
     * @return ApiResponse with Unit
     */
    @DELETE("api/supliers/{id}")
    suspend fun deleteSupplier(@Path("id") id: String): ApiResponse<Unit>
}
