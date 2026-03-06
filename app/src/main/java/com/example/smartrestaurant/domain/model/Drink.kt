package com.example.smartrestaurant.domain.model

/**
 * Domain model for Drink entity
 * Validates: Requirements 7.4
 */
data class Drink(
    val id: String?,
    val name: String,
    val description: String,
    val price: Double,
    val stockUnits: Int,
    val imageUrl: String?,
    val state: DrinkState,
    val category: Category
)

/**
 * State enum for Drink
 */
enum class DrinkState {
    ACTIVE,
    INACTIVE
}
