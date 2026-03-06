package com.example.smartrestaurant.data.repository

import com.example.smartrestaurant.data.mapper.AdditionMapper
import com.example.smartrestaurant.data.remote.api.AdditionApi
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.repository.AdditionRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of AdditionRepository interface.
 * Handles API calls through Retrofit and converts between DTOs and domain models.
 * Implements comprehensive error handling for network, API, and system errors.
 * 
 * Validates: Requirements 8.1, 8.3, 8.6, 8.7
 */
class AdditionRepositoryImpl @Inject constructor(
    private val api: AdditionApi,
    private val mapper: AdditionMapper
) : AdditionRepository {
    
    /**
     * Get paginated list of additions.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param page Page number for pagination
     * @return Result containing list of Addition or error
     */
    override suspend fun getAdditions(page: Int): Result<List<Addition>> = try {
        val response = api.getAdditions(page)
        if (response.error) {
            Result.failure(Exception("Error fetching additions"))
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
     * Get a single addition by ID.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Addition ID
     * @return Result containing Addition or error
     */
    override suspend fun getAdditionById(id: String): Result<Addition> = try {
        val response = api.getAdditionById(id)
        if (response.error) {
            Result.failure(Exception("Addition not found"))
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
     * Create a new addition.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param addition Addition to create
     * @return Result containing created Addition or error
     */
    override suspend fun createAddition(addition: Addition): Result<Addition> = try {
        val additionDto = mapper.toDto(addition)
        val response = api.createAddition(additionDto)
        if (response.error) {
            Result.failure(Exception("Addition creation failed"))
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
     * Update an existing addition.
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Addition ID
     * @param addition Addition with updated data
     * @return Result containing updated Addition or error
     */
    override suspend fun updateAddition(id: String, addition: Addition): Result<Addition> = try {
        val additionDto = mapper.toDto(addition)
        val response = api.updateAddition(id, additionDto)
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
     * Delete an addition (soft delete - marks as INACTIVE).
     * Handles both API errors (error: true in response) and network exceptions.
     * 
     * @param id Addition ID
     * @return Result with Unit on success or error
     */
    override suspend fun deleteAddition(id: String): Result<Unit> = try {
        val response = api.deleteAddition(id)
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
