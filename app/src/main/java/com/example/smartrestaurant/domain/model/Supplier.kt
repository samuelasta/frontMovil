package com.example.smartrestaurant.domain.model

data class Supplier(
    val id: String?,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val state: SupplierState
)
