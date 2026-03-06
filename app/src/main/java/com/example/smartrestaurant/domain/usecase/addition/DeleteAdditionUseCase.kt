package com.example.smartrestaurant.domain.usecase.addition

import com.example.smartrestaurant.domain.repository.AdditionRepository
import javax.inject.Inject

/**
 * Use case for deleting an addition.
 * Validates: Requirements 8.7
 */
class DeleteAdditionUseCase @Inject constructor(
    private val repository: AdditionRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteAddition(id)
}
