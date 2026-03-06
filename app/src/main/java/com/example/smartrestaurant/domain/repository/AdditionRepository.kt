package com.example.smartrestaurant.domain.repository

import com.example.smartrestaurant.domain.model.Addition

/**
 * Repository interface for Addition operations.
 * Validates: Requirements 8.1, 8.3, 8.6, 8.7
 */
interface AdditionRepository {
    suspend fun getAdditions(page: Int): Result<List<Addition>>
    suspend fun getAdditionById(id: String): Result<Addition>
    suspend fun createAddition(addition: Addition): Result<Addition>
    suspend fun updateAddition(id: String, addition: Addition): Result<Addition>
    suspend fun deleteAddition(id: String): Result<Unit>
}
