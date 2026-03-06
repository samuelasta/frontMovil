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
 * Unit tests for SubtractStockUseCase.
 * Tests validation of positive quantity requirement.
 * Validates: Requirements 2.5, 12.3
 */
class SubtractStockUseCaseTest {
    
    private lateinit var repository: ProductRepository
    private lateinit var useCase: SubtractStockUseCase
    
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
        stock = 10.0,
        minimumStock = 2.0,
        imageUrl = null,
        state = ProductState.ACTIVE,
        supplier = testSupplier
    )
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = SubtractStockUseCase(repository)
    }
    
    @Test
    fun `invoke returns success when quantity is positive and repository succeeds`() = runTest {
        // Given
        val updatedProduct = testProduct.copy(stock = 7.0)
        coEvery { repository.subtractStock("1", 3.0, "Usage") } returns Result.success(updatedProduct)
        
        // When
        val result = useCase("1", 3.0, "Usage")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(7.0, result.getOrNull()?.stock, 0.01)
        coVerify(exactly = 1) { repository.subtractStock("1", 3.0, "Usage") }
    }
    
    @Test
    fun `invoke returns failure when quantity is zero`() = runTest {
        // When
        val result = useCase("1", 0.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Quantity must be positive", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.subtractStock(any(), any(), any()) }
    }
    
    @Test
    fun `invoke returns failure when quantity is negative`() = runTest {
        // When
        val result = useCase("1", -2.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Quantity must be positive", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.subtractStock(any(), any(), any()) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        coEvery { repository.subtractStock("1", 5.0, null) } returns Result.failure(
            Exception("Insufficient stock")
        )
        
        // When
        val result = useCase("1", 5.0, null)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Insufficient stock", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.subtractStock("1", 5.0, null) }
    }
    
    @Test
    fun `invoke works with optional reason parameter`() = runTest {
        // Given
        val updatedProduct = testProduct.copy(stock = 8.0)
        coEvery { repository.subtractStock("1", 2.0, null) } returns Result.success(updatedProduct)
        
        // When
        val result = useCase("1", 2.0)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.subtractStock("1", 2.0, null) }
    }
    
    @Test
    fun `invoke validates quantity before calling repository`() = runTest {
        // When
        val result = useCase("1", -10.0, "Invalid")
        
        // Then
        assertTrue(result.isFailure)
        // Repository should never be called for invalid input
        coVerify(exactly = 0) { repository.subtractStock(any(), any(), any()) }
    }
}
