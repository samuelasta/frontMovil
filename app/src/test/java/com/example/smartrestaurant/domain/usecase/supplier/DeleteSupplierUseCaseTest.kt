package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.repository.SupplierRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for DeleteSupplierUseCase.
 * Validates: Requirements 4.7
 */
class DeleteSupplierUseCaseTest {
    
    private lateinit var repository: SupplierRepository
    private lateinit var useCase: DeleteSupplierUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteSupplierUseCase(repository)
    }
    
    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        coEvery { repository.deleteSupplier("1") } returns Result.success(Unit)
        
        // When
        val result = useCase("1")
        
        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteSupplier("1") }
    }
    
    @Test
    fun `invoke returns failure when supplier not found`() = runTest {
        // Given
        coEvery { repository.deleteSupplier("999") } returns Result.failure(
            Exception("Supplier not found")
        )
        
        // When
        val result = useCase("999")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Supplier not found", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.deleteSupplier("999") }
    }
    
    @Test
    fun `invoke returns failure when supplier has associated products`() = runTest {
        // Given
        coEvery { repository.deleteSupplier("1") } returns Result.failure(
            Exception("Cannot delete supplier with associated products")
        )
        
        // When
        val result = useCase("1")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Cannot delete supplier with associated products", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.deleteSupplier("1") }
    }
    
    @Test
    fun `invoke returns failure when repository fails with network error`() = runTest {
        // Given
        coEvery { repository.deleteSupplier("1") } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase("1")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.deleteSupplier("1") }
    }
    
    @Test
    fun `invoke correctly passes supplier id to repository`() = runTest {
        // Given
        coEvery { repository.deleteSupplier("123") } returns Result.success(Unit)
        
        // When
        useCase("123")
        
        // Then
        coVerify(exactly = 1) { repository.deleteSupplier("123") }
    }
}
