package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Product

/**
 * Repository interface for Product operations.
 * Defines the contract for product data access following Clean Architecture principles.
 * All methods return Result<T> for proper error handling.
 */
interface ProductRepository {
    
    /**
     * Get paginated list of products.
     * @param page Page number for pagination
     * @return Result containing list of Product or error
     */
    suspend fun getProducts(page: Int): Result<List<Product>>
    
    /**
     * Get a single product by ID.
     * @param id Product ID
     * @return Result containing Product or error
     */
    suspend fun getProductById(id: String): Result<Product>
    
    /**
     * Create a new product.
     * @param product Product to create
     * @return Result containing created Product or error
     */
    suspend fun createProduct(product: Product): Result<Product>
    
    /**
     * Update an existing product.
     * @param id Product ID
     * @param product Product with updated data
     * @return Result containing updated Product or error
     */
    suspend fun updateProduct(id: String, product: Product): Result<Product>
    
    /**
     * Delete a product (soft delete).
     * @param id Product ID
     * @return Result with Unit on success or error
     */
    suspend fun deleteProduct(id: String): Result<Unit>
    
    /**
     * Add stock to a product.
     * @param id Product ID
     * @param quantity Quantity to add
     * @param reason Optional reason for stock addition
     * @return Result containing updated Product or error
     */
    suspend fun addStock(id: String, quantity: Double, reason: String?): Result<Product>
    
    /**
     * Subtract stock from a product.
     * @param id Product ID
     * @param quantity Quantity to subtract
     * @param reason Optional reason for stock subtraction
     * @return Result containing updated Product or error
     */
    suspend fun subtractStock(id: String, quantity: Double, reason: String?): Result<Product>
}
