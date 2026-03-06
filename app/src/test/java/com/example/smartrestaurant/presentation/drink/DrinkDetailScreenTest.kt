package com.example.smartrestaurant.presentation.drink

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.model.DrinkState
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DrinkDetailScreen UI State
 * Validates: Requirements 7.4
 */
class DrinkDetailScreenTest {

    private val testCategory = Category(
        id = "cat1",
        name = "Bebidas Calientes",
        description = "Café, té, etc.",
        state = CategoryState.ACTIVE
    )

    @Test
    fun `DrinkDetailUiState displays all required fields`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Café Americano",
            description = "Café negro de alta calidad",
            price = 2.50,
            stockUnits = 100,
            imageUrl = "https://example.com/cafe.jpg",
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(drink = drink)

        assertNotNull(state.drink)
        assertEquals("Café Americano", state.drink?.name)
        assertEquals("Café negro de alta calidad", state.drink?.description)
        assertEquals(2.50, state.drink?.price ?: 0.0, 0.01)
        assertEquals(100, state.drink?.stockUnits)
        assertEquals("Bebidas Calientes", state.drink?.category?.name)
        assertEquals("https://example.com/cafe.jpg", state.drink?.imageUrl)
        assertEquals(DrinkState.ACTIVE, state.drink?.state)
    }

    @Test
    fun `DrinkDetailUiState handles loading state correctly`() {
        // Validates: Requirements 7.4
        val state = DrinkDetailUiState(
            drink = null,
            isLoading = true,
            error = null
        )

        assertTrue(state.isLoading)
        assertNull(state.error)
        assertNull(state.drink)
    }

    @Test
    fun `DrinkDetailUiState handles error state correctly`() {
        // Validates: Requirements 7.4
        val errorMessage = "Failed to load drink"
        val state = DrinkDetailUiState(
            drink = null,
            isLoading = false,
            error = errorMessage
        )

        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.drink)
    }

    @Test
    fun `DrinkDetailUiState handles delete confirmation dialog state`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Café Americano",
            description = "Café negro",
            price = 2.50,
            stockUnits = 100,
            imageUrl = null,
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(
            drink = drink,
            showDeleteConfirmation = true
        )

        assertTrue(state.showDeleteConfirmation)
        assertNotNull(state.drink)
    }

    @Test
    fun `DrinkDetailUiState shows stock units correctly`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Té Verde",
            description = "Té verde orgánico",
            price = 2.00,
            stockUnits = 50,
            imageUrl = null,
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(drink = drink)

        assertEquals(50, state.drink?.stockUnits)
    }

    @Test
    fun `DrinkDetailUiState handles drink with zero stock`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Jugo de Naranja",
            description = "Jugo natural",
            price = 3.50,
            stockUnits = 0,
            imageUrl = null,
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(drink = drink)

        assertEquals(0, state.drink?.stockUnits)
    }

    @Test
    fun `DrinkDetailUiState handles inactive drink state`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Café Descontinuado",
            description = "Ya no disponible",
            price = 2.50,
            stockUnits = 0,
            imageUrl = null,
            state = DrinkState.INACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(drink = drink)

        assertEquals(DrinkState.INACTIVE, state.drink?.state)
    }

    @Test
    fun `DrinkDetailUiState handles drink without image`() {
        // Validates: Requirements 7.4
        val drink = Drink(
            id = "1",
            name = "Café Simple",
            description = "Café sin imagen",
            price = 2.50,
            stockUnits = 100,
            imageUrl = null,
            state = DrinkState.ACTIVE,
            category = testCategory
        )

        val state = DrinkDetailUiState(drink = drink)

        assertNull(state.drink?.imageUrl)
    }
}
