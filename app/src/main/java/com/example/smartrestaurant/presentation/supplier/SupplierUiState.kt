package com.example.smartrestaurant.presentation.supplier

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.Supplier

/**
 * UI State for Supplier List Screen
 * Validates: Requirements 4.1
 */
data class SupplierListUiState(
    val suppliers: List<Supplier> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
) {
    val filteredSuppliers: List<Supplier>
        get() = suppliers.filter { supplier ->
            supplier.name.contains(searchQuery, ignoreCase = true)
        }
}

/**
 * UI State for Supplier Detail Screen
 * Validates: Requirements 4.4, 4.5
 */
data class SupplierDetailUiState(
    val supplier: Supplier? = null,
    val associatedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false
)

/**
 * UI State for Supplier Form Screen (Create/Edit)
 * Validates: Requirements 4.2, 4.3, 4.5, 4.6, 12.2
 */
data class SupplierFormUiState(
    val supplier: Supplier? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || 
                emailError != null || 
                phoneError != null || 
                addressError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                address.isNotBlank() &&
                !hasErrors
}
