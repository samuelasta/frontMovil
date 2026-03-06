package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO for Drink entity
 * Validates: Requirements 7.1
 */
@Serializable
data class DrinkDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val stockUnits: Int,
    val imageUrl: String? = null,
    val state: String = "ACTIVE",
    val category: CategoryDto
)
