package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Supplier

/**
 * Repository interface for Supplier operations.
 * Defines the contract for supplier data access following Clean Architecture principles.
 * All methods return Result<T> for proper error handling.
 */
interface SupplierRepository {
    
    /**
     * Get list of suppliers.
     * @return Result containing list of Supplier or error
     */
    suspend fun getSuppliers(): Result<List<Supplier>>
    
    /**
     * Get a single supplier by ID.
     * @param id Supplier ID
     * @return Result containing Supplier or error
     */
    suspend fun getSupplierById(id: String): Result<Supplier>
    
    /**
     * Create a new supplier.
     * @param supplier Supplier to create
     * @return Result containing created Supplier or error
     */
    suspend fun createSupplier(supplier: Supplier): Result<Supplier>
    
    /**
     * Update an existing supplier.
     * @param id Supplier ID
     * @param supplier Supplier with updated data
     * @return Result containing updated Supplier or error
     */
    suspend fun updateSupplier(id: String, supplier: Supplier): Result<Supplier>
    
    /**
     * Delete a supplier (soft delete).
     * @param id Supplier ID
     * @return Result with Unit on success or error
     */
    suspend fun deleteSupplier(id: String): Result<Unit>
}
