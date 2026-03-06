package com.example.smartrestaurant.domain.model

/**
 * Domain model for Addition entity
 * Validates: Requirements 8.4
 */
data class Addition(
    val id: String?,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val state: AdditionState
)

/**
 * State enum for Addition
 */
enum class AdditionState {
    ACTIVE,
    INACTIVE
}
