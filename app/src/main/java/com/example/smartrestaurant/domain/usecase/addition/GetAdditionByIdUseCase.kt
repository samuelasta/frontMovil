package com.example.smartrestaurant.domain.usecase.addition

import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.repository.AdditionRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single addition by ID.
 * Validates: Requirements 8.1
 */
class GetAdditionByIdUseCase @Inject constructor(
    private val repository: AdditionRepository
) {
    suspend operator fun invoke(id: String): Result<Addition> = repository.getAdditionById(id)
}
