package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.CategoryDto
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import javax.inject.Inject

class CategoryMapper @Inject constructor() {
    
    fun toDomain(dto: CategoryDto): Category = Category(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        state = CategoryState.valueOf(dto.state)
    )
    
    fun toDto(domain: Category): CategoryDto = CategoryDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        state = domain.state.name
    )
}
