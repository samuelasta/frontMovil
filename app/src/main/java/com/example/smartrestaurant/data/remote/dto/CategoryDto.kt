package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val state: String = "ACTIVE"
)
