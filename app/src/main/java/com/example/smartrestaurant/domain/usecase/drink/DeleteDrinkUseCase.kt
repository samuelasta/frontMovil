package com.example.smartrestaurant.domain.usecase.drink

import com.example.smartrestaurant.domain.repository.DrinkRepository
import javax.inject.Inject

class DeleteDrinkUseCase @Inject constructor(
    private val repository: DrinkRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteDrink(id)
}
