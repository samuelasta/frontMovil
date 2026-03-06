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
 * Unit tests for GetSupplierByIdUseCase.
 * Validates: Requirements 4.1
 */
class GetSupplierByIdUseCaseTest {
    
    private lateinit var repository: SupplierRepository
    private lateinit var useCase: GetSupplierByIdUseCase
    
    private val testSupplier = Supplier(
        id = "1",
        name = "Test Supplier",
        email = "supplier@test.com",
        phone = "123456789",
        address = "Test Address",
        state = SupplierState.ACTIVE
    )
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSupplierByIdUseCase(repository)
    }
    
    @Test
    fun `invoke returns success with supplier when repository succeeds`() = runTest {
        // Given
        coEvery { repository.getSupplierById("1") } returns Result.success(testSupplier)
        
        // When
        val result = useCase("1")
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Test Supplier", result.getOrNull()?.name)
        assertEquals("supplier@test.com", result.getOrNull()?.email)
        coVerify(exactly = 1) { repository.getSupplierById("1") }
    }
    
    @Test
    fun `invoke returns failure when supplier not found`() = runTest {
        // Given
        coEvery { repository.getSupplierById("999") } returns Result.failure(
            Exception("Supplier not found")
        )
        
        // When
        val result = useCase("999")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Supplier not found", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getSupplierById("999") }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        coEvery { repository.getSupplierById("1") } returns Result.failure(
            Exception("Network error")
        )
        
        // When
        val result = useCase("1")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getSupplierById("1") }
    }
}
