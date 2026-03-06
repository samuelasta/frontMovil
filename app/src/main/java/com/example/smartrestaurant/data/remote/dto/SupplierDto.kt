package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SupplierDto(
    val id: String? = null,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val state: String = "ACTIVE"
)
