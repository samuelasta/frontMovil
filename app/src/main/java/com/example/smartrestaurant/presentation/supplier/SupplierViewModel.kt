package com.example.smartrestaurant.presentation.supplier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import com.example.smartrestaurant.domain.repository.ProductRepository
import com.example.smartrestaurant.domain.usecase.supplier.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Supplier screens
 * Validates: Requirements 4.1, 4.3, 4.4, 4.6, 4.7, 12.2, 15.2
 */
@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val getSuppliersUseCase: GetSuppliersUseCase,
    private val getSupplierByIdUseCase: GetSupplierByIdUseCase,
    private val createSupplierUseCase: CreateSupplierUseCase,
    private val updateSupplierUseCase: UpdateSupplierUseCase,
    private val deleteSupplierUseCase: DeleteSupplierUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(SupplierListUiState())
    val listState: StateFlow<SupplierListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(SupplierDetailUiState())
    val detailState: StateFlow<SupplierDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(SupplierFormUiState())
    val formState: StateFlow<SupplierFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadSuppliers()
    }

    // List Screen Methods

    /**
     * Load suppliers
     * Validates: Requirements 4.1
     */
    fun loadSuppliers() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadSuppliers() }

            getSuppliersUseCase().fold(
                onSuccess = { suppliers ->
                    _listState.update {
                        it.copy(
                            suppliers = suppliers,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load suppliers"
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

    // Detail Screen Methods

    /**
     * Load supplier by ID with associated products
     * Validates: Requirements 4.4
     */
    fun loadSupplierById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadSupplierById(id) }

            getSupplierByIdUseCase(id).fold(
                onSuccess = { supplier ->
                    _detailState.update {
                        it.copy(
                            supplier = supplier,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Load associated products
                    loadAssociatedProducts(id)
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load supplier"
                        )
                    }
                }
            )
        }
    }

    /**
     * Load products associated with a supplier
     * Validates: Requirements 4.4
     */
    private fun loadAssociatedProducts(supplierId: String) {
        viewModelScope.launch {
            // Load all products and filter by supplier
            productRepository.getProducts(0).fold(
                onSuccess = { products ->
                    val associatedProducts = products.filter { it.supplier.id == supplierId }
                    _detailState.update {
                        it.copy(associatedProducts = associatedProducts)
                    }
                },
                onFailure = { error ->
                    // Don't update error state for associated products failure
                    // The supplier details are still valid
                    _detailState.update {
                        it.copy(associatedProducts = emptyList())
                    }
                }
            )
        }
    }

    /**
     * Show delete confirmation dialog
     * Validates: Requirements 4.7
     */
    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete supplier
     * Validates: Requirements 4.7
     */
    fun deleteSupplier(onSuccess: () -> Unit) {
        val supplierId = _detailState.value.supplier?.id ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }

            deleteSupplierUseCase(supplierId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to delete supplier",
                            showDeleteConfirmation = false
                        )
                    }
                }
            )
        }
    }

    // Form Screen Methods

    /**
     * Load supplier for editing
     * Validates: Requirements 4.5
     */
    fun loadSupplierForEdit(id: String) {
        viewModelScope.launch {
            getSupplierByIdUseCase(id).fold(
                onSuccess = { supplier ->
                    _formState.update {
                        it.copy(
                            supplier = supplier,
                            name = supplier.name,
                            email = supplier.email,
                            phone = supplier.phone,
                            address = supplier.address
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(submitError = error.message ?: "Failed to load supplier")
                    }
                }
            )
        }
    }

    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email, emailError = null) }
    }

    fun onPhoneChange(phone: String) {
        _formState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onAddressChange(address: String) {
        _formState.update { it.copy(address = address, addressError = null) }
    }

    /**
     * Validate form fields including email format
     * Validates: Requirements 12.2
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

        if (state.email.isBlank()) {
            errors["email"] = "Email is required"
            isValid = false
        } else if (!isValidEmail(state.email)) {
            errors["email"] = "Invalid email format"
            isValid = false
        }

        if (state.phone.isBlank()) {
            errors["phone"] = "Phone is required"
            isValid = false
        }

        if (state.address.isBlank()) {
            errors["address"] = "Address is required"
            isValid = false
        }

        _formState.update {
            it.copy(
                nameError = errors["name"],
                emailError = errors["email"],
                phoneError = errors["phone"],
                addressError = errors["address"]
            )
        }

        return isValid
    }

    /**
     * Validate email format
     * Validates: Requirements 12.2
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Submit supplier (create or update)
     * Validates: Requirements 4.3, 4.6, 12.2
     */
    fun submitSupplier(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val state = _formState.value
        val supplier = Supplier(
            id = state.supplier?.id,
            name = state.name,
            email = state.email,
            phone = state.phone,
            address = state.address,
            state = state.supplier?.state ?: SupplierState.ACTIVE
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.supplier == null) {
                createSupplierUseCase(supplier)
            } else {
                updateSupplierUseCase(state.supplier.id!!, supplier)
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
                            submitError = error.message ?: "Failed to save supplier"
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
        _formState.value = SupplierFormUiState()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = SupplierDetailUiState()
    }
}
