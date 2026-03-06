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
 * Unit tests for CreateSupplierUseCase.
 * Validates: Requirements 4.3
 */
class CreateSupplierUseCaseTest {
    
    private lateinit var repository: SupplierRepository
    private lateinit var useCase: CreateSupplierUseCase
    
    private val newSupplier = Supplier(
        id = null,
        name = "New Supplier",
        email = "new@test.com",
        phone = "123456789",
        address = "New Address",
        state = SupplierState.ACTIVE
    )
    
    private val createdSupplier = newSupplier.copy(id = "1")
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = CreateSupplierUseCase(repository)
    }
    
    @Test
    fun `invoke returns success with created supplier when repository succeeds`() = runTest {
        // Given
        coEvery { repository.createSupplier(newSupplier) } returns Result.success(createdSupplier)
        
        // When
        val result = useCase(newSupplier)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
        assertEquals("New Supplier", result.getOrNull()?.name)
        coVerify(exactly = 1) { repository.createSupplier(newSupplier) }
    }
    
    @Test
    fun `invoke returns failure when repository fails with validation error`() = runTest {
        // Given
        coEvery { repository.createSupplier(newSupplier) } returns Result.failure(
            Exception("Email already exists")
        )
        
        // When
        val result = useCase(newSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Email already exists", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.createSupplier(newSupplier) }
    }
    
    @Test
    fun `invoke returns failure when repository fails with network error`() = runTest {
        // Given
        coEvery { repository.createSupplier(newSupplier) } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase(newSupplier)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.createSupplier(newSupplier) }
    }
    
    @Test
    fun `invoke correctly passes supplier data to repository`() = runTest {
        // Given
        coEvery { repository.createSupplier(newSupplier) } returns Result.success(createdSupplier)
        
        // When
        useCase(newSupplier)
        
        // Then
        coVerify(exactly = 1) { 
            repository.createSupplier(
                match { 
                    it.name == "New Supplier" && 
                    it.email == "new@test.com" &&
                    it.phone == "123456789" &&
                    it.address == "New Address"
                }
            ) 
        }
    }
}
