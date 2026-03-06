package com.example.smartrestaurant.presentation.drink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.model.DrinkState
import com.example.smartrestaurant.domain.repository.CategoryRepository
import com.example.smartrestaurant.domain.usecase.drink.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Drink screens
 * Validates: Requirements 7.1, 7.3, 7.4, 7.6, 7.7, 11.2, 15.2, 15.5
 */
@HiltViewModel
class DrinkViewModel @Inject constructor(
    private val getDrinksUseCase: GetDrinksUseCase,
    private val getDrinkByIdUseCase: GetDrinkByIdUseCase,
    private val createDrinkUseCase: CreateDrinkUseCase,
    private val updateDrinkUseCase: UpdateDrinkUseCase,
    private val deleteDrinkUseCase: DeleteDrinkUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(DrinkListUiState())
    val listState: StateFlow<DrinkListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(DrinkDetailUiState())
    val detailState: StateFlow<DrinkDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(DrinkFormUiState())
    val formState: StateFlow<DrinkFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadDrinks()
    }

    // List Screen Methods

    /**
     * Load drinks with pagination
     * Validates: Requirements 7.1, 11.2
     */
    fun loadDrinks(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadDrinks(page) }

            getDrinksUseCase(page).fold(
                onSuccess = { drinks ->
                    _listState.update {
                        it.copy(
                            drinks = if (page == 0) drinks else it.drinks + drinks,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = drinks.size == 10,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load drinks"
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
     * Filter by category
     * Validates: Requirements 15.5
     */
    fun filterByCategory(category: Category?) {
        _listState.update { it.copy(selectedCategory = category) }
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _listState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = null
            )
        }
    }

    // Detail Screen Methods

    /**
     * Load drink by ID
     * Validates: Requirements 7.4
     */
    fun loadDrinkById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadDrinkById(id) }

            getDrinkByIdUseCase(id).fold(
                onSuccess = { drink ->
                    _detailState.update {
                        it.copy(
                            drink = drink,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load drink"
                        )
                    }
                }
            )
        }
    }

    /**
     * Show delete confirmation dialog
     * Validates: Requirements 7.7
     */
    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete drink
     * Validates: Requirements 7.7
     */
    fun deleteDrink(onSuccess: () -> Unit) {
        val drinkId = _detailState.value.drink?.id ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }

            deleteDrinkUseCase(drinkId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to delete drink",
                            showDeleteConfirmation = false
                        )
                    }
                }
            )
        }
    }

    // Form Screen Methods

    /**
     * Load categories for form
     * Validates: Requirements 10.1
     */
    fun loadCategories() {
        viewModelScope.launch {
            _formState.update { it.copy(isLoadingCategories = true) }
            
            categoryRepository.getCategories().fold(
                onSuccess = { categories ->
                    _formState.update { it.copy(availableCategories = categories, isLoadingCategories = false) }
                },
                onFailure = { 
                    _formState.update { it.copy(isLoadingCategories = false) }
                }
            )
        }
    }

    /**
     * Load drink for editing
     * Validates: Requirements 7.5
     */
    fun loadDrinkForEdit(id: String) {
        viewModelScope.launch {
            getDrinkByIdUseCase(id).fold(
                onSuccess = { drink ->
                    _formState.update {
                        it.copy(
                            drink = drink,
                            name = drink.name,
                            description = drink.description,
                            price = drink.price.toString(),
                            stockUnits = drink.stockUnits.toString(),
                            selectedCategory = drink.category,
                            imageUri = drink.imageUrl
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update {
                        it.copy(submitError = error.message ?: "Failed to load drink")
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

    fun onStockUnitsChange(stockUnits: String) {
        _formState.update { it.copy(stockUnits = stockUnits, stockUnitsError = null) }
    }

    fun onCategorySelected(category: Category) {
        _formState.update { it.copy(selectedCategory = category, categoryError = null) }
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

        // Validate numeric fields
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            errors["price"] = "Price must be a positive number"
            isValid = false
        }

        val stockUnits = state.stockUnits.toIntOrNull()
        if (stockUnits == null || stockUnits < 0) {
            errors["stockUnits"] = "Stock units must be a non-negative number"
            isValid = false
        }

        // Validate category
        if (state.selectedCategory == null) {
            errors["category"] = "Category is required"
            isValid = false
        }

        _formState.update {
            it.copy(
                nameError = errors["name"],
                priceError = errors["price"],
                stockUnitsError = errors["stockUnits"],
                categoryError = errors["category"]
            )
        }

        return isValid
    }

    /**
     * Submit drink (create or update)
     * Validates: Requirements 7.3, 7.6, 12.6
     */
    fun submitDrink(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val state = _formState.value
        val drink = Drink(
            id = state.drink?.id,
            name = state.name,
            description = state.description,
            price = state.price.toDouble(),
            stockUnits = state.stockUnits.toInt(),
            imageUrl = state.imageUri,
            state = state.drink?.state ?: DrinkState.ACTIVE,
            category = state.selectedCategory!!
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.drink == null) {
                createDrinkUseCase(drink)
            } else {
                updateDrinkUseCase(state.drink.id!!, drink)
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
                            submitError = error.message ?: "Failed to save drink"
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
        _formState.value = DrinkFormUiState()
    }

    /**
     * Reset detail state
     */
    fun resetDetailState() {
        _detailState.value = DrinkDetailUiState()
    }
}
