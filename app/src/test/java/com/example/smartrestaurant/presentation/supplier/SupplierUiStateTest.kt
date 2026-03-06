package com.example.smartrestaurant.presentation.supplier

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.ProductState
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import org.junit.Assert.*
import org.junit.Test

class SupplierUiStateTest {

    @Test
    fun `SupplierListUiState filters suppliers by search query`() {
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "XYZ Distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE),
            Supplier("3", "ABC Foods", "foods@example.com", "555555555", "Address 3", SupplierState.ACTIVE)
        )

        val state = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = "ABC"
        )

        assertEquals(2, state.filteredSuppliers.size)
        assertTrue(state.filteredSuppliers.all { it.name.contains("ABC", ignoreCase = true) })
    }

    @Test
    fun `SupplierListUiState returns all suppliers when search query is empty`() {
        val suppliers = listOf(
            Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE),
            Supplier("2", "XYZ Distributors", "xyz@example.com", "987654321", "Address 2", SupplierState.ACTIVE)
        )

        val state = SupplierListUiState(
            suppliers = suppliers,
            searchQuery = ""
        )

        assertEquals(2, state.filteredSuppliers.size)
    }

    @Test
    fun `SupplierDetailUiState holds supplier and associated products`() {
        val supplier = Supplier("1", "ABC Suppliers", "abc@example.com", "123456789", "Address 1", SupplierState.ACTIVE)
        val products = listOf(
            Product("1", "Product 1", "Description", 1.0, "kg", 10.0, 50.0, 10.0, null, ProductState.ACTIVE, supplier),
            Product("2", "Product 2", "Description", 2.0, "kg", 20.0, 30.0, 5.0, null, ProductState.ACTIVE, supplier)
        )

        val state = SupplierDetailUiState(
            supplier = supplier,
            associatedProducts = products
        )

        assertNotNull(state.supplier)
        assertEquals(2, state.associatedProducts.size)
        assertEquals("ABC Suppliers", state.supplier?.name)
    }

    @Test
    fun `SupplierFormUiState hasErrors returns true when any field has error`() {
        val stateWithNameError = SupplierFormUiState(nameError = "Name is required")
        assertTrue(stateWithNameError.hasErrors)

        val stateWithEmailError = SupplierFormUiState(emailError = "Invalid email format")
        assertTrue(stateWithEmailError.hasErrors)

        val stateWithNoErrors = SupplierFormUiState()
        assertFalse(stateWithNoErrors.hasErrors)
    }

    @Test
    fun `SupplierFormUiState isFormValid returns false when required fields are empty`() {
        val stateWithEmptyName = SupplierFormUiState(
            name = "",
            email = "test@example.com",
            phone = "123456789",
            address = "Address"
        )
        assertFalse(stateWithEmptyName.isFormValid)

        val stateWithEmptyEmail = SupplierFormUiState(
            name = "Supplier Name",
            email = "",
            phone = "123456789",
            address = "Address"
        )
        assertFalse(stateWithEmptyEmail.isFormValid)
    }

    @Test
    fun `SupplierFormUiState isFormValid returns true when all fields are valid`() {
        val validState = SupplierFormUiState(
            name = "ABC Suppliers",
            email = "abc@example.com",
            phone = "123456789",
            address = "123 Main Street"
        )
        assertTrue(validState.isFormValid)
    }

    @Test
    fun `SupplierFormUiState isFormValid returns false when has validation errors`() {
        val stateWithErrors = SupplierFormUiState(
            name = "ABC Suppliers",
            email = "invalid-email",
            phone = "123456789",
            address = "123 Main Street",
            emailError = "Invalid email format"
        )
        assertFalse(stateWithErrors.isFormValid)
    }

    @Test
    fun `SupplierFormUiState pre-populates from existing supplier`() {
        val existingSupplier = Supplier(
            "1",
            "ABC Suppliers",
            "abc@example.com",
            "123456789",
            "123 Main Street",
            SupplierState.ACTIVE
        )

        val state = SupplierFormUiState(
            supplier = existingSupplier,
            name = existingSupplier.name,
            email = existingSupplier.email,
            phone = existingSupplier.phone,
            address = existingSupplier.address
        )

        assertEquals("ABC Suppliers", state.name)
        assertEquals("abc@example.com", state.email)
        assertEquals("123456789", state.phone)
        assertEquals("123 Main Street", state.address)
    }
}
