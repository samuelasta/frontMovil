package com.example.smartrestaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val weight: Double,
    val unit: String,
    val price: Double,
    val stock: Double,
    val minimumStock: Double,
    val imageUrl: String? = null,
    val state: String = "ACTIVE",
    val suplier: SupplierDto
)

@Serializable
data class StockMovementDto(
    val weight: Double
)
