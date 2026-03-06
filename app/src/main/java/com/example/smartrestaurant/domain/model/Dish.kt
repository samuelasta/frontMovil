package com.example.smartrestaurant.domain.model

/**
 * Domain model for Dish entity
 * Validates: Requirements 6.5
 */
data class Dish(
    val id: String?,
    val name: String,
    val description: String,
    val price: Double,
    val preparationTime: Int,
    val imageUrl: String?,
    val state: DishState,
    val category: Category,
    val recipe: List<RecipeItem>
)

/**
 * Domain model for Recipe Item (ingredient in a dish)
 * Validates: Requirements 6.5
 */
data class RecipeItem(
    val product: Product,
    val quantity: Double
)

/**
 * State enum for Dish
 */
enum class DishState {
    ACTIVE,
    INACTIVE
}
