package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.AdditionDto
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import javax.inject.Inject

/**
 * Mapper for Addition entity
 * Validates: Requirements 8.1, 8.4
 */
class AdditionMapper @Inject constructor() {
    
    fun toDomain(dto: AdditionDto): Addition = Addition(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        price = dto.price,
        imageUrl = dto.imageUrl,
        state = AdditionState.valueOf(dto.state)
    )
    
    fun toDto(domain: Addition): AdditionDto = AdditionDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        price = domain.price,
        imageUrl = domain.imageUrl,
        state = domain.state.name
    )
}
