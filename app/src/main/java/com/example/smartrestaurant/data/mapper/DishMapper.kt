package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.DishDto
import com.example.smartrestaurant.data.remote.dto.RecipeItemDto
import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.model.DishState
import com.example.smartrestaurant.domain.model.RecipeItem
import javax.inject.Inject

/**
 * Mapper for Dish entity
 * Validates: Requirements 6.1, 6.5
 */
class DishMapper @Inject constructor(
    private val categoryMapper: CategoryMapper,
    private val productMapper: ProductMapper
) {
    
    fun toDomain(dto: DishDto): Dish = Dish(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        price = dto.price,
        preparationTime = dto.preparationTime,
        imageUrl = dto.imageUrl,
        state = DishState.valueOf(dto.state),
        category = categoryMapper.toDomain(dto.category),
        recipe = dto.recipe.map { recipeItemToDomain(it) }
    )
    
    fun toDto(domain: Dish): DishDto = DishDto(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        price = domain.price,
        preparationTime = domain.preparationTime,
        imageUrl = domain.imageUrl,
        state = domain.state.name,
        category = categoryMapper.toDto(domain.category),
        recipe = domain.recipe.map { recipeItemToDto(it) }
    )
    
    private fun recipeItemToDomain(dto: RecipeItemDto): RecipeItem = RecipeItem(
        product = productMapper.toDomain(dto.product),
        quantity = dto.quantity
    )
    
    private fun recipeItemToDto(domain: RecipeItem): RecipeItemDto = RecipeItemDto(
        product = productMapper.toDto(domain.product),
        quantity = domain.quantity
    )
}
