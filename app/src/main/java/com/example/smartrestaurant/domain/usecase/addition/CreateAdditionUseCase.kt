package com.example.smartrestaurant.domain.usecase.addition

import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.repository.AdditionRepository
import javax.inject.Inject

/**
 * Use case for creating a new addition.
 * Validates: Requirements 8.3
 */
class CreateAdditionUseCase @Inject constructor(
    private val repository: AdditionRepository
) {
    suspend operator fun invoke(addition: Addition): Result<Addition> = repository.createAddition(addition)
}
