package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: T,
    val error: Boolean
)
