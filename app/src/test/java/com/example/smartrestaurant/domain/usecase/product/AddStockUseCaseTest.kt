package com.example.smartrestaurant.domain.usecase.product

import com.example.smartrestaurant.domain.model.Product
import com.example.smartrestaurant.domain.model.ProductState
import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import com.example.smartrestaurant.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AddStockUseCase.
 * Tests validation of positive quantity requirement.
 * Validates: Requirements 2.3, 12.3
 */
class AddStockUseCaseTest {
    
    private lateinit var repository: ProductRepository
    private lateinit var useCase: AddStockUseCase
    
    private val testSupplier = Supplier(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = SupplierState.ACTIVE
    )
    
    private val testProduct = Product(
        id = "1",
        name = "Test Product",
        description = "Test Description",
        weight = 1.0,
        unit = "kg",
        price = 10.0,
        stock = 5.0,
        minimumStock = 2.0,
        imageUrl = null,
        state = ProductState.ACTIVE,
        supplier = testSupplier
    )
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = AddStockUseCase(repository)
    }
    
    @Test
    fun `invoke returns success when quantity is positive and repository succeeds`() = runTest {
        // Given
        val updatedProduct = testProduct.copy(stock = 15.0)
        coEvery { repository.addStock("1", 10.0, "Restock") } returns Result.success(updatedProduct)
        
        // When
        val result = useCase("1", 10.0, "Restock")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(15.0, result.getOrNull()?.stock, 0.01)
        coVerify(exactly = 1) { repository.addStock("1", 10.0, "Restock") }
    }
    
    @Test
    fun `invoke returns failure when quantity is zero`() = runTest {
        // When
        val result = useCase("1", 0.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Quantity must be positive", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.addStock(any(), any(), any()) }
    }
    
    @Test
    fun `invoke returns failure when quantity is negative`() = runTest {
        // When
        val result = useCase("1", -5.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Quantity must be positive", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.addStock(any(), any(), any()) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        coEvery { repository.addStock("1", 10.0, null) } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase("1", 10.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.addStock("1", 10.0, null) }
    }
    
    @Test
    fun `invoke works with optional reason parameter`() = runTest {
        // Given
        val updatedProduct = testProduct.copy(stock = 10.0)
        coEvery { repository.addStock("1", 5.0, null) } returns Result.success(updatedProduct)
        
        // When
        val result = useCase("1", 5.0)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addStock("1", 5.0, null) }
    }
    
    @Test
    fun `invoke validates quantity before calling repository`() = runTest {
        // When
        val result = useCase("1", -1.0, "Invalid")
        
        // Then
        assertTrue(result.isFailure)
        // Repository should never be called for invalid input
        coVerify(exactly = 0) { repository.addStock(any(), any(), any()) }
    }
}
