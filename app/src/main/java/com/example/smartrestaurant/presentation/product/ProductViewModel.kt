package com.example.smartrestaurant.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.usecase.product.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Product screens
 * Validates: Requirements 1.1, 1.3, 1.6, 1.7, 2.3, 2.5, 3.2, 12.1, 12.3, 12.4, 12.6, 13.5, 15.2
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val addStockUseCase: AddStockUseCase,
    private val subtractStockUseCase: SubtractStockUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListUiState())
    val listState: StateFlow<ProductListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ProductDetailUiState())
    val detailState: StateFlow<ProductDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(ProductFormUiState())
    val formState: StateFlow<ProductFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadProducts()
    }

    // List Screen Methods

    /**
     * Load products with pagination
     * Validates: Requirements 1.1, 11.1
     */
    fun loadProducts(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadProducts(page) }

            getProductsUseCase(page).fold(
                onSuccess = { products ->
                    _listState.update {
                        it.copy(
                            products = if (page == 0) products else it.products + products,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = products.size == 10,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load products"
                        )
                    }
                }
            )
        }
    }

    /**
     * Handle search query changes with local filtering
     * Validates: Requirements 15.2
     */
    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
    }

    /**
     * Toggle low stock filter
     * Validates: Requirements 3.2
     */
    fun toggleLowStockFilter() {
        _listState.update { it.copy(showLowStockOnly = !it.showLowStockOnly) }
    }

    /**
     * Filter by supplier
     * Validates: Requirements 15.4
     */
    fun filterBySupplier(supplier: Supplier?) {
        _listState.update { it.copy(selectedSupplier = supplier) }
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _listState.update {
            it.copy(
                searchQuery = "",
                selectedSupplier = null,
                showLowStockOnly = false
            )
        }
    }

    // Detail Screen Methods

    /**
     * Load product by ID
     * Validates: Requirements 1.4
     */
    fun loadProductById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadProductById(id) }

            getProductByIdUseCase(id).fold(
                onSuccess = { product ->
                    _detailState.update {
                        it.copy(
                            product = product,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load product"
                        )
                    }
                }
            )
        }
    }

    /**
     * Show delete confirmation dialog
     * Validates: Requirements 1.7
     */
    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete product
     * Validates: Requirements 1.7
     */
    fun deleteProduct(onSuccess: () -> Unit) {
        val productId = _detailState.value.product?.id ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }

            deleteProductUseCase(productId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to delete product",
                            showDeleteConfirmation = false
                        )
                    }
                }
            )
        }
    }

    // Stock Management Methods

    fun showAddStockDialog() {
        _detailState.update {
            it.copy(
                showAddStockDialog = true,
                stockQuantity = "",
                stockReason = "",
                stockQuantityError = null,
                stockOperationError = null
            )
        }
    }

    fun hideAddStockDialog() {
        _detailState.update { it.copy(showAddStockDialog = false) }
    }

    fun showSubtractStockDialog() {
        _detailState.update {
            it.copy(
                showSubtractStockDialog = true,
                stockQuantity = "",
                stockReason = "",
                stockQuantityError = null,
                stockOperationError = null
            )
        }
    }

    fun hideSubtractStockDialog() {
        _detailState.update { it.copy(showSubtractStockDialog = false) }
    }

    fun onStockQuantityChange(quantity: String) {
        _detailState.update { it.copy(stockQuantity = quantity, stockQuantityError = null) }
    }

    fun onStockReasonChange(reason: String) {
        _detailState.update { it.copy(stockReason = reason) }
    }

    /**
     * Add stock to product
     * Validates: Requirements 2.3, 12.3
     */
    fun addStock() {
        val productId = _detailState.value.product?.id ?: return
        val quantityStr = _detailState.value.stockQuantity
        val reason = _detailState.value.stockReason.ifBlank { null }

        // Validate quantity
        val quantity = quantityStr.toDoubleOrNull()
        if (quantity == null || quantity <= 0) {
            _detailState.update {
                it.copy(stockQuantityError = "Quantity must be a positive number")
            }
            return
        }

        viewModelScope.launch {
            _detailState.update { it.copy(isSubmittingStock = true, stockOperationError = null) }

            addStockUseCase(productId, quantity, reason).fold(
                onSuccess = { updatedProduct ->
                    _detailState.update {
                        it.copy(
                            product = updatedProduct,
                            isSubmittingStock = false,
                            showAddStockDialog = false,
                            stockQuantity = "",
                            stockReason = ""
                        )
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isSubmittingStock = false,
                            stockOperationError = error.message ?: "Failed to add stock"
                        )
                    }
                }
            )
        }
    }

    /**
     * Subtract stock from product
     * Validates: Requirements 2.5, 12.3
     */
    fun subtractStock() {
        val productId = _detailState.value.product?.id ?: return
        val quantityStr = _detailState.value.stockQuantity
        val reason = _detailState.value.stockReason.ifBlank { null }

        // Validate quantity
        val quantity = quantityStr.toDoubleOrNull()
        if (quantity == null || quantity <= 0) {
            _detailState.update {
                it.copy(stockQuantityError = "Quantity must be a positive number")
            }
            return
        }

        viewModelScope.launch {
            _detailState.update { it.copy(isSubmittingStock = true, stockOperationError = null) }

            subtractStockUseCase(productId, quantity, reason).fold(
                onSuccess = { updatedProduct ->
                    _detailState.update {
                        it.copy(
                            product = updatedProduct,
                            isSubmittingStock = false,
                            showSubtractStockDialog = false,
                            stockQuantity = "",
                            stockReason = ""
                        )
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isSubmittingStock = false,
                            stockOperationError = error.message ?: "Failed to subtract stock"
                        )
                    }
                }
            )
        }
    }

    // Form Screen Methods

    /**
     * Load product for editing
     * Validates: Requirements 1.5
     */
    fun loadProductForEdit(id: String) {
        viewModelScope.launch {
            getProductByIdUseCase(id).fold(
                onSuccess = { product ->
                    _formState.update {
                        it.copy(
                            product = product,
                            name = product.name,
                            description = product.description,
                            weight = product.weight.toString(),
                            unit = product.unit,
                            price = product.price.toString(),
                            minimumStock = product.minimumStock.toString(),
                            selectedSupplier = product.supplier,
                            imageUri = product.imageUrl
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(submitError = error.message ?: "Failed to load product")
                    }
                }
            )
        }
    }

    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun onDescriptionChange(description: String) {
        _formState.update { it.copy(description = description) }
    }

    fun onWeightChange(weight: String) {
        _formState.update { it.copy(weight = weight, weightError = null) }
    }

    fun onUnitChange(unit: String) {
        _formState.update { it.copy(unit = unit, unitError = null) }
    }

    fun onPriceChange(price: String) {
        _formState.update { it.copy(price = price, priceError = null) }
    }

    fun onMinimumStockChange(minimumStock: String) {
        _formState.update { it.copy(minimumStock = minimumStock, minimumStockError = null) }
    }

    fun onSupplierSelected(supplier: Supplier) {
        _formState.update { it.copy(selectedSupplier = supplier, supplierError = null) }
    }

    fun onImageSelected(uri: String?) {
        _formState.update { it.copy(imageUri = uri) }
    }

    /**
     * Validate form fields
     * Validates: Requirements 12.1, 12.3, 12.4
     */
    private fun validateForm(): Boolean {
        val state = _formState.value
        var isValid = true
        val errors = mutableMapOf<String, String>()

        // Validate required fields
        if (state.name.isBlank()) {
            errors["name"] = "Name is required"
            isValid = false
        }

        if (state.unit.isBlank()) {
            errors["unit"] = "Unit is required"
            isValid = false
        }

        // Validate numeric fields
        val weight = state.weight.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            errors["weight"] = "Weight must be a positive number"
            isValid = false
        }

        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            errors["price"] = "Price must be a positive number"
            isValid = false
        }

        val minimumStock = state.minimumStock.toDoubleOrNull()
        if (minimumStock == null || minimumStock < 0) {
            errors["minimumStock"] = "Minimum stock must be a non-negative number"
            isValid = false
        }

        // Validate supplier
        if (state.selectedSupplier == null) {
            errors["supplier"] = "Supplier is required"
            isValid = false
        }

        _formState.update {
            it.copy(
                nameError = errors["name"],
                weightError = errors["weight"],
                unitError = errors["unit"],
                priceError = errors["price"],
                minimumStockError = errors["minimumStock"],
                supplierError = errors["supplier"]
            )
        }

        return isValid
    }

    /**
     * Submit product (create or update)
     * Validates: Requirements 1.3, 1.6, 12.6
     */
    fun submitProduct(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val state = _formState.value
        val product = Product(
            id = state.product?.id,
            name = state.name,
            description = state.description,
            weight = state.weight.toDouble(),
            unit = state.unit,
            price = state.price.toDouble(),
            stock = state.product?.stock ?: 0.0,
            minimumStock = state.minimumStock.toDouble(),
            imageUrl = state.imageUri,
            state = state.product?.state ?: com.example.smartrestaurant.domain.model.ProductState.ACTIVE,
            supplier = state.selectedSupplier!!
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.product == null) {
                createProductUseCase(product)
            } else {
                updateProductUseCase(state.product.id!!, product)
            }

            result.fold(
                onSuccess = {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            submitSuccess = true
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.message ?: "Failed to save product"
                        )
                    }
                }
            )
        }
    }

    /**
     * Retry last failed operation
     * Validates: Requirements 13.5
     */
    fun retryLastOperation() {
        lastFailedOperation?.invoke()
    }

    /**
     * Reset form state
     */
    fun resetFormState() {
        _formState.value = ProductFormUiState()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = ProductDetailUiState()
    }
}
