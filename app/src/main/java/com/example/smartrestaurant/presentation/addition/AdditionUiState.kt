package com.example.smartrestaurant.presentation.addition

import com.example.smartrestaurant.domain.model.Addition

/**
 * UI State for Addition list screen
 * Validates: Requirements 8.1
 */
data class AdditionListUiState(
    val additions: List<Addition> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
) {
    val filteredAdditions: List<Addition>
        get() = additions.filter { it.name.contains(searchQuery, ignoreCase = true) }
}

/**
 * UI State for Addition detail screen
 * Validates: Requirements 8.4
 */
data class AdditionDetailUiState(
    val addition: Addition? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false
)

/**
 * UI State for Addition form screen (create/edit)
 * Validates: Requirements 8.5
 */
data class AdditionFormUiState(
    val addition: Addition? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUri: String? = null,
    val nameError: String? = null,
    val priceError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || priceError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() && price.isNotBlank() && !hasErrors
}
