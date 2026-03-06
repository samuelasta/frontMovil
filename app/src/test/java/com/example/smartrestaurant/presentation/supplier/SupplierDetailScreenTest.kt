package com.example.smartrestaurant.presentation.supplier

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.ProductState
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Unit tests for SupplierDetailScreen
 * Validates: Requirements 4.4
 */
class SupplierDetailScreenTest : StringSpec({

    "SupplierDetailUiState should contain supplier information" {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "123456789",
            address = "Test Address",
            state = SupplierState.ACTIVE
        )

        val state = SupplierDetailUiState(
            supplier = supplier,
            isLoading = false,
            error = null
        )

        state.supplier shouldNotBe null
        state.supplier?.name shouldBe "Test Supplier"
        state.supplier?.email shouldBe "test@example.com"
        state.supplier?.phone shouldBe "123456789"
        state.supplier?.address shouldBe "Test Address"
    }

    "SupplierDetailUiState should contain associated products list" {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "123456789",
            address = "Test Address",
            state = SupplierState.ACTIVE
        )

        val products = listOf(
            Product(
                id = "1",
                name = "Product 1",
                description = "Description 1",
                weight = 1.0,
                unit = "kg",
                price = 10.0,
                stock = 50.0,
                minimumStock = 10.0,
                imageUrl = null,
                state = ProductState.ACTIVE,
                supplier = supplier
            ),
            Product(
                id = "2",
                name = "Product 2",
                description = "Description 2",
                weight = 2.0,
                unit = "kg",
                price = 20.0,
                stock = 5.0,
                minimumStock = 10.0,
                imageUrl = null,
                state = ProductState.ACTIVE,
                supplier = supplier
            )
        )

        val state = SupplierDetailUiState(
            supplier = supplier,
            associatedProducts = products,
            isLoading = false,
            error = null
        )

        state.associatedProducts.size shouldBe 2
        state.associatedProducts[0].name shouldBe "Product 1"
        state.associatedProducts[1].name shouldBe "Product 2"
    }

    "SupplierDetailUiState should show loading state" {
        val state = SupplierDetailUiState(
            supplier = null,
            isLoading = true,
            error = null
        )

        state.isLoading shouldBe true
        state.supplier shouldBe null
    }

    "SupplierDetailUiState should show error state" {
        val state = SupplierDetailUiState(
            supplier = null,
            isLoading = false,
            error = "Failed to load supplier"
        )

        state.error shouldNotBe null
        state.error shouldBe "Failed to load supplier"
        state.supplier shouldBe null
    }

    "SupplierDetailUiState should handle delete confirmation dialog state" {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "123456789",
            address = "Test Address",
            state = SupplierState.ACTIVE
        )

        val state = SupplierDetailUiState(
            supplier = supplier,
            showDeleteConfirmation = true
        )

        state.showDeleteConfirmation shouldBe true
    }

    "Associated products should include low stock indicator information" {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "123456789",
            address = "Test Address",
            state = SupplierState.ACTIVE
        )

        val lowStockProduct = Product(
            id = "1",
            name = "Low Stock Product",
            description = "Description",
            weight = 1.0,
            unit = "kg",
            price = 10.0,
            stock = 5.0,
            minimumStock = 10.0,
            imageUrl = null,
            state = ProductState.ACTIVE,
            supplier = supplier
        )

        lowStockProduct.isLowStock shouldBe true
    }

    "SupplierDetailUiState should handle empty associated products list" {
        val supplier = Supplier(
            id = "1",
            name = "Test Supplier",
            email = "test@example.com",
            phone = "123456789",
            address = "Test Address",
            state = SupplierState.ACTIVE
        )

        val state = SupplierDetailUiState(
            supplier = supplier,
            associatedProducts = emptyList()
        )

        state.associatedProducts.isEmpty() shouldBe true
    }
})
