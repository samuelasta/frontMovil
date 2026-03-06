package com.example.smartrestaurant.presentation.addition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import com.example.smartrestaurant.domain.usecase.addition.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Addition screens
 * Validates: Requirements 8.1, 8.3, 8.4, 8.6, 8.7, 11.2, 15.2
 */
@HiltViewModel
class AdditionViewModel @Inject constructor(
    private val getAdditionsUseCase: GetAdditionsUseCase,
    private val getAdditionByIdUseCase: GetAdditionByIdUseCase,
    private val createAdditionUseCase: CreateAdditionUseCase,
    private val updateAdditionUseCase: UpdateAdditionUseCase,
    private val deleteAdditionUseCase: DeleteAdditionUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow(AdditionListUiState())
    val listState: StateFlow<AdditionListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(AdditionDetailUiState())
    val detailState: StateFlow<AdditionDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(AdditionFormUiState())
    val formState: StateFlow<AdditionFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadAdditions()
    }

    // List Screen Methods

    /**
     * Load additions with pagination
     * Validates: Requirements 8.1, 11.2
     */
    fun loadAdditions(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadAdditions(page) }

            getAdditionsUseCase(page).fold(
                onSuccess = { additions ->
                    _listState.update {
                        it.copy(
                            additions = if (page == 0) additions else it.additions + additions,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = additions.size == 10,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load additions"
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
     * Clear search query
     */
    fun clearSearch() {
        _listState.update { it.copy(searchQuery = "") }
    }

    // Detail Screen Methods

    /**
     * Load addition by ID
     * Validates: Requirements 8.4
     */
    fun loadAdditionById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadAdditionById(id) }

            getAdditionByIdUseCase(id).fold(
                onSuccess = { addition ->
                    _detailState.update {
                        it.copy(
                            addition = addition,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load addition"
                        )
                    }
                }
            )
        }
    }

    /**
     * Show delete confirmation dialog
     * Validates: Requirements 8.7
     */
    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete addition
     * Validates: Requirements 8.7
     */
    fun deleteAddition(onSuccess: () -> Unit) {
        val additionId = _detailState.value.addition?.id ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }

            deleteAdditionUseCase(additionId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to delete addition",
                            showDeleteConfirmation = false
                        )
                    }
                }
            )
        }
    }

    // Form Screen Methods

    /**
     * Load addition for editing
     * Validates: Requirements 8.5
     */
    fun loadAdditionForEdit(id: String) {
        viewModelScope.launch {
            getAdditionByIdUseCase(id).fold(
                onSuccess = { addition ->
                    _formState.update {
                        it.copy(
                            addition = addition,
                            name = addition.name,
                            description = addition.description,
                            price = addition.price.toString(),
                            imageUri = addition.imageUrl
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(submitError = error.message ?: "Failed to load addition")
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

    fun onPriceChange(price: String) {
        _formState.update { it.copy(price = price, priceError = null) }
    }

    fun onImageSelected(uri: String?) {
        _formState.update { it.copy(imageUri = uri) }
    }

    /**
     * Validate form fields
     * Validates: Requirements 12.1, 12.3
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

        // Validate numeric fields
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            errors["price"] = "Price must be a positive number"
            isValid = false
        }

        _formState.update {
            it.copy(
                nameError = errors["name"],
                priceError = errors["price"]
            )
        }

        return isValid
    }

    /**
     * Submit addition (create or update)
     * Validates: Requirements 8.3, 8.6, 12.6
     */
    fun submitAddition(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val state = _formState.value
        val addition = Addition(
            id = state.addition?.id,
            name = state.name,
            description = state.description,
            price = state.price.toDouble(),
            imageUrl = state.imageUri,
            state = state.addition?.state ?: AdditionState.ACTIVE
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.addition == null) {
                createAdditionUseCase(addition)
            } else {
                updateAdditionUseCase(state.addition.id!!, addition)
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
                            submitError = error.message ?: "Failed to save addition"
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
        _formState.value = AdditionFormUiState()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = AdditionDetailUiState()
    }
}
