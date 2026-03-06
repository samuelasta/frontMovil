package com.example.smartrestaurant.presentation.drink

import com.example.smartrestaurant.domain.model.Category
import com.example.smartrestaurant.domain.model.CategoryState
import com.example.smartrestaurant.domain.model.Drink
import com.example.smartrestaurant.domain.model.DrinkState
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DrinkListScreen UI State
 * Validates: Requirements 7.1, 11.2, 11.3, 11.4, 15.1, 15.2, 15.5
 */
class DrinkListScreenTest {

    private val testCategory1 = Category(
        id = "cat1",
        name = "Bebidas Calientes",
        description = "Café, té, etc.",
        state = CategoryState.ACTIVE
    )

    private val testCategory2 = Category(
        id = "cat2",
        name = "Bebidas Frías",
        description = "Jugos, refrescos, etc.",
        state = CategoryState.ACTIVE
    )

    @Test
    fun `DrinkListUiState displays all required fields for drinks`() {
        // Property 1: Entity List Display Completeness - Drinks
        // Validates: Requirements 7.1, 11.3, 11.4
        val drink = Drink(
            id = "1",
            name = "Café Americano",
            description = "Café negro",
            price = 2.50,
            stockUnits = 100,
            imageUrl = "https://example.com/image.jpg",
            state = DrinkState.ACTIVE,
            category = testCategory1
        )

        val state = DrinkListUiState(drinks = listOf(drink))

        assertEquals(1, state.drinks.size)
        assertEquals("Café Americano", state.drinks[0].name)
        assertEquals(2.50, state.drinks[0].price, 0.01)
        assertEquals("Bebidas Calientes", state.drinks[0].category.name)
        assertEquals(100, state.drinks[0].stockUnits)
        assertEquals("https://example.com/image.jpg", state.drinks[0].imageUrl)
    }

    @Test
    fun `DrinkListUiState filters drinks by name in real-time`() {
        // Property 34: Search Filter Real-time Update
        // Validates: Requirements 15.2
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Té Verde", "Té caliente", 2.00, 50, null, DrinkState.ACTIVE, testCategory1),
            Drink("3", "Jugo de Naranja", "Jugo natural", 3.50, 30, null, DrinkState.ACTIVE, testCategory2)
        )

        val state = DrinkListUiState(drinks = drinks, searchQuery = "café")

        assertEquals(1, state.filteredDrinks.size)
        assertEquals("Café Americano", state.filteredDrinks[0].name)
    }

    @Test
    fun `DrinkListUiState search is case insensitive`() {
        // Validates: Requirements 15.2
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Té Verde", "Té caliente", 2.00, 50, null, DrinkState.ACTIVE, testCategory1)
        )

        val state = DrinkListUiState(drinks = drinks, searchQuery = "CAFÉ")

        assertEquals(1, state.filteredDrinks.size)
        assertEquals("Café Americano", state.filteredDrinks[0].name)
    }

    @Test
    fun `DrinkListUiState filters drinks by category`() {
        // Validates: Requirements 15.5
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Té Verde", "Té caliente", 2.00, 50, null, DrinkState.ACTIVE, testCategory1),
            Drink("3", "Jugo de Naranja", "Jugo natural", 3.50, 30, null, DrinkState.ACTIVE, testCategory2)
        )

        val state = DrinkListUiState(drinks = drinks, selectedCategory = testCategory1)

        assertEquals(2, state.filteredDrinks.size)
        assertTrue(state.filteredDrinks.all { it.category.id == "cat1" })
    }

    @Test
    fun `DrinkListUiState combines search and category filters`() {
        // Validates: Requirements 15.2, 15.5
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Café Latte", "Café con leche", 3.00, 80, null, DrinkState.ACTIVE, testCategory1),
            Drink("3", "Jugo de Naranja", "Jugo natural", 3.50, 30, null, DrinkState.ACTIVE, testCategory2)
        )

        val state = DrinkListUiState(
            drinks = drinks,
            searchQuery = "café",
            selectedCategory = testCategory1
        )

        assertEquals(2, state.filteredDrinks.size)
        assertTrue(state.filteredDrinks.all { it.name.contains("Café", ignoreCase = true) })
        assertTrue(state.filteredDrinks.all { it.category.id == "cat1" })
    }

    @Test
    fun `DrinkListUiState shows empty list when no drinks match filters`() {
        // Validates: Requirements 15.2, 15.5
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Té Verde", "Té caliente", 2.00, 50, null, DrinkState.ACTIVE, testCategory1)
        )

        val state = DrinkListUiState(drinks = drinks, searchQuery = "pizza")

        assertTrue(state.filteredDrinks.isEmpty())
    }

    @Test
    fun `DrinkListUiState handles loading state correctly`() {
        // Validates: Requirements 11.2
        val state = DrinkListUiState(
            drinks = emptyList(),
            isLoading = true,
            error = null
        )

        assertTrue(state.isLoading)
        assertNull(state.error)
        assertTrue(state.drinks.isEmpty())
    }

    @Test
    fun `DrinkListUiState handles error state correctly`() {
        // Validates: Requirements 11.2
        val errorMessage = "Failed to load drinks"
        val state = DrinkListUiState(
            drinks = emptyList(),
            isLoading = false,
            error = errorMessage
        )

        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `DrinkListUiState handles pagination state`() {
        // Validates: Requirements 11.2
        val drinks = List(10) { index ->
            Drink(
                id = "drink$index",
                name = "Bebida $index",
                description = "Descripción $index",
                price = 2.50 + index,
                stockUnits = 100,
                imageUrl = null,
                state = DrinkState.ACTIVE,
                category = testCategory1
            )
        }

        val state = DrinkListUiState(
            drinks = drinks,
            currentPage = 0,
            hasMorePages = true,
            isLoading = false
        )

        assertEquals(0, state.currentPage)
        assertTrue(state.hasMorePages)
        assertEquals(10, state.drinks.size)
    }

    @Test
    fun `DrinkListUiState returns all drinks when no filters applied`() {
        // Validates: Requirements 7.1
        val drinks = listOf(
            Drink("1", "Café Americano", "Café negro", 2.50, 100, null, DrinkState.ACTIVE, testCategory1),
            Drink("2", "Té Verde", "Té caliente", 2.00, 50, null, DrinkState.ACTIVE, testCategory1),
            Drink("3", "Jugo de Naranja", "Jugo natural", 3.50, 30, null, DrinkState.ACTIVE, testCategory2)
        )

        val state = DrinkListUiState(drinks = drinks)

        assertEquals(3, state.filteredDrinks.size)
        assertEquals(drinks, state.filteredDrinks)
    }
}
