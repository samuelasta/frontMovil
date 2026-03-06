package com.example.smartrestaurant.presentation.dish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartrestaurant.domain.model.*
import com.example.smartrestaurant.domain.repository.CategoryRepository
import com.example.smartrestaurant.domain.repository.ProductRepository
import com.example.smartrestaurant.domain.usecase.dish.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DishViewModel @Inject constructor(
    private val getDishesUseCase: GetDishesUseCase,
    private val getDishByIdUseCase: GetDishByIdUseCase,
    private val createDishUseCase: CreateDishUseCase,
    private val updateDishUseCase: UpdateDishUseCase,
    private val deleteDishUseCase: DeleteDishUseCase,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(DishListUiState())
    val listState: StateFlow<DishListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(DishDetailUiState())
    val detailState: StateFlow<DishDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(DishFormUiState())
    val formState: StateFlow<DishFormUiState> = _formState.asStateFlow()

    private var lastFailedOperation: (() -> Unit)? = null

    init {
        loadDishes()
    }

    fun loadDishes(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadDishes(page) }

            getDishesUseCase(page).fold(
                onSuccess = { dishes ->
                    _listState.update {
                        it.copy(
                            dishes = if (page == 0) dishes else it.dishes + dishes,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = dishes.size == 10
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
    }

    fun filterByCategory(category: Category?) {
        _listState.update { it.copy(selectedCategory = category) }
    }

    fun loadDishById(id: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            lastFailedOperation = { loadDishById(id) }

            getDishByIdUseCase(id).fold(
                onSuccess = { dish ->
                    _detailState.update { it.copy(dish = dish, isLoading = false) }
                },
                onFailure = { error ->
                    _detailState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun showDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _detailState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteDish(onSuccess: () -> Unit) {
        val dishId = _detailState.value.dish?.id ?: return
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true) }
            deleteDishUseCase(dishId).fold(
                onSuccess = {
                    _detailState.update { it.copy(isLoading = false, showDeleteConfirmation = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _detailState.update { it.copy(isLoading = false, error = error.message, showDeleteConfirmation = false) }
                }
            )
        }
    }

    fun loadDishForEdit(id: String) {
        viewModelScope.launch {
            getDishByIdUseCase(id).fold(
                onSuccess = { dish ->
                    _formState.update {
                        it.copy(
                            dish = dish,
                            name = dish.name,
                            description = dish.description,
                            price = dish.price.toString(),
                            preparationTime = dish.preparationTime.toString(),
                            selectedCategory = dish.category,
                            imageUri = dish.imageUrl,
                            recipe = dish.recipe.map { item ->
                                RecipeItemUi(item.product, item.quantity.toString())
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _formState.update { it.copy(submitError = error.message) }
                }
            )
        }
    }

    fun loadCategoriesAndProducts() {
        viewModelScope.launch {
            _formState.update { it.copy(isLoadingCategories = true, isLoadingProducts = true) }
            
            categoryRepository.getCategories().fold(
                onSuccess = { categories ->
                    _formState.update { it.copy(availableCategories = categories, isLoadingCategories = false) }
                },
                onFailure = { _formState.update { it.copy(isLoadingCategories = false) } }
            )
            
            productRepository.getProducts(0).fold(
                onSuccess = { products ->
                    _formState.update { it.copy(availableProducts = products, isLoadingProducts = false) }
                },
                onFailure = { _formState.update { it.copy(isLoadingProducts = false) } }
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

    fun onPreparationTimeChange(time: String) {
        _formState.update { it.copy(preparationTime = time, preparationTimeError = null) }
    }

    fun onCategoryChange(category: Category) {
        _formState.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun onImageSelected(uri: String?) {
        _formState.update { it.copy(imageUri = uri) }
    }

    fun addIngredient(product: Product) {
        val currentRecipe = _formState.value.recipe
        if (currentRecipe.any { it.product.id == product.id }) return
        
        _formState.update {
            it.copy(recipe = currentRecipe + RecipeItemUi(product, "1"), recipeError = null)
        }
    }

    fun removeIngredient(product: Product) {
        _formState.update {
            it.copy(recipe = it.recipe.filter { item -> item.product.id != product.id })
        }
    }

    fun updateIngredientQuantity(product: Product, quantity: String) {
        _formState.update {
            it.copy(
                recipe = it.recipe.map { item ->
                    if (item.product.id == product.id) item.copy(quantity = quantity, quantityError = null)
                    else item
                }
            )
        }
    }

    private fun validateForm(): Boolean {
        val state = _formState.value
        var isValid = true

        if (state.name.isBlank()) {
            _formState.update { it.copy(nameError = "Name is required") }
            isValid = false
        }

        if (state.price.toDoubleOrNull() == null || state.price.toDouble() <= 0) {
            _formState.update { it.copy(priceError = "Valid price is required") }
            isValid = false
        }

        if (state.preparationTime.toIntOrNull() == null || state.preparationTime.toInt() <= 0) {
            _formState.update { it.copy(preparationTimeError = "Valid preparation time is required") }
            isValid = false
        }

        if (state.selectedCategory == null) {
            _formState.update { it.copy(categoryError = "Category is required") }
            isValid = false
        }

        if (state.recipe.isEmpty()) {
            _formState.update { it.copy(recipeError = "At least one ingredient is required") }
            isValid = false
        }

        return isValid
    }

    fun submitDish(onSuccess: () -> Unit) {
        if (!validateForm()) return

        val state = _formState.value
        val recipeItems = state.recipe.mapNotNull { it.toDomain() }
        
        if (recipeItems.size != state.recipe.size) {
            _formState.update { it.copy(recipeError = "Invalid quantities in recipe") }
            return
        }

        val dish = Dish(
            id = state.dish?.id,
            name = state.name,
            description = state.description,
            price = state.price.toDouble(),
            preparationTime = state.preparationTime.toInt(),
            imageUrl = state.imageUri,
            state = state.dish?.state ?: DishState.ACTIVE,
            category = state.selectedCategory!!,
            recipe = recipeItems
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, submitError = null) }

            val result = if (state.dish == null) {
                createDishUseCase(dish)
            } else {
                updateDishUseCase(state.dish.id!!, dish)
            }

            result.fold(
                onSuccess = {
                    _formState.update { it.copy(isSubmitting = false, submitSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _formState.update { it.copy(isSubmitting = false, submitError = error.message) }
                }
            )
        }
    }

    fun retryLastOperation() {
        lastFailedOperation?.invoke()
    }

    fun resetFormState() {
        _formState.value = DishFormUiState()
    }

    fun resetDetailState() {
        _detailState.value = DishDetailUiState()
    }
}
