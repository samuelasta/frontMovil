package com.example.smartrestaurant.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import com.example.smartrestaurant.domain.usecase.category.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Category screens
 * Validates: Requirements 5.1, 5.3, 5.4, 5.6, 5.7, 15.2
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow(CategoryListUiState())
    val listState: StateFlow<CategoryListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(CategoryDetailUiState())
    val detailState: StateFlow<CategoryDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(CategoryFormUiState())
    val formState: StateFlow<CategoryFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadCategories()
    }

    // List Screen Methods

    /**
     * Load categories
     * Validates: Requirements 5.1
     */
    fun loadCategories() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadCategories() }

            getCategoriesUseCase().fold(
                onSuccess = { categories ->
                    _listState.update {
                        it.copy(
                            categories = categories,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load categories"
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
     * Load category by ID with associated items
     * Validates: Requirements 5.4
     */
    fun loadCategoryById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadCategoryById(id) }

            getCategoryByIdUseCase(id).fold(
                onSuccess = { category ->
                    _detailState.update {
                        it.copy(
                            category = category,
                            isLoading = false,
                            error = null
                        )
                    }
                    // TODO: Load associated dishes/drinks count when those modules are implemented
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load category"
                        )
                    }
                }
            )
        }
    }

    /**
     * Show delete confirmation dialog
     * Validates: Requirements 5.7
     */
    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete category
     * Validates: Requirements 5.7
     */
    fun deleteCategory(onSuccess: () -> Unit) {
        val categoryId = _detailState.value.category?.id ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }

            deleteCategoryUseCase(categoryId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to delete category",
                            showDeleteConfirmation = false
                        )
                    }
                }
            )
        }
    }

    // Form Screen Methods

    /**
     * Load category for editing
     * Validates: Requirements 5.5
     */
    fun loadCategoryForEdit(id: String) {
        viewModelScope.launch {
            getCategoryByIdUseCase(id).fold(
                onSuccess = { category ->
                    _formState.update {
                        it.copy(
                            category = category,
                            name = category.name,
                            description = category.description
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(submitError = error.message ?: "Failed to load category")
                    }
                }
            )
        }
    }

    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun onDescriptionChange(description: String) {
        _formState.update { it.copy(description = description, descriptionError = null) }
    }

    /**
     * Validate form fields
     * Validates: Requirements 12.1
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

        if (state.description.isBlank()) {
            errors["description"] = "Description is required"
            isValid = false
        }

        _formState.update {
            it.copy(
                nameError = errors["name"],
                descriptionError = errors["description"]
            )
        }

        return isValid
    }

    /**
     * Submit category (create or update)
     * Validates: Requirements 5.3, 5.6
     */
    fun submitCategory(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val state = _formState.value
        val category = Category(
            id = state.category?.id,
            name = state.name,
            description = state.description,
            state = state.category?.state ?: CategoryState.ACTIVE
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.category == null) {
                createCategoryUseCase(category)
            } else {
                updateCategoryUseCase(state.category.id!!, category)
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
                            submitError = error.message ?: "Failed to save category"
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
        _formState.value = CategoryFormUiState()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = CategoryDetailUiState()
    }
}
