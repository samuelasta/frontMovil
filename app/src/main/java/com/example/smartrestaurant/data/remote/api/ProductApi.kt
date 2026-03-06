package com.example.smartrestaurant.data.remote.api

import com.example.smartrestaurant.data.remote.dto.ApiResponse
import com.example.smartrestaurant.data.remote.dto.ProductDto
import com.example.smartrestaurant.data.remote.dto.StockMovementDto
import retrofit2.http.*

/**
 * Retrofit API interface for Product endpoints.
 * Defines REST API operations for product management including CRUD operations and stock management.
 */
interface ProductApi {
    
    /**
     * Get paginated list of products.
     * @param page Page number for pagination (default: 0)
     * @return ApiResponse containing list of ProductDto
     */
    @GET("api/products")
    suspend fun getProducts(@Query("page") page: Int = 0): ApiResponse<List<ProductDto>>
    
    /**
     * Get a single product by ID.
     * @param id Product ID
     * @return ApiResponse containing ProductDto
     */
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>
    
    /**
     * Create a new product.
     * @param product ProductDto with product data
     * @return ApiResponse containing created ProductDto
     */
    @POST("api/products")
    suspend fun createProduct(@Body product: ProductDto): ApiResponse<ProductDto>
    
    /**
     * Update an existing product.
     * @param id Product ID
     * @param product ProductDto with updated product data
     * @return ApiResponse containing updated ProductDto
     */
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductDto
    ): ApiResponse<ProductDto>
    
    /**
     * Delete a product (soft delete - marks as INACTIVE).
     * @param id Product ID
     * @return ApiResponse with Unit
     */
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): ApiResponse<Unit>
    
    /**
     * Add stock to a product.
     * @param id Product ID
     * @param request StockMovementDto with weight to add
     * @return ApiResponse containing updated ProductDto
     */
    @PUT("api/products/add-stock/{id}")
    suspend fun addStock(
        @Path("id") id: String,
        @Body request: StockMovementDto
    ): ApiResponse<ProductDto>
    
    /**
     * Subtract stock from a product.
     * @param id Product ID
     * @param request StockMovementDto with weight to subtract
     * @return ApiResponse containing updated ProductDto
     */
    @PUT("api/products/subtract-stock/{id}")
    suspend fun subtractStock(
        @Path("id") id: String,
        @Body request: StockMovementDto
    ): ApiResponse<ProductDto>
}
