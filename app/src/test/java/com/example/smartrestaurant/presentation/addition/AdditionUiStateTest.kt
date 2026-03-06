package com.example.smartrestaurant.presentation.addition

import com.example.smartrestaurant.domain.model.Addition
import com.example.smartrestaurant.domain.model.AdditionState
import org.junit.Assert.*
import org.junit.Test

class AdditionUiStateTest {

    @Test
    fun `AdditionListUiState filters additions by search query`() {
        val additions = listOf(
            Addition("1", "Extra Cheese", "Additional cheese topping", 2.50, null, AdditionState.ACTIVE),
            Addition("2", "Bacon Strips", "Crispy bacon strips", 3.00, null, AdditionState.ACTIVE),
            Addition("3", "Extra Sauce", "Additional sauce portion", 1.50, null, AdditionState.ACTIVE)
        )

        val state = AdditionListUiState(
            additions = additions,
            searchQuery = "Extra"
        )

        assertEquals(2, state.filteredAdditions.size)
        assertTrue(state.filteredAdditions.all { it.name.contains("Extra", ignoreCase = true) })
    }

    @Test
    fun `AdditionListUiState returns all additions when search query is empty`() {
        val additions = listOf(
            Addition("1", "Extra Cheese", "Additional cheese topping", 2.50, null, AdditionState.ACTIVE),
            Addition("2", "Bacon Strips", "Crispy bacon strips", 3.00, null, AdditionState.ACTIVE)
        )

        val state = AdditionListUiState(
            additions = additions,
            searchQuery = ""
        )

        assertEquals(2, state.filteredAdditions.size)
    }

    @Test
    fun `AdditionListUiState search is case insensitive`() {
        val additions = listOf(
            Addition("1", "Extra Cheese", "Additional cheese topping", 2.50, null, AdditionState.ACTIVE),
            Addition("2", "Bacon Strips", "Crispy bacon strips", 3.00, null, AdditionState.ACTIVE)
        )

        val state = AdditionListUiState(
            additions = additions,
            searchQuery = "cheese"
        )

        assertEquals(1, state.filteredAdditions.size)
        assertEquals("Extra Cheese", state.filteredAdditions.first().name)
    }

    @Test
    fun `AdditionDetailUiState holds addition data`() {
        val addition = Addition(
            "1",
            "Extra Cheese",
            "Additional cheese topping",
            2.50,
            "https://example.com/cheese.jpg",
            AdditionState.ACTIVE
        )

        val state = AdditionDetailUiState(
            addition = addition
        )

        assertNotNull(state.addition)
        assertEquals("Extra Cheese", state.addition?.name)
        assertEquals(2.50, state.addition?.price ?: 0.0, 0.01)
    }

    @Test
    fun `AdditionDetailUiState showDeleteConfirmation defaults to false`() {
        val state = AdditionDetailUiState()

        assertFalse(state.showDeleteConfirmation)
    }

    @Test
    fun `AdditionFormUiState hasErrors returns true when any field has error`() {
        val stateWithNameError = AdditionFormUiState(nameError = "Name is required")
        assertTrue(stateWithNameError.hasErrors)

        val stateWithPriceError = AdditionFormUiState(priceError = "Price must be positive")
        assertTrue(stateWithPriceError.hasErrors)

        val stateWithNoErrors = AdditionFormUiState()
        assertFalse(stateWithNoErrors.hasErrors)
    }

    @Test
    fun `AdditionFormUiState isFormValid returns false when required fields are empty`() {
        val stateWithEmptyName = AdditionFormUiState(
            name = "",
            description = "Description",
            price = "2.50"
        )
        assertFalse(stateWithEmptyName.isFormValid)

        val stateWithEmptyPrice = AdditionFormUiState(
            name = "Extra Cheese",
            description = "Description",
            price = ""
        )
        assertFalse(stateWithEmptyPrice.isFormValid)
    }

    @Test
    fun `AdditionFormUiState isFormValid returns true when all fields are valid`() {
        val validState = AdditionFormUiState(
            name = "Extra Cheese",
            description = "Additional cheese topping",
            price = "2.50"
        )
        assertTrue(validState.isFormValid)
    }

    @Test
    fun `AdditionFormUiState isFormValid returns false when has validation errors`() {
        val stateWithErrors = AdditionFormUiState(
            name = "Extra Cheese",
            description = "Additional cheese topping",
            price = "-2.50",
            priceError = "Price must be positive"
        )
        assertFalse(stateWithErrors.isFormValid)
    }

    @Test
    fun `AdditionFormUiState pre-populates from existing addition`() {
        val existingAddition = Addition(
            "1",
            "Extra Cheese",
            "Additional cheese topping",
            2.50,
            "https://example.com/cheese.jpg",
            AdditionState.ACTIVE
        )

        val state = AdditionFormUiState(
            addition = existingAddition,
            name = existingAddition.name,
            description = existingAddition.description,
            price = existingAddition.price.toString(),
            imageUri = existingAddition.imageUrl
        )

        assertEquals("Extra Cheese", state.name)
        assertEquals("Additional cheese topping", state.description)
        assertEquals("2.5", state.price)
        assertEquals("https://example.com/cheese.jpg", state.imageUri)
    }

    @Test
    fun `AdditionFormUiState submitSuccess defaults to false`() {
        val state = AdditionFormUiState()

        assertFalse(state.submitSuccess)
    }

    @Test
    fun `AdditionFormUiState isSubmitting defaults to false`() {
        val state = AdditionFormUiState()

        assertFalse(state.isSubmitting)
    }

    @Test
    fun `AdditionListUiState pagination state defaults correctly`() {
        val state = AdditionListUiState()

        assertEquals(0, state.currentPage)
        assertTrue(state.hasMorePages)
    }

    @Test
    fun `AdditionListUiState isLoading defaults to false`() {
        val state = AdditionListUiState()

        assertFalse(state.isLoading)
    }
}
