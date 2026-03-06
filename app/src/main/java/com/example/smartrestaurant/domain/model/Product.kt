package com.example.smartrestaurant.domain.model

data class Product(
    val id: String?,
    val name: String,
    val description: String,
    val weight: Double,
    val unit: String,
    val price: Double,
    val stock: Double,
    val minimumStock: Double,
    val imageUrl: String?,
    val state: ProductState,
    val supplier: Supplier
) {
    val isLowStock: Boolean
        get() = stock < minimumStock
}
