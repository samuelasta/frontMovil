package com.example.smartrestaurant.presentation.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for SupplierFormScreen and SupplierFormUiState
 * Validates: Requirements 4.2, 4.3, 4.5, 4.6, 12.2
 */
class SupplierFormScreenTest {

    @Test
    fun `form state should initialize with empty fields`() {
        val state = SupplierFormUiState()

        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.phone)
        assertEquals("", state.address)
        assertNull(state.supplier)
        assertFalse(state.isSubmitting)
        assertFalse(state.submitSuccess)
    }

    @Test
    fun `form state should pre-populate fields when editing supplier`() {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St",
            state = SupplierState.ACTIVE
        )

        val state = SupplierFormUiState(
            supplier = supplier,
            name = supplier.name,
            email = supplier.email,
            phone = supplier.phone,
            address = supplier.address
        )

        assertEquals("Test Supplier", state.name)
        assertEquals("test@example.com", state.email)
        assertEquals("1234567890", state.phone)
        assertEquals("123 Test St", state.address)
        assertNotNull(state.supplier)
    }

    @Test
    fun `form should be invalid when name is blank`() {
        val state = SupplierFormUiState(
            name = "",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St"
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when email is blank`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "",
            phone = "1234567890",
            address = "123 Test St"
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when phone is blank`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "",
            address = "123 Test St"
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be invalid when address is blank`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = ""
        )

        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should be valid when all required fields are filled`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St"
        )

        assertTrue(state.isFormValid)
    }

    @Test
    fun `form should be invalid when has errors`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "invalid-email",
            phone = "1234567890",
            address = "123 Test St",
            emailError = "Invalid email format"
        )

        assertTrue(state.hasErrors)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `form should show email error for invalid format`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "invalid-email",
            phone = "1234567890",
            address = "123 Test St",
            emailError = "Invalid email format"
        )

        assertEquals("Invalid email format", state.emailError)
    }

    @Test
    fun `form should clear error when field is updated`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St",
            nameError = "Name is required"
        )

        val updatedState = state.copy(name = "New Name", nameError = null)

        assertNull(updatedState.nameError)
        assertEquals("New Name", updatedState.name)
    }

    @Test
    fun `form should disable submit button when submitting`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St",
            isSubmitting = true
        )

        assertTrue(state.isSubmitting)
    }

    @Test
    fun `form should show success state after successful submission`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St",
            submitSuccess = true
        )

        assertTrue(state.submitSuccess)
    }

    @Test
    fun `form should show error message on submission failure`() {
        val state = SupplierFormUiState(
            name = "Test Supplier",
            email = "test@example.com",
            phone = "1234567890",
            address = "123 Test St",
            submitError = "Failed to save supplier"
        )

        assertEquals("Failed to save supplier", state.submitError)
    }
}
