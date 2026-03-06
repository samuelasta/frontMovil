package com.example.smartrestaurant.domain.model

data class Category(
    val id: String?,
    val name: String,
    val description: String,
    val state: CategoryState
)
