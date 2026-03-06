package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO for InventoryMovement entity
 * Validates: Requirements 9.1
 */
@Serializable
data class InventoryMovementDto(
    val id: String? = null,
    val product: ProductDto,
    val movementType: String,
    val quantity: Double,
    val reason: String? = null,
    val timestamp: String
)

/**
 * Enum for movement types
 * Validates: Requirements 9.1
 */
@Serializable
enum class MovementType {
    ENTRY,
    EXIT
}
