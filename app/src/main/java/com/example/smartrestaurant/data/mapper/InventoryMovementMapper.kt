package com.example.smartrestaurant.data.mapper

import com.example.smartrestaurant.data.remote.dto.InventoryMovementDto
import com.example.smartrestaurant.domain.model.InventoryMovement
import com.example.smartrestaurant.domain.model.MovementType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Mapper for InventoryMovement entity
 * Handles conversion between DTO and domain model, including timestamp conversion
 * Validates: Requirements 9.1, 9.5
 */
class InventoryMovementMapper @Inject constructor(
    private val productMapper: ProductMapper
) {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    
    /**
     * Convert InventoryMovementDto to domain model
     * Handles timestamp string to LocalDateTime conversion
     */
    fun toDomain(dto: InventoryMovementDto): InventoryMovement = InventoryMovement(
        id = dto.id,
        product = productMapper.toDomain(dto.product),
        movementType = MovementType.valueOf(dto.movementType),
        quantity = dto.quantity,
        reason = dto.reason,
        timestamp = LocalDateTime.parse(dto.timestamp, dateTimeFormatter)
    )
}
