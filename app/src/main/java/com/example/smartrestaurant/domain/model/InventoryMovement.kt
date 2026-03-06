package com.example.smartrestaurant.domain.model

import java.time.LocalDateTime

/**
 * Domain model for InventoryMovement entity
 * Validates: Requirements 9.5
 */
data class InventoryMovement(
    val id: String?,
    val product: Product,
    val movementType: MovementType,
    val quantity: Double,
    val reason: String?,
    val timestamp: LocalDateTime
)

/**
 * Enum for movement types
 * Validates: Requirements 9.1, 9.4
 */
enum class MovementType {
    ENTRY,
    EXIT
}
