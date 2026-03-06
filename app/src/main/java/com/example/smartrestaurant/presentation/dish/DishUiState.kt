package com.example.smartrestaurant.presentation.dish

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.Dish
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.RecipeItem

/**
 * UI State for Dish List Screen
 * Validates: Requirements 6.1, 6.3, 6.5, 6.6, 15.5
 */
data class DishListUiState(
    val dishes: List<Dish> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
) {
    val filteredDishes: List<Dish>
        get() = dishes
            .filter { dish ->
                // Filter by search query
                dish.name.contains(searchQuery, ignoreCase = true)
            }
            .filter { dish ->
                // Filter by category if selected
                selectedCategory == null || dish.category.id == selectedCategory.id
            }
}

/**
 * UI State for Dish Detail Screen
 * Validates: Requirements 6.5
 */
data class DishDetailUiState(
    val dish: Dish? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false
)

/**
 * UI State for Dish Form Screen (Create/Edit)
 * Validates: Requirements 6.2, 6.3, 6.4, 6.6, 6.7, 6.9, 10.1, 10.2
 */
data class DishFormUiState(
    val dish: Dish? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val preparationTime: String = "",
    val selectedCategory: Category? = null,
    val imageUri: String? = null,
    val recipe: List<RecipeItemUi> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val availableProducts: List<Product> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val nameError: String? = null,
    val priceError: String? = null,
    val preparationTimeError: String? = null,
    val categoryError: String? = null,
    val recipeError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || 
                priceError != null || 
                preparationTimeError != null ||
                categoryError != null ||
                recipeError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                price.isNotBlank() &&
                preparationTime.isNotBlank() &&
                selectedCategory != null &&
                recipe.isNotEmpty() &&
                !hasErrors
}

/**
 * UI representation of a recipe item for form editing
 */
data class RecipeItemUi(
    val product: Product,
    val quantity: String,
    val quantityError: String? = null
) {
    fun toDomain(): RecipeItem? {
        val qty = quantity.toDoubleOrNull() ?: return null
        if (qty <= 0) return null
        return RecipeItem(product = product, quantity = qty)
    }
}
