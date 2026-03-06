package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.ProductMapper
import com.example.smartrestaurant.data.remote.api.ProductApi
import com.example.smartrestaurant.data.remote.dto.StockMovementDto
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.repository.ProductRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of ProductRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 1.3, 1.6, 1.7, 1.8, 2.3, 2.5, 13.3, 13.4
 */
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val mapper: ProductMapper
) : ProductRepository {
    
    /**
     * Get paginated list of products.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param page Page number for pagination
     * @return Result containing list of Product or error
     */
    override suspend fun getProducts(page: Int): Result<List<Product>> = try {
        val response = api.getProducts(page)
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
     * Get a single product by ID.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Product ID
     * @return Result containing Product or error
     */
    override suspend fun getProductById(id: String): Result<Product> = try {
        val response = api.getProductById(id)
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
     * Create a new product.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param product Product to create
     * @return Result containing created Product or error
     */
    override suspend fun createProduct(product: Product): Result<Product> = try {
        val productDto = mapper.toDto(product)
        val response = api.createProduct(productDto)
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
     * Update an existing product.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Product ID
     * @param product Product with updated data
     * @return Result containing updated Product or error
     */
    override suspend fun updateProduct(id: String, product: Product): Result<Product> = try {
        val productDto = mapper.toDto(product)
        val response = api.updateProduct(id, productDto)
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
     * Delete a product (soft delete - marks as INACTIVE).
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Product ID
     * @return Result with Unit on success or error
     */
    override suspend fun deleteProduct(id: String): Result<Unit> = try {
        val response = api.deleteProduct(id)
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
    
    /**
     * Add stock to a product.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Product ID
     * @param quantity Quantity to add
     * @param reason Optional reason for stock addition (not used by current API)
     * @return Result containing updated Product or error
     */
    override suspend fun addStock(id: String, quantity: Double, reason: String?): Result<Product> = try {
        val request = StockMovementDto(weight = quantity)
        val response = api.addStock(id, request)
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
     * Subtract stock from a product.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Product ID
     * @param quantity Quantity to subtract
     * @param reason Optional reason for stock subtraction (not used by current API)
     * @return Result containing updated Product or error
     */
    override suspend fun subtractStock(id: String, quantity: Double, reason: String?): Result<Product> = try {
        val request = StockMovementDto(weight = quantity)
        val response = api.subtractStock(id, request)
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
}
