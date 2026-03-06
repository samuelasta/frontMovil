package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.SupplierMapper
import com.example.smartrestaurant.data.remote.api.SupplierApi
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.repository.SupplierRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of SupplierRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 4.1, 4.3, 4.6, 4.7, 13.3, 13.4
 */
class SupplierRepositoryImpl @Inject constructor(
    private val api: SupplierApi,
    private val mapper: SupplierMapper
) : SupplierRepository {
    
    /**
     * Get list of suppliers.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @return Result containing list of Supplier or error
     */
    override suspend fun getSuppliers(): Result<List<Supplier>> = try {
        val response = api.getSuppliers()
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
     * Get a single supplier by ID.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Supplier ID
     * @return Result containing Supplier or error
     */
    override suspend fun getSupplierById(id: String): Result<Supplier> = try {
        val response = api.getSupplierById(id)
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
     * Create a new supplier.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param supplier Supplier to create
     * @return Result containing created Supplier or error
     */
    override suspend fun createSupplier(supplier: Supplier): Result<Supplier> = try {
        val supplierDto = mapper.toDto(supplier)
        val response = api.createSupplier(supplierDto)
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
     * Update an existing supplier.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Supplier ID
     * @param supplier Supplier with updated data
     * @return Result containing updated Supplier or error
     */
    override suspend fun updateSupplier(id: String, supplier: Supplier): Result<Supplier> = try {
        val supplierDto = mapper.toDto(supplier)
        val response = api.updateSupplier(id, supplierDto)
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
     * Delete a supplier (soft delete - marks as INACTIVE).
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Supplier ID
     * @return Result with Unit on success or error
     */
    override suspend fun deleteSupplier(id: String): Result<Unit> = try {
        val response = api.deleteSupplier(id)
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
}
