package com.example.smartrestaurant.domain.usecase.drink

import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.repository.DrinkRepository
import javax.inject.Inject

class GetDrinkByIdUseCase @Inject constructor(
    private val repository: DrinkRepository
) {
    suspend operator fun invoke(id: String): Result<Drink> = repository.getDrinkById(id)
}
