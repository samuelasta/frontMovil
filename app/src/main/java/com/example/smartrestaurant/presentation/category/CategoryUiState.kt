package com.example.smartrestaurant.presentation.category

import com.example.smartrestaurant.domain.model.Category

/**
 * UI State for Category List Screen
 * Validates: Requirements 5.1
 */
data class CategoryListUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
) {
    val filteredCategories: List<Category>
        get() = categories.filter { category ->
            category.name.contains(searchQuery, ignoreCase = true) ||
            category.description.contains(searchQuery, ignoreCase = true)
        }
}

/**
 * UI State for Category Detail Screen
 * Validates: Requirements 5.4, 5.5
 */
data class CategoryDetailUiState(
    val category: Category? = null,
    val associatedItemsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false
)

/**
 * UI State for Category Form Screen (Create/Edit)
 * Validates: Requirements 5.2, 5.3, 5.5, 5.6
 */
data class CategoryFormUiState(
    val category: Category? = null,
    val name: String = "",
    val description: String = "",
    val nameError: String? = null,
    val descriptionError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || descriptionError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                description.isNotBlank() &&
                !hasErrors
}
