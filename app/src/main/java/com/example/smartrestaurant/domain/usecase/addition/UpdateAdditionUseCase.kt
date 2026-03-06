package com.example.smartrestaurant.domain.usecase.addition

import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.repository.AdditionRepository
import javax.inject.Inject

/**
 * Use case for updating an existing addition.
 * Validates: Requirements 8.6
 */
class UpdateAdditionUseCase @Inject constructor(
    private val repository: AdditionRepository
) {
    suspend operator fun invoke(id: String, addition: Addition): Result<Addition> = 
        repository.updateAddition(id, addition)
}
