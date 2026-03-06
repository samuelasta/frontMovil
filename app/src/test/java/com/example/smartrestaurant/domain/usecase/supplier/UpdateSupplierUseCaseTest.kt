package com.example.smartrestaurant.domain.usecase.supplier

import com.example.smartrestaurant.domain.model.Supplier
import com.example.smartrestaurant.domain.model.SupplierState
import com.example.smartrestaurant.domain.repository.SupplierRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for UpdateSupplierUseCase.
 * Validates: Requirements 4.6
 */
class UpdateSupplierUseCaseTest {
    
    private lateinit var repository: SupplierRepository
    private lateinit var useCase: UpdateSupplierUseCase
    
    private val existingSupplier = Supplier(
        id = "1",
        name = "Original Supplier",
        email = "original@test.com",
        phone = "123456789",
        address = "Original Address",
        state = SupplierState.ACTIVE
    )
    
    private val updatedSupplier = existingSupplier.copy(
        name = "Updated Supplier",
        email = "updated@test.com"
    )
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdateSupplierUseCase(repository)
    }
    
    @Test
    fun `invoke returns success with updated supplier when repository succeeds`() = runTest {
        // Given
        coEvery { repository.updateSupplier("1", updatedSupplier) } returns Result.success(updatedSupplier)
        
        // When
        val result = useCase("1", updatedSupplier)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Updated Supplier", result.getOrNull()?.name)
        assertEquals("updated@test.com", result.getOrNull()?.email)
        coVerify(exactly = 1) { repository.updateSupplier("1", updatedSupplier) }
    }
    
    @Test
    fun `invoke returns failure when supplier not found`() = runTest {
        // Given
        coEvery { repository.updateSupplier("999", updatedSupplier) } returns Result.failure(
            Exception("Supplier not found")
        )
        
        // When
        val result = useCase("999", updatedSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Supplier not found", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.updateSupplier("999", updatedSupplier) }
    }
    
    @Test
    fun `invoke returns failure when repository fails with validation error`() = runTest {
        // Given
        coEvery { repository.updateSupplier("1", updatedSupplier) } returns Result.failure(
            Exception("Email already exists")
        )
        
        // When
        val result = useCase("1", updatedSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Email already exists", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.updateSupplier("1", updatedSupplier) }
    }
    
    @Test
    fun `invoke returns failure when repository fails with network error`() = runTest {
        // Given
        coEvery { repository.updateSupplier("1", updatedSupplier) } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase("1", updatedSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.updateSupplier("1", updatedSupplier) }
    }
    
    @Test
    fun `invoke correctly passes id and supplier data to repository`() = runTest {
        // Given
        coEvery { repository.updateSupplier("1", updatedSupplier) } returns Result.success(updatedSupplier)
        
        // When
        useCase("1", updatedSupplier)
        
        // Then
        coVerify(exactly = 1) { 
            repository.updateSupplier(
                "1",
                match { 
                    it.name == "Updated Supplier" && 
                    it.email == "updated@test.com"
                }
            ) 
        }
    }
}
