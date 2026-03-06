package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO for Dish entity
 * Validates: Requirements 6.1, 6.3
 */
@Serializable
data class DishDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTime: Int,
    val imageUrl: String? = null,
    val state: String = "ACTIVE",
    val category: CategoryDto,
    val recipe: List<RecipeItemDto>
)

/**
 * DTO for Recipe Item (ingredient in a dish)
 * Validates: Requirements 6.3
 */
@Serializable
data class RecipeItemDto(
    val product: ProductDto,
    val quantity: Double
)
