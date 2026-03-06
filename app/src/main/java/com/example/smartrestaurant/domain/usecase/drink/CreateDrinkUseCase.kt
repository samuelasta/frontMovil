package com.example.smartrestaurant.domain.usecase.drink

import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.repository.DrinkRepository
import javax.inject.Inject

class CreateDrinkUseCase @Inject constructor(
    private val repository: DrinkRepository
) {
    suspend operator fun invoke(drink: Drink): Result<Drink> = repository.createDrink(drink)
}
