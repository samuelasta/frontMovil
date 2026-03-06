package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO for Addition entity
 * Validates: Requirements 8.1
 */
@Serializable
data class AdditionDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val state: String = "ACTIVE"
)
