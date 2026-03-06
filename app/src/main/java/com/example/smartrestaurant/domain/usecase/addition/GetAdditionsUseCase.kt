package com.example.smartrestaurant.domain.usecase.addition

import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.repository.AdditionRepository
import javax.inject.Inject

/**
 * Use case for retrieving paginated list of additions.
 * Validates: Requirements 8.1
 */
class GetAdditionsUseCase @Inject constructor(
    private val repository: AdditionRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Addition>> = repository.getAdditions(page)
}
