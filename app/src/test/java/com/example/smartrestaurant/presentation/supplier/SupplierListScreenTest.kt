package com.example.smartrestaurant.presentation.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SupplierListScreen
 * Validates: Requirements 4.1, 15.2
 */
class SupplierListScreenTest {

    @Test
    fun `SupplierListUiState displays all required fields for suppliers`() {
        // Property 1: Entity List Display Completeness - Suppliers
        val suppliers = listOf(
            Supplier(
                id = "1",
                name = "ABC Suppliers",
                email = "abc@example.com",
                phone = "123456789",
                address = "Address 1",
                state = SupplierState.ACTIVE
            ),
            Supplier(
                id = "2",
                name = "XYZ Distributors",
                email = "xyz@example.com",
                phone = "987654321",
                address = "Address 2",
                state = SupplierState.ACTIVE
            )
        )

        val state = SupplierListUiState(suppliers = suppliers)

        // Verify all suppliers are present
        assertEquals(2, state.suppliers.size)
        
        // Verify required fields are accessible for display
        suppliers.forEach { supplier ->
            assertNotNull(supplier.name)
            assertNotNull(supplier.email)
            assertNotNull(supplier.phone)
        }
    }

    @Test
    fun `SupplierListUiState filters suppliers by name in real-time`() {
        // Property 34: Search Filter Real-time Update
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "XYZ Distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE),
            Supplier("3", "ABC Foods", "foods@example.com", "555555555", "Address 3", SupplierState.ACTIVE)
        )

        val state = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = "ABC"
        )

        // Verify filtered results contain only matching suppliers
        assertEquals(2, state.filteredSuppliers.size)
        assertTrue(state.filteredSuppliers.all { it.name.contains("ABC", ignoreCase = true) })
    }

    @Test
    fun `SupplierListUiState search is case insensitive`() {
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "xyz distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE)
        )

        val stateUpperCase = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = "ABC"
        )
        
        val stateLowerCase = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = "abc"
        )

        assertEquals(1, stateUpperCase.filteredSuppliers.size)
        assertEquals(1, stateLowerCase.filteredSuppliers.size)
        assertEquals(stateUpperCase.filteredSuppliers.first().id, stateLowerCase.filteredSuppliers.first().id)
    }

    @Test
    fun `SupplierListUiState shows empty list when no suppliers match search`() {
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "XYZ Distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE)
        )

        val state = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = "NonExistent"
        )

        assertTrue(state.filteredSuppliers.isEmpty())
    }

    @Test
    fun `SupplierListUiState handles loading state correctly`() {
        val state = SupplierListUiState(
            isLoading = true,
            suppliers = emptyList()
        )

        assertTrue(state.isLoading)
        assertTrue(state.suppliers.isEmpty())
    }

    @Test
    fun `SupplierListUiState handles error state correctly`() {
        val errorMessage = "Failed to load suppliers"
        val state = SupplierListUiState(
            error = errorMessage,
            suppliers = emptyList()
        )

        assertEquals(errorMessage, state.error)
        assertTrue(state.suppliers.isEmpty())
    }

    @Test
    fun `SupplierListUiState returns all suppliers when search query is empty`() {
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "XYZ Distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE),
            Supplier("3", "DEF Foods", "def@example.com", "111111111", "Address 3", SupplierState.ACTIVE)
        )

        val state = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = ""
        )

        assertEquals(suppliers.size, state.filteredSuppliers.size)
        assertEquals(suppliers, state.filteredSuppliers)
    }
}
