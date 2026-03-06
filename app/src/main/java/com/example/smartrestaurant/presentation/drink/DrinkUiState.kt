package com.example.smartrestaurant.presentation.drink

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.Drink

data class DrinkListUiState(
    val drinks: List<Drink> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
) {
    val filteredDrinks: List<Drink>
        get() = drinks
            .filter { it.name.contains(searchQuery, ignoreCase = true) }
            .filter { selectedCategory == null || it.category.id == selectedCategory.id }
}

data class DrinkDetailUiState(
    val drink: Drink? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false
)

data class DrinkFormUiState(
    val drink: Drink? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stockUnits: String = "",
    val selectedCategory: Category? = null,
    val imageUri: String? = null,
    val availableCategories: List<Category> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val nameError: String? = null,
    val priceError: String? = null,
    val stockUnitsError: String? = null,
    val categoryError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || priceError != null || stockUnitsError != null || categoryError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() && price.isNotBlank() && stockUnits.isNotBlank() && 
                selectedCategory != null && !hasErrors
}
