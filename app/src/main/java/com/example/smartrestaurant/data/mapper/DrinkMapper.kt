package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.DrinkDto
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.model.DrinkState
import javax.inject.Inject

/**
 * Mapper for Drink entity
 * Validates: Requirements 7.1, 7.4
 */
class DrinkMapper @Inject constructor(
    private val categoryMapper: CategoryMapper
) {
    
    fun toDomain(dto: DrinkDto): Drink = Drink(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        price = dto.price,
        stockUnits = dto.stockUnits,
        imageUrl = dto.imageUrl,
        state = DrinkState.valueOf(dto.state),
        category = categoryMapper.toDomain(dto.category)
    )
    
    fun toDto(domain: Drink): DrinkDto = DrinkDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        price = domain.price,
        stockUnits = domain.stockUnits,
        imageUrl = domain.imageUrl,
        state = domain.state.name,
        category = categoryMapper.toDto(domain.category)
    )
}
