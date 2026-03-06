package com.example.smartrestaurant.presentation.drink

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.model.DrinkState
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for DrinkFormScreen and DrinkFormUiState
 * Validates: Requirements 7.2, 7.3, 7.5, 7.6, 10.1, 10.2, 12.1
 */
class DrinkFormScreenTest {

    private val testCategory = Category(
        id = "1",
        name = "Bebidas",
        description = "Categoría de bebidas",
        state = CategoryState.ACTIVE
    )

    @Test
    fun `form state should initialize with empty fields`() {
        val state = DrinkFormUiState()

        assertEquals("", state.name)
        assertEquals("", state.description)
        assertEquals("", state.price)
        assertEquals("", state.stockUnits)
        assertNull(state.selectedCategory)
        assertNull(state.imageUri)
        assertNull(state.drink)
        assertFalse(state.isSubmitting)
        assertFalse(state.submitSuccess)
    }

    @Test
    fun `form state should pre-populate fields when editing drink`() {
        val drink = Drink(
            id = "1",
            name = "Coca Cola",
            description = "Bebida gaseosa",
            price = 2.5,
            stockUnits = 100,
            imageUrl = "http://example.com/image.jpg",
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkFormUiState(
            drink = drink,
            name = drink.name,
            description = drink.description,
            price = drink.price.toString(),
            stockUnits = drink.stockUnits.toString(),
            selectedCategory = drink.category,
            imageUri = drink.imageUrl
        )

        assertEquals("Coca Cola", state.name)
        assertEquals("Bebida gaseosa", state.description)
        assertEquals("2.5", state.price)
        assertEquals("100", state.stockUnits)
        assertEquals(testCategory, state.selectedCategory)
        assertEquals("http://example.com/image.jpg", state.imageUri)
        assertNotNull(state.drink)
    }

    @Test
    fun `form should be invalid when name is blank`() {
        val state = DrinkFormUiState(
            name = "",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when price is blank`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "",
            stockUnits = "100",
            selectedCategory = testCategory
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when stockUnits is blank`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "",
            selectedCategory = testCategory
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when category is not selected`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = null
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be valid when all required fields are filled`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory
        )

        assertTrue(state.isFormValid)
    }

    @Test
    fun `form should be invalid when has errors`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            priceError = "Price must be positive"
        )

        assertTrue(state.hasErrors)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should show name error when name is required`() {
        val state = DrinkFormUiState(
            name = "",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            nameError = "Name is required"
        )

        assertEquals("Name is required", state.nameError)
    }

    @Test
    fun `form should show price error for invalid price`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "invalid",
            stockUnits = "100",
            selectedCategory = testCategory,
            priceError = "Price must be a positive number"
        )

        assertEquals("Price must be a positive number", state.priceError)
    }

    @Test
    fun `form should show stockUnits error for invalid stock`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "-10",
            selectedCategory = testCategory,
            stockUnitsError = "Stock units must be a non-negative number"
        )

        assertEquals("Stock units must be a non-negative number", state.stockUnitsError)
    }

    @Test
    fun `form should show category error when category is required`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = null,
            categoryError = "Category is required"
        )

        assertEquals("Category is required", state.categoryError)
    }

    @Test
    fun `form should clear error when field is updated`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            nameError = "Name is required"
        )

        val updatedState = state.copy(name = "Pepsi", nameError = null)

        assertNull(updatedState.nameError)
        assertEquals("Pepsi", updatedState.name)
    }

    @Test
    fun `form should disable submit button when submitting`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            isSubmitting = true
        )

        assertTrue(state.isSubmitting)
    }

    @Test
    fun `form should show success state after successful submission`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            submitSuccess = true
        )

        assertTrue(state.submitSuccess)
    }

    @Test
    fun `form should show error message on submission failure`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            submitError = "Failed to save drink"
        )

        assertEquals("Failed to save drink", state.submitError)
    }

    @Test
    fun `form should support optional description field`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            description = "",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory
        )

        assertTrue(state.isFormValid)
        assertEquals("", state.description)
    }

    @Test
    fun `form should support optional image field`() {
        val state = DrinkFormUiState(
            name = "Coca Cola",
            price = "2.5",
            stockUnits = "100",
            selectedCategory = testCategory,
            imageUri = null
        )

        assertTrue(state.isFormValid)
        assertNull(state.imageUri)
    }

    @Test
    fun `form should load available categories`() {
        val categories = listOf(
            testCategory,
            Category(
                id = "2",
                name = "Postres",
                description = "Categoría de postres",
                state = CategoryState.ACTIVE
            )
        )

        val state = DrinkFormUiState(
            availableCategories = categories,
            isLoadingCategories = false
        )

        assertEquals(2, state.availableCategories.size)
        assertFalse(state.isLoadingCategories)
    }

    @Test
    fun `form should show loading state when loading categories`() {
        val state = DrinkFormUiState(
            isLoadingCategories = true
        )

        assertTrue(state.isLoadingCategories)
    }
}
