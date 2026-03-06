package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.InventoryMovementMapper
import com.example.smartrestaurant.data.remote.api.InventoryApi
import com.example.smartrestaurant.domain.model.InventoryMovement
import com.example.smartrestaurant.domain.repository.InventoryRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of InventoryRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 9.2, 13.3, 13.4
 */
class InventoryRepositoryImpl @Inject constructor(
    private val api: InventoryApi,
    private val mapper: InventoryMovementMapper
) : InventoryRepository {
    
    /**
     * Get all inventory movements.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @return Result containing list of InventoryMovement or error
     */
    override suspend fun getInventoryMovements(): Result<List<InventoryMovement>> = try {
        val response = api.getAllInventoryMovements()
        if (response.error) {
            Result.failure(Exception("Error fetching inventory movements"))
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
}
