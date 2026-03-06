package com.example.smartrestaurant.domain.usecase.drink

import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.repository.DrinkRepository
import javax.inject.Inject

class GetDrinksUseCase @Inject constructor(
    private val repository: DrinkRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Drink>> = repository.getDrinks(page)
}
