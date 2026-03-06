package com.example.smartrestaurant.presentation.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.Supplier

/**
 * UI State for Product List Screen
 * Validates: Requirements 1.1, 1.4, 1.5, 12.1, 12.5
 */
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedSupplier: Supplier? = null,
    val showLowStockOnly: Boolean = false,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true
) {
    val filteredProducts: List<Product>
        get() = products
            .filter { product ->
                // Filter by search query
                product.name.contains(searchQuery, ignoreCase = true)
            }
            .filter { product ->
                // Filter by supplier if selected
                selectedSupplier == null || product.supplier.id == selectedSupplier.id
            }
            .filter { product ->
                // Filter by low stock if enabled
                !showLowStockOnly || product.isLowStock
            }
    
    val lowStockCount: Int
        get() = products.count { it.isLowStock }
}

/**
 * UI State for Product Detail Screen
 * Validates: Requirements 1.4, 2.1
 */
data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val showAddStockDialog: Boolean = false,
    val showSubtractStockDialog: Boolean = false,
    val stockQuantity: String = "",
    val stockReason: String = "",
    val stockQuantityError: String? = null,
    val isSubmittingStock: Boolean = false,
    val stockOperationError: String? = null
)

/**
 * UI State for Product Form Screen (Create/Edit)
 * Validates: Requirements 1.2, 1.3, 1.5, 1.6, 10.1, 10.2, 12.1, 12.5, 12.6
 */
data class ProductFormUiState(
    val product: Product? = null,
    val name: String = "",
    val description: String = "",
    val weight: String = "",
    val unit: String = "kg",
    val price: String = "",
    val minimumStock: String = "",
    val selectedSupplier: Supplier? = null,
    val imageUri: String? = null,
    val availableSuppliers: List<Supplier> = emptyList(),
    val isLoadingSuppliers: Boolean = false,
    val nameError: String? = null,
    val weightError: String? = null,
    val unitError: String? = null,
    val priceError: String? = null,
    val minimumStockError: String? = null,
    val supplierError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
) {
    val hasErrors: Boolean
        get() = nameError != null || 
                weightError != null || 
                unitError != null ||
                priceError != null || 
                minimumStockError != null || 
                supplierError != null
    
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                weight.isNotBlank() &&
                unit.isNotBlank() &&
                price.isNotBlank() &&
                minimumStock.isNotBlank() &&
                selectedSupplier != null &&
                !hasErrors
}
